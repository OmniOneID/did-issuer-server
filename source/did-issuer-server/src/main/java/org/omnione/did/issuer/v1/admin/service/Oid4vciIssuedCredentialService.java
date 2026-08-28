/*
 * Copyright 2026 OmniOne.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 */
package org.omnione.did.issuer.v1.admin.service;

import jakarta.persistence.criteria.Predicate;
import lombok.RequiredArgsConstructor;
import org.omnione.did.base.db.domain.Oid4vcCredentialIssuanceEntity;
import org.omnione.did.base.db.domain.Oid4vcCredentialStatusHistoryEntity;
import org.omnione.did.base.db.domain.IssuanceState;
import org.omnione.did.base.db.repository.Oid4vcCredentialIssuanceRepository;
import org.omnione.did.base.db.repository.Oid4vcCredentialStatusHistoryRepository;
import org.omnione.did.base.db.repository.Oid4vcStatusListRepository;
import org.omnione.did.base.exception.ErrorCode;
import org.omnione.did.base.exception.OpenDidException;
import org.omnione.did.issuer.v1.admin.dto.oid4vci.Oid4vciIssuedCredentialDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.omnione.did.oid4vc.oid4vci.status.model.CredentialStatus;
import org.omnione.did.oid4vc.oid4vci.status.model.CredentialStatusTransitionPolicy;
import org.omnione.did.oid4vc.oid4vci.status.model.StatusListValidator;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class Oid4vciIssuedCredentialService {
    private static final String ADMIN_CONSOLE_ACTOR = "admin-console";

    private final Oid4vcCredentialIssuanceRepository repository;
    private final Oid4vcCredentialStatusHistoryRepository historyRepository;
    private final Oid4vcStatusListRepository statusListRepository;

    public Page<Oid4vciIssuedCredentialDto> search(
            String searchKey, String searchValue, Pageable pageable) {
        Specification<Oid4vcCredentialIssuanceEntity> specification = (root, query, builder) -> {
            List<Predicate> predicates = new ArrayList<>();
            if (StringUtils.hasText(searchKey) && StringUtils.hasText(searchValue)) {
                try {
                    switch (searchKey) {
                        case "issuanceId", "userId", "configId", "format" ->
                                predicates.add(builder.like(
                                        builder.lower(root.get(searchKey)),
                                        "%" + searchValue.toLowerCase() + "%"));
                        case "issuanceState" -> predicates.add(builder.equal(
                                root.get(searchKey), IssuanceState.valueOf(searchValue.toUpperCase())));
                        case "credentialStatus" -> predicates.add(builder.equal(
                                root.get(searchKey), CredentialStatus.valueOf(searchValue.toUpperCase())));
                        default -> predicates.add(builder.disjunction());
                    }
                } catch (IllegalArgumentException exception) {
                    predicates.add(builder.disjunction());
                }
            }
            return builder.and(predicates.toArray(Predicate[]::new));
        };
        return repository.findAll(specification, pageable)
                .map(Oid4vciIssuedCredentialDto::fromEntity);
    }

    public Oid4vciIssuedCredentialDto findById(Long id) {
        return repository.findById(id)
                .map(Oid4vciIssuedCredentialDto::fromEntity)
                .orElseThrow(() -> new OpenDidException(ErrorCode.VC_NOT_FOUND));
    }

    @Transactional
    public Oid4vciIssuedCredentialDto changeStatus(
            Long id, CredentialStatus newStatus, String reason) {
        if (newStatus == null || !StringUtils.hasText(reason)) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST, "Status and reason are required.");
        }

        Oid4vcCredentialIssuanceEntity issuance = repository.findByIdForUpdate(id)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "OID4VCI credential was not found."));
        if (issuance.getIssuanceState() != IssuanceState.ISSUED) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST, "Only issued credentials can change status.");
        }

        CredentialStatus currentStatus = issuance.getCredentialStatus();
        if (currentStatus == newStatus) {
            return Oid4vciIssuedCredentialDto.fromEntity(issuance);
        }
        if (!CredentialStatusTransitionPolicy.isAllowed(currentStatus, newStatus)) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Credential status transition is not allowed: " + currentStatus + " -> " + newStatus);
        }

        var statusList = statusListRepository.findByIdForUpdate(issuance.getStatusList().getId())
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "OID4VCI status list was not found."));
        try {
            StatusListValidator.validateStatusValue(statusList.getBits(), newStatus.getValue());
        } catch (IllegalArgumentException exception) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, exception.getMessage(), exception);
        }

        Instant changedAt = Instant.now();
        issuance.setCredentialStatus(newStatus);
        issuance.setStatusChangedAt(changedAt);
        statusList.setListVersion(statusList.getListVersion() + 1);

        Oid4vcCredentialStatusHistoryEntity history = new Oid4vcCredentialStatusHistoryEntity();
        history.setCredentialIssuance(issuance);
        history.setPreviousStatus(currentStatus);
        history.setNewStatus(newStatus);
        history.setReason(reason.trim());
        history.setChangedBy(ADMIN_CONSOLE_ACTOR);
        history.setChangedAt(changedAt);
        historyRepository.save(history);

        return Oid4vciIssuedCredentialDto.fromEntity(issuance);
    }
}

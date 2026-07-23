/*
 * Copyright 2026 OmniOne.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 */
package org.omnione.did.base.db.store;

import org.omnione.did.base.db.domain.Oid4vcCredentialIssuanceEntity;
import org.omnione.did.base.db.domain.Oid4vcStatusListEntity;
import org.omnione.did.base.db.repository.Oid4vcCredentialIssuanceRepository;
import org.omnione.did.base.db.repository.Oid4vcStatusListRepository;
import lombok.RequiredArgsConstructor;
import org.omnione.did.oid4vc.oid4vci.status.model.StatusListDescriptor;
import org.omnione.did.oid4vc.oid4vci.status.model.StatusListSnapshot;
import org.omnione.did.oid4vc.oid4vci.status.spi.StatusListSnapshotSource;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * JPA adapter that converts persisted Status List state into an SDK snapshot.
 */
@Component
@Profile("!local")
@RequiredArgsConstructor
public class JpaStatusListSnapshotSource implements StatusListSnapshotSource {

    private final Oid4vcStatusListRepository statusListRepository;
    private final Oid4vcCredentialIssuanceRepository issuanceRepository;

    @Override
    @Transactional(readOnly = true)
    public Optional<StatusListSnapshot> findById(String listId) {
        Long databaseId = parseDatabaseId(listId);
        if (databaseId == null) {
            return Optional.empty();
        }
        return statusListRepository.findById(databaseId)
                .map(statusList -> toSnapshot(statusList, databaseId));
    }

    private StatusListSnapshot toSnapshot(Oid4vcStatusListEntity statusList, Long databaseId) {
        List<Oid4vcCredentialIssuanceEntity> issuances =
                issuanceRepository.findAllByStatusListIdOrderByStatusListIndexAsc(databaseId);
        Map<Long, Integer> statuses = new LinkedHashMap<>();
        issuances.forEach(issuance -> statuses.put(
                issuance.getStatusListIndex(), issuance.getCredentialStatus().getValue()));

        StatusListDescriptor descriptor = new StatusListDescriptor(
                databaseId.toString(),
                statusList.getListUri(),
                statusList.getFormat(),
                statusList.getSigningKeyId(),
                statusList.getBits(),
                statusList.getCapacity(),
                statusList.getTtlSeconds(),
                statusList.getListVersion());
        return new StatusListSnapshot(descriptor, statuses);
    }

    private Long parseDatabaseId(String listId) {
        if (listId == null || listId.isBlank()) {
            return null;
        }
        try {
            return Long.valueOf(listId);
        } catch (NumberFormatException e) {
            return null;
        }
    }
}

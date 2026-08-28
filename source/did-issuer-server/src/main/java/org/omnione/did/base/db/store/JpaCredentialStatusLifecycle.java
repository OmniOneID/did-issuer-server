/*
 * Copyright 2026 OmniOne.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 */
package org.omnione.did.base.db.store;

import io.micrometer.core.instrument.Metrics;
import lombok.extern.slf4j.Slf4j;
import org.omnione.did.base.db.domain.IssuanceState;
import org.omnione.did.base.db.domain.Oid4vcCredentialIssuanceEntity;
import org.omnione.did.base.db.repository.Oid4vcCredentialIssuanceRepository;
import org.omnione.did.issuer.v1.agent.service.oid4vc.status.StatusIndexAllocator;
import org.omnione.did.oid4vc.oid4vci.exception.OID4VCIErrorCode;
import org.omnione.did.oid4vc.oid4vci.exception.OID4VCIException;
import org.omnione.did.oid4vc.oid4vci.spi.CredentialStatusAllocation;
import org.omnione.did.oid4vc.oid4vci.spi.CredentialStatusLifecycle;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.Instant;
import java.util.HexFormat;
import java.util.Optional;

/**
 * JPA persistence adapter for status-enabled credential issuance lifecycle transitions.
 */
@Service
@Profile("!local")
@Slf4j
public class JpaCredentialStatusLifecycle implements CredentialStatusLifecycle {

    private final StatusIndexAllocator statusIndexAllocator;
    private final Oid4vcCredentialIssuanceRepository issuanceRepository;

    public JpaCredentialStatusLifecycle(
            StatusIndexAllocator statusIndexAllocator,
            Oid4vcCredentialIssuanceRepository issuanceRepository) {
        this.statusIndexAllocator = statusIndexAllocator;
        this.issuanceRepository = issuanceRepository;
    }

    @Override
    public Optional<CredentialStatusAllocation> allocate(
            String userId, String configId, String format) throws OID4VCIException {
        if (!isStatusEnabledFormat(format)) {
            return Optional.empty();
        }
        return Optional.of(statusIndexAllocator.allocate(userId, configId, format, null));
    }

    @Override
    @Transactional
    public void markIssued(String issuanceId, Object credential) throws OID4VCIException {
        Oid4vcCredentialIssuanceEntity issuance = issuanceRepository
                .findByIssuanceIdForUpdate(issuanceId)
                .orElseThrow(() -> credentialNotFound(issuanceId));
        if (issuance.getIssuanceState() != IssuanceState.ALLOCATED) {
            throw invalidTransition(
                    issuance.getIssuanceState() + " -> " + IssuanceState.ISSUED);
        }

        issuance.setIssuanceState(IssuanceState.ISSUED);
        issuance.setIssuedAt(Instant.now());
        issuance.setTokenHash(sha256(String.valueOf(credential)));
        Metrics.counter("oid4vc.credential.issuance", "format", issuance.getFormat(),
                "state", "issued").increment();
        log.info("Credential issuance completed: issuanceId={}, format={}, listId={}, idx={}",
                issuanceId, issuance.getFormat(), issuance.getStatusList().getId(),
                issuance.getStatusListIndex());
    }

    @Override
    @Transactional
    public void markFailed(String issuanceId, String failureCode, String failureMessage)
            throws OID4VCIException {
        Oid4vcCredentialIssuanceEntity issuance = issuanceRepository
                .findByIssuanceIdForUpdate(issuanceId)
                .orElseThrow(() -> credentialNotFound(issuanceId));
        if (issuance.getIssuanceState() == IssuanceState.FAILED) {
            return;
        }
        if (issuance.getIssuanceState() != IssuanceState.ALLOCATED) {
            throw invalidTransition(
                    issuance.getIssuanceState() + " -> " + IssuanceState.FAILED);
        }
        issuance.setIssuanceState(IssuanceState.FAILED);
        issuance.setFailedAt(Instant.now());
        issuance.setFailureCode(truncate(failureCode, 64));
        issuance.setFailureMessage(truncate(failureMessage, 512));
        Metrics.counter("oid4vc.credential.issuance", "format", issuance.getFormat(),
                "state", "failed").increment();
        log.warn("Credential issuance failed: issuanceId={}, format={}, code={}",
                issuanceId, issuance.getFormat(), issuance.getFailureCode());
    }

    private boolean isStatusEnabledFormat(String format) {
        return format != null
                && (format.startsWith("dc+sd-jwt") || format.startsWith("mso_mdoc"));
    }

    private OID4VCIException credentialNotFound(String issuanceId) {
        return new OID4VCIException(
                OID4VCIErrorCode.ERR_CODE_STATUS_LIST_CREDENTIAL_NOT_FOUND,
                "issuanceId=" + issuanceId);
    }

    private OID4VCIException invalidTransition(String transition) {
        return new OID4VCIException(
                OID4VCIErrorCode.ERR_CODE_STATUS_LIST_INVALID_TRANSITION,
                transition);
    }

    private String truncate(String value, int maximumLength) {
        if (value == null || value.length() <= maximumLength) {
            return value;
        }
        return value.substring(0, maximumLength);
    }

    private String sha256(String credential) {
        try {
            byte[] digest = MessageDigest.getInstance("SHA-256")
                    .digest(credential.getBytes(StandardCharsets.UTF_8));
            return HexFormat.of().formatHex(digest);
        } catch (NoSuchAlgorithmException e) {
            throw new IllegalStateException("SHA-256 is unavailable", e);
        }
    }
}

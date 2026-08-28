/*
 * Copyright 2026 OmniOne.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 */
package org.omnione.did.issuer.v1.admin.dto.oid4vci;

import lombok.Builder;
import lombok.Getter;
import org.omnione.did.base.db.domain.Oid4vcCredentialIssuanceEntity;

import java.time.Instant;

@Getter
@Builder
public class Oid4vciIssuedCredentialDto {
    private Long id;
    private String issuanceId;
    private String userId;
    private String configId;
    private String format;
    private String issuanceState;
    private String credentialStatus;
    private Instant issuedAt;
    private Instant expiresAt;
    private Instant statusChangedAt;
    private String statusListUri;
    private Long statusListIndex;
    private String tokenHash;
    private Instant failedAt;
    private String failureCode;
    private String failureMessage;
    private Instant createdAt;
    private Instant updatedAt;

    public static Oid4vciIssuedCredentialDto fromEntity(Oid4vcCredentialIssuanceEntity entity) {
        return Oid4vciIssuedCredentialDto.builder()
                .id(entity.getId())
                .issuanceId(entity.getIssuanceId())
                .userId(entity.getUserId())
                .configId(entity.getConfigId())
                .format(entity.getFormat())
                .issuanceState(entity.getIssuanceState().name())
                .credentialStatus(entity.getCredentialStatus().name())
                .issuedAt(entity.getIssuedAt())
                .expiresAt(entity.getExpiresAt())
                .statusChangedAt(entity.getStatusChangedAt())
                .statusListUri(entity.getStatusList().getListUri())
                .statusListIndex(entity.getStatusListIndex())
                .tokenHash(entity.getTokenHash())
                .failedAt(entity.getFailedAt())
                .failureCode(entity.getFailureCode())
                .failureMessage(entity.getFailureMessage())
                .createdAt(entity.getCreatedAt())
                .updatedAt(entity.getUpdatedAt())
                .build();
    }
}

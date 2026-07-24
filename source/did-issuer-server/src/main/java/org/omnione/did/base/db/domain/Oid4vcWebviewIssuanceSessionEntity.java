/*
 * Copyright 2026 OmniOne.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 */
package org.omnione.did.base.db.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.Instant;

@Getter
@Setter
@Entity
@Table(name = "t_oid4vc_webview_issuance_session")
public class Oid4vcWebviewIssuanceSessionEntity extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "session_token", nullable = false, unique = true, length = 64)
    private String sessionToken;

    @Column(name = "csrf_token", nullable = false, length = 64)
    private String csrfToken;

    @Column(name = "user_id", nullable = false, length = 128)
    private String userId;

    @Column(name = "credential_configuration_id", nullable = false, length = 100)
    private String credentialConfigurationId;

    @Column(name = "credential_type", nullable = false, length = 128)
    private String credentialType;

    @Column(name = "status", nullable = false, length = 24)
    private String status;

    @Column(name = "expires_at", nullable = false)
    private Instant expiresAt;

    @Column(name = "credential_offer_uri", columnDefinition = "TEXT")
    private String credentialOfferUri;
}

/*
 * Copyright 2026 OmniOne.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.omnione.did.base.db.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.omnione.did.oid4vc.oid4vci.status.model.CredentialStatus;

import java.time.Instant;

@Getter
@Setter
@Entity
@Table(name = "t_oid4vc_credential_issuance", uniqueConstraints = {
        @UniqueConstraint(name = "uk_oid4vc_issuance_id", columnNames = "issuance_id"),
        @UniqueConstraint(name = "uk_oid4vc_status_list_index", columnNames = {
                "status_list_id", "status_list_index"
        })
})
public class Oid4vcCredentialIssuanceEntity extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "issuance_id", nullable = false, length = 36)
    private String issuanceId;

    @Column(name = "user_id", nullable = false, length = 128)
    private String userId;

    @Column(name = "config_id", nullable = false, length = 128)
    private String configId;

    @Column(name = "format", nullable = false, length = 64)
    private String format;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "status_list_id", nullable = false)
    private Oid4vcStatusListEntity statusList;

    @Column(name = "status_list_index", nullable = false)
    private long statusListIndex;

    @Enumerated(EnumType.STRING)
    @Column(name = "credential_status", nullable = false, length = 20)
    private CredentialStatus credentialStatus = CredentialStatus.VALID;

    @Enumerated(EnumType.STRING)
    @Column(name = "issuance_state", nullable = false, length = 20)
    private IssuanceState issuanceState = IssuanceState.ALLOCATED;

    @Column(name = "issued_at")
    private Instant issuedAt;

    @Column(name = "expires_at")
    private Instant expiresAt;

    @Column(name = "status_changed_at")
    private Instant statusChangedAt;

    @Column(name = "token_hash", length = 64)
    private String tokenHash;

    @Column(name = "failed_at")
    private Instant failedAt;

    @Column(name = "failure_code", length = 64)
    private String failureCode;

    @Column(name = "failure_message", length = 512)
    private String failureMessage;
}

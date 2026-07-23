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
@Table(name = "t_oid4vc_credential_status_history")
public class Oid4vcCredentialStatusHistoryEntity extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "credential_issuance_id", nullable = false)
    private Oid4vcCredentialIssuanceEntity credentialIssuance;

    @Enumerated(EnumType.STRING)
    @Column(name = "previous_status", length = 20)
    private CredentialStatus previousStatus;

    @Enumerated(EnumType.STRING)
    @Column(name = "new_status", nullable = false, length = 20)
    private CredentialStatus newStatus;

    @Column(name = "reason", length = 512)
    private String reason;

    @Column(name = "changed_by", nullable = false, length = 128)
    private String changedBy;

    @Column(name = "changed_at", nullable = false)
    private Instant changedAt;
}

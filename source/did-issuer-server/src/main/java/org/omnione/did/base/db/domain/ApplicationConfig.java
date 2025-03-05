/*
 * Copyright 2024 OmniOne.
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
import lombok.*;
import org.omnione.did.base.datamodel.enums.VerifyAuthType;
import org.omnione.did.data.model.enums.vc.VcStatus;

import java.io.Serializable;


/**
 * Entity class for the revoke_vc table.
 * Represents a VC revocation entity in the database.
 */
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "application_config")
@Entity
public class ApplicationConfig extends BaseEntity implements Serializable {
    @Id
    @Column(name = "id", nullable = false)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "tas_url", nullable = false)
    private String tasUrl;

    @Column(name = "assert_key_id", nullable = false)
    private String assertKeyId;

    @Enumerated(value = EnumType.STRING)
    @Column(name = "revoke_auth_type", nullable = false)
    private VerifyAuthType revokeAuthType;

}

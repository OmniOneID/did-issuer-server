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

import java.time.Instant;

@Getter
@Setter
@Entity
@Table(name = "t_oid4vc_pre_auth_code")
public class Oid4vcPreAuthCodeEntity extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "code", unique = true, nullable = false, length = 128)
    private String code;

    @Column(name = "user_id", length = 128)
    private String userId;

    @Column(name = "user_pin", length = 256)
    private String userPin;

    @Column(name = "scopes", columnDefinition = "TEXT")
    private String scopes;

    @Column(name = "consumed")
    private Boolean consumed = false;

    @Column(name = "expires_at", nullable = false)
    private Instant expiresAt;
}

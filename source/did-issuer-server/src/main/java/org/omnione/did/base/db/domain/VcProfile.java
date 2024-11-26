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

import java.io.Serializable;

/**
 * Entity class for the vc_profile table.
 * Represents a VC profile entity in the database.
 */
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@ToString
@Entity
@Table(name = "vc_profile")
public class VcProfile extends BaseEntity implements Serializable {
    /**
     * The ID of the VC profile.
     */
    @Id
    @Column(name = "id", nullable = false)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * The ID of the profile.
     */
    @Column(name = "profile_id")
    private String profileId;

    /**
     * The DID of the VC profile.
     */
    @Column(name = "did")
    private String did;

    /**
     * nonce
     */
    @Column(name = "nonce")
    private String nonce;

    /**
     * user_id
     */
    @Column(name = "user_id")
    private Long userId;

    /**
     * transaction_id
     */
    @Column(name = "transaction_id")
    private Long transactionId;
}

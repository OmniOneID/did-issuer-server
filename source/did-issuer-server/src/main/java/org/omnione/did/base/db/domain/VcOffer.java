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
import org.omnione.did.base.datamodel.enums.OfferType;

import java.io.Serializable;
import java.time.Instant;

/**
 * Entity class for the vc_offer table.
 * Represents a VC offer entity in the database.
 */
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@ToString
@Entity
@Table(name = "vc_offer")
public class VcOffer extends BaseEntity implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "offer_id")
    private String offerId;

    @Enumerated(value = EnumType.STRING)
    @Column(name = "offer_type", columnDefinition = "varchar(50)")
    private OfferType offerType;

    @Column(name = "vc_plan_id")
    private String vcPlanId;

    @Column(name = "did")
    private String did;

    @Column(name = "valid_until")
    private Instant validUntil;

    @Column(name = "transaction_id")
    private Long transactionId;
}

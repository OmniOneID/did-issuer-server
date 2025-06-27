/*
 * Copyright 2025 OmniOne.
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
import org.omnione.did.base.datamodel.enums.InitiateType;
import org.omnione.did.base.db.converter.StringListConverter;

import java.io.Serializable;
import java.util.List;

/**
 * Entity class for the issue_profile table.
 * Represents a IssueProfile entity in the database.
 */
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@ToString
@Entity
@Table(name = "issue_profile")
public class IssueProfile extends BaseEntity implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "vc_schema_id")
    private Long vcSchemaId;

    @Column(name = "vc_plan_id")
    private String vcPlanId;

    @Column(name = "title")
    private String title;

    @Column(name = "description")
    private String description;

    @Convert(converter = StringListConverter.class)
    @Column(name = "endpoints")
    private List<String> endpoints;

    @Column(name = "cipher")
    private String cipher;

    @Column(name = "curve")
    private String curve;

    @Column(name = "padding")
    private String padding;

    @Column(name = "language")
    private String language;

    @Enumerated(EnumType.STRING)
    @Column(name = "initiate_type")
    private InitiateType initiateType = InitiateType.USER_INIT;

    @Convert(converter = StringListConverter.class)
    @Column(name = "tags")
    private List<String> tags;

    @Column(name = "zkp_enabled", nullable = false)
    private Boolean zkpEnabled;

    @Column(name = "definition_id")
    private String definitionId;
}

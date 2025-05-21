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

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.omnione.did.base.db.constant.ZkpCredentialDefinitionStatus;
import org.omnione.did.zkp.datamodel.enums.CredentialType;

import java.io.Serializable;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@ToString
@Entity
@Table(name = "zkp_credential_definition")
public class ZkpCredentialDefinition extends BaseEntity implements Serializable {
    @Id
    @Column(name = "id", nullable = false)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "definition_id", unique = true, length = 100)
    private String definitionId;

    @Column(name = "schema_id", nullable = false, length = 100)
    private String schemaId;

    @Column(name = "type", nullable = false)
    @Enumerated(EnumType.STRING)
    private CredentialType type;

    @Column(name = "alias", nullable = false, length = 50)
    private String alias;

    @Column(name = "tag", nullable = false, length = 500)
    private String tag;

    @Column(name = "version", nullable = false, length = 10)
    private String version;

    @Column(name = "definition", nullable = false)
    private String definition;

    @Column(name = "zkp_schema_id", nullable = false)
    private Long zkpSchemaId;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, columnDefinition = "VARCHAR(50)")
    private ZkpCredentialDefinitionStatus status;
}

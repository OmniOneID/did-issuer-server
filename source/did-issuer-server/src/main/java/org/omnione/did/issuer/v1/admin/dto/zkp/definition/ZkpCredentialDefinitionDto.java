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
package org.omnione.did.issuer.v1.admin.dto.zkp.definition;

import lombok.Builder;
import lombok.Getter;
import org.omnione.did.base.db.constant.ZkpCredentialDefinitionStatus;
import org.omnione.did.base.db.domain.ZkpCredentialDefinition;
import org.omnione.did.zkp.datamodel.enums.CredentialType;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;

@Getter
@Builder
public class ZkpCredentialDefinitionDto {
    private final Long id;
    private final String definitionId;
    private final String schemaId;
    private final CredentialType type;
    private final String alias;
    private final String tag;
    private final String version;
    private final String definition;
    private final Long zkpSchemaId;
    private final ZkpCredentialDefinitionStatus status;
    private final String createdAt;
    private final String updatedAt;
    private final String schemaName;

    public static ZkpCredentialDefinitionDto fromEntity(ZkpCredentialDefinition zkpCredentialDefinition) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        return ZkpCredentialDefinitionDto.builder()
                .id(zkpCredentialDefinition.getId())
                .definitionId(zkpCredentialDefinition.getDefinitionId())
                .schemaId(zkpCredentialDefinition.getSchemaId())
                .type(zkpCredentialDefinition.getType())
                .alias(zkpCredentialDefinition.getAlias())
                .tag(zkpCredentialDefinition.getTag())
                .version(zkpCredentialDefinition.getVersion())
                .definition(zkpCredentialDefinition.getDefinition())
                .zkpSchemaId(zkpCredentialDefinition.getZkpSchemaId())
                .status(zkpCredentialDefinition.getStatus())
                .createdAt(formatInstant(zkpCredentialDefinition.getCreatedAt(), formatter))
                .updatedAt(formatInstant(zkpCredentialDefinition.getUpdatedAt(), formatter))
                .build();
    }

    public static ZkpCredentialDefinitionDto fromEntity(ZkpCredentialDefinition zkpCredentialDefinition, String schemaName) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        return ZkpCredentialDefinitionDto.builder()
                .id(zkpCredentialDefinition.getId())
                .definitionId(zkpCredentialDefinition.getDefinitionId())
                .schemaId(zkpCredentialDefinition.getSchemaId())
                .type(zkpCredentialDefinition.getType())
                .alias(zkpCredentialDefinition.getAlias())
                .tag(zkpCredentialDefinition.getTag())
                .version(zkpCredentialDefinition.getVersion())
                .definition(zkpCredentialDefinition.getDefinition())
                .zkpSchemaId(zkpCredentialDefinition.getZkpSchemaId())
                .status(zkpCredentialDefinition.getStatus())
                .schemaName(schemaName)
                .createdAt(formatInstant(zkpCredentialDefinition.getCreatedAt(), formatter))
                .updatedAt(formatInstant(zkpCredentialDefinition.getUpdatedAt(), formatter))
                .build();
    }

    private static String formatInstant(Instant instant, DateTimeFormatter formatter) {
        if (instant == null) return null;
        return LocalDateTime.ofInstant(instant, ZoneId.systemDefault()).format(formatter);
    }

}

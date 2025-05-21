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
package org.omnione.did.issuer.v1.admin.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.omnione.did.base.db.domain.IssuerInfo;
import org.omnione.did.base.db.domain.ZkpCredentialDefinition;
import org.omnione.did.base.exception.ErrorCode;
import org.omnione.did.base.exception.OpenDidException;
import org.omnione.did.issuer.v1.admin.api.dto.EmptyResDto;
import org.omnione.did.issuer.v1.admin.dto.zkp.definition.VerifyCredentialDefinitionAliasUniqueResDto;
import org.omnione.did.issuer.v1.admin.dto.zkp.definition.ZkpCredentialDefinitionDto;
import org.omnione.did.issuer.v1.admin.dto.zkp.definition.ZkpCredentialDefinitionInfoDto;
import org.omnione.did.issuer.v1.admin.dto.zkp.schema.ZkpSchemaInfoDto;
import org.omnione.did.issuer.v1.admin.service.query.ZkpCredentialDefinitionQueryService;
import org.omnione.did.issuer.v1.agent.service.query.IssuerInfoQueryService;
import org.omnione.did.zkp.datamodel.definition.CredentialDefinition;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.omnione.did.zkp.datamodel.enums.CredentialType;

@Slf4j
@Transactional
@RequiredArgsConstructor
@Service
public class ZkpDefinitionService {
    private final ZkpCredentialDefinitionQueryService zkpCredentialDefinitionQueryService;
    private final IssuerInfoQueryService issuerInfoQueryService;

    public Page<ZkpCredentialDefinitionDto> searchZkpDefinitionList(String searchKey, String searchValue, Pageable pageable) {
        return zkpCredentialDefinitionQueryService.searchZkpCredentialDefinitionList(searchKey, searchValue, pageable);
    }

    public VerifyCredentialDefinitionAliasUniqueResDto verifyCredentialDefinitionAliasUnique(String alias) {
        long count = zkpCredentialDefinitionQueryService.countByAlias(alias);
        return VerifyCredentialDefinitionAliasUniqueResDto.builder()
                .isUnique(count == 0)
                .build();
    }

    public EmptyResDto createZkpCredentialDefinition(ZkpCredentialDefinitionInfoDto request) {
    // Find Issuer Info
        log.debug("Finding Issuer Info");
        IssuerInfo issuerInfo = issuerInfoQueryService.findIssuerInfo();
        log.debug("Found Issuer Info: {}", issuerInfo);

        // Generate Definition ID
        log.debug("Generating Schema ID");
        String definitionId = generateSchemaId(request, issuerInfo);
        log.debug("Generated Schema ID: {}", definitionId);

        // Check if Alias already exists
        log.debug("Checking if Alias already exists");
        if (zkpCredentialDefinitionQueryService.existByAlias(request.getAlias())) {
            log.error("Alias already exists: {}", request.getAlias());
            throw new OpenDidException(ErrorCode.CREDENTIAL_DEFINITION_ALIAS_ALREADY_EXISTS);
        }

        // Generate Credential Definition
        log.debug("Generating Credential Definition");

//        ZkpCredentialDefinition.builder()
//                .definitionId(request.getd())
//                .schemaId(request.getSchemaId())
//                .alias(request.getAlias())
//                .version(request.getVersion())
//                .type(request.getType())
//                .tag(request.getTag())
//                .build();

        return new EmptyResDto();
    }

    private String generateSchemaId(ZkpCredentialDefinitionInfoDto request, IssuerInfo issuerInfo) {
        CredentialType credentialType = request.getType();
        return String.format("%s:3:%s:%s:%s",
                issuerInfo.getDid(),
                credentialType.toString(),
                request.getSchemaId(),
                request.getTag());
    }

    //@TODO
    private String generateCredentialDefinition(ZkpCredentialDefinitionInfoDto request, String credentialDefinitionId) {
        CredentialDefinition credentialDefinition = new CredentialDefinition();
        credentialDefinition.setId(credentialDefinitionId);
        credentialDefinition.setSchemaId(request.getSchemaId());
        credentialDefinition.setVer(request.getVersion());
        credentialDefinition.setType(request.getType());


        return null;

    }
}

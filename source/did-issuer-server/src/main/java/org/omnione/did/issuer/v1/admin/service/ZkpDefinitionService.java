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
import org.omnione.did.base.db.constant.ZkpCredentialDefinitionStatus;
import org.omnione.did.base.db.domain.IssuerInfo;
import org.omnione.did.base.db.domain.ZkpCredentialDefinition;
import org.omnione.did.base.db.domain.ZkpSchema;
import org.omnione.did.base.exception.ErrorCode;
import org.omnione.did.base.exception.OpenDidException;
import org.omnione.did.issuer.v1.admin.api.dto.EmptyResDto;
import org.omnione.did.issuer.v1.admin.dto.zkp.definition.VerifyCredentialDefinitionAliasUniqueResDto;
import org.omnione.did.issuer.v1.admin.dto.zkp.definition.ZkpCredentialDefinitionDto;
import org.omnione.did.issuer.v1.admin.dto.zkp.definition.ZkpCredentialDefinitionSaveDto;
import org.omnione.did.issuer.v1.admin.service.query.ZkpCredentialDefinitionQueryService;
import org.omnione.did.issuer.v1.admin.service.query.ZkpSchemaQueryService;
import org.omnione.did.issuer.v1.agent.service.query.IssuerInfoQueryService;
import org.omnione.did.issuer.v1.common.service.ZkpWalletService;
import org.omnione.did.zkp.core.manager.ZkpCredentialMetadataManager;
import org.omnione.did.zkp.crypto.keypair.CredentialPrimaryPublicKey;
import org.omnione.did.zkp.datamodel.definition.CredentialDefinition;
import org.omnione.did.zkp.datamodel.schema.CredentialSchema;
import org.omnione.did.zkp.datamodel.util.GsonWrapper;
import org.springframework.data.domain.PageImpl;
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
    private final ZkpSchemaQueryService zkpSchemaQueryService;
    private final ZkpWalletService zkpWalletService;

    public PageImpl<ZkpCredentialDefinitionDto> searchZkpDefinitionList(String searchKey, String searchValue, Pageable pageable) {
        return zkpCredentialDefinitionQueryService.searchZkpCredentialDefinitionList(searchKey, searchValue, pageable);
    }

    public VerifyCredentialDefinitionAliasUniqueResDto verifyCredentialDefinitionAliasUnique(String alias) {
        long count = zkpCredentialDefinitionQueryService.countByAlias(alias);
        return VerifyCredentialDefinitionAliasUniqueResDto.builder()
                .isUnique(count == 0)
                .build();
    }

    public EmptyResDto createZkpCredentialDefinition(ZkpCredentialDefinitionSaveDto request) {
        // Find Issuer Info
        log.debug("Finding Issuer Info");
        IssuerInfo issuerInfo = issuerInfoQueryService.findIssuerInfo();
        log.debug("Found Issuer Info: {}", issuerInfo);

        // Find Schema
        log.debug("Finding Schema");
        ZkpSchema zkpSchema = zkpSchemaQueryService.findBySchemaId(request.getSchemaId());
        log.debug("Parsing Credential Schema");
        CredentialSchema credentialSchema = GsonWrapper.getGson()
                .fromJson(zkpSchema.getSchema(), CredentialSchema.class);

        // Check if Alias already exists
        log.debug("Checking if Alias already exists");
        if (zkpCredentialDefinitionQueryService.existByAlias(request.getAlias())) {
            log.error("Alias already exists: {}", request.getAlias());
            throw new OpenDidException(ErrorCode.CREDENTIAL_DEFINITION_ALIAS_ALREADY_EXISTS);
        }

        // Generate Credential Definition
        log.debug("Generating Credential Definition");
        CredentialDefinition credentialDefinition = generateCredentialDefinition(request, issuerInfo, credentialSchema);

        // Save Credential Definition
        log.debug("Saving Credential Definition");
        saveCredentialDefinition(request, credentialDefinition, zkpSchema);

        try {
            // Register to Blockchain
            log.debug("Registering Credential Definition to Blockchain");
            registerToBlockchain(credentialDefinition);

            // Register to List Provider
            log.debug("Registering Credential Definition to List Provider");
            registerToListProvider(credentialDefinition);
        } catch (OpenDidException e) {
            log.error("Failed to register to Blockchain or List Provider: {}", e.getMessage(), e);
        } catch (Exception e) {
            log.error("Failed to register to Blockchain or List Provider: {}", e.getMessage(), e);
        }

        log.debug("Credential Definition created successfully");
        return new EmptyResDto();
    }

    private CredentialDefinition generateCredentialDefinition(ZkpCredentialDefinitionSaveDto request, IssuerInfo issuerInfo, CredentialSchema credentialSchema) {
        try {
            // Initialize ZKP Wallet
            log.debug("Initializing ZKP Wallet");
            zkpWalletService.initializeZkpWallet();

            // Generate ZKP Key
            log.debug("Generating ZKP Key");
            zkpWalletService.generateRandomZkpKey(request.getAlias(), credentialSchema.getAttrNames());

            // Get Credential Primary Public Key
            log.debug("Getting Credential Primary Public Key");
            CredentialPrimaryPublicKey credentialPrimaryPublicKey = zkpWalletService.getZkpWalletManager()
                    .getCredentialPrimaryPublicKey(request.getAlias());

            // Create Credential Definition
            log.debug("Creating Credential Definition");
            CredentialDefinition generatedCredentialDefinition = new ZkpCredentialMetadataManager().createDefinition(issuerInfo.getDid(), credentialSchema, credentialPrimaryPublicKey, request.getTag(), request.getVersion());
            log.debug("Generated Credential Definition: {}", GsonWrapper.getGsonPrettyPrinting().toJson(generatedCredentialDefinition));

            return generatedCredentialDefinition;
        } catch (Exception e) {
            log.error("Error generating Credential Definition", e);
            throw new OpenDidException(ErrorCode.CREDENTIAL_DEFINITION_GENERATION_FAILED);
        }
    }

    private void saveCredentialDefinition(ZkpCredentialDefinitionSaveDto request, CredentialDefinition credentialDefinition, ZkpSchema zkpSchema) {
        zkpCredentialDefinitionQueryService.saveCredentialDefinition(
                ZkpCredentialDefinition.builder()
                .definitionId(credentialDefinition.getId())
                .schemaId(request.getSchemaId())
                .type(request.getType())
                .alias(request.getAlias())
                .tag(request.getTag())
                .version(request.getVersion())
                .definition(credentialDefinition.toJson())
                .status(ZkpCredentialDefinitionStatus.NEED_BLOCKCHAIN_REGISTRATION)
                .zkpSchemaId(zkpSchema.getId())
                .build()
        );
    }

    // @TODO: Blockchain registration
    private void registerToBlockchain(CredentialDefinition credentialDefinition) {
        throw new OpenDidException(ErrorCode.CREDENTIAL_DEFINITION_REGISTRATION_FAILED);
    }

    // @TODO: List Provider registration
    private void registerToListProvider(CredentialDefinition credentialDefinition) {
        throw new OpenDidException(ErrorCode.CREDENTIAL_DEFINITION_REGISTRATION_FAILED);
    }

    public ZkpCredentialDefinitionDto getZkpCredentialDefinitionInfo(Long id) {
        // Find Credential Definition
        log.debug("Finding Credential Definition");
        ZkpCredentialDefinition zkpCredentialDefinition = zkpCredentialDefinitionQueryService.findById(id);
        ZkpSchema zkpSchema = zkpSchemaQueryService.findBySchemaId(zkpCredentialDefinition.getSchemaId());

        return ZkpCredentialDefinitionDto.fromEntity(zkpCredentialDefinition, zkpSchema.getName());
    }
}

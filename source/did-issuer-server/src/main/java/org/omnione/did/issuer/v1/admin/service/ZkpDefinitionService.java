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
import org.omnione.did.issuer.v1.common.service.StorageService;
import org.omnione.did.issuer.v1.common.service.ZkpWalletService;
import org.omnione.did.zkp.core.manager.ZkpCredentialMetadataManager;
import org.omnione.did.zkp.crypto.keypair.CredentialPrimaryPublicKey;
import org.omnione.did.zkp.datamodel.definition.CredentialDefinition;
import org.omnione.did.zkp.datamodel.schema.CredentialSchema;
import org.omnione.did.zkp.datamodel.util.GsonWrapper;
import org.omnione.did.zkp.exception.ZkpException;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Slf4j
@Transactional
@RequiredArgsConstructor
@Service
public class ZkpDefinitionService {
    private final ZkpCredentialDefinitionQueryService zkpCredentialDefinitionQueryService;
    private final IssuerInfoQueryService issuerInfoQueryService;
    private final ZkpSchemaQueryService zkpSchemaQueryService;
    private final ZkpWalletService zkpWalletService;
    private final StorageService storageService;
    private final ListCommunityService listCommunityService;

    public PageImpl<ZkpCredentialDefinitionDto> searchZkpDefinitionList(String searchKey, String searchValue, Pageable pageable) {
        return zkpCredentialDefinitionQueryService.searchZkpCredentialDefinitionList(searchKey, searchValue, pageable);
    }

    public VerifyCredentialDefinitionAliasUniqueResDto verifyCredentialDefinitionAliasUnique(String alias) {
        long count = zkpCredentialDefinitionQueryService.countByAlias(alias);
        boolean isUnique = count == 0;
        
        if (!isUnique) {
            log.debug("Alias already exists: {}", alias);
        }
        
        return VerifyCredentialDefinitionAliasUniqueResDto.builder()
                .isUnique(isUnique)
                .build();
    }

    public EmptyResDto createZkpCredentialDefinition(ZkpCredentialDefinitionSaveDto request) {
        // Find Issuer Info
        log.debug("Finding Issuer Info");
        IssuerInfo issuerInfo = issuerInfoQueryService.getIssuerInfo();
        log.debug("Found Issuer Info: {}", issuerInfo);

        // Find Schema
        log.debug("Finding Schema");
        ZkpSchema zkpSchema = zkpSchemaQueryService.findBySchemaId(request.getSchemaId());
        if (zkpCredentialDefinitionQueryService.existBySchemaId(request.getSchemaId())) {
            log.error("Credential schema is already in use: {}", request.getSchemaId());
            throw new OpenDidException(ErrorCode.CREDENTIAL_DEFINITION_SCHEMA_ALREADY_IN_USE);
        }
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
        ZkpCredentialDefinition zkpCredentialDefinition = saveCredentialDefinition(request, credentialDefinition, zkpSchema);

        register(zkpCredentialDefinition, credentialDefinition);
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
            CredentialDefinition generatedCredentialDefinition = new ZkpCredentialMetadataManager().createDefinition(issuerInfo.getDid(), credentialSchema, credentialPrimaryPublicKey, request.getVersion(), request.getTag());
            log.debug("Generated Credential Definition: {}", GsonWrapper.getGsonPrettyPrinting().toJson(generatedCredentialDefinition));

            return generatedCredentialDefinition;
        } catch (OpenDidException | ZkpException e) {
            log.error("Error generating Credential Definition", e);
            zkpWalletService.deleteZkpKeyByAlias(request.getAlias());
            throw new OpenDidException(ErrorCode.CREDENTIAL_DEFINITION_GENERATION_FAILED);
        }
    }

    private ZkpCredentialDefinition saveCredentialDefinition(ZkpCredentialDefinitionSaveDto request, CredentialDefinition credentialDefinition, ZkpSchema zkpSchema) {
        return zkpCredentialDefinitionQueryService.saveCredentialDefinition(
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

    private void registerToBlockchain(ZkpCredentialDefinition zkpCredentialDefinition, CredentialDefinition credentialDefinition) {

        try {
            log.debug("Registering to Blockchain: {}", zkpCredentialDefinition.getSchemaId());
            registerCredentialDefinitionToBlockchain(credentialDefinition);

            zkpCredentialDefinition.setStatus(ZkpCredentialDefinitionStatus.NEED_LIST_PROVIDER_REGISTRATION);
            zkpCredentialDefinitionQueryService.updateZkpCredentialStatusById(zkpCredentialDefinition.getId(), zkpCredentialDefinition.getStatus());
        } catch (OpenDidException e) {
            log.error("Failed to register to Blockchain: {}", e.getMessage(), e);
            throw e;
        } catch (Exception e) {
            log.error("Failed to register to Blockchain: {}", e.getMessage(), e);
            throw new OpenDidException(ErrorCode.CREDENTIAL_DEFINITION_REGISTRATION_FAILED);
        }
    }

    private void registerCredentialDefinitionToBlockchain(CredentialDefinition credentialDefinition) {
        storageService.registerCredentialDefinition(credentialDefinition);
    }

    private void registerToListProvider(ZkpCredentialDefinition zkpCredentialDefinition, CredentialDefinition credentialDefinition) {
        try {
            log.debug("Registering to List Provider: {}", zkpCredentialDefinition.getDefinitionId());
            registerCredentialDefinitionToListProvider(credentialDefinition);

            zkpCredentialDefinition.setStatus(ZkpCredentialDefinitionStatus.ACTIVATE);
            zkpCredentialDefinitionQueryService.updateZkpCredentialStatusById(
                    zkpCredentialDefinition.getId(), zkpCredentialDefinition.getStatus());
        } catch (OpenDidException e) {
            log.error("Failed to register to List Provider: {}", e.getMessage(), e);
            throw e;
        } catch (Exception e) {
            log.error("Failed to register to List Provider: {}", e.getMessage(), e);
            throw new OpenDidException(ErrorCode.CREDENTIAL_DEFINITION_REGISTRATION_FAILED);
        }
    }

    private void registerCredentialDefinitionToListProvider(CredentialDefinition credentialDefinition) {
        listCommunityService.registerCredentialDefinition(credentialDefinition);
    }

    public ZkpCredentialDefinitionDto getZkpCredentialDefinitionInfo(Long id) {
        // Find Credential Definition
        log.debug("Finding Credential Definition");
        ZkpCredentialDefinition zkpCredentialDefinition = zkpCredentialDefinitionQueryService.findById(id);
        ZkpSchema zkpSchema = zkpSchemaQueryService.findBySchemaId(zkpCredentialDefinition.getSchemaId());

        return ZkpCredentialDefinitionDto.fromEntity(zkpCredentialDefinition, zkpSchema.getName());
    }

    public EmptyResDto reRegisterZkpCredentialDefinition() {
        var credentialDefinitionList = zkpCredentialDefinitionQueryService.findByStatus(ZkpCredentialDefinitionStatus.ACTIVATE);

        credentialDefinitionList.forEach(zkpCredentialDefinition -> {
            try {
                var status = zkpCredentialDefinition.getStatus();
                var credentialDefinition = GsonWrapper.getGson()
                        .fromJson(zkpCredentialDefinition.getDefinition(), CredentialDefinition.class);

                if (ZkpCredentialDefinitionStatus.NEED_BLOCKCHAIN_REGISTRATION.equals(status)) {
                    register(zkpCredentialDefinition, credentialDefinition);
                } else if (ZkpCredentialDefinitionStatus.NEED_LIST_PROVIDER_REGISTRATION.equals(status)) {
                    registerToListProvider(zkpCredentialDefinition, credentialDefinition);
                }
            } catch (OpenDidException e) {
                log.error("Failed to register for definition {}: {}", zkpCredentialDefinition.getId(), e.getMessage(), e);
            } catch (Exception e) {
                log.error("Unexpected error for definition {}: {}", zkpCredentialDefinition.getId(), e.getMessage(), e);
            }
        });

        return new EmptyResDto();
    }

    private void register(ZkpCredentialDefinition zkpCredentialDefinition, CredentialDefinition credentialDefinition) {
        try {
            // Register to Blockchain
            log.debug("Registering Credential Definition to Blockchain");
            registerToBlockchain(zkpCredentialDefinition, credentialDefinition);

            // Register to List Provider
            log.debug("Registering Credential Definition to List Provider");
            registerToListProvider(zkpCredentialDefinition, credentialDefinition);
        } catch (OpenDidException e) {
            log.error("Failed to register to Blockchain or List Provider: {}", e.getMessage(), e);
        } catch (Exception e) {
            log.error("Failed to register to Blockchain or List Provider: {}", e.getMessage(), e);
        }
    }
}

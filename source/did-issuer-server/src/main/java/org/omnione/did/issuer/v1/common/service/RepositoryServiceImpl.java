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

package org.omnione.did.issuer.v1.common.service;

import com.google.gson.JsonSyntaxException;
import feign.FeignException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.omnione.did.base.exception.ErrorCode;
import org.omnione.did.base.exception.OpenDidException;
import org.omnione.did.base.util.BaseCoreDidUtil;
import org.omnione.did.base.util.BaseCoreVcUtil;
import org.omnione.did.base.util.BaseMultibaseUtil;
import org.omnione.did.core.manager.DidManager;
import org.omnione.did.data.model.did.DidDocument;
import org.omnione.did.data.model.enums.vc.VcStatus;
import org.omnione.did.data.model.schema.VcSchema;
import org.omnione.did.data.model.vc.VcMeta;
import org.omnione.did.issuer.v1.agent.api.RepositoryFeign;
import org.omnione.did.issuer.v1.agent.api.dto.InputVcSchemaReqDto;
import org.omnione.did.issuer.v1.agent.api.dto.InputZkpCredentialDefinitionReqDto;
import org.omnione.did.issuer.v1.agent.api.dto.InputZkpCredentialSchemaReqDto;
import org.omnione.did.issuer.v1.agent.api.dto.UpdateVcStatusApiReqDto;
import org.omnione.did.zkp.datamodel.definition.CredentialDefinition;
import org.omnione.did.zkp.datamodel.schema.CredentialSchema;
import org.omnione.did.zkp.datamodel.util.GsonWrapper;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;

/**
 * Implementation of the StorageService interface.
 * This service provides methods for interacting with the repository service.
 */
@Service
@RequiredArgsConstructor
@Slf4j
@Primary
@Profile("lss")
public class RepositoryServiceImpl implements StorageService {
    private final RepositoryFeign repositoryFeign;

    /**
     * Finds a DID document by DID key URL.
     *
     * @param didKeyUrl The DID key URL.
     * @return The DID document.
     * @throws OpenDidException if the DID document is not found.
     */
    @Override
    public DidDocument findDidDoc(String didKeyUrl) {
        try {
            String didDocument = repositoryFeign.getDid(didKeyUrl);

            DidManager didManager = BaseCoreDidUtil.parseDidDoc(didDocument);

            return didManager.getDocument();
        } catch (FeignException e) {
            log.error("Failed to find DID document.", e);
            throw new OpenDidException(ErrorCode.DID_DOC_FIND_FAILURE);
        } catch (Exception e) {
            log.error("Failed to find DID document.", e);
            throw new OpenDidException(ErrorCode.DID_DOC_FIND_FAILURE);
        }
    }

    /**
     * Registers a verifiable credential metadata.
     *
     * @param vcMeta The VC metadata to register.
     */
    @Override
    public void registerVcMeta(VcMeta vcMeta) {
        repositoryFeign.inputVcMeta(vcMeta);
    }

    /**
     * Updates the status of a verifiable credential (VC).
     *
     * @param vcId     The VC identifier.
     * @param vcStatus The new status of the VC.
     */
    @Override
    public void updateVcStatus(String vcId, VcStatus vcStatus) {

        repositoryFeign.updateVcStatus(UpdateVcStatusApiReqDto.builder()
                .vcId(vcId)
                .vcStatus(vcStatus)
                .build());
    }

    /**
     * Retrieves the metadata of a verifiable credential (VC) by its identifier.
     *
     * @param vcId The VC identifier.
     * @return The found VC metadata.
     */
    @Override
    public VcMeta getVcMetaByVcId(String vcId) {
        try {
            String vcMetaData = repositoryFeign.getVcMetaData(vcId);

            return BaseCoreVcUtil.parseVcMeta(vcMetaData);
        } catch (OpenDidException e) {
            log.error("Failed to find VC meta data.", e);
            throw e;
        } catch (FeignException e) {
            log.error("Failed to find VC meta data.", e);
            throw new OpenDidException(ErrorCode.LSS_FIND_VC_META_FAILED);
        } catch (Exception e) {
            log.error("Failed to find VC meta data.", e);
            throw new OpenDidException(ErrorCode.LSS_FIND_VC_META_FAILED);
        }
    }

    @Override
    public void registerCredentialSchema(CredentialSchema credentialSchema) {
        String encodedCredentialSchema = encodeCredentialSchema(credentialSchema);
        repositoryFeign.registerCredentialSchema(
                InputZkpCredentialSchemaReqDto.builder()
                        .credentialSchema(encodedCredentialSchema)
                        .build()
        );
    }

    private String encodeCredentialSchema(CredentialSchema credentialSchema) {
        try {
            String credentialSchemaJson = GsonWrapper.getGson().toJson(credentialSchema);
            return BaseMultibaseUtil.encode(credentialSchemaJson.getBytes(StandardCharsets.UTF_8));
        } catch (JsonSyntaxException e) {
            log.error("\t--> Failed to encode Credential Schema: {}", e.getMessage());
            throw new OpenDidException(ErrorCode.CRYPTO_ENCODING_FAILED);
        } catch (Exception e) {
            log.error("\t--> Unexpected error while encoding Credential Schema: {}", e.getMessage());
            throw new OpenDidException(ErrorCode.CRYPTO_ENCODING_FAILED);
        }
    }

    @Override
    public void registerCredentialDefinition(CredentialDefinition credentialDefinition) {
        String encodedCredentialDefinition = encodeCredentialDefinition(credentialDefinition);
        repositoryFeign.registerCredentialDefinition(
                InputZkpCredentialDefinitionReqDto.builder()
                        .credentialDefinition(encodedCredentialDefinition)
                        .build()
        );
    }

    @Override
    public void registerVcSchema(VcSchema vcSchema, String did) {
        String encodedVcSchema = encodeVcSchema(vcSchema);
        repositoryFeign.registerVcSchema(
                InputVcSchemaReqDto.builder()
                        .did(did)
                        .vcSchema(encodedVcSchema)
                        .build()
        );
    }

    @Override
    public VcSchema getVcSchema(String vcSchemaId) {
        String vcSchema = repositoryFeign.getVcSchema(vcSchemaId);
        return GsonWrapper.getGson().fromJson(vcSchema, VcSchema.class);
    }

    private String encodeCredentialDefinition(CredentialDefinition credentialDefinition) {
        try {
            String credentialDefinitionJson = GsonWrapper.getGson().toJson(credentialDefinition);
            return BaseMultibaseUtil.encode(credentialDefinitionJson.getBytes(StandardCharsets.UTF_8));
        } catch (JsonSyntaxException e) {
            log.error("\t--> Failed to encode Credential Schema: {}", e.getMessage());
            throw new OpenDidException(ErrorCode.CRYPTO_ENCODING_FAILED);
        } catch (Exception e) {
            log.error("\t--> Unexpected error while encoding Credential Schema: {}", e.getMessage());
            throw new OpenDidException(ErrorCode.CRYPTO_ENCODING_FAILED);
        }
    }

    private String encodeVcSchema(VcSchema vcSchema) {
        try {
            String vcSchemaJson = GsonWrapper.getGson().toJson(vcSchema);
            return BaseMultibaseUtil.encode(vcSchemaJson.getBytes(StandardCharsets.UTF_8));
        } catch (JsonSyntaxException e) {
            log.error("\t--> Failed to encode Credential Schema: {}", e.getMessage());
            throw new OpenDidException(ErrorCode.CRYPTO_ENCODING_FAILED);
        } catch (Exception e) {
            log.error("\t--> Unexpected error while encoding Credential Schema: {}", e.getMessage());
            throw new OpenDidException(ErrorCode.CRYPTO_ENCODING_FAILED);
        }
    }

    @Override
    public CredentialSchema getCredentialSchema(String credentialSchemaId) {
        String credentialSchemaJson = repositoryFeign.getCredentialSchema(credentialSchemaId);
        return parseCredentialSchema(credentialSchemaJson);
    }

    private CredentialSchema parseCredentialSchema(String credentialSchemaJson) {
        try {
            log.debug("\t--> Parsing Credential Schema JSON");

            return GsonWrapper.getGson().fromJson(credentialSchemaJson, CredentialSchema.class);
        } catch (JsonSyntaxException e) {
            log.error("\t--> Failed to decode or parse Credential Schema: {}", e.getMessage());
            throw new OpenDidException(ErrorCode.JSON_DE_SERIALIZE_FAILED);
        } catch (Exception e) {
            log.error("\t--> Unexpected error while parsing Credential Schema: {}", e.getMessage());
            throw new OpenDidException(ErrorCode.JSON_DE_SERIALIZE_FAILED);
        }
    }

    @Override
    public CredentialDefinition getCredentialDefinition(String credentialDefinitionId) {
        String credentialDefinitionJson = repositoryFeign.getCredentialDefinition(credentialDefinitionId);
        return parseCredentialDefinition(credentialDefinitionJson);
    }

    private CredentialDefinition parseCredentialDefinition(String credentialDefinitionJson) {
        try {
            log.debug("\t--> Parsing Credential Definition");
            return GsonWrapper.getGson().fromJson(credentialDefinitionJson, CredentialDefinition.class);
        } catch (JsonSyntaxException e) {
            log.error("\t--> Failed to parse Credential Definition: {}", e.getMessage());
            throw new OpenDidException(ErrorCode.JSON_DE_SERIALIZE_FAILED);
        } catch (Exception e) {
            log.error("\t--> Unexpected error while parsing Credential Definition: {}", e.getMessage());
            throw new OpenDidException(ErrorCode.JSON_DE_SERIALIZE_FAILED);
        }
    }
}

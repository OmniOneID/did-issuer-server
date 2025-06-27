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

package org.omnione.did.issuer.v1.agent.api;


import org.omnione.did.base.constants.UrlConstant;
import org.omnione.did.data.model.vc.VcMeta;
import org.omnione.did.issuer.v1.agent.api.dto.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.cloud.openfeign.FeignClient;


/**
 * RepositoryFeign
 * This is a Feign client that communicates with the Storage API.
 */
@FeignClient(value = "Storage", url = "${lss.url:http://127.0.0.1:8098}" + UrlConstant.LSS.V1)
public interface RepositoryFeign {

    /**
     * Gets a DID document by its DID.
     *
     * @param did DID to get the document for.
     * @return Found DID document.
     */
    @GetMapping(UrlConstant.LSS.DID)
    String getDid(@RequestParam(name = "did") String did);

    /**
     * Gets metadata for a Verifiable Credential (VC) by vc-id
     *
     * @param vcId Identifier of the Verifiable Credential.
     * @return Found VC metadata.
     */
    @GetMapping(UrlConstant.LSS.VC_META)
    String getVcMetaData(@RequestParam(name = "vcId") String vcId);

    /**
     * Register a VC metadata.
     * @param vcMeta VC Metadata to register
     */
    @PostMapping(UrlConstant.LSS.VC_META)
    void inputVcMeta(@RequestBody VcMeta vcMeta);

    /**
     * Update the status of a VC.
     *
     * @param request Request containing the VC ID and the new status.
     */
    @PutMapping(UrlConstant.LSS.VC_META)
    void updateVcStatus(@RequestBody UpdateVcStatusApiReqDto request);

    /**
     * Register a ZKP Credential Schema.
     * @param credentialSchema Credential Schema to register
     */
    @PostMapping("/credential-schema")
    void registerCredentialSchema(InputZkpCredentialSchemaReqDto credentialSchema);

    /**
     * Register a ZKP Credential Definition.
     * @param credentialDefinition Credential Definition to register
     */
    @PostMapping("/credential-definition")
    void registerCredentialDefinition(InputZkpCredentialDefinitionReqDto credentialDefinition);

    /**
     * Get a Credential Schema by schema-id
     * @param schemaId the credential schema id
     * @return the encoded Credential Schema
     */
    @GetMapping("/credential-schema")
    String getCredentialSchema(@RequestParam(name = "schemaId") String schemaId);

    /**
     * Get a Credential Definition by credential-definition-id
     * @param definitionId the credential definition id
     * @return the encoded Credential Definition
     */
    @GetMapping("/credential-definition")
    String getCredentialDefinition(@RequestParam(name = "definitionId") String definitionId);

    /**
     * Register a ZKP Credential Definition.
     * @param vcSchema the VC Schema to register
     */
    @PostMapping("/vc-schema")
    void registerVcSchema(InputVcSchemaReqDto vcSchema);

    /**
     * Get a Credential Schema by schema-id
     * @param schemaId the credential schema id
     * @return the encoded Credential Schema
     */
    @GetMapping("/vc-schema")
    String getVcSchema(@RequestParam(name = "schemaId") String schemaId);
}

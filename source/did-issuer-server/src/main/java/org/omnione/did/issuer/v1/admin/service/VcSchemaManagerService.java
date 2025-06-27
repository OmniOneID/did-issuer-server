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
import org.omnione.did.base.db.domain.Namespace;
import org.omnione.did.base.db.domain.VcSchema;
import org.omnione.did.base.db.domain.VcSchemaNamespace;
import org.omnione.did.base.exception.ErrorCode;
import org.omnione.did.base.exception.OpenDidException;
import org.omnione.did.data.model.schema.MetaData;
import org.omnione.did.data.model.schema.SchemaClaims;
import org.omnione.did.data.model.schema.SchemaCredentialSubject;
import org.omnione.did.issuer.v1.admin.dto.vc.CreateVcSchemaResDto;
import org.omnione.did.issuer.v1.admin.dto.vc.GetVcSchemaResDto;
import org.omnione.did.issuer.v1.admin.dto.vc.VcSchemaDto;
import org.omnione.did.issuer.v1.admin.dto.vc.VcSchemaReqDto;
import org.omnione.did.issuer.v1.admin.service.query.IssueProfileQueryService;
import org.omnione.did.issuer.v1.admin.service.query.NamespaceQueryService;
import org.omnione.did.issuer.v1.admin.service.query.VcSchemaQueryService;
import org.omnione.did.issuer.v1.agent.helper.VcServiceHelper;
import org.omnione.did.issuer.v1.agent.service.query.IssuerInfoQueryService;
import org.omnione.did.issuer.v1.common.service.StorageService;
import org.springframework.context.annotation.Profile;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Service for managing Verifiable Credential (VC) schemas in the Admin Console.
 * <p>
 * Provides functionality for creating, updating, deleting, retrieving, and publishing VC schemas,
 * including their association with namespaces and integration with the List Community (TAS).
 */
@Transactional
@Profile("!sample")
@RequiredArgsConstructor
@Service
@Slf4j
public class VcSchemaManagerService {

    private final VcSchemaQueryService vcSchemaQueryService;
    private final NamespaceQueryService namespaceQueryService;
    private final IssueProfileQueryService issueProfileQueryService;
    private final ListCommunityService listCommunityService;
    private final StorageService storageService;
    private final IssuerInfoQueryService issuerInfoQueryService;
    private final VcServiceHelper vcServiceHelper;

    /**
     * Creates a new VC schema and registers it to the List Community.
     *
     * @param request DTO containing schema creation data
     * @return response DTO with created schema ID
     */
    public CreateVcSchemaResDto createVcSchema(VcSchemaReqDto request) {
        VcSchema vcSchema = vcSchemaQueryService.save(VcSchema.builder()
                .vcSchemaId(request.getVcSchemaId())
                .title(request.getTitle())
                .description(request.getDescription())
                .language(request.getLanguage())
                .version(request.getVersion())
                .build());

        List<VcSchemaNamespace> vcSchemaNamespaceList = request.getNamespaces().stream()
                .map(namespace -> VcSchemaNamespace.builder()
                        .vcSchemaId(vcSchema.getId())
                        .namespaceId(namespace)
                        .build())
                .collect(Collectors.toList());

        vcSchemaQueryService.saveVcSchemaNamespace(vcSchemaNamespaceList);
        listCommunityService.registerVcSchema(vcSchema.getId());
        registerVcSchemaToBlockchain(vcSchema);

        return CreateVcSchemaResDto.builder()
                .vcSchemaId(vcSchema.getId())
                .build();
    }

    public void registerVcSchemaToBlockchain(VcSchema vcSchemaInfo) {
        try {
            org.omnione.did.data.model.schema.VcSchema vcSchema = vcServiceHelper.getVcSchemaBySchemaId(vcSchemaInfo.getVcSchemaId());
            IssuerInfo issuerInfo = issuerInfoQueryService.findIssuerInfo();

            storageService.registerVcSchema(vcSchema, issuerInfo.getDid());
        } catch (OpenDidException e) {
            log.error("Failed to register VC schema to blockchain: {}", e.getMessage());
            throw e;
        } catch (Exception e) {
            log.error("Unexpected error while registering VC schema to blockchain: {}", e.getMessage());
            throw new OpenDidException(ErrorCode.BLOCKCHAIN_VC_SCHEMA_REGISTRATION_FAILED);
        }
    }

    /**
     * Retrieves a paginated list of all VC schemas.
     *
     * @param pageable pagination information
     * @return page of VC schemas
     */
    public Page<VcSchema> getVcSchemaList(Pageable pageable) {
        return vcSchemaQueryService.findAll(pageable);
    }

    /**
     * Retrieves a VC schema and its associated namespaces by ID.
     *
     * @param id the schema ID
     * @return detailed VC schema response DTO
     */
    public GetVcSchemaResDto getVcSchemaById(Long id) {
        VcSchema vcSchema = vcSchemaQueryService.findById(id);
        List<Long> relationByVcSchemaId = vcSchemaQueryService.findRelationByVcSchemaId(id);
        List<Namespace> items = namespaceQueryService.findAllById(relationByVcSchemaId);

        return GetVcSchemaResDto.builder()
                .vcSchema(vcSchema)
                .items(items)
                .build();
    }

    /**
     * Deletes a VC schema by ID if not associated with any issue profile.
     *
     * @param id the schema ID
     * @throws OpenDidException if schema is still in use
     */
    public void deleteVcSchemaById(Long id) {
        if (issueProfileQueryService.existsByVcSchemaId(id)) {
            throw new OpenDidException(ErrorCode.VC_SCHEMA_DELETE_CONFLICT);
        }
        vcSchemaQueryService.deleteById(id);
    }

    /**
     * Searches VC schemas by keyword and value with pagination.
     *
     * @param searchKey   the key to search on
     * @param searchValue the value to search for
     * @param pageable    pagination information
     * @return page of matching VC schema DTOs
     */
    public PageImpl<VcSchemaDto> searchVcSchemaList(String searchKey, String searchValue, Pageable pageable) {
        return vcSchemaQueryService.searchNamespaceList(searchKey, searchValue, pageable);
    }

    /**
     * Updates an existing VC schema and re-associates its namespace mappings.
     *
     * @param request DTO containing updated schema data
     * @return updated schema DTO
     */
    public VcSchemaDto updateVcSchema(VcSchemaReqDto request) {
        VcSchema vcSchema = vcSchemaQueryService.findById(request.getId());
        vcSchema.setTitle(request.getTitle());
        vcSchema.setDescription(request.getDescription());
        vcSchema.setLanguage(request.getLanguage());
        vcSchema.setVersion(request.getVersion());

        vcSchemaQueryService.deleteByVcSchemaId(request.getId());

        List<VcSchemaNamespace> vcSchemaNamespaceList = request.getNamespaces().stream()
                .map(namespace -> VcSchemaNamespace.builder()
                        .vcSchemaId(vcSchema.getId())
                        .namespaceId(namespace)
                        .build())
                .collect(Collectors.toList());

        vcSchemaQueryService.save(vcSchema);
        vcSchemaQueryService.saveVcSchemaNamespace(vcSchemaNamespaceList);

        return VcSchemaDto.fromEntity(vcSchema);
    }
}
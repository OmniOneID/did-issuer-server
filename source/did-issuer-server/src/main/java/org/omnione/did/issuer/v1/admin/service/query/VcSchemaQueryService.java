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

package org.omnione.did.issuer.v1.admin.service.query;

import lombok.RequiredArgsConstructor;
import org.omnione.did.base.db.domain.VcSchema;
import org.omnione.did.base.db.domain.VcSchemaNamespace;
import org.omnione.did.base.db.repository.VcSchemaNamespaceRepository;
import org.omnione.did.base.db.repository.VcSchemaRepository;
import org.omnione.did.base.exception.ErrorCode;
import org.omnione.did.base.exception.OpenDidException;
import org.omnione.did.issuer.v1.admin.dto.vc.VcSchemaDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Query service for managing Verifiable Credential (VC) Schemas in the Admin Console.
 * <p>
 * Provides methods to save, retrieve, associate namespaces, and delete VC Schemas.
 * Also supports search functionality with pagination.
 */
@RequiredArgsConstructor
@Service
public class VcSchemaQueryService {

    private final VcSchemaRepository vcSchemaRepository;
    private final VcSchemaNamespaceRepository vcSchemaNamespaceRepository;

    /**
     * Saves a VC schema to the repository.
     *
     * @param vcSchema the VC schema entity to persist
     * @return the saved VC schema
     */
    public VcSchema save(VcSchema vcSchema) {
        return vcSchemaRepository.save(vcSchema);
    }

    /**
     * Retrieves all VC schemas with pagination.
     *
     * @param pageable pagination information
     * @return a page of VC schema entities
     */
    public Page<VcSchema> findAll(Pageable pageable) {
        return vcSchemaRepository.findAll(pageable);
    }

    /**
     * Saves a list of VC schema-namespace associations.
     *
     * @param vcSchemaNamespace list of namespace associations
     * @return the saved associations
     */
    public List<VcSchemaNamespace> saveVcSchemaNamespace(List<VcSchemaNamespace> vcSchemaNamespace) {
        return vcSchemaNamespaceRepository.saveAll(vcSchemaNamespace);
    }

    /**
     * Retrieves a VC schema by its ID.
     *
     * @param id the schema ID
     * @return the VC schema
     * @throws OpenDidException if not found
     */
    public VcSchema findById(Long id) {
        return vcSchemaRepository.findById(id)
                .orElseThrow(() -> new OpenDidException(ErrorCode.VC_SCHEMA_NOT_FOUND));
    }

    /**
     * Retrieves all namespace IDs associated with the specified VC schema ID.
     *
     * @param vcSchemaId the schema ID
     * @return list of namespace IDs
     */
    public List<Long> findRelationByVcSchemaId(Long vcSchemaId) {
        List<VcSchemaNamespace> vcSchemaNamespaceList = vcSchemaNamespaceRepository.findAllByVcSchemaId(vcSchemaId);
        return vcSchemaNamespaceList.stream()
                .map(VcSchemaNamespace::getNamespaceId)
                .collect(Collectors.toList());
    }

    /**
     * Deletes a VC schema and its associated namespaces by schema ID.
     *
     * @param id the schema ID
     */
    public void deleteById(Long id) {
        vcSchemaNamespaceRepository.deleteAllByVcSchemaId(id);
        vcSchemaRepository.deleteById(id);
    }

    /**
     * Searches VC schemas using a key-value pair and pagination.
     *
     * @param searchKey the key to search on (e.g., title)
     * @param searchValue the value to search for
     * @param pageable pagination information
     * @return page of matching VC schema DTOs
     */
    public PageImpl<VcSchemaDto> searchNamespaceList(String searchKey, String searchValue, Pageable pageable) {
        Page<VcSchema> entityPage = vcSchemaRepository.searchVcSchema(searchKey, searchValue, pageable);
        List<VcSchemaDto> vcSchemaDtos = entityPage.getContent().stream()
                .map(VcSchemaDto::fromEntity)
                .collect(Collectors.toList());

        return new PageImpl<>(vcSchemaDtos, pageable, entityPage.getTotalElements());
    }

    /**
     * Deletes only the namespace associations for a VC schema by schema ID.
     *
     * @param id the schema ID
     */
    public void deleteByVcSchemaId(Long id) {
        vcSchemaNamespaceRepository.deleteAllByVcSchemaId(id);
    }

    /**
     * Retrieves a VC schema by its string-based identifier.
     *
     * @param name the schema identifier
     * @return the VC schema
     * @throws OpenDidException if not found
     */
    public VcSchema findByVcSchemaId(String name) {
        return vcSchemaRepository.findByVcSchemaId(name)
                .orElseThrow(() -> new OpenDidException(ErrorCode.VC_NOT_FOUND));
    }
}

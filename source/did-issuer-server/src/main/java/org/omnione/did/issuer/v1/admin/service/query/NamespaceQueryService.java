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
import org.springframework.transaction.annotation.Transactional;
import org.omnione.did.base.db.domain.Namespace;
import org.omnione.did.base.db.repository.NamespaceRepository;
import org.omnione.did.base.db.repository.VcSchemaNamespaceRepository;
import org.omnione.did.base.exception.ErrorCode;
import org.omnione.did.base.exception.OpenDidException;
import org.omnione.did.issuer.v1.admin.dto.namespace.NamespaceDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Query service for managing namespace entities in the Admin Console.
 * <p>
 * Provides methods to save, retrieve, search, and safely delete namespaces,
 * including checks for VC schema associations.
 */
@RequiredArgsConstructor
@Service
public class NamespaceQueryService {

    private final NamespaceRepository namespaceRepository;
    private final VcSchemaNamespaceRepository vcSchemaNamespaceRepository;

    /**
     * Saves a namespace to the database.
     *
     * @param namespace the namespace entity to save
     * @return the saved Namespace
     */
    public Namespace save(Namespace namespace) {
        return namespaceRepository.save(namespace);
    }

    /**
     * Retrieves all namespaces with pagination support.
     *
     * @param pageable pagination information
     * @return a page of Namespace entities
     */
    @Transactional(readOnly = true)
    public Page<Namespace> findAll(Pageable pageable) {
        return namespaceRepository.findAll(pageable);
    }

    /**
     * Retrieves multiple namespaces by their IDs.
     *
     * @param id list of namespace IDs
     * @return list of matching Namespace entities
     */
    public List<Namespace> findAllById(List<Long> id) {
        return namespaceRepository.findAllById(id);
    }

    /**
     * Deletes a namespace by its ID, with a check to ensure it is not referenced by any VC schema.
     *
     * @param id the ID of the namespace to delete
     * @throws OpenDidException if the namespace is associated with a VC schema
     */
    public void deleteById(Long id) {
        if (existsByNamespaceId(id)) {
            throw new OpenDidException(ErrorCode.NAMESPACE_DELETE_CONFLICT);
        }
        namespaceRepository.deleteById(id);
    }

    /**
     * Retrieves a namespace by its ID.
     *
     * @param id the ID of the namespace
     * @return the matching Namespace entity
     * @throws OpenDidException if not found
     */
    @Transactional(readOnly = true)
    public Namespace findById(Long id) {
        return namespaceRepository.findById(id)
                .orElseThrow(() -> new OpenDidException(ErrorCode.NAMESPACE_NOT_FOUND));
    }

    /**
     * Searches namespaces by keyword and value with pagination.
     *
     * @param searchKey the field to filter by
     * @param searchValue the value to match
     * @param pageable pagination information
     * @return page of NamespaceDto
     */
    @Transactional(readOnly = true)
    public PageImpl<NamespaceDto> searchNamespaceList(String searchKey, String searchValue, Pageable pageable) {
        Page<Namespace> entityPage = namespaceRepository.searchNamespaces(searchKey, searchValue, pageable);

        List<NamespaceDto> namespaceDtos = entityPage.getContent().stream()
                .map(NamespaceDto::fromEntity)
                .collect(Collectors.toList());

        return new PageImpl<>(namespaceDtos, pageable, entityPage.getTotalElements());
    }

    public boolean existsByNamespaceId(Long id) {
        return vcSchemaNamespaceRepository.existsByNamespaceId(id);
    }

    /**
     * Counts the number of VC schemas associated with a specific namespace.
     *
     * @param namespaceId the ID of the namespace
     * @return the count of VC schemas using this namespace
     */
    @Transactional(readOnly = true)
    public int countVcSchemasByNamespaceId(Long namespaceId) {
        return (int) vcSchemaNamespaceRepository.countByNamespaceId(namespaceId);
    }
}

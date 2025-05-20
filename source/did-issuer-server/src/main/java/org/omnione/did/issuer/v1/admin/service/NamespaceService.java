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
import org.omnione.did.base.db.domain.Namespace;
import org.omnione.did.data.model.schema.SchemaClaims;
import org.omnione.did.issuer.v1.admin.dto.namespace.CreateNamespaceResDto;
import org.omnione.did.issuer.v1.admin.dto.namespace.NamespaceDto;
import org.omnione.did.issuer.v1.admin.dto.namespace.UpdateNamespaceReqDto;
import org.omnione.did.issuer.v1.admin.service.query.NamespaceQueryService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

/**
 * Service for managing namespace definitions in the Admin Console.
 * <p>
 * This service provides operations for creating, updating, deleting, retrieving,
 * and searching namespace metadata, including schema claim associations.
 */
@Transactional
@RequiredArgsConstructor
@Service
public class NamespaceService {

    private final NamespaceQueryService namespaceQueryService;

    /**
     * Creates a new namespace using provided schema claims.
     *
     * @param request the schema claims data
     * @return response DTO indicating creation result
     */
    public CreateNamespaceResDto createNamespaceReqDto(SchemaClaims request) {
        Namespace namespace = namespaceQueryService.save(Namespace.builder()
                .namespaceId(request.getNamespace().getId())
                .name(request.getNamespace().getName())
                .ref(request.getNamespace().getRef())
                .schemaClaims(request)
                .build());

        return CreateNamespaceResDto.builder().build();
    }

    /**
     * Retrieves a paginated list of namespaces with schemaClaims set to null for response trimming.
     *
     * @param pageable pagination information
     * @return page of namespaces
     */
    public Page<Namespace> getNamespacesByPageable(Pageable pageable) {
        Page<Namespace> namespaceList = namespaceQueryService.findAll(pageable);
        for (Namespace namespace : namespaceList.getContent()) {
            namespace.setSchemaClaims(null);
        }
        return namespaceQueryService.findAll(pageable);
    }

    /**
     * Updates an existing namespace with new schema claims data.
     *
     * @param request the update request DTO containing schema claims
     * @return the updated namespace DTO
     */
    public NamespaceDto updateNamespace(UpdateNamespaceReqDto request) {
        Namespace namespace = namespaceQueryService.findById(request.getId());
        namespace.setName(request.getSchemaClaims().getNamespace().getName());
        namespace.setRef(request.getSchemaClaims().getNamespace().getRef());
        namespace.setSchemaClaims(request.getSchemaClaims());

        namespaceQueryService.save(namespace);
        return NamespaceDto.fromEntity(namespace);
    }

    /**
     * Deletes a namespace by its ID.
     *
     * @param id the ID of the namespace to delete
     */
    public void deleteNamespaceById(Long id) {
        namespaceQueryService.deleteById(id);
    }

    /**
     * Retrieves a namespace by its ID.
     *
     * @param id the ID of the namespace
     * @return the namespace DTO
     */
    public NamespaceDto getNamespaceById(Long id) {
        return NamespaceDto.fromEntity(namespaceQueryService.findById(id));
    }

    /**
     * Searches namespaces by key and value with pagination support.
     *
     * @param searchKey   the field to filter on
     * @param searchValue the value to match
     * @param pageable    pagination information
     * @return page of matching namespace DTOs
     */
    public PageImpl<NamespaceDto> searchNamespaceList(String searchKey, String searchValue, Pageable pageable) {
        return namespaceQueryService.searchNamespaceList(searchKey, searchValue, pageable);
    }
}

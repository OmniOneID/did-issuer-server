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

import lombok.RequiredArgsConstructor;
import org.springframework.transaction.annotation.Transactional;
import org.omnione.did.base.db.domain.Namespace;
import org.omnione.did.base.exception.ErrorCode;
import org.omnione.did.base.exception.OpenDidException;
import org.omnione.did.data.model.schema.SchemaClaims;
import org.omnione.did.issuer.v1.admin.dto.namespace.CreateNamespaceResDto;
import org.omnione.did.issuer.v1.admin.dto.namespace.NamespaceDto;
import org.omnione.did.issuer.v1.admin.dto.namespace.UpdateNamespaceReqDto;
import org.omnione.did.issuer.v1.admin.service.query.NamespaceQueryService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Service for managing namespace definitions in the Admin Console.
 * <p>
 * This service provides operations for creating, updating, deleting, retrieving,
 * and searching namespace metadata, including schema claim associations.
 */
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
    @Transactional
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
    @Transactional(readOnly = true)
    public Page<Namespace> getNamespacesByPageable(Pageable pageable) {
        return namespaceQueryService.findAll(pageable);
    }

    /**
     * Searches namespaces by key and value with pagination support.
     *
     * @param searchKey   the field to filter on
     * @param searchValue the value to match
     * @param pageable    pagination information
     * @return page of matching namespace DTOs with VC schema counts
     */
    @Transactional(readOnly = true)
    public PageImpl<NamespaceDto> searchNamespaceList(String searchKey, String searchValue, Pageable pageable) {
        PageImpl<NamespaceDto> result = namespaceQueryService.searchNamespaceList(searchKey, searchValue, pageable);
        
        // Add VC schema counts to each namespace
        List<NamespaceDto> namespaceDtosWithCount = result.getContent().stream()
                .map(dto -> {
                    int vcSchemaCount = namespaceQueryService.countVcSchemasByNamespaceId(dto.getId());
                    return NamespaceDto.builder()
                            .id(dto.getId())
                            .namespaceId(dto.getNamespaceId())
                            .name(dto.getName())
                            .ref(dto.getRef())
                            .schemaClaims(dto.getSchemaClaims())
                            .createdAt(dto.getCreatedAt())
                            .updatedAt(dto.getUpdatedAt())
                            .vcSchemaCount(vcSchemaCount)
                            .build();
                })
                .collect(Collectors.toList());

        return new PageImpl<>(namespaceDtosWithCount, pageable, result.getTotalElements());
    }

    /**
     * Updates an existing namespace with new schema claims data.
     * Checks if the namespace is in use by VC schemas before allowing update.
     *
     * @param request the update request DTO containing schema claims
     * @return the updated namespace DTO
     */
    @Transactional
    public NamespaceDto updateNamespace(UpdateNamespaceReqDto request) {
        // Check if namespace is in use by VC schemas
        int vcSchemaCount = namespaceQueryService.countVcSchemasByNamespaceId(request.getId());
        if (vcSchemaCount > 0) {
            throw new OpenDidException(ErrorCode.NAMESPACE_UPDATE_CONFLICT);
        }

        Namespace namespace = namespaceQueryService.findById(request.getId());
        namespace.setName(request.getSchemaClaims().getNamespace().getName());
        namespace.setRef(request.getSchemaClaims().getNamespace().getRef());
        namespace.setSchemaClaims(request.getSchemaClaims());

        namespaceQueryService.save(namespace);
        return NamespaceDto.fromEntityWithCount(namespace, vcSchemaCount);
    }

    /**
     * Deletes a namespace by its ID.
     * Checks if the namespace is in use by VC schemas before allowing deletion.
     *
     * @param id the ID of the namespace to delete
     */
    @Transactional
    public void deleteNamespaceById(Long id) {
        // Check if namespace is in use by VC schemas
        int vcSchemaCount = namespaceQueryService.countVcSchemasByNamespaceId(id);
        if (vcSchemaCount > 0) {
            throw new OpenDidException(ErrorCode.NAMESPACE_DELETE_CONFLICT);
        }
        
        namespaceQueryService.deleteById(id);
    }

    /**
     * Retrieves a namespace by its ID.
     *
     * @param id the ID of the namespace
     * @return the namespace DTO with VC schema count
     */
    @Transactional(readOnly = true)
    public NamespaceDto getNamespaceById(Long id) {
        Namespace namespace = namespaceQueryService.findById(id);
        int vcSchemaCount = namespaceQueryService.countVcSchemasByNamespaceId(id);
        return NamespaceDto.fromEntityWithCount(namespace, vcSchemaCount);
    }
}

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
import org.omnione.did.issuer.v1.admin.dto.*;
import org.omnione.did.issuer.v1.admin.service.query.NamespaceQueryService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

/**
 * Description...
 *
 */
@Transactional
@RequiredArgsConstructor
@Service
public class NamespaceService {

    private final NamespaceQueryService namespaceQueryService;
    public CreateNamespaceResDto createNamespaceReqDto(SchemaClaims request) {
        Namespace namespace = namespaceQueryService.save(Namespace.builder()
                .namespaceId(request.getNamespace().getId())
                .name(request.getNamespace().getName())
                .ref(request.getNamespace().getRef())
                .schemaClaims(request)
                .build());

        return CreateNamespaceResDto.builder()
                .build();
    }

    public Page<Namespace> getNamespacesByPageable(Pageable pageable) {
        Page<Namespace> namespaceList = namespaceQueryService.findAll(pageable);
        for (Namespace namespace : namespaceList.getContent()) {
            namespace.setSchemaClaims(null);
        }
        return namespaceQueryService.findAll(pageable);
    }


    public NamespaceDto updateNamespace(UpdateNamespaceReqDto request) {
        Namespace namespace = namespaceQueryService.findById(request.getId());
//        namespace.setNamespaceId(request.getSchemaClaims().getNamespace().getId());
        namespace.setName(request.getSchemaClaims().getNamespace().getName());
        namespace.setRef(request.getSchemaClaims().getNamespace().getRef());

        namespace.setSchemaClaims(request.getSchemaClaims());
        namespaceQueryService.save(namespace);

        return NamespaceDto.fromEntity(namespace);
    }

    public void deleteNamespaceById(Long id) {

        namespaceQueryService.deleteById(id);
    }

    public NamespaceDto getNamespaceById(Long id) {
        return NamespaceDto.fromEntity(namespaceQueryService.findById(id));
    }

    public Page<NamespaceDto> searchNamespaceList(String searchKey, String searchValue, Pageable pageable) {
        return namespaceQueryService.searchNamespaceList(searchKey, searchValue, pageable);
    }
}

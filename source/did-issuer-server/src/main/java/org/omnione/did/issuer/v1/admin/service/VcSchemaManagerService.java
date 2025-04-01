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
import org.omnione.did.base.db.domain.VcSchema;
import org.omnione.did.base.db.domain.VcSchemaNamespace;
import org.omnione.did.issuer.v1.admin.dto.*;
import org.omnione.did.issuer.v1.admin.service.query.NamespaceQueryService;
import org.omnione.did.issuer.v1.admin.service.query.VcSchemaQueryService;
import org.omnione.did.issuer.v1.agent.service.query.IssuerInfoQueryService;
import org.springframework.context.annotation.Profile;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Description...
 */
@Transactional
@Profile("!sample")
@RequiredArgsConstructor
@Service
public class VcSchemaManagerService {

    private final VcSchemaQueryService vcSchemaQueryService;

    private final NamespaceQueryService namespaceQueryService;
    private final ListCommunityService listCommunityService;
    public CreateVcSchemaResDto createVcSchema(VcSchemaReqDto request) {

        VcSchema vcSchema = vcSchemaQueryService.save(VcSchema.builder()
                .vcSchemaId(request.getVcSchemaId())
                .title(request.getTitle())
                .description(request.getDescription())
                .language(request.getLanguage())
                .version(request.getVersion())
                .build());

        // TODO: check Namespace exists
        List<VcSchemaNamespace> vcSchemaNamespaceList = request.getNamespaces().stream()
                .map(namespace -> VcSchemaNamespace.builder()
                        .vcSchemaId(vcSchema.getId())
                        .namespaceId(namespace)
                        .build())
                .collect(Collectors.toList());

        vcSchemaQueryService.saveVcSchemaNamespace(vcSchemaNamespaceList);

        listCommunityService.registerVcSchema(vcSchema.getId());

        return CreateVcSchemaResDto.builder()
                .vcSchemaId(vcSchema.getId())
                .build();
    }

    public Page<VcSchema> getVcSchemaList(Pageable pageable) {

        return vcSchemaQueryService.findAll(pageable);
    }

    public GetVcSchemaResDto getVcSchemaById(Long id) {
        VcSchema vcSchema = vcSchemaQueryService.findById(id);
        List<Long> relationByVcSchemaId = vcSchemaQueryService.findRelationByVcSchemaId(id);
        List<Namespace> items = namespaceQueryService.findAllById(relationByVcSchemaId);

        return GetVcSchemaResDto.builder()
                .vcSchema(vcSchema)
                .items(items)
                .build();
    }

    public void deleteVcSchemaById(Long id) {
        vcSchemaQueryService.deleteById(id);
    }

    public Page<VcSchemaDto> searchVcSchemaList(String searchKey, String searchValue, Pageable pageable) {
        return vcSchemaQueryService.searchNamespaceList(searchKey, searchValue, pageable);
    }

    public VcSchemaDto updateVcSchema(VcSchemaReqDto request) {
        VcSchema vcSchema = vcSchemaQueryService.findById(request.getId());
        vcSchema.setTitle(request.getTitle());
        vcSchema.setDescription(request.getDescription());
        vcSchema.setLanguage(request.getLanguage());
        vcSchema.setVersion(request.getVersion());

        vcSchemaQueryService.deleteByVcSchemaId(request.getId());

        // TODO: check Namespace exists
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

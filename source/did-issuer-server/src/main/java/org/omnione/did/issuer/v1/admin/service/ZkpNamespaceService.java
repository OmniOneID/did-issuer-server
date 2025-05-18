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

import com.google.protobuf.Empty;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.omnione.did.base.db.domain.ZkpAttribute;
import org.omnione.did.base.db.domain.ZkpNamespace;
import org.omnione.did.issuer.v1.admin.api.dto.EmptyResDto;
import org.omnione.did.issuer.v1.admin.dto.zkp.namespace.CreateZkpNamespaceReqDto;
import org.omnione.did.issuer.v1.admin.dto.zkp.namespace.ZkpNamespaceDto;
import org.omnione.did.issuer.v1.admin.service.query.ZkpNamespaceQueryService;
import org.omnione.did.zkp.datamodel.schema.AttributeDef;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Transactional
@RequiredArgsConstructor
@Service
public class ZkpNamespaceService {
    private final ZkpNamespaceQueryService zkpNamespaceQueryService;

    public Page<ZkpNamespaceDto> searchZkpNamespaceList(String searchKey, String searchValue, Pageable pageable) {
       return zkpNamespaceQueryService.searchZkpNamespaceList(searchKey, searchValue, pageable);
    }

    public EmptyResDto createZkpNamespaceReqDto(CreateZkpNamespaceReqDto request) {

        // Save ZKP Namespace
        log.debug("Saving ZKP Namespace: {}", request.getNamespace());
        ZkpNamespace zkpNamespace = ZkpNamespace.builder()
                .namespaceId(request.getNamespace().getNamespaceId())
                .name(request.getNamespace().getName())
                .ref(request.getNamespace().getRef())
                .build();

        // Save ZKP Attributes
        log.debug("Saving ZKP Attributes: {}", request.getAttributes());
        ZkpNamespace saveZkpNamespace = zkpNamespaceQueryService.save(zkpNamespace);

        List<ZkpAttribute> attributes = request.getAttributes().stream()
                .map(attr -> ZkpAttribute.builder()
                        .label(attr.getLabel())
                        .type(AttributeDef.ATTR_TYPE.valueOf(attr.getType()))
                        .caption(attr.getCaption())
                        .zkpNamespaceId(saveZkpNamespace.getId())
                        .build())
                .toList();

        zkpNamespaceQueryService.saveAllAttributes(attributes);

        return new EmptyResDto();
    }
}

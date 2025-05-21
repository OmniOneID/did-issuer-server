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
import org.omnione.did.base.db.domain.ZkpAttribute;
import org.omnione.did.base.db.domain.ZkpNamespace;
import org.omnione.did.base.db.domain.ZkpSchemaAttribute;
import org.omnione.did.base.db.repository.ZkpAttributeRepository;
import org.omnione.did.base.db.repository.ZkpNamespaceRepository;
import org.omnione.did.base.db.repository.ZkpSchemaAttributeRepository;
import org.omnione.did.base.db.repository.projection.NamespaceIdProjection;
import org.omnione.did.base.exception.ErrorCode;
import org.omnione.did.base.exception.OpenDidException;
import org.omnione.did.issuer.v1.admin.dto.zkp.namespace.ZkpNamespaceDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Service
public class ZkpNamespaceQueryService {
    private final ZkpNamespaceRepository zkpNamespaceRepository;
    private final ZkpAttributeRepository zkpAttributeRepository;
    private final ZkpSchemaAttributeRepository zkpSchemaAttributeRepository;

    public ZkpNamespace save(ZkpNamespace zkpNamespace) {
        return zkpNamespaceRepository.save(zkpNamespace);
    }

    public PageImpl<ZkpNamespaceDto> searchZkpNamespaceList(String searchKey, String searchValue, Pageable pageable) {
        Page<ZkpNamespace> entityPage = zkpNamespaceRepository.searchNamespaces(searchKey, searchValue, pageable);
        List<ZkpNamespace> namespaceList = entityPage.getContent();

        // 1. Extract list of namespace IDs
        List<String> namespaceIds = namespaceList.stream()
                .map(ZkpNamespace::getNamespaceId)
                .collect(Collectors.toList());

        // 2. Fetch counts in batch
        List<NamespaceIdProjection> countResults = zkpSchemaAttributeRepository.countByNamespaceIdIn(namespaceIds);

        // 3. Convert results to a Map
        Map<String, Long> countMap = countResults.stream()
                .collect(Collectors.toMap(NamespaceIdProjection::getNamespaceId, NamespaceIdProjection::getCount));

        // 4. Convert to DTOs
        List<ZkpNamespaceDto> zkpNamespaceDtos = namespaceList.stream()
                .map(ns -> {
                    Long count = countMap.getOrDefault(ns.getNamespaceId(), 0L);
                    return ZkpNamespaceDto.fromEntity(ns, count);
                })
                .collect(Collectors.toList());

        return new PageImpl<>(zkpNamespaceDtos, pageable, entityPage.getTotalElements());
    }

    public long getSchemaCountByNamespaceId(String namespaceId) {
        return zkpSchemaAttributeRepository.countByNamespaceId(namespaceId);
    }

    public void saveAllAttributes(List<ZkpAttribute> zkpAttributes) {
        zkpAttributeRepository.saveAll(zkpAttributes);
    }

    public ZkpNamespace findNamespaceById(Long id) {
        return zkpNamespaceRepository.findById(id)
                .orElseThrow(() -> new OpenDidException(ErrorCode.ZKP_NAMESPACE_NOT_FOUND));
    }

    public List<ZkpAttribute> findAttributesByNamespaceId(Long zkpNamespaceId) {
        return zkpAttributeRepository.findByZkpNamespaceId(zkpNamespaceId);
    }

    public void deleteAttributesByZkpNamespaceId(Long zkpNamespaceId) {
        zkpAttributeRepository.deleteByZkpNamespaceId(zkpNamespaceId);
    }

    public void deleteZkpNamespaceById(Long id) {
        zkpNamespaceRepository.deleteById(id);
        zkpAttributeRepository.deleteByZkpNamespaceId(id);
    }

    public long countByNamespaceId(String namespaceId) {
        return zkpNamespaceRepository.countByNamespaceId(namespaceId);
    }

    public List<ZkpNamespace> findAllNamespaces() {
        return zkpNamespaceRepository.findAll();
    }

    public ZkpAttribute findAttributeById(Long id) {
        return zkpAttributeRepository.findById(id)
                .orElseThrow(() -> new OpenDidException(ErrorCode.ZKP_ATTRIBUTE_NOT_FOUND));
    }

    public List<ZkpSchemaAttribute> findZkpSchemaAttributesBySchemaId(Long id) {
        return zkpSchemaAttributeRepository.findByZkpSchemaId(id);
    }

    public ZkpNamespace findNamespaceByNamespaceId(String namespaceId) {
        return zkpNamespaceRepository.findByNamespaceId(namespaceId)
                .orElseThrow(() -> new OpenDidException(ErrorCode.ZKP_NAMESPACE_NOT_FOUND));
    }
}

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
import org.omnione.did.base.db.constant.ZkpSchemaStatus;
import org.omnione.did.base.db.domain.ZkpSchema;
import org.omnione.did.base.db.domain.ZkpSchemaAttribute;
import org.omnione.did.base.db.repository.ZkpCredentialDefinitionRepository;
import org.omnione.did.base.db.repository.ZkpSchemaAttributeRepository;
import org.omnione.did.base.db.repository.ZkpSchemaRepository;
import org.omnione.did.base.db.repository.projection.SchemaIdProjection;
import org.omnione.did.base.exception.ErrorCode;
import org.omnione.did.base.exception.OpenDidException;
import org.omnione.did.issuer.v1.admin.dto.zkp.schema.ZkpSchemaDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Service
public class ZkpSchemaQueryService {
    private final ZkpSchemaRepository zkpSchemaRepository;
    private final ZkpSchemaAttributeRepository zkpSchemaAttributeRepository;
    private final ZkpCredentialDefinitionRepository zkpCredentialDefinitionRepository;

    public PageImpl<ZkpSchemaDto> searchZkpSchemaList(String searchKey, String searchValue, Pageable pageable) {
        Page<ZkpSchema> entityPage = zkpSchemaRepository.searchSchemas(searchKey, searchValue, pageable);
        List<ZkpSchema> schemaList = entityPage.getContent();

        // 1. Extract list of schema IDs
        List<String> schemaIds = schemaList.stream()
                .map(ZkpSchema::getSchemaId)
                .toList();

        // 2. Fetch counts in batch
        List<SchemaIdProjection> countResults = zkpCredentialDefinitionRepository.countBySchemaIdIn(schemaIds);

        // 3. Convert results to a Map
        Map<String, Long> countMap = countResults.stream()
                .collect(Collectors.toMap(SchemaIdProjection::getSchemaId, SchemaIdProjection::getCount));

        // 4. Convert to DTOs
        List<ZkpSchemaDto> zkpSchemaDtos = schemaList.stream()
                .map(schema -> {
                    long count = countMap.getOrDefault(schema.getSchemaId(), 0L);
                    return ZkpSchemaDto.fromEntity(schema, count);
                })
                .toList();

        return new PageImpl<>(zkpSchemaDtos, pageable, entityPage.getTotalElements());
    }

    public ZkpSchema saveZkpSchema(ZkpSchema zkpSchema) {
        return zkpSchemaRepository.save(zkpSchema);
    }

    public void saveAllSchemaAttributes(List<ZkpSchemaAttribute> attributes) {
        zkpSchemaAttributeRepository.saveAll(attributes);
    }

    public void updateZkpSchemaStatusById(@Param("id") Long id, @Param("status") ZkpSchemaStatus status) {
        zkpSchemaRepository.updateStatusById(id, status);
    }

    public boolean existsBySchemaId(String schemaId) {
        return zkpSchemaRepository.existsBySchemaId(schemaId);
    }

    public ZkpSchema findById(Long id) {
        return zkpSchemaRepository.findById(id)
                .orElseThrow(() -> new OpenDidException(ErrorCode.ZKP_SCHEMA_NOT_FOUND));
    }

    public ZkpSchema findBySchemaId(String schemaId) {
        return zkpSchemaRepository.findBySchemaId(schemaId)
                .orElseThrow(() -> new OpenDidException(ErrorCode.ZKP_SCHEMA_NOT_FOUND));
    }


    public List<ZkpSchema> findAllSchemas() {
        return zkpSchemaRepository.findAll();
    }

    public List<ZkpSchema> findByStatusNot(ZkpSchemaStatus status) {
        return zkpSchemaRepository.findAllByStatusNot(status);
    }
}

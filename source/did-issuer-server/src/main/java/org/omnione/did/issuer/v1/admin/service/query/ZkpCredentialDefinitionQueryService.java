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
import org.omnione.did.base.db.constant.ZkpCredentialDefinitionStatus;
import org.omnione.did.base.db.domain.ZkpCredentialDefinition;
import org.omnione.did.base.db.repository.ZkpCredentialDefinitionRepository;
import org.omnione.did.base.db.repository.ZkpSchemaRepository;
import org.omnione.did.base.db.repository.projection.SchemaNameProjection;
import org.omnione.did.base.exception.ErrorCode;
import org.omnione.did.base.exception.OpenDidException;
import org.omnione.did.issuer.v1.admin.dto.zkp.definition.ZkpCredentialDefinitionDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Service
public class ZkpCredentialDefinitionQueryService {
    private final ZkpCredentialDefinitionRepository zkpCredentialDefinitionRepository;
    private final ZkpSchemaRepository zkpSchemaRepository;

    public PageImpl<ZkpCredentialDefinitionDto> searchZkpCredentialDefinitionList(String searchKey, String searchValue, Pageable pageable) {
        Page<ZkpCredentialDefinition> entityPage = zkpCredentialDefinitionRepository.searchCredentialDefinitions(searchKey, searchValue, pageable);
        List<ZkpCredentialDefinition> definitionList = entityPage.getContent();

        // 1. Extract list of schema IDs
        List<String> schemaIds = definitionList.stream()
                .map(ZkpCredentialDefinition::getSchemaId)
                .toList();

        // 2. Fetch counts in batch
        List<SchemaNameProjection> schemaNameResults = zkpSchemaRepository.findNamesBySchemaIds(schemaIds);

        // 3. Convert results to a Map
        Map<String, String> schemaNameMap = schemaNameResults.stream()
                .collect(Collectors.toMap(SchemaNameProjection::getSchemaId, SchemaNameProjection::getName));

        // 4. Convert to DTOs
        List<ZkpCredentialDefinitionDto> zkpCredentialDefinitionDtos = definitionList.stream()
                .map(zkpCredentialDefinition -> {
                    String schemaName = schemaNameMap.get(zkpCredentialDefinition.getSchemaId());
                    return ZkpCredentialDefinitionDto.fromEntity(zkpCredentialDefinition, schemaName);
                })
                .toList();

        return new PageImpl<>(zkpCredentialDefinitionDtos, pageable, entityPage.getTotalElements());
    }

    public long countByAlias(String alias) {
        return zkpCredentialDefinitionRepository.countByAlias(alias);
    }

    public boolean existByAlias(String alias) {
        return zkpCredentialDefinitionRepository.existsByAlias(alias);
    }

    public ZkpCredentialDefinition saveCredentialDefinition(ZkpCredentialDefinition zkpCredentialDefinition) {
        return zkpCredentialDefinitionRepository.save(zkpCredentialDefinition);
    }

    public ZkpCredentialDefinition findById(Long id) {
        return zkpCredentialDefinitionRepository.findById(id)
                .orElseThrow(() -> new OpenDidException(ErrorCode.ZKP_CREDENTIAL_DEFINITION_NOT_FOUND));
    }

    public ZkpCredentialDefinition findByDefinitionId(String definitionId) {
        return zkpCredentialDefinitionRepository.findByDefinitionId(definitionId)
                .orElseThrow(() -> new OpenDidException(ErrorCode.ZKP_CREDENTIAL_DEFINITION_NOT_FOUND));
    }

    public void updateZkpCredentialStatusById(Long id, ZkpCredentialDefinitionStatus status) {
        zkpCredentialDefinitionRepository.updateStatusById(id, status);
    }

    public List<ZkpCredentialDefinition> findByStatus(ZkpCredentialDefinitionStatus status) {
        return zkpCredentialDefinitionRepository.findAllByStatusNot(status);
    }
}

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
package org.omnione.did.base.db.repository;

import jakarta.transaction.Transactional;
import org.omnione.did.base.db.constant.ZkpCredentialDefinitionStatus;
import org.omnione.did.base.db.domain.ZkpCredentialDefinition;
import org.omnione.did.base.db.repository.projection.SchemaIdProjection;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface ZkpCredentialDefinitionRepository extends JpaRepository<ZkpCredentialDefinition, Long>, QuerydslPredicateExecutor<ZkpCredentialDefinition>, ZkpCredentialDefinitionRepositoryAdmin {

    @Query("SELECT cd.schemaId AS schemaId, COUNT(cd) AS count " +
            "FROM ZkpCredentialDefinition cd " +
            "WHERE cd.schemaId IN :schemaIds " +
            "GROUP BY cd.schemaId")
    List<SchemaIdProjection> countBySchemaIdIn(@Param("schemaIds") List<String> schemaIds);

    long countByAlias(String alias);

    boolean existsByAlias(String alias);

    @Modifying
    @Transactional
    @Query("UPDATE ZkpCredentialDefinition s SET s.status = :status WHERE s.id = :id")
    void updateStatusById(@Param("id") Long id, @Param("status") ZkpCredentialDefinitionStatus status);

    Optional<ZkpCredentialDefinition> findByDefinitionId(String definitionId);

    List<ZkpCredentialDefinition> findAllByStatusNot(ZkpCredentialDefinitionStatus status);

    Boolean existsBySchemaId(String schemaId);
}

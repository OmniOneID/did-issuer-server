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
import org.omnione.did.base.db.constant.ZkpSchemaStatus;
import org.omnione.did.base.db.domain.ZkpNamespace;
import org.omnione.did.base.db.domain.ZkpSchema;
import org.omnione.did.base.db.repository.projection.SchemaNameProjection;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface ZkpSchemaRepository extends JpaRepository<ZkpSchema, Long>, QuerydslPredicateExecutor<ZkpSchema>, ZkpSchemaRepositoryAdmin  {
    @Modifying
    @Transactional
    @Query("UPDATE ZkpSchema s SET s.status = :status WHERE s.id = :id")
    void updateStatusById(@Param("id") Long id, @Param("status") ZkpSchemaStatus status);

    boolean existsBySchemaId(String schemaId);

    @Query("SELECT s.schemaId AS schemaId, s.name AS name " +
            "FROM ZkpSchema s " +
            "WHERE s.schemaId IN :schemaIds")
    List<SchemaNameProjection> findNamesBySchemaIds(@Param("schemaIds") List<String> schemaIds);

    Optional<ZkpSchema> findBySchemaId(String schemaId);

    List<ZkpSchema> findAllByStatusNot(ZkpSchemaStatus status);
}

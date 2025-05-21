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

import org.omnione.did.base.db.domain.ZkpSchemaAttribute;
import org.omnione.did.base.db.repository.projection.NamespaceIdProjection;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ZkpSchemaAttributeRepository extends JpaRepository<ZkpSchemaAttribute, Long> {
    long countByNamespaceId(String namespaceId);

    @Query("SELECT a.namespaceId AS namespaceId, COUNT(a) AS count " +
            "FROM ZkpSchemaAttribute a " +
            "WHERE a.namespaceId IN :namespaceIds " +
            "GROUP BY a.namespaceId")
    List<NamespaceIdProjection> countByNamespaceIdIn(@Param("namespaceIds") List<String> namespaceIds);

    List<ZkpSchemaAttribute> findByZkpSchemaId(Long id);
}

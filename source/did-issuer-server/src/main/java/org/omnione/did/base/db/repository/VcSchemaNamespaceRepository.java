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

import org.omnione.did.base.db.domain.VcSchemaNamespace;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

/**
 * Repository interface for VcSchemaNamespace entity operations.
 * Provides CRUD operations for VcSchemaNamespace entities and custom query methods.
 */
public interface VcSchemaNamespaceRepository extends JpaRepository<VcSchemaNamespace, Long> {
    List<VcSchemaNamespace> findAllByVcSchemaId(Long vcSchemaId);

    void deleteAllByVcSchemaId(Long vcSchemaId);

    boolean existsByNamespaceId(Long namespaceId);

    long countByNamespaceId(Long namespaceId);
}

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

import org.omnione.did.base.db.domain.VcSchema;
import org.omnione.did.base.db.domain.ZkpNamespace;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;

import java.util.Optional;

public interface ZkpNamespaceRepository extends JpaRepository<ZkpNamespace, Long>, QuerydslPredicateExecutor<ZkpNamespace>, ZkpNamespaceRepositoryAdmin {
    long countByNamespaceId(String namespaceId);
    Optional<ZkpNamespace> findByNamespaceId(String namespaceId);
}

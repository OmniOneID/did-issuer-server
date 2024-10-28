/*
 * Copyright 2024 OmniOne.
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

import org.omnione.did.base.db.domain.E2E;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

/**
 * Repository interface for E2E entity operations.
 * Provides CRUD operations for E2E entities and custom query methods.
 */
public interface E2ERepository extends JpaRepository<E2E, Long> {
    /**
     * Finds a E2E entity by its associated transaction ID.
     *
     * @param id The ID of the transaction to search for.
     * @return An Optional containing the E2E if found, or an empty Optional if not found.
     */
    Optional<E2E> findByTransactionId(Long id);
}

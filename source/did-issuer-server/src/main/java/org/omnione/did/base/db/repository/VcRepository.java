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

import org.omnione.did.base.db.domain.Vc;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

/**
 * Repository interface for Vc entity operations.
 * Provides CRUD operations for Vc entities and custom query methods.
 */
public interface VcRepository extends JpaRepository<Vc, Long> {
    /**
     * Finds a Vc entity by its associated VC ID.
     *
     * @param vcRepository The ID of the VC to search for.
     * @return An Optional containing the Vc if found, or an empty Optional if not found.
     */
    Optional<Vc> findByVcId(VcRepository vcRepository);

    /**
     * Finds a Vc entity by its associated user ID.
     *
     * @param id The ID of the user to search for.
     * @return An Optional containing the Vc if found, or an empty Optional if not found.
     */
    Optional<Vc> findByUserId(Long id);

    /**
     * Finds a Vc entity by its associated user ID and VC plan ID.
     *
     * @param id The ID of the user to search for.
     * @param vcPlanId The ID of the VC plan to search for.
     * @return An Optional containing the Vc if found, or an empty Optional if not found.
     */
    Optional<Vc> findByUserIdAndVcPlanId(Long id, String vcPlanId);

    Optional<Vc> findByTxId(String txId);
}

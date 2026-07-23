/*
 * Copyright 2026 OmniOne.
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

import jakarta.persistence.LockModeType;
import org.omnione.did.base.db.domain.Oid4vcCredentialIssuanceEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

@Repository
public interface Oid4vcCredentialIssuanceRepository extends JpaRepository<Oid4vcCredentialIssuanceEntity, Long> {
    Optional<Oid4vcCredentialIssuanceEntity> findByIssuanceId(String issuanceId);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select i from Oid4vcCredentialIssuanceEntity i where i.issuanceId = :issuanceId")
    Optional<Oid4vcCredentialIssuanceEntity> findByIssuanceIdForUpdate(String issuanceId);

    List<Oid4vcCredentialIssuanceEntity> findAllByUserIdOrderByCreatedAtDesc(String userId);
    List<Oid4vcCredentialIssuanceEntity> findAllByStatusListIdOrderByStatusListIndexAsc(Long statusListId);

    @Query("""
            select count(i) from Oid4vcCredentialIssuanceEntity i
             where i.statusList.id = :statusListId
               and i.issuanceState = org.omnione.did.base.db.domain.IssuanceState.ISSUED
               and (i.expiresAt is null or i.expiresAt > :now)
            """)
    long countActiveIssuedByStatusListId(Long statusListId, Instant now);
}

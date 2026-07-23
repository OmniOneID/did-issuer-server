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
import org.omnione.did.base.db.domain.Oid4vcStatusListEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface Oid4vcStatusListRepository extends JpaRepository<Oid4vcStatusListEntity, Long> {
    Optional<Oid4vcStatusListEntity> findByListUri(String listUri);
    List<Oid4vcStatusListEntity> findAllByFormatAndEnabledTrueOrderByIdAsc(String format);
    List<Oid4vcStatusListEntity> findAllByEnabledTrueOrderByIdAsc();

    @Query(value = "select lock_id from t_oid4vc_status_list_bootstrap_lock where lock_id = 1 for update",
            nativeQuery = true)
    Integer lockBootstrap();

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select s from Oid4vcStatusListEntity s where s.format = :format and s.enabled = true order by s.id")
    List<Oid4vcStatusListEntity> findEnabledByFormatForUpdate(String format);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select s from Oid4vcStatusListEntity s where s.id = :id")
    Optional<Oid4vcStatusListEntity> findByIdForUpdate(Long id);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("""
            select s
              from Oid4vcStatusListEntity s
             where s.format = :format
               and s.enabled = true
               and s.nextIndex < s.capacity
             order by s.id asc
            """)
    List<Oid4vcStatusListEntity> findAllocatableByFormatForUpdate(String format);
}

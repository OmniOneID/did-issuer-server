/*
 * Copyright 2026 OmniOne.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 */
package org.omnione.did.base.db.repository;

import jakarta.persistence.LockModeType;
import org.omnione.did.base.db.domain.Oid4vcWebviewIssuanceSessionEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface Oid4vcWebviewIssuanceSessionRepository
        extends JpaRepository<Oid4vcWebviewIssuanceSessionEntity, Long> {
    Optional<Oid4vcWebviewIssuanceSessionEntity> findBySessionToken(String sessionToken);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select s from Oid4vcWebviewIssuanceSessionEntity s where s.sessionToken = :sessionToken")
    Optional<Oid4vcWebviewIssuanceSessionEntity> findBySessionTokenForUpdate(
            @Param("sessionToken") String sessionToken);
}

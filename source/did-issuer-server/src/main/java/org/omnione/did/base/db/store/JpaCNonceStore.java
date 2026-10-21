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

package org.omnione.did.base.db.store;

import lombok.RequiredArgsConstructor;
import org.omnione.did.base.db.domain.Oid4vcCnonceEntity;
import org.omnione.did.base.db.repository.Oid4vcCnonceRepository;
import org.omnione.did.oid4vc.oid4vci.service.store.CNonceStore;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;

@Component
@RequiredArgsConstructor
@ConditionalOnProperty(name = "spring.datasource.url")
public class JpaCNonceStore implements CNonceStore {

    private final Oid4vcCnonceRepository repository;

    @Override
    public void save(String cNonce) {
        Oid4vcCnonceEntity entity = new Oid4vcCnonceEntity();
        entity.setCnonce(cNonce);
        entity.setExpiresAt(Instant.now().plusSeconds(10 * 60));
        
        repository.save(entity);
    }

    @Override
    public boolean contains(String cNonce) {
        Instant now = Instant.now();
        return repository.findByCnonce(cNonce)
                .filter(entity -> entity.getConsumedAt() == null)
                .filter(entity -> entity.getExpiresAt() == null || entity.getExpiresAt().isAfter(now))
                .isPresent();
    }

    @Override
    public void remove(String cNonce) {
        consume(cNonce);
    }

    @Override
    @Transactional
    public boolean consume(String cNonce) {
        Instant now = Instant.now();
        return repository.findByCnonceForUpdate(cNonce)
                .filter(entity -> entity.getConsumedAt() == null)
                .filter(entity -> entity.getExpiresAt() == null || entity.getExpiresAt().isAfter(now))
                .map(entity -> {
                    entity.setConsumedAt(now);
                    return true;
                })
                .orElse(false);
    }
}

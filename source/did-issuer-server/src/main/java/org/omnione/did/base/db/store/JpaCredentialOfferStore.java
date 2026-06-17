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
import org.omnione.did.base.db.domain.Oid4vcOfferEntity;
import org.omnione.did.base.db.repository.Oid4vcOfferRepository;
import org.omnione.did.oid4vc.oid4vci.api.dto.PreAuthorizeResponse;
import org.omnione.did.oid4vc.oid4vci.service.store.CredentialOfferStore;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
@RequiredArgsConstructor
@ConditionalOnProperty(name = "spring.datasource.url")
public class JpaCredentialOfferStore implements CredentialOfferStore {

    private final Oid4vcOfferRepository repository;

    @Override
    public void save(String requestId, PreAuthorizeResponse response) {
        Oid4vcOfferEntity entity = new Oid4vcOfferEntity();
        entity.setOfferId(requestId);
        entity.setPreAuthCode(response.getPreAuthorizedCode());
        entity.setUserPin(response.getUserPin());
        entity.setStatus("CREATED");
        // expiresIn is seconds.
        if (response.getExpiresIn() > 0) {
            entity.setExpiresAt(LocalDateTime.now().plusSeconds(response.getExpiresIn()));
        }

        repository.save(entity);
    }

    @Override
    public PreAuthorizeResponse consume(String requestId) {
        return repository.findByOfferId(requestId)
                .map(entity -> {
                    PreAuthorizeResponse response = new PreAuthorizeResponse();
                    response.setPreAuthorizedCode(entity.getPreAuthCode());
                    response.setUserPin(entity.getUserPin());
                    if (entity.getExpiresAt() != null) {
                        long diff = java.time.Duration.between(LocalDateTime.now(), entity.getExpiresAt()).getSeconds();
                        response.setExpiresIn(diff > 0 ? (int) diff : 0);
                    }

                    // Delete to match InMemory behavior (consume = remove)
                    repository.delete(entity);
                    return response;
                })
                .orElse(null);
    }
}

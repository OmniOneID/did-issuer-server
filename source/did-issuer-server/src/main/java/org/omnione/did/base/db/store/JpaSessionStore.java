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

import com.google.gson.Gson;
import lombok.RequiredArgsConstructor;
import org.omnione.did.base.db.domain.Oid4vcDeferredTransactionEntity;
import org.omnione.did.base.db.domain.Oid4vcSessionMapEntity;
import org.omnione.did.base.db.repository.Oid4vcDeferredTransactionRepository;
import org.omnione.did.base.db.repository.Oid4vcSessionMapRepository;
import org.omnione.did.oid4vc.oid4vci.dto.credential.CredentialRequest;
import org.omnione.did.oid4vc.oid4vci.service.store.SessionStore;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

import java.time.Instant;

@Component
@RequiredArgsConstructor
@ConditionalOnProperty(name = "spring.datasource.url")
public class JpaSessionStore implements SessionStore {

    private final Oid4vcSessionMapRepository sessionMapRepository;
    private final Oid4vcDeferredTransactionRepository deferredTransactionRepository;
    private final Gson gson = new Gson();

    private static final String TYPE_ISSUER_STATE = "ISSUER_STATE";
    private static final String TYPE_PRE_AUTH_CODE = "PRE_AUTH_CODE_MAP";

    @Override
    public void saveIssuerState(String state, String userId) {
        saveSessionMap(state, TYPE_ISSUER_STATE, userId);
    }

    @Override
    public String consumeIssuerState(String state) {
        return consumeSessionMap(state, TYPE_ISSUER_STATE);
    }

    @Override
    public void savePreAuthorizedCode(String code, String userId) {
        saveSessionMap(code, TYPE_PRE_AUTH_CODE, userId);
    }

    @Override
    public String consumePreAuthorizedCode(String code) {
        return consumeSessionMap(code, TYPE_PRE_AUTH_CODE);
    }

    @Override
    public void saveDeferredCredentialRequest(String transactionId, CredentialRequest request) {
        Oid4vcDeferredTransactionEntity entity = new Oid4vcDeferredTransactionEntity();
        entity.setTransactionId(transactionId);
        entity.setRequestJson(gson.toJson(request));
        entity.setExpiresAt(Instant.now().plusSeconds(60 * 60)); // Default 1 hour expiry

        deferredTransactionRepository.save(entity);
    }

    @Override
    public CredentialRequest consumeDeferredCredentialRequest(String transactionId) {
        return deferredTransactionRepository.findByTransactionId(transactionId)
                .map(entity -> {
                    CredentialRequest request = gson.fromJson(entity.getRequestJson(), CredentialRequest.class);
                    deferredTransactionRepository.delete(entity);
                    return request;
                })
                .orElse(null);
    }

    private void saveSessionMap(String key, String type, String userId) {
        Oid4vcSessionMapEntity entity = new Oid4vcSessionMapEntity();
        entity.setSessionKey(key);
        entity.setSessionType(type);
        entity.setUserId(userId);
        entity.setExpiresAt(Instant.now().plusSeconds(10 * 60)); // Default 10 min

        sessionMapRepository.save(entity);
    }

    private String consumeSessionMap(String key, String type) {
        return sessionMapRepository.findBySessionKeyAndSessionType(key, type)
                .map(entity -> {
                    String userId = entity.getUserId();
                    sessionMapRepository.delete(entity);
                    return userId;
                })
                .orElse(null);
    }
}

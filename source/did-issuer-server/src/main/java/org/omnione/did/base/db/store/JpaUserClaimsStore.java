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
import com.google.gson.reflect.TypeToken;
import lombok.RequiredArgsConstructor;
import org.omnione.did.base.db.domain.Oid4vcUserClaimsEntity;
import org.omnione.did.base.db.repository.Oid4vcUserClaimsRepository;
import org.omnione.did.oid4vc.oid4vci.service.UserClaimsStore;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service("jpaUserClaimsStore")
@Primary
@RequiredArgsConstructor
@ConditionalOnProperty(name = "spring.datasource.url")
public class JpaUserClaimsStore implements UserClaimsStore {

    private final Oid4vcUserClaimsRepository repository;
    private final Gson gson = new Gson();

    @Override
    public void saveClaims(String userId, String credentialType, Map<String, Object> claims) {
        Oid4vcUserClaimsEntity entity = repository.findByUserIdAndCredentialType(userId, credentialType)
                .orElse(new Oid4vcUserClaimsEntity());
        
        entity.setUserId(userId);
        entity.setCredentialType(credentialType);
        entity.setClaims(gson.toJson(claims));
        
        repository.save(entity);
    }

    @Override
    public Map<String, Object> getClaims(String userId, String credentialType) {
        return repository.findByUserIdAndCredentialType(userId, credentialType)
                .map(entity -> (Map<String, Object>) gson.fromJson(entity.getClaims(), new TypeToken<Map<String, Object>>(){}.getType()))
                .orElse(null);
    }

    @Override
    public Map<String, Map<String, Object>> getAllEntries() {
        Map<String, Map<String, Object>> result = new HashMap<>();
        repository.findAll().forEach(entity -> {
            Map<String, Object> claims = gson.fromJson(entity.getClaims(), new TypeToken<Map<String, Object>>(){}.getType());
            result.put(entity.getUserId() + ":" + entity.getCredentialType(), claims);
        });
        return result;
    }
}

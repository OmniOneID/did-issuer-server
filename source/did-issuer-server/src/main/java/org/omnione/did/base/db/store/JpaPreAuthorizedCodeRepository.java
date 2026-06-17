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
import org.omnione.did.base.db.domain.Oid4vcPreAuthCodeEntity;
import org.omnione.did.base.db.repository.Oid4vcPreAuthCodeRepository;
import org.omnione.did.oid4vc.authorization.authorization.oid4vci.preauthorized.dto.PreAuthorizedCode;
import org.omnione.did.oid4vc.authorization.authorization.repository.PreAuthorizedCodeRepository;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Component;

import java.time.ZoneId;
import java.util.HashSet;
import java.util.Set;

@Component
@Primary
@RequiredArgsConstructor
@ConditionalOnProperty(name = "spring.datasource.url")
public class JpaPreAuthorizedCodeRepository implements PreAuthorizedCodeRepository {

    private final Oid4vcPreAuthCodeRepository repository;
    private final Gson gson = new Gson();

    @Override
    public void save(PreAuthorizedCode preAuthorizedCode) {
        Oid4vcPreAuthCodeEntity entity = new Oid4vcPreAuthCodeEntity();
        entity.setCode(preAuthorizedCode.getValue());
        entity.setUserId(preAuthorizedCode.getUserId());
        entity.setUserPin(preAuthorizedCode.getUserPin());
        entity.setConsumed(preAuthorizedCode.isConsumed());
        
        if (preAuthorizedCode.getExpiresAt() != null) {
            entity.setExpiresAt(preAuthorizedCode.getExpiresAt().atZone(ZoneId.systemDefault()).toLocalDateTime());
        }

        if (preAuthorizedCode.getScopes() != null) {
            entity.setScopes(gson.toJson(preAuthorizedCode.getScopes()));
        }

        repository.save(entity);
    }

    @Override
    public java.util.Optional<PreAuthorizedCode> findByValue(String code) {
        return repository.findByCode(code).map(this::toDto);
    }

    @Override
    public java.util.Optional<PreAuthorizedCode> remove(String code) {
        return repository.findByCode(code).map(entity -> {
            PreAuthorizedCode dto = toDto(entity);
            repository.delete(entity);
            return dto;
        });
    }

    private PreAuthorizedCode toDto(Oid4vcPreAuthCodeEntity entity) {
        Set<String> scopes = new HashSet<>();
        if (entity.getScopes() != null) {
            scopes = gson.fromJson(entity.getScopes(), new TypeToken<Set<String>>(){}.getType());
        }

        return new PreAuthorizedCode(
                entity.getCode(),
                entity.getUserId(),
                entity.getExpiresAt().atZone(ZoneId.systemDefault()).toInstant(),
                scopes,
                entity.getUserPin(),
                null 
        );
    }
}

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

package org.omnione.did.base.config;

import org.omnione.did.base.db.domain.CredentialConfig;
import org.omnione.did.base.db.repository.CredentialConfigRepository;
import org.omnione.did.oid4vc.oid4vci.spi.CredentialConfigurationSource;
import org.omnione.did.oid4vc.oid4vci.spi.model.CredentialConfigurationDefinition;
import org.springframework.stereotype.Component;

import java.util.LinkedHashSet;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Component("dbCredentialConfigurationSource")
public class DbCredentialConfigurationSource implements CredentialConfigurationSource {

    private final CredentialConfigRepository repository;

    public DbCredentialConfigurationSource(CredentialConfigRepository repository) {
        this.repository = repository;
    }

    @Override
    public Optional<CredentialConfigurationDefinition> findByConfigId(String configId) {
        return toDefinition(repository.findAllByIdAndEnabledTrue(configId));
    }

    @Override
    public Optional<CredentialConfigurationDefinition> findByIdentifier(String identifier) {
        return toDefinition(repository.findAllByIdentifiersContainingAndEnabledTrue(identifier));
    }

    @Override
    public List<CredentialConfigurationDefinition> findAll() {
        var grouped = repository.findAllByEnabledTrue().stream()
                .collect(Collectors.groupingBy(
                        CredentialConfig::getId,
                        java.util.LinkedHashMap::new,
                        Collectors.toList()));

        return grouped.values().stream()
                .map(this::mapDefinition)
                .toList();
    }

    private Optional<CredentialConfigurationDefinition> toDefinition(List<CredentialConfig> entities) {
        if (entities == null || entities.isEmpty()) {
            return Optional.empty();
        }
        return Optional.of(mapDefinition(entities));
    }

    private CredentialConfigurationDefinition mapDefinition(List<CredentialConfig> entities) {
        CredentialConfig first = entities.get(0);
        LinkedHashSet<String> identifiers = new LinkedHashSet<>();
        for (CredentialConfig entity : entities) {
            identifiers.addAll(entity.getIdentifiers());
        }
        return new CredentialConfigurationDefinition(first.getId(), first.getFormat(), identifiers, first.getEnabled());
    }
}

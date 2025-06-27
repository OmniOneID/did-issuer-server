/*
 * Copyright 2025 OmniOne.
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
package org.omnione.did.issuer.v1.admin.service.query;

import lombok.RequiredArgsConstructor;
import org.omnione.did.base.db.domain.ApplicationConfig;
import org.omnione.did.base.db.repository.ApplicationConfigRepository;
import org.omnione.did.base.exception.ErrorCode;
import org.omnione.did.base.exception.OpenDidException;
import org.springframework.stereotype.Service;

/**
 * Query service for retrieving application-level configuration values in the Admin Console.
 * <p>
 * Provides access to global configuration properties such as TAS URLs, feature toggles, etc.
 */
@RequiredArgsConstructor
@Service
public class ApplicationConfigQueryService {

    private final ApplicationConfigRepository applicationConfigRepository;

    /**
     * Retrieves the first available application configuration from the database.
     *
     * @return the application configuration
     * @throws OpenDidException if no configuration is found
     */
    public ApplicationConfig getApplicationConfig() {
        return applicationConfigRepository.findFirstBy()
                .orElseThrow(() -> new OpenDidException(ErrorCode.APPLICATION_CONFIG_NOT_FOUND));
    }
}

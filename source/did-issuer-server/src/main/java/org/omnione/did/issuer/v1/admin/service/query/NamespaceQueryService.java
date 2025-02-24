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
import org.omnione.did.base.db.domain.Namespace;
import org.omnione.did.base.db.repository.NamespaceRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Description...
 *
 */
@RequiredArgsConstructor
@Service
public class NamespaceQueryService {
    private final NamespaceRepository namespaceRepository;

    public Namespace save(Namespace namespace) {
        return namespaceRepository.save(namespace);
    }

    public Page<Namespace> findAll(Pageable pageable) {

        return namespaceRepository.findAll(pageable);
    }

    public void deleteById(Long id) {
        namespaceRepository.deleteById(id);
    }
}

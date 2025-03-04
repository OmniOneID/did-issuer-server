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

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.omnione.did.base.db.domain.Namespace;
import org.omnione.did.base.db.repository.NamespaceRepository;
import org.omnione.did.base.db.repository.VcSchemaNamespaceRepository;
import org.omnione.did.base.db.repository.VcSchemaRepository;
import org.omnione.did.base.exception.ErrorCode;
import org.omnione.did.base.exception.OpenDidException;
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
    private final VcSchemaNamespaceRepository vcSchemaNamespaceRepository;

    public Namespace save(Namespace namespace) {
        return namespaceRepository.save(namespace);
    }

    public Page<Namespace> findAll(Pageable pageable) {

        return namespaceRepository.findAll(pageable);
    }

    public List<Namespace> findAllById(List<Long> id) {
        return namespaceRepository.findAllById(id);
    }

    public void deleteById(Long id) {
        if (vcSchemaNamespaceRepository.existsByNamespaceId(id)) {
            throw new OpenDidException(ErrorCode.TODO);
        }
        namespaceRepository.deleteById(id);
    }

    public Namespace findById(Long id) {
        return namespaceRepository.findById(id).orElseThrow(() ->
                new OpenDidException(ErrorCode.TODO));
    }
}

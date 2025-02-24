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
import org.omnione.did.base.db.domain.VcSchema;
import org.omnione.did.base.db.domain.VcSchemaNamespace;
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
public class VcSchemaQueryService {
    private final VcSchemaRepository vcSchemaRepository;
    private final VcSchemaNamespaceRepository vcSchemaNamespaceRepository;

    public VcSchema save(VcSchema vcSchema) {

        return vcSchemaRepository.save(vcSchema);
    }

    public Page<VcSchema> findAll(Pageable pageable) {

        return vcSchemaRepository.findAll(pageable);
    }


    public List<VcSchemaNamespace> saveVcSchemaNamespace(List<VcSchemaNamespace> vcSchemaNamespace) {
        return vcSchemaNamespaceRepository.saveAll(vcSchemaNamespace);
    }

    public VcSchema findById(Long id) {
        // TODO: Error Code
        return vcSchemaRepository.findById(id)
                .orElseThrow(() -> new OpenDidException(ErrorCode.TODO));
    }
}

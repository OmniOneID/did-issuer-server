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
package org.omnione.did.issuer.v1.admin.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.omnione.did.issuer.v1.admin.dto.zkp.schema.ZkpSchemaDto;
import org.omnione.did.issuer.v1.admin.service.query.ZkpSchemaQueryService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Slf4j
@Transactional
@RequiredArgsConstructor
@Service
public class ZkpSchemaService {
    private final ZkpSchemaQueryService zkpSchemaQueryService;

    public PageImpl<ZkpSchemaDto> searchZkpSchemaList(String searchKey, String searchValue, Pageable pageable) {
        return zkpSchemaQueryService.searchZkpSchemaList(searchKey, searchValue, pageable);
    }
}

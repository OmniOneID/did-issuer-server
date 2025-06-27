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

import lombok.RequiredArgsConstructor;
import org.omnione.did.base.db.domain.Vc;
import org.omnione.did.issuer.v1.admin.dto.vc.IssuedVcDto;
import org.omnione.did.issuer.v1.admin.service.query.IssuedVcQueryService;
import org.omnione.did.issuer.v1.common.service.StorageService;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

/**
 * Service for handling operations related to issued Verifiable Credentials in the Admin Console.
 * Provides functionality to search issued VCs and retrieve detailed VC information by ID.
 */
@RequiredArgsConstructor
@Service
public class IssuedVcService {

    private final IssuedVcQueryService issuedVcQueryService;
    private final StorageService storageService;

    /**
     * Searches the list of issued VCs with optional filtering and pagination.
     *
     * @param searchKey   the key to filter by (e.g., subject, schema)
     * @param searchValue the value to filter for
     * @param pageable    the pagination information
     * @return a page of IssuedVcDto
     */
    public PageImpl<IssuedVcDto> searchIssuedVcList(String searchKey, String searchValue, Pageable pageable) {
        return issuedVcQueryService.searchIssuedVcList(searchKey, searchValue, pageable);
    }

    /**
     * Finds an issued VC by its ID.
     *
     * @param id the ID of the issued VC
     * @return the corresponding IssuedVcDto
     */
    public IssuedVcDto findById(Long id) {
        Vc vc = issuedVcQueryService.findById(id);
        return IssuedVcDto.fromEntity(vc);
    }
}

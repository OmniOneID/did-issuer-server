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
package org.omnione.did.issuer.v1.admin.controller;

import lombok.RequiredArgsConstructor;
import org.omnione.did.base.constants.UrlConstant;
import org.omnione.did.issuer.v1.admin.dto.vc.IssuedVcDto;
import org.omnione.did.issuer.v1.admin.service.IssuedVcService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * The IssuedVcAdminController class provides Admin Console endpoints for managing issued verifiable credentials.
 * It allows searching for issued VCs with pagination and retrieving individual VC details by ID.
 */
@RequiredArgsConstructor
@RestController
@RequestMapping(UrlConstant.Admin.V1 + UrlConstant.Admin.ISSUED_VCS)
public class IssuedVcAdminController {
    private final IssuedVcService issuedVcService;

    /**
     * Searches the list of issued verifiable credentials with optional filtering and pagination.
     *
     * @param searchKey the key to search by (e.g., subject, issuer)
     * @param searchValue the value to search for
     * @param pageable the pagination information
     * @return a page of issued verifiable credentials
     */
    @GetMapping
    public PageImpl<IssuedVcDto> searchIssuedVcList(String searchKey, String searchValue, Pageable pageable){

        return issuedVcService.searchIssuedVcList(searchKey, searchValue, pageable);
    }

    /**
     * Retrieves the issued verifiable credential by its ID.
     *
     * @param id the ID of the issued verifiable credential
     * @return the issued verifiable credential details
     */
    @GetMapping(UrlConstant.Admin.PATH_VARIABLE_ID)
    public ResponseEntity<IssuedVcDto> getIssuedVcById(@PathVariable("id") Long id) {
        return ResponseEntity.ok(issuedVcService.findById(id));
    }

}

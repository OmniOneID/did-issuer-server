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
import org.omnione.did.issuer.v1.admin.api.dto.EmptyResDto;
import org.omnione.did.issuer.v1.admin.dto.PageResponse;
import org.omnione.did.issuer.v1.admin.dto.vc.GetVcSchemaResDto;
import org.omnione.did.issuer.v1.admin.dto.vc.VcSchemaDto;
import org.omnione.did.issuer.v1.admin.dto.vc.VcSchemaReqDto;
import org.omnione.did.issuer.v1.admin.service.VcSchemaManagerService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * The VcSchemaAdminController class provides Admin Console endpoints for managing Verifiable Credential (VC) schemas.
 * It includes operations for creating, updating, deleting, searching, and retrieving individual VC schemas.
 */
@RequiredArgsConstructor
@RestController
@RequestMapping(value = UrlConstant.Admin.V1 + UrlConstant.Admin.VC_SCHEMA)
public class VcSchemaAdminController {

    private final VcSchemaManagerService vcSchemaManagerService;

    /**
     * Creates a new VC schema.
     *
     * @param request the DTO containing VC schema details
     * @return an empty response on success
     */
    @PostMapping
    public ResponseEntity<EmptyResDto> createVcSchema(@RequestBody VcSchemaReqDto request) {
        vcSchemaManagerService.createVcSchema(request);
        return ResponseEntity.ok(EmptyResDto.builder().build());
    }

    /**
     * Updates an existing VC schema.
     *
     * @param request the DTO containing updated VC schema details
     * @return the updated VC schema
     */
    @PatchMapping
    public ResponseEntity<VcSchemaDto> updateVcSchema(@RequestBody VcSchemaReqDto request) {
        return ResponseEntity.ok(vcSchemaManagerService.updateVcSchema(request));
    }

    /**
     * Deletes a VC schema by its ID.
     *
     * @param id the ID of the VC schema to delete
     */
    @DeleteMapping
    public void deleteVcSchema(@RequestParam("id") Long id) {
        vcSchemaManagerService.deleteVcSchemaById(id);
    }

    /**
     * Searches the list of VC schemas with optional filtering and pagination.
     *
     * @param searchKey   the key to search by (e.g., schema name)
     * @param searchValue the value to search for
     * @param pageable    the pagination information
     * @return a page of VC schemas
     */
    @GetMapping
    public PageImpl<VcSchemaDto> searchVcSchemaList(@RequestParam(value = "searchKey", required = false)String searchKey,
                                                    @RequestParam(value = "searchValue", required = false)String searchValue,
                                                    Pageable pageable) {
        return vcSchemaManagerService.searchVcSchemaList(searchKey, searchValue, pageable);
    }

    /**
     * Retrieves a VC schema by its ID.
     *
     * @param id the ID of the VC schema
     * @return the VC schema details
     */
    @GetMapping(UrlConstant.Admin.PATH_VARIABLE_ID)
    public ResponseEntity<GetVcSchemaResDto> getVcSchema(@PathVariable("id") Long id) {
        return ResponseEntity.ok(vcSchemaManagerService.getVcSchemaById(id));
    }
}

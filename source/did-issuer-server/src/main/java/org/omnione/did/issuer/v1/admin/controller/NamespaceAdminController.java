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
import org.omnione.did.data.model.schema.SchemaClaims;
import org.omnione.did.issuer.v1.admin.api.dto.EmptyResDto;
import org.omnione.did.issuer.v1.admin.dto.namespace.CreateNamespaceResDto;
import org.omnione.did.issuer.v1.admin.dto.namespace.NamespaceDto;
import org.omnione.did.issuer.v1.admin.dto.namespace.UpdateNamespaceReqDto;
import org.omnione.did.issuer.v1.admin.service.NamespaceService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * The NamespaceAdminController class provides Admin Console endpoints for managing namespaces.
 * It includes operations for creating, updating, deleting, searching, and retrieving namespace information.
 */
@RequiredArgsConstructor
@RestController
@RequestMapping(value = UrlConstant.Admin.V1 + UrlConstant.Admin.NAMESPACE)
public class NamespaceAdminController {

    private final NamespaceService namespaceService;

    /**
     * Creates a new namespace using the provided schema claims.
     *
     * @param request the schema claims for the new namespace
     * @return the response containing created namespace information
     */
    @PostMapping
    public ResponseEntity<CreateNamespaceResDto> createNamespaceResDto(@RequestBody SchemaClaims request) {
        return ResponseEntity.ok(namespaceService.createNamespaceReqDto(request));
    }

    /**
     * Updates an existing namespace.
     *
     * @param request the DTO containing updated namespace details
     * @return the updated namespace information
     */
    @PatchMapping
    @ResponseBody
    public ResponseEntity<NamespaceDto> updateNamespace(@RequestBody UpdateNamespaceReqDto request) {
        return ResponseEntity.ok(namespaceService.updateNamespace(request));
    }

    /**
     * Deletes a namespace by ID.
     *
     * @param id the ID of the namespace to delete
     * @return an empty response on success
     */
    @DeleteMapping
    public ResponseEntity<EmptyResDto> deleteNamespace(@RequestParam(name = "id") Long id) {
        namespaceService.deleteNamespaceById(id);
        return ResponseEntity.ok(new EmptyResDto());
    }

    /**
     * Searches the list of namespaces with optional filtering and pagination.
     *
     * @param searchKey the key to search by (e.g., name)
     * @param searchValue the value to search for
     * @param pageable the pagination information
     * @return a page of namespaces
     */
    @GetMapping
    public PageImpl<NamespaceDto> searchNamespaceList(String searchKey, String searchValue, Pageable pageable) {
        return namespaceService.searchNamespaceList(searchKey, searchValue, pageable);
    }

    /**
     * Retrieves a namespace by its ID.
     *
     * @param id the ID of the namespace
     * @return the namespace details
     */
    @GetMapping(UrlConstant.Admin.PATH_VARIABLE_ID)
    public ResponseEntity<NamespaceDto> getNamespaceInfo(@PathVariable(name = "id") Long id) {
        return ResponseEntity.ok(namespaceService.getNamespaceById(id));
    }
}

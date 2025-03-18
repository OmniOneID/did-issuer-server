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
import org.omnione.did.base.db.domain.Namespace;
import org.omnione.did.data.model.schema.SchemaClaims;
import org.omnione.did.issuer.v1.admin.dto.*;
import org.omnione.did.issuer.v1.admin.service.NamespaceService;
import org.omnione.did.issuer.v1.admin.utils.ResponseUtil;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * Description...
 *
 */

@RequiredArgsConstructor
@RestController
@RequestMapping(value = UrlConstant.Admin.V1 + UrlConstant.Admin.NAMESPACE)
public class NamespaceAdminController {

    private final NamespaceService namespaceService;

    @PostMapping
    public ResponseEntity<CreateNamespaceResDto> createNamespaceResDto(@RequestBody SchemaClaims request) {
        return ResponseEntity.ok(namespaceService.createNamespaceReqDto(request));
    }

    @PatchMapping
    @ResponseBody
    public ResponseEntity<NamespaceDto> updateNamespace(@RequestBody UpdateNamespaceReqDto request) {
        return ResponseEntity.ok(namespaceService.updateNamespace(request));
    }

    @DeleteMapping(UrlConstant.Admin.PATH_VARIABLE_ID)
    public ResponseEntity<DeleteNamespaceResDto> deleteNamespace(@PathVariable(name = "id") Long id) {

        namespaceService.deleteNamespaceById(id);
        return ResponseEntity.ok(null);
    }

//    @GetMapping(UrlConstant.Admin.LIST)
//    public ResponseEntity<ResponseDto> getNamespaceList(
//            @PageableDefault(sort = "id") Pageable pageable) {
//
//        Page<Namespace> page = namespaceService.getNamespacesByPageable(pageable);
//        ResponseDto response = ResponseUtil.generateBodyWithPage(page.getContent(), page.getTotalElements());
//
//        return ResponseEntity.ok(response);
//    }

//    @GetMapping
//    public ResponseEntity<Namespace> getNamespace(@RequestParam(name = "id") Long id) {
//        return ResponseEntity.ok(namespaceService.getNamespaceById(id));
//    }

    @GetMapping
    public Page<NamespaceDto> searchNamespaceList(String searchKey, String searchValue, Pageable pageable) {
        return namespaceService.searchNamespaceList(searchKey, searchValue, pageable);
    }

    @GetMapping(UrlConstant.Admin.PATH_VARIABLE_ID)
    public ResponseEntity<NamespaceDto> getNamespaceInfo(@PathVariable(name = "id") Long id) {
        return ResponseEntity.ok(namespaceService.getNamespaceById(id));
    }
}

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
import org.omnione.did.issuer.v1.admin.dto.*;
import org.omnione.did.issuer.v1.admin.service.VcSchemaManagerService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * Description...
 *
 */

@RequiredArgsConstructor
@RestController
@RequestMapping(value = UrlConstant.Admin.V1 + UrlConstant.Admin.VC_SCHEMA)
public class VcSchemaAdminController {

    private final VcSchemaManagerService vcSchemaManagerService;

    @PostMapping
    public ResponseEntity<EmptyResDto> createVcSchema(@RequestBody VcSchemaReqDto request) {
        vcSchemaManagerService.createVcSchema(request);
        return ResponseEntity.ok(EmptyResDto.builder().build());
    }

    @PatchMapping
    public ResponseEntity<VcSchemaDto> updateVcSchema(@RequestBody VcSchemaReqDto request) {
        return ResponseEntity.ok(vcSchemaManagerService.updateVcSchema(request));
    }

    @DeleteMapping
    public void deleteVcSchema(@RequestParam("id") Long id) {
        vcSchemaManagerService.deleteVcSchemaById(id);
    }

    @GetMapping
    public Page<VcSchemaDto> searchVcSchemaList(String searchKey, String searchValue, Pageable pageable) {
        return vcSchemaManagerService.searchVcSchemaList(searchKey, searchValue, pageable);

    }


    @GetMapping(UrlConstant.Admin.PATH_VARIABLE_ID)
    public ResponseEntity<GetVcSchemaResDto> getVcSchema(@PathVariable("id") Long id) {

        return ResponseEntity.ok(vcSchemaManagerService.getVcSchemaById(id));
    }
}

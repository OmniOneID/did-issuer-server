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
import org.omnione.did.base.db.domain.VcSchema;
import org.omnione.did.issuer.v1.admin.dto.CreateVcSchemaReqDto;
import org.omnione.did.issuer.v1.admin.dto.CreateVcSchemaResDto;
import org.omnione.did.issuer.v1.admin.dto.ResponseDto;
import org.omnione.did.issuer.v1.admin.service.VcSchemaManagerService;
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
@RequestMapping(value = UrlConstant.Admin.V1 + UrlConstant.Admin.VC_SCHEMA)
public class VcSchemaAdminController {
    private final VcSchemaManagerService vcSchemaManagerService;
    // Create
    @PostMapping
    public ResponseEntity<CreateVcSchemaResDto> createVcSchema(@RequestBody CreateVcSchemaReqDto request) {

        return ResponseEntity.ok(vcSchemaManagerService.createVcSchema(request));
    }

    // Update
    @PatchMapping
    public void updateVcSchema() {
        // TODO
    }
    // Delete
    @DeleteMapping
    public void deleteVcSchema(@RequestParam("id") Long id) {
        // TODO
    }

    // Get
    @GetMapping(UrlConstant.Admin.LIST)
    public ResponseEntity<ResponseDto> getVcSchemaList(
            @PageableDefault(sort = "id") Pageable pageable) {

        Page<VcSchema> page = vcSchemaManagerService.getVcSchemaList(pageable);
        ResponseDto response = ResponseUtil.generateBodyWithPage(page.getContent(), page.getTotalElements());

        return ResponseEntity.ok(response);
    }

    @GetMapping
    public ResponseEntity<VcSchema> getVcSchema(@RequestParam("id") Long id) {

        return ResponseEntity.ok(vcSchemaManagerService.getVcSchemaById(id));
    }
}

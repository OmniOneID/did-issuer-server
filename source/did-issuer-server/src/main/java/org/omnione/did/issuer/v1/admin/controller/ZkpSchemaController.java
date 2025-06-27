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
import org.omnione.did.base.db.domain.ZkpSchema;
import org.omnione.did.issuer.v1.admin.api.dto.EmptyResDto;
import org.omnione.did.issuer.v1.admin.dto.zkp.namespace.ZkpNamespaceInfoDto;
import org.omnione.did.issuer.v1.admin.dto.zkp.schema.ZkpSchemaInfoDto;
import org.omnione.did.issuer.v1.admin.dto.zkp.schema.ZkpSchemaDto;
import org.omnione.did.issuer.v1.admin.service.ZkpSchemaService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RequiredArgsConstructor
@RestController
@RequestMapping(value = UrlConstant.Admin.V1 + UrlConstant.Admin.ZKP_SCHEMA)
public class ZkpSchemaController {
    private final ZkpSchemaService zkpSchemaService;

    @GetMapping
    public PageImpl<ZkpSchemaDto> searchZkpSchemaList(String searchKey, String searchValue, Pageable pageable) {
        return zkpSchemaService.searchZkpSchemaList(searchKey, searchValue, pageable);
    }

    @PostMapping
    public ResponseEntity<EmptyResDto> createZkpSchema(@RequestBody ZkpSchemaInfoDto request) {
        return ResponseEntity.ok(zkpSchemaService.createZkpSchema(request));
    }

    @PostMapping(UrlConstant.Admin.RE_REGISTER)
    public ResponseEntity<EmptyResDto> reRegisterZkpCredentialSchema() {
        return ResponseEntity.ok(zkpSchemaService.reRegisterZkpCredentialSchema());
    }

    @GetMapping(UrlConstant.Admin.PATH_VARIABLE_ID)
    public ResponseEntity<ZkpSchemaInfoDto> getZkpNamespaceInfo(@PathVariable(name = "id") Long id) {
        return ResponseEntity.ok(zkpSchemaService.getZkpSchemaInfo(id));
    }

    @GetMapping(UrlConstant.Admin.FIND_ALL)
    public ResponseEntity<List<ZkpSchemaDto>> getAllZkpSchemas() {
        return ResponseEntity.ok(zkpSchemaService.getAllZkpSchemas());
    }
}

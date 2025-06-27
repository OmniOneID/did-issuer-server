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
import org.omnione.did.base.constants.UrlConstant.Admin;
import org.omnione.did.issuer.v1.admin.api.dto.EmptyResDto;
import org.omnione.did.issuer.v1.admin.dto.zkp.definition.VerifyCredentialDefinitionAliasUniqueResDto;
import org.omnione.did.issuer.v1.admin.dto.zkp.definition.ZkpCredentialDefinitionDto;
import org.omnione.did.issuer.v1.admin.dto.zkp.definition.ZkpCredentialDefinitionSaveDto;
import org.omnione.did.issuer.v1.admin.service.ZkpDefinitionService;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
@RequestMapping(value = UrlConstant.Admin.V1 + UrlConstant.Admin.ZKP_CREDENTIAL_DEFINITION)
public class ZkpCredentialDefinitionController {
    private final ZkpDefinitionService zkpDefinitionService;

    @GetMapping
    public PageImpl<ZkpCredentialDefinitionDto> searchZkpDefinitionList(String searchKey, String searchValue, Pageable pageable) {
        return zkpDefinitionService.searchZkpDefinitionList(searchKey, searchValue, pageable);
    }

    @GetMapping(Admin.CHECK_CREDENTIAL_DEFINITION_ALIAS)
    public VerifyCredentialDefinitionAliasUniqueResDto verifyCredentialDefinitionAliasUnique(@RequestParam String alias) {
        return zkpDefinitionService.verifyCredentialDefinitionAliasUnique(alias);
    }

    @PostMapping
    public ResponseEntity<EmptyResDto> createZkpCredentialDefinition(@RequestBody ZkpCredentialDefinitionSaveDto request) {
        return ResponseEntity.ok(zkpDefinitionService.createZkpCredentialDefinition(request));
    }

    @PostMapping(Admin.RE_REGISTER)
    public ResponseEntity<EmptyResDto> reRegisterZkpCredentialDefinition() {
        return ResponseEntity.ok(zkpDefinitionService.reRegisterZkpCredentialDefinition());
    }

    @GetMapping(UrlConstant.Admin.PATH_VARIABLE_ID)
    public ResponseEntity<ZkpCredentialDefinitionDto> getZkpCredentialDefinitionInfo(@PathVariable(name = "id") Long id) {
        return ResponseEntity.ok(zkpDefinitionService.getZkpCredentialDefinitionInfo(id));
    }
}

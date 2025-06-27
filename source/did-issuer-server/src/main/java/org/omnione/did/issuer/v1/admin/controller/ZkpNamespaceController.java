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
import org.omnione.did.issuer.v1.admin.dto.zkp.namespace.VerifyNamespaceIdUniqueResDto;
import org.omnione.did.issuer.v1.admin.dto.zkp.namespace.ZkpAttributeDto;
import org.omnione.did.issuer.v1.admin.dto.zkp.namespace.ZkpNamespaceDto;
import org.omnione.did.issuer.v1.admin.dto.zkp.namespace.ZkpNamespaceInfoDto;
import org.omnione.did.issuer.v1.admin.dto.zkp.namespace.ZkpNamespaceUpdateDto;
import org.omnione.did.issuer.v1.admin.service.ZkpNamespaceService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RequiredArgsConstructor
@RestController
@RequestMapping(value = UrlConstant.Admin.V1 + UrlConstant.Admin.ZKP_NAMESPACE)
public class ZkpNamespaceController {
    private final ZkpNamespaceService zkpNamespaceService;

    @GetMapping
    public PageImpl<ZkpNamespaceDto> searchZkpNamespaceList(String searchKey, String searchValue, Pageable pageable) {
        return zkpNamespaceService.searchZkpNamespaceList(searchKey, searchValue, pageable);
    }

    @PostMapping
    public ResponseEntity<EmptyResDto> createZkpNamespace(@RequestBody ZkpNamespaceInfoDto request) {
        return ResponseEntity.ok(zkpNamespaceService.createZkpNamespace(request));
    }

    @GetMapping(UrlConstant.Admin.PATH_VARIABLE_ID)
    public ResponseEntity<ZkpNamespaceInfoDto> getZkpNamespaceInfo(@PathVariable(name = "id") Long id) {
        return ResponseEntity.ok(zkpNamespaceService.getZkpNamespaceInfoById(id));
    }

    @PatchMapping
    @ResponseBody
    public ResponseEntity<EmptyResDto> updateZkpNamespace(@RequestBody ZkpNamespaceUpdateDto request) {
        return ResponseEntity.ok(zkpNamespaceService.updateZkpNamespace(request));
    }

    @DeleteMapping
    public ResponseEntity<EmptyResDto> deleteZkpNamespace(@RequestParam(name = "id") Long id) {
        zkpNamespaceService.deleteZkpNamespaceById(id);
        return ResponseEntity.ok(new EmptyResDto());
    }

    @GetMapping(UrlConstant.Admin.CHECK_NAMESPACE_ID)
    public VerifyNamespaceIdUniqueResDto verifyNamespaceIdUnique(@RequestParam String namespaceId) {
        return zkpNamespaceService.verifyNamespaceIdUnique(namespaceId);
    }

    @GetMapping(UrlConstant.Admin.FIND_ALL)
    public ResponseEntity<List<ZkpNamespaceDto>> getAllNamespaces() {
        return ResponseEntity.ok(zkpNamespaceService.getAllNamespaces());
    }

    @GetMapping(Admin.ZKP_ATTRIBUTES + UrlConstant.Admin.FIND_ALL)
    public ResponseEntity<List<ZkpAttributeDto>> getAttributes(@RequestParam(name = "id") Long namespaceId) {
        return ResponseEntity.ok(zkpNamespaceService.getAttributesByNamespaceId(namespaceId));
    }
}
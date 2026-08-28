/*
 * Copyright 2026 OmniOne.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 */
package org.omnione.did.issuer.v1.admin.controller;

import lombok.RequiredArgsConstructor;
import org.omnione.did.base.constants.UrlConstant;
import org.omnione.did.issuer.v1.admin.dto.oid4vci.Oid4vciIssuedCredentialDto;
import org.omnione.did.issuer.v1.admin.dto.oid4vci.ChangeOid4vciCredentialStatusReqDto;
import org.omnione.did.issuer.v1.admin.service.Oid4vciIssuedCredentialService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping(UrlConstant.Admin.V1 + UrlConstant.Admin.OID4VCI
        + UrlConstant.Admin.OID4VCI_ISSUED_CREDENTIALS)
public class Oid4vciIssuedCredentialAdminController {
    private final Oid4vciIssuedCredentialService service;

    @GetMapping
    public Page<Oid4vciIssuedCredentialDto> search(
            @RequestParam(required = false) String searchKey,
            @RequestParam(required = false) String searchValue,
            @PageableDefault(sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable) {
        return service.search(searchKey, searchValue, pageable);
    }

    @GetMapping(UrlConstant.Admin.PATH_VARIABLE_ID)
    public ResponseEntity<Oid4vciIssuedCredentialDto> findById(@PathVariable Long id) {
        return ResponseEntity.ok(service.findById(id));
    }

    @PatchMapping(UrlConstant.Admin.PATH_VARIABLE_ID + "/status")
    public ResponseEntity<Oid4vciIssuedCredentialDto> changeStatus(
            @PathVariable Long id,
            @RequestBody ChangeOid4vciCredentialStatusReqDto request) {
        return ResponseEntity.ok(service.changeStatus(id, request.getStatus(), request.getReason()));
    }
}

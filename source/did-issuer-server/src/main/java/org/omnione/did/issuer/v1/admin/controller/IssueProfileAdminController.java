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
import org.omnione.did.base.db.domain.IssueProfile;
import org.omnione.did.issuer.v1.admin.dto.CreateIssueProfileReqDto;
import org.omnione.did.issuer.v1.admin.dto.CreateIssueProfileResDto;
import org.omnione.did.issuer.v1.admin.dto.ResponseDto;
import org.omnione.did.issuer.v1.admin.service.IssueProfileService;
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
@RequestMapping(value = UrlConstant.Admin.V1 + UrlConstant.Admin.ISSUE_PROFILE)
public class IssueProfileAdminController {

    private final IssueProfileService issueProfileService;

    // Create
    @PostMapping
    public ResponseEntity<CreateIssueProfileResDto> createIssueProfile(
            @RequestBody CreateIssueProfileReqDto request) {

        return ResponseEntity.ok(null);
    }
    // Update
    @PatchMapping
    public void updateIssueProfile() {

    }

    // Delete
    @DeleteMapping
    public void deleteIssueProfile() {

    }

    // get
    @GetMapping(UrlConstant.Admin.LIST)
    public ResponseEntity<ResponseDto> getIssueProfileList(
            @PageableDefault(sort = "id") Pageable pageable) {
        Page<IssueProfile> page = issueProfileService.getIssueProfileList(pageable);
        ResponseDto response = ResponseUtil.generateBodyWithPage(page.getContent(), page.getTotalElements());

        return ResponseEntity.ok(response);
    }

    @GetMapping
    public ResponseEntity<IssueProfile> getVcSchema(@RequestParam("id") Long id) {

        return ResponseEntity.ok(issueProfileService.getIssueProfileById(id));
    }
}

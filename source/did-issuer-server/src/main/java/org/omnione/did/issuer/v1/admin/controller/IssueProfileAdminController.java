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
import org.omnione.did.issuer.v1.admin.dto.profile.CreateIssueProfileReqDto;
import org.omnione.did.issuer.v1.admin.dto.profile.GetIssueProfileResDto;
import org.omnione.did.issuer.v1.admin.dto.profile.IssueProfileDto;
import org.omnione.did.issuer.v1.admin.service.IssueProfileService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * The IssueProfileAdminController class provides Admin Console endpoints for managing issue profiles.
 * It supports creating, updating, deleting, searching, and retrieving individual issue profiles.
 */
@RequiredArgsConstructor
@RestController
@RequestMapping(value = UrlConstant.Admin.V1 + UrlConstant.Admin.ISSUE_PROFILE)
public class IssueProfileAdminController {

    private final IssueProfileService issueProfileService;

    /**
     * Creates a new issue profile.
     *
     * @param request the DTO containing the issue profile creation details
     * @return an empty response on success
     */
    @PostMapping
    public ResponseEntity<EmptyResDto> createIssueProfile(@RequestBody CreateIssueProfileReqDto request) {
        issueProfileService.createIssueProfile(request);
        return ResponseEntity.ok(EmptyResDto.builder().build());
    }

    /**
     * Updates an existing issue profile.
     *
     * @param request the DTO containing the updated issue profile details
     * @return an empty response on success
     */
    @PatchMapping
    public ResponseEntity<EmptyResDto> updateIssueProfile(@RequestBody CreateIssueProfileReqDto request) {
        issueProfileService.updateIssueProfile(request);
        return ResponseEntity.ok(EmptyResDto.builder().build());
    }

    /**
     * Deletes an issue profile by ID.
     *
     * @param id the ID of the issue profile to delete
     * @return an empty response on success
     */
    @DeleteMapping
    public ResponseEntity<EmptyResDto> deleteIssueProfile(@RequestParam(name = "id") Long id) {
        issueProfileService.deleteIssueProfileById(id);
        return ResponseEntity.ok(EmptyResDto.builder().build());
    }

    /**
     * Searches the list of issue profiles with optional filtering and pagination.
     *
     * @param searchKey   the key to search by (e.g., profile name)
     * @param searchValue the value to search for
     * @param pageable    the pagination information
     * @return a page of issue profiles
     */
    @GetMapping
    public PageImpl<IssueProfileDto> searchIssueProfileList(String searchKey, String searchValue, Pageable pageable) {
        return issueProfileService.searchIssueProfileList(searchKey, searchValue, pageable);
    }

    /**
     * Retrieves the issue profile by its ID.
     *
     * @param id the ID of the issue profile
     * @return the detailed issue profile information
     */
    @GetMapping(UrlConstant.Admin.PATH_VARIABLE_ID)
    public ResponseEntity<GetIssueProfileResDto> getIssueProfile(@PathVariable("id") Long id) {
        return ResponseEntity.ok(issueProfileService.getIssueProfileById(id));
    }
}
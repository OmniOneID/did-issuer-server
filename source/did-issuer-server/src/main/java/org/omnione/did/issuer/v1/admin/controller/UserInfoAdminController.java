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
import lombok.extern.slf4j.Slf4j;
import org.omnione.did.base.constants.UrlConstant;
import org.omnione.did.issuer.v1.admin.dto.user.CreateUserInfoFromDemoReqDto;
import org.omnione.did.issuer.v1.admin.dto.user.CreateUserInfoReqDto;
import org.omnione.did.issuer.v1.admin.dto.user.UserDto;
import org.omnione.did.issuer.v1.admin.service.UserManagementService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * The UserInfoAdminController class provides Admin Console endpoints for managing user information.
 * It supports creating, updating, retrieving, and searching user records.
 */
@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping(value = UrlConstant.Admin.V1 + UrlConstant.Admin.USER)
public class UserInfoAdminController {

    private final UserManagementService userManagementService;

    /**
     * Searches the list of users with optional filtering and pagination.
     *
     * @param searchKey the key to search by (e.g., name, email)
     * @param searchValue the value to search for
     * @param pageable the pagination information
     * @return a page of user information
     */
    @GetMapping
    public PageImpl<UserDto> searchUserInfoList(String searchKey, String searchValue, Pageable pageable) {
        return userManagementService.searchUserInfoList(searchKey, searchValue, pageable);
    }

    /**
     * Creates a new user.
     *
     * @param request the DTO containing user creation details
     */
    @PostMapping
    public void createUserInfo(@RequestBody CreateUserInfoReqDto request) {
        userManagementService.createUserInfo(request);
    }

    /**
     * Creates a new user from Demo.
     *
     * @param request the DTO containing user creation details
     */
    @PostMapping("/demo")
    public void createUserInfoFromDemo(@RequestBody CreateUserInfoFromDemoReqDto request) {
        userManagementService.createUserInfo(request);
    }

    /**
     * Retrieves user information by ID.
     *
     * @param id the ID of the user
     * @return the user information
     */
    @GetMapping(UrlConstant.Admin.PATH_VARIABLE_ID)
    public ResponseEntity<UserDto> getUserInfo(@PathVariable("id") Long id) {
        return ResponseEntity.ok(userManagementService.findById(id));
    }

    /**
     * Updates an existing user.
     *
     * @param request the DTO containing updated user information
     */
    @PatchMapping
    public void updateUserInfo(@RequestBody CreateUserInfoReqDto request) {
        userManagementService.updateUserInfo(request);
    }
}


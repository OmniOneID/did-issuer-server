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

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.omnione.did.base.constants.UrlConstant;
import org.omnione.did.base.constants.UrlConstant.Admin;
import org.omnione.did.issuer.v1.admin.api.dto.EmptyResDto;
import org.omnione.did.issuer.v1.admin.dto.admin.AdminDto;
import org.omnione.did.issuer.v1.admin.dto.admin.ResetPasswordReqDto;
import org.omnione.did.issuer.v1.admin.dto.admin.RegisterAdminReqDto;
import org.omnione.did.issuer.v1.admin.dto.admin.ResetPasswordByRootReqDto;
import org.omnione.did.issuer.v1.admin.dto.admin.VerifyAdminIdUniqueResDto;
import org.omnione.did.issuer.v1.admin.service.AdminManagementService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;



/**
 * The AdminManagementController class handles administrative operations related to admin users.
 * It provides endpoints for managing admin accounts, including registration, deletion, password reset,
 * searching, and verifying login ID uniqueness.
 */
@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping(value = UrlConstant.Admin.V1 + Admin.ADMIN)
public class AdminManagementController {
    private final AdminManagementService adminManagementService;

    /**
     * Resets the password for an admin user.
     *
     * @param resetPasswordReqDto the DTO containing reset password request information
     * @return the updated admin user information
     */
    @PostMapping(value = "/reset-password")
    @ResponseBody
    public AdminDto resetPassword(@Valid @RequestBody ResetPasswordReqDto resetPasswordReqDto) {
        return adminManagementService.resetPassword(resetPasswordReqDto);
    }

    /**
     * Searches admin users based on a given search key and value with pagination support.
     *
     * @param searchKey the key to search by (e.g., name, email)
     * @param searchValue the value to search for
     * @param pageable the pagination information
     * @return a page of admin user results
     */
    @GetMapping(value = "/list")
    public PageImpl<AdminDto> searchAdmins(String searchKey, String searchValue, Pageable pageable) {
        return adminManagementService.searchAdmins(searchKey, searchValue, pageable);
    }

    /**
     * Retrieves the admin user by ID.
     *
     * @param id the ID of the admin user
     * @return the admin user information
     */
    @GetMapping
    public AdminDto getAdmin(@RequestParam Long id) {
        return adminManagementService.findById(id);
    }

    /**
     * Registers a new admin user.
     *
     * @param registerAdminReqDto the DTO containing registration information
     * @return an empty response on success
     */
    @PostMapping
    public EmptyResDto registerAdmin(@RequestBody RegisterAdminReqDto registerAdminReqDto) {
        return adminManagementService.registerAdmin(registerAdminReqDto);
    }

    /**
     * Verifies the uniqueness of the admin login ID.
     *
     * @param loginId the login ID to verify
     * @return a result indicating whether the login ID is unique
     */
    @GetMapping(value = "/check-admin-id")
    public VerifyAdminIdUniqueResDto verifyAdminIdUnique(@RequestParam String loginId) {
        return adminManagementService.verifyAdminIdUnique(loginId);
    }

    /**
     * Deletes an admin user by ID.
     *
     * @param id the ID of the admin user to delete
     * @return an empty response on success
     */
    @DeleteMapping
    public EmptyResDto deleteAdmin(@RequestParam Long id) {
        return adminManagementService.deleteAdmin(id);
    }

    /**
     * Resets the password of an admin user by a root user.
     *
     * @param resetPasswordByRootReqDto the DTO containing reset request information
     * @return an empty response on success
     */
    @PostMapping(value = "/root/reset-password")
    @ResponseBody
    public EmptyResDto resetPasswordByRoot(@RequestBody ResetPasswordByRootReqDto resetPasswordByRootReqDto) {
        return adminManagementService.resetPasswordByRoot(resetPasswordByRootReqDto);
    }

}
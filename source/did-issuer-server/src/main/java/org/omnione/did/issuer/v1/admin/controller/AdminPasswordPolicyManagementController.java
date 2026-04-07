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
import org.omnione.did.issuer.v1.admin.dto.admin.AdminPasswordPolicyDto;
import org.omnione.did.issuer.v1.admin.dto.admin.RegisterAdminPasswordPolicyReqDto;
import org.omnione.did.issuer.v1.admin.service.AdminPasswordPolicyManagementService;
import org.springframework.web.bind.annotation.*;

/**
 * Controller for managing the admin password policy in the Admin Console.
 */
@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping(value = UrlConstant.Admin.V1)
public class AdminPasswordPolicyManagementController {

    private final AdminPasswordPolicyManagementService adminPasswordPolicyManagementService;

    /**
     * Retrieves the current admin password policy.
     *
     * @return the password policy DTO
     */
    @GetMapping(value = "/admin-password-policy")
    public AdminPasswordPolicyDto findAdminPasswordPolicy() {
        return adminPasswordPolicyManagementService.findAdminPasswordPolicy();
    }

    /**
     * Registers or updates the admin password policy.
     *
     * @param req the request DTO
     * @return the saved password policy DTO
     */
    @PostMapping(value = "/admin-password-policy")
    public AdminPasswordPolicyDto registerAdminPasswordPolicy(@Valid @RequestBody RegisterAdminPasswordPolicyReqDto req) {
        return adminPasswordPolicyManagementService.registerAdminPasswordPolicy(req);
    }
}

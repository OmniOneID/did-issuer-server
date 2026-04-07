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
package org.omnione.did.issuer.v1.admin.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.omnione.did.base.db.domain.AdminPasswordPolicy;
import org.omnione.did.base.db.repository.AdminPasswordPolicyRepository;
import org.omnione.did.base.exception.ErrorCode;
import org.omnione.did.base.exception.OpenDidException;
import org.omnione.did.issuer.v1.admin.dto.admin.AdminPasswordPolicyDto;
import org.omnione.did.issuer.v1.admin.dto.admin.RegisterAdminPasswordPolicyReqDto;
import org.springframework.stereotype.Service;

/**
 * Service for managing the admin password policy in the Admin Console.
 */
@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class AdminPasswordPolicyManagementService {

    private final AdminPasswordPolicyRepository adminPasswordPolicyRepository;

    /**
     * Retrieves the current admin password policy.
     *
     * @return the password policy DTO
     * @throws OpenDidException if no policy is found
     */
    public AdminPasswordPolicyDto findAdminPasswordPolicy() {
        AdminPasswordPolicy policy = adminPasswordPolicyRepository.findTop1ByOrderByIdAsc()
                .orElseThrow(() -> new OpenDidException(ErrorCode.ADMIN_INFO_NOT_FOUND));
        return AdminPasswordPolicyDto.fromAdminPasswordPolicy(policy);
    }

    /**
     * Registers or updates the admin password policy (upsert).
     *
     * @param req the request DTO containing policy settings
     * @return the saved password policy DTO
     */
    public AdminPasswordPolicyDto registerAdminPasswordPolicy(RegisterAdminPasswordPolicyReqDto req) {
        AdminPasswordPolicy policy = adminPasswordPolicyRepository.findTop1ByOrderByIdAsc()
                .orElse(AdminPasswordPolicy.builder().build());

        policy.setMinLength(req.getMinLength());
        policy.setRequireUppercase(req.getRequireUppercase());
        policy.setRequireNumber(req.getRequireNumber());
        policy.setRequireSpecial(req.getRequireSpecial());
        policy.setPasswordExpiryDays(req.getPasswordExpiryDays());

        AdminPasswordPolicy saved = adminPasswordPolicyRepository.save(policy);
        return AdminPasswordPolicyDto.fromAdminPasswordPolicy(saved);
    }
}

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
import org.omnione.did.base.db.constant.PasswordResetReason;
import org.omnione.did.base.db.domain.Admin;
import org.omnione.did.base.db.domain.AdminPasswordPolicy;
import org.omnione.did.base.db.repository.AdminPasswordPolicyRepository;
import org.omnione.did.base.db.repository.AdminRepository;
import org.omnione.did.issuer.v1.admin.dto.admin.AdminDto;
import org.omnione.did.issuer.v1.admin.dto.admin.RequestAdminLoginReqDto;
import org.omnione.did.issuer.v1.admin.service.query.AdminQueryService;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.temporal.ChronoUnit;

/**
 * Service for handling admin login sessions in the Admin Console.
 * <p>
 * Provides functionality for verifying login credentials and returning admin account details.
 */
@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class SessionService {

    private final AdminQueryService adminQueryService;
    private final AdminPasswordPolicyRepository adminPasswordPolicyRepository;
    private final AdminRepository adminRepository;

    /**
     * Authenticates an admin using login ID and password.
     * Also checks if the password has expired based on the password policy.
     *
     * @param requestAdminLoginReqDto DTO containing login credentials
     * @return authenticated admin as AdminDto
     */
    public AdminDto requestAdminLogin(RequestAdminLoginReqDto requestAdminLoginReqDto) {
        Admin admin = adminQueryService.findByLoginIdAndLoginPassword(
                requestAdminLoginReqDto.getLoginId(),
                requestAdminLoginReqDto.getLoginPassword()
        );

        // If already flagged for password reset and reason is not EXPIRED, return as-is
        if (Boolean.TRUE.equals(admin.getRequirePasswordReset())
                && admin.getPasswordResetReason() != null
                && admin.getPasswordResetReason() != PasswordResetReason.EXPIRED) {
            return AdminDto.fromAdmin(admin);
        }

        // Check password expiry
        adminPasswordPolicyRepository.findTop1ByOrderByIdAsc().ifPresent(policy -> {
            Instant lastChanged = admin.getLastPasswordChangedAt();
            if (lastChanged != null) {
                Instant expiryDate = lastChanged.plus(policy.getPasswordExpiryDays(), ChronoUnit.DAYS);
                if (Instant.now().isAfter(expiryDate)) {
                    admin.setRequirePasswordReset(true);
                    admin.setPasswordResetReason(PasswordResetReason.EXPIRED);
                    adminRepository.save(admin);
                }
            }
        });

        return AdminDto.fromAdmin(admin);
    }
}
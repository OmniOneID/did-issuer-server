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
import org.omnione.did.base.db.repository.AdminRepository;
import org.omnione.did.issuer.v1.admin.dto.admin.ChangeAdminIdAndPasswordReqDto;
import org.omnione.did.base.exception.ErrorCode;
import org.omnione.did.base.exception.OpenDidException;
import org.omnione.did.issuer.v1.admin.api.dto.EmptyResDto;
import org.omnione.did.issuer.v1.admin.dto.admin.AdminDto;
import org.omnione.did.issuer.v1.admin.dto.admin.ResetPasswordReqDto;
import org.omnione.did.issuer.v1.admin.dto.admin.RegisterAdminReqDto;
import org.omnione.did.issuer.v1.admin.dto.admin.ResetPasswordByRootReqDto;
import org.omnione.did.issuer.v1.admin.dto.admin.VerifyAdminIdUniqueResDto;
import org.omnione.did.issuer.v1.admin.service.query.AdminQueryService;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

/**
 * Service for managing administrator accounts in the Admin Console.
 * This includes functionality for registration, deletion, password reset,
 * login ID validation, and account lookup.
 */
@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class AdminManagementService {

    private final AdminQueryService adminQueryService;
    private final AdminRepository adminRepository;

    /**
     * Resets the password for an admin after validating the current password.
     *
     * @param resetPasswordReqDto request DTO containing loginId, old and new passwords
     * @return updated AdminDto
     */
    public AdminDto resetPassword(ResetPasswordReqDto resetPasswordReqDto) {
        Admin admin = adminQueryService.findByLoginIdAndLoginPassword(resetPasswordReqDto.getLoginId(), resetPasswordReqDto.getOldPassword());
        admin.setLoginPassword(resetPasswordReqDto.getNewPassword());
        admin.setRequirePasswordReset(false);
        admin.setPasswordResetReason(null);
        admin.setLastPasswordChangedAt(java.time.Instant.now());

        return AdminDto.fromAdmin(adminRepository.save(admin));
    }

    /**
     * Searches for admin users with optional search parameters and pagination.
     *
     * @param searchKey   the field to search on
     * @param searchValue the value to search for
     * @param pageable    pagination information
     * @return page of AdminDto
     */
    public PageImpl<AdminDto> searchAdmins(String searchKey, String searchValue, Pageable pageable) {
        return adminQueryService.searchAdminList(searchKey, searchValue, pageable);
    }

    /**
     * Finds an admin by ID.
     *
     * @param id the admin ID
     * @return the corresponding AdminDto
     */
    public AdminDto findById(Long id) {
        return AdminDto.fromAdmin(adminQueryService.findById(id));
    }

    /**
     * Registers a new admin account after validating uniqueness of login ID.
     *
     * @param registerAdminReqDto DTO with registration information
     * @return an empty response on success
     */
    public EmptyResDto registerAdmin(RegisterAdminReqDto registerAdminReqDto) {
        Admin existingAdmin = adminQueryService.findByLoginIdOrNull(registerAdminReqDto.getLoginId());
        if (existingAdmin != null) {
            throw new OpenDidException(ErrorCode.ADMIN_ALREADY_EXISTS);
        }

        // @TODO: Check if the role is valid
        // @TODO: createBy should be the logged in user
        Admin admin = Admin.builder()
                .loginId(registerAdminReqDto.getLoginId())
                .role(registerAdminReqDto.getRole())
                .loginPassword(registerAdminReqDto.getLoginPassword())
                .requirePasswordReset(true)
                .passwordResetReason(PasswordResetReason.FIRST_LOGIN)
                .emailVerified(false)
                .createdBy("SYSTEM")
                .build();

        adminRepository.save(admin);

        return new EmptyResDto();
    }

    /**
     * Verifies whether an admin login ID is unique.
     *
     * @param loginId the login ID to check
     * @return result indicating uniqueness
     */
    public VerifyAdminIdUniqueResDto verifyAdminIdUnique(String loginId) {
        long count = adminQueryService.countByLoginId(loginId);
        return VerifyAdminIdUniqueResDto.builder()
                .unique(count == 0)
                .build();
    }

    /**
     * Deletes an admin account by ID.
     *
     * @param id the ID of the admin to delete
     * @return an empty response on success
     */
    public EmptyResDto deleteAdmin(Long id) {
        adminQueryService.findById(id);
        adminRepository.deleteById(id);
        return new EmptyResDto();
    }

    /**
     * Resets an admin’s password directly by a root user.
     *
     * @param resetPasswordByRootReqDto DTO containing loginId and new password
     * @return an empty response on success
     */
    public EmptyResDto resetPasswordByRoot(ResetPasswordByRootReqDto resetPasswordByRootReqDto) {
        Admin admin = adminQueryService.findByLoginId(resetPasswordByRootReqDto.getLoginId());
        admin.setLoginPassword(resetPasswordByRootReqDto.getNewPassword());
        admin.setRequirePasswordReset(true);
        admin.setPasswordResetReason(PasswordResetReason.ADMIN_FORCED);

        adminRepository.save(admin);
        return new EmptyResDto();
    }

    /**
     * Changes both the login ID and password for an admin (used on first login).
     *
     * @param req DTO containing old/new login ID and old/new password
     * @return updated AdminDto
     */
    public AdminDto changeAdminIdAndPassword(ChangeAdminIdAndPasswordReqDto req) {
        Admin admin = adminQueryService.findByLoginIdAndLoginPassword(req.getOldLoginId(), req.getOldPassword());

        if (!req.getOldLoginId().equals(req.getNewLoginId())) {
            Admin existingWithNewId = adminQueryService.findByLoginIdOrNull(req.getNewLoginId());
            if (existingWithNewId != null) {
                throw new OpenDidException(ErrorCode.ADMIN_ALREADY_EXISTS);
            }
        } else {
            throw new OpenDidException(ErrorCode.ADMIN_ALREADY_EXISTS);
        }

        admin.setLoginId(req.getNewLoginId());
        admin.setLoginPassword(req.getNewPassword());
        admin.setRequirePasswordReset(false);
        admin.setPasswordResetReason(null);
        admin.setLastPasswordChangedAt(java.time.Instant.now());

        return AdminDto.fromAdmin(adminRepository.save(admin));
    }
}

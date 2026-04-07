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
package org.omnione.did.issuer.v1.admin.dto.admin;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.omnione.did.base.db.domain.AdminPasswordPolicy;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;

/**
 * DTO for admin password policy information.
 */
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor(force = true)
public class AdminPasswordPolicyDto {
    private final Long id;
    private final Short minLength;
    private final Boolean requireUppercase;
    private final Boolean requireNumber;
    private final Boolean requireSpecial;
    private final Short passwordExpiryDays;
    private final String createdAt;
    private final String updatedAt;

    public static AdminPasswordPolicyDto fromAdminPasswordPolicy(AdminPasswordPolicy policy) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

        return AdminPasswordPolicyDto.builder()
                .id(policy.getId())
                .minLength(policy.getMinLength())
                .requireUppercase(policy.getRequireUppercase())
                .requireNumber(policy.getRequireNumber())
                .requireSpecial(policy.getRequireSpecial())
                .passwordExpiryDays(policy.getPasswordExpiryDays())
                .createdAt(formatInstant(policy.getCreatedAt(), formatter))
                .updatedAt(formatInstant(policy.getUpdatedAt(), formatter))
                .build();
    }

    private static String formatInstant(Instant instant, DateTimeFormatter formatter) {
        if (instant == null) return null;
        return LocalDateTime.ofInstant(instant, ZoneId.systemDefault()).format(formatter);
    }
}

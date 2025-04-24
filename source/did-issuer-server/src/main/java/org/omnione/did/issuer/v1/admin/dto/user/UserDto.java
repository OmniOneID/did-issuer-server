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
package org.omnione.did.issuer.v1.admin.dto.user;

import lombok.Builder;
import lombok.Getter;
import org.omnione.did.base.db.domain.User;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;

/**
 * DTO representing user information in the Admin Console.
 */
@Getter
@Builder
public class UserDto {
    private Long id;
    private String pii;
    private String did;
    private Long vcSchemaId;
    private String vcSchemaName;
    private String data;
    private String createdAt;
    private String updatedAt;

    public static UserDto fromEntity(User user, String vcSchemaName) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

        return UserDto.builder()
                .id(user.getId())
                .pii(user.getPii())
                .did(user.getDid())
                .vcSchemaId(user.getVcSchemaId())
                .vcSchemaName(vcSchemaName)
                .data(user.getData())
                .createdAt(formatInstant(user.getCreatedAt(), formatter))
                .updatedAt(formatInstant(user.getUpdatedAt(), formatter))
                .build();
    }

    private static String formatInstant(Instant instant, DateTimeFormatter formatter) {
        if (instant == null) return null;
        return LocalDateTime.ofInstant(instant, ZoneId.systemDefault()).format(formatter);
    }
}

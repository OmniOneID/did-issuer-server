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
package org.omnione.did.issuer.v1.admin.dto.vc;

import lombok.Builder;
import lombok.Getter;
import org.omnione.did.base.db.domain.Vc;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;

/**
 * DTO representing issued VC information in the Admin Console.
 */
@Getter
@Builder
public class IssuedVcDto {
    private Long id;
    private String vcId;
    private String did;
    private String vcSchemaId;
    private String status;
    private String createdAt;
    private String updatedAt;

    public static IssuedVcDto fromEntity(Vc vc) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

        return IssuedVcDto.builder()
                .id(vc.getId())
                .vcId(vc.getVcId())
                .did(vc.getDid())
                .vcSchemaId(vc.getVcSchemaId())
                .status(vc.getStatus())
                .createdAt(formatInstant(vc.getCreatedAt(), formatter))
                .updatedAt(formatInstant(vc.getUpdatedAt(), formatter))
                .build();
    }

    private static String formatInstant(Instant instant, DateTimeFormatter formatter) {
        if (instant == null) return null;
        return LocalDateTime.ofInstant(instant, ZoneId.systemDefault()).format(formatter);
    }
}

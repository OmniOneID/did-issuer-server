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

import com.google.gson.annotations.Expose;
import lombok.Builder;
import lombok.Getter;
import org.omnione.did.base.db.domain.VcSchema;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;

/**
 * DTO representing Verifiable Credential (VC) schema information in the Admin Console.
 */
@Getter
@Builder
public class VcSchemaDto {
    @Expose
    private Long id;
    @Expose
    private String vcSchemaId;
    @Expose
    private String title;
    @Expose
    private String description;
    @Expose
    private String language;
    @Expose
    private String version;
    @Expose
    private String createdAt;
    @Expose
    private String updatedAt;

    public static VcSchemaDto fromEntity(VcSchema vcSchema) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

        return VcSchemaDto.builder()
                .id(vcSchema.getId())
                .vcSchemaId(vcSchema.getVcSchemaId())
                .title(vcSchema.getTitle())
                .description(vcSchema.getDescription())
                .language(vcSchema.getLanguage())
                .version(vcSchema.getVersion())
                .createdAt(formatInstant(vcSchema.getCreatedAt(), formatter))
                .updatedAt(formatInstant(vcSchema.getUpdatedAt(), formatter))
                .build();
    }

    private static String formatInstant(Instant instant, DateTimeFormatter formatter) {
        if (instant == null) return null;
        return LocalDateTime.ofInstant(instant, ZoneId.systemDefault()).format(formatter);
    }
}

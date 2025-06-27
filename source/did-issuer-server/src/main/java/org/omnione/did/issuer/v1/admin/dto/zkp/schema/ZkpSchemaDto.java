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
package org.omnione.did.issuer.v1.admin.dto.zkp.schema;

import lombok.Builder;
import lombok.Getter;
import org.omnione.did.base.db.constant.ZkpSchemaStatus;
import org.omnione.did.base.db.domain.ZkpSchema;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;

@Getter
@Builder
public class ZkpSchemaDto {
    private final Long id;
    private final String schemaId;
    private final String name;
    private final String tag;
    private final String version;
    private final Long definitionCount;
    private final ZkpSchemaStatus status;
    private final String createdAt;
    private final String updatedAt;

    public static ZkpSchemaDto fromEntity(ZkpSchema zkpSchema) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

        return ZkpSchemaDto.builder()
                .id(zkpSchema.getId())
                .schemaId(zkpSchema.getSchemaId())
                .name(zkpSchema.getName())
                .tag(zkpSchema.getTag())
                .version(zkpSchema.getVersion())
                .definitionCount(0L)
                .status(zkpSchema.getStatus())
                .createdAt(formatInstant(zkpSchema.getCreatedAt(), formatter))
                .updatedAt(formatInstant(zkpSchema.getUpdatedAt(), formatter))
                .build();
    }

    public static ZkpSchemaDto fromEntity(ZkpSchema zkpSchema, Long definitionCount) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

        return ZkpSchemaDto.builder()
                .id(zkpSchema.getId())
                .schemaId(zkpSchema.getSchemaId())
                .name(zkpSchema.getName())
                .tag(zkpSchema.getTag())
                .version(zkpSchema.getVersion())
                .definitionCount(definitionCount)
                .status(zkpSchema.getStatus())
                .createdAt(formatInstant(zkpSchema.getCreatedAt(), formatter))
                .updatedAt(formatInstant(zkpSchema.getUpdatedAt(), formatter))
                .build();
    }

    private static String formatInstant(Instant instant, DateTimeFormatter formatter) {
        if (instant == null) return null;
        return LocalDateTime.ofInstant(instant, ZoneId.systemDefault()).format(formatter);
    }

}

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
package org.omnione.did.issuer.v1.admin.dto.namespace;

import lombok.Builder;
import lombok.Getter;
import org.omnione.did.base.db.domain.Namespace;
import org.omnione.did.data.model.schema.SchemaClaims;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;

/**
 * DTO representing namespace information in the Admin Console.
 */
@Getter
@Builder
public class NamespaceDto {
    private final Long id;
    private final String namespaceId;
    private final String name;
    private final String ref;
    private final SchemaClaims schemaClaims;
    private final String createdAt;
    private final String updatedAt;
    private final int vcSchemaCount;

    public static NamespaceDto fromEntity(Namespace namespace) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

        return NamespaceDto.builder()
                .id(namespace.getId())
                .namespaceId(namespace.getNamespaceId())
                .name(namespace.getName())
                .ref(namespace.getRef())
                .schemaClaims(namespace.getSchemaClaims())
                .createdAt(formatInstant(namespace.getCreatedAt(), formatter))
                .updatedAt(formatInstant(namespace.getUpdatedAt(), formatter))
                .vcSchemaCount(0) // 기본값, 실제 값은 서비스에서 설정
                .build();
    }

    public static NamespaceDto fromEntityWithCount(Namespace namespace, int vcSchemaCount) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

        return NamespaceDto.builder()
                .id(namespace.getId())
                .namespaceId(namespace.getNamespaceId())
                .name(namespace.getName())
                .ref(namespace.getRef())
                .schemaClaims(namespace.getSchemaClaims())
                .createdAt(formatInstant(namespace.getCreatedAt(), formatter))
                .updatedAt(formatInstant(namespace.getUpdatedAt(), formatter))
                .vcSchemaCount(vcSchemaCount)
                .build();
    }

    private static String formatInstant(Instant instant, DateTimeFormatter formatter) {
        if (instant == null) return null;
        return LocalDateTime.ofInstant(instant, ZoneId.systemDefault()).format(formatter);
    }
}

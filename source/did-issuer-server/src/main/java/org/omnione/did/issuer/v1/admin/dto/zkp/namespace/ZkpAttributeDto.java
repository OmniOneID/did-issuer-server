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
package org.omnione.did.issuer.v1.admin.dto.zkp.namespace;

import lombok.Builder;
import lombok.Getter;
import org.omnione.did.base.db.domain.ZkpAttribute;
import org.omnione.did.zkp.datamodel.schema.AttributeDef;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;

@Getter
@Builder
public class ZkpAttributeDto {
    private final Long id;
    private final String label;
    private final AttributeDef.ATTR_TYPE type;
    private final String caption;
    private final Long zkpNamespaceId;
    private final String createdAt;
    private final String updatedAt;

    public static ZkpAttributeDto fromEntity(ZkpAttribute zkpAttribute) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        return ZkpAttributeDto.builder()
                .id(zkpAttribute.getId())
                .label(zkpAttribute.getLabel())
                .type(zkpAttribute.getType())
                .caption(zkpAttribute.getCaption())
                .zkpNamespaceId(zkpAttribute.getZkpNamespaceId())
                .createdAt(formatInstant(zkpAttribute.getCreatedAt(), formatter))
                .updatedAt(formatInstant(zkpAttribute.getUpdatedAt(), formatter))
                .build();
    }

    private static String formatInstant(Instant instant, DateTimeFormatter formatter) {
        if (instant == null) return null;
        return LocalDateTime.ofInstant(instant, ZoneId.systemDefault()).format(formatter);
    }
}

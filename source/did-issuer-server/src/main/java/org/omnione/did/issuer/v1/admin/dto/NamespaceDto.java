package org.omnione.did.issuer.v1.admin.dto;

import lombok.Builder;
import lombok.Getter;
import org.omnione.did.base.db.domain.Namespace;
import org.omnione.did.data.model.schema.SchemaClaims;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;

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
                .build();
    }

    private static String formatInstant(Instant instant, DateTimeFormatter formatter) {
        if (instant == null) return null;
        return LocalDateTime.ofInstant(instant, ZoneId.systemDefault()).format(formatter);
    }
}

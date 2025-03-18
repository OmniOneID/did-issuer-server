package org.omnione.did.issuer.v1.admin.dto;

import jakarta.persistence.Column;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.Builder;
import lombok.Getter;
import org.omnione.did.base.db.domain.Namespace;
import org.omnione.did.base.db.domain.VcSchema;
import org.omnione.did.data.model.schema.SchemaClaims;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;

@Getter
@Builder
public class VcSchemaDto {
    private Long id;
    private String vcSchemaId;
    private String title;
    private String description;
    private String language;
    private String version;
    private String createdAt;
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

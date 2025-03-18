package org.omnione.did.issuer.v1.admin.dto;

import jakarta.persistence.Column;
import lombok.Builder;
import lombok.Getter;
import org.omnione.did.base.db.domain.User;
import org.omnione.did.base.db.domain.VcSchema;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;

/**
 * Description...
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

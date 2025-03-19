package org.omnione.did.issuer.v1.admin.dto;

import lombok.Builder;
import lombok.Getter;
import org.omnione.did.base.db.domain.Vc;
import org.omnione.did.data.model.enums.vc.VcStatus;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;

/**
 * Description...
 */
@Getter
@Builder
public class IssuedVcDto {
    private Long id;
    private String vcId;
    private String did;
    private String vcSchemaName;
    private String status;
    private String createdAt;
    private String updatedAt;

    public static IssuedVcDto fromEntity(Vc vc) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

        return IssuedVcDto.builder()
                .id(vc.getId())
                .vcId(vc.getVcId())
                .did(vc.getDid())
//                .vcSchemaName(vcSchemaName)
                .createdAt(formatInstant(vc.getCreatedAt(), formatter))
                .build();
    }
    public static IssuedVcDto fromEntity(Vc vc, String vcSchemaName, String status) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

        return IssuedVcDto.builder()
                .id(vc.getId())
                .vcId(vc.getVcId())
                .did(vc.getDid())
                .vcSchemaName(vcSchemaName)
                .status(status)
                .createdAt(formatInstant(vc.getCreatedAt(), formatter))
                .updatedAt(formatInstant(vc.getUpdatedAt(), formatter))
                .build();
    }

    private static String formatInstant(Instant instant, DateTimeFormatter formatter) {
        if (instant == null) return null;
        return LocalDateTime.ofInstant(instant, ZoneId.systemDefault()).format(formatter);
    }
}

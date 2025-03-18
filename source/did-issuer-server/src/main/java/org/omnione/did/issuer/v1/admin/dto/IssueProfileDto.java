package org.omnione.did.issuer.v1.admin.dto;

import lombok.Builder;
import lombok.Getter;
import org.omnione.did.base.datamodel.enums.InitiateType;
import org.omnione.did.base.db.domain.IssueProfile;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.List;

/**
 * Description...
 */
@Getter
@Builder
public class IssueProfileDto {
    private Long id;
    private String vcSchemaId;
    private String vcPlanId;
    private String title;
    private String description;
    private List<String> endpoints;
    private String cipher;
    private String curve;
    private String padding;
    private String language;
    private InitiateType initiateType = InitiateType.USER_INIT;
    private final String createdAt;
    private final String updatedAt;

    public static IssueProfileDto fromEntity(IssueProfile issueProfile, String vcSchemaId) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

        return IssueProfileDto.builder()
                .id(issueProfile.getId())
                .vcSchemaId(vcSchemaId)
                .vcPlanId(issueProfile.getVcPlanId())
                .title(issueProfile.getTitle())
                .description(issueProfile.getDescription())
                .endpoints(issueProfile.getEndpoints())
                .cipher(issueProfile.getCipher())
                .curve(issueProfile.getCurve())
                .padding(issueProfile.getPadding())
                .language(issueProfile.getLanguage())
                .initiateType(issueProfile.getInitiateType())
                .createdAt(formatInstant(issueProfile.getCreatedAt(), formatter))
                .updatedAt(formatInstant(issueProfile.getUpdatedAt(), formatter))
                .build();
    }

    private static String formatInstant(Instant instant, DateTimeFormatter formatter) {
        if (instant == null) return null;
        return LocalDateTime.ofInstant(instant, ZoneId.systemDefault()).format(formatter);
    }
}

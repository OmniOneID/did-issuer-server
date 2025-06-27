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
package org.omnione.did.issuer.v1.admin.dto.profile;

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
 * DTO representing issue profile information in the Admin Console.
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
    private InitiateType initiateType;
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

package org.omnione.did.issuer.v1.admin.api.dto;

import lombok.*;
import org.omnione.did.base.db.domain.IssueProfile;

/**
 * Description...
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
@Builder
public class RegisterVcPlanReqDto {
    private String vcPlan;
    private String issuerDid;
}

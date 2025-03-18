package org.omnione.did.issuer.v1.admin.api.dto;

import lombok.*;

/**
 * Description...
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
@Builder
public class DeleteIssuePlanIdReqDto {
    private String vcPlan;
    private String issuerDid;
}

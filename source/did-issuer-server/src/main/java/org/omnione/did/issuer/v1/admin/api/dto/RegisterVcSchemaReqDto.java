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
public class RegisterVcSchemaReqDto {
    private String vcSchema;
    private String issuerDid;
}

package org.omnione.did.issuer.v1.admin.dto.issuer;

import lombok.*;
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
@Builder
public class SendCertificateVcReqDto {
    private String certificateVc;
}


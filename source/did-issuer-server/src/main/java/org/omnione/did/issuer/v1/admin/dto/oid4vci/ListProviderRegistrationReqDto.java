package org.omnione.did.issuer.v1.admin.dto.oid4vci;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ListProviderRegistrationReqDto {
    private String credentialIssuer;
    private String credentialIssuerMetadataUri;
    private String userInitiationUri;
}

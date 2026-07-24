package org.omnione.did.issuer.v1.admin.dto.oid4vci;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.Instant;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ListProviderRegistrationDto {
    private Long registrationId;
    private String status;
    private String listProviderUrl;
    private String credentialIssuer;
    private String credentialIssuerMetadataUri;
    private String userInitiationUri;
    private Instant requestedAt;
}

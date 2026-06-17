package org.omnione.did.base.datamodel.data.oid4vci;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.*;

import java.util.List;
import java.util.Map;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@JsonIgnoreProperties(ignoreUnknown = true)
public class IssuerMetadata {
    private String credentialIssuer;
    private List<String> authorizationServer;
    private String credentialOfferEndpoint;
    private String credentialEndpoint;
    private String nonceEndpoint;
    private String deferredCredentialEndpoint;
    private String notificationEndpoint;
    private CredentialRequestEncryption credentialRequestEncryption;
    private CredentialResponseEncryption credentialResponseEncryption;
    private Map<String, Object> credentialConfigurationsSupported;

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class CredentialRequestEncryption {
        private List<String> encValuesSupported;
        private boolean encryptionRequired;
    }

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class CredentialResponseEncryption {
        private List<String> algValuesSupported;
        private List<String> encValuesSupported;
        private boolean encryptionRequired;
    }
}

package org.omnione.did.base.datamodel.data.oid4vci.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.gson.annotations.SerializedName;
import lombok.*;

import java.util.List;
import java.util.Map;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class Oid4vciMetadataResponse {
    @JsonProperty("credential_issuer")
    @SerializedName("credential_issuer")
    private String credentialIssuer;

    @JsonProperty("authorization_servers")
    @SerializedName("authorization_servers")
    private List<String> authorizationServers;

    @JsonProperty("credential_offer_endpoint")
    @SerializedName("credential_offer_endpoint")
    private String credentialOfferEndpoint;

    @JsonProperty("credential_endpoint")
    @SerializedName("credential_endpoint")
    private String credentialEndpoint;

    @JsonProperty("nonce_endpoint")
    @SerializedName("nonce_endpoint")
    private String nonceEndpoint;

    @JsonProperty("deferred_credential_endpoint")
    @SerializedName("deferred_credential_endpoint")
    private String deferredCredentialEndpoint;

    @JsonProperty("notification_endpoint")
    @SerializedName("notification_endpoint")
    private String notificationEndpoint;

    @JsonProperty("credential_request_encryption")
    @SerializedName("credential_request_encryption")
    private CredentialRequestEncryptionResponse credentialRequestEncryption;

    @JsonProperty("credential_response_encryption")
    @SerializedName("credential_response_encryption")
    private CredentialResponseEncryptionResponse credentialResponseEncryption;

    @JsonProperty("credential_configurations_supported")
    @SerializedName("credential_configurations_supported")
    private Map<String, Object> credentialConfigurationsSupported;

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public static class CredentialRequestEncryptionResponse {
        @JsonProperty("enc_values_supported")
        @SerializedName("enc_values_supported")
        private List<String> encValuesSupported;

        @JsonProperty("encryption_required")
        @SerializedName("encryption_required")
        private boolean encryptionRequired;
    }

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public static class CredentialResponseEncryptionResponse {
        @JsonProperty("alg_values_supported")
        @SerializedName("alg_values_supported")
        private List<String> algValuesSupported;

        @JsonProperty("enc_values_supported")
        @SerializedName("enc_values_supported")
        private List<String> encValuesSupported;

        @JsonProperty("encryption_required")
        @SerializedName("encryption_required")
        private boolean encryptionRequired;
    }
}

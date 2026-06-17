package org.omnione.did.base.datamodel.data.oid4vci;

import lombok.*;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Oid4vcProperty {
    private String issuerUrl;
    private Clients clients;

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class Clients {
        private String clientId;
        private String clientSecret;
        private String redirectUrl;
        private List<String> redirectUris;
        private List<String> scopes;
        private ClientPlatform android;
        private ClientPlatform ios;
        private ClientPlatform androidOpenid;
        private ClientPlatform iosOpenid;
        private IssuerServer issuerServer;
    }

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class ClientPlatform {
        private String clientId;
        private List<String> redirectUris;
        private List<String> scopes;
    }

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class IssuerServer {
        private String url;
    }
}

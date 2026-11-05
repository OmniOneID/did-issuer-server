package org.omnione.did.base.config;

import org.junit.jupiter.api.Test;
import org.omnione.did.base.datamodel.data.oid4vci.Oid4vcProperty;
import org.omnione.did.issuer.v1.admin.service.Oid4vciConfigService;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.security.oauth2.server.authorization.client.RegisteredClient;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class Oid4vcAuthServerConfigTest {

    @Test
    void refreshesMainAndPlatformClientsWithoutRecreatingTheRepository() throws Exception {
        Oid4vciConfigService service = mock(Oid4vciConfigService.class);
        when(service.getProperties()).thenReturn(properties("old-client", "old-scope"),
                properties("new-client", "new-scope"));
        Oid4vcAuthServerConfig config = new Oid4vcAuthServerConfig(service);
        RefreshableRegisteredClientRepository repository =
                (RefreshableRegisteredClientRepository) config.registeredClientRepository();

        assertThat(repository.findByClientId("old-client")).isNotNull();

        try (AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext()) {
            context.registerBean(RefreshableRegisteredClientRepository.class, () -> repository);
            context.refresh();
            context.publishEvent(new Oid4vciConfigService.PropertiesUpdatedEvent());
        }

        assertThat(repository.findByClientId("old-client")).isNull();
        RegisteredClient current = repository.findByClientId("new-client");
        assertThat(current.getRedirectUris()).contains("https://issuer.example.com/auth/callback");
        assertThat(current.getScopes()).contains("new-scope");
        assertThat(repository.findByClientId("oid4vci-android").getScopes()).contains("new-scope");
    }

    private Oid4vcProperty properties(String clientId, String scope) {
        return Oid4vcProperty.builder()
                .issuerUrl("https://issuer.example.com")
                .clients(Oid4vcProperty.Clients.builder()
                        .clientId(clientId)
                        .clientSecret("{noop}test-secret")
                        .redirectUrl("https://issuer.example.com/auth/callback")
                        .scopes(List.of("openid", scope))
                        .android(Oid4vcProperty.ClientPlatform.builder()
                                .clientId("oid4vci-android")
                                .redirectUris(List.of("org.omnione.did.sdk.oid4vc://callback"))
                                .scopes(List.of("openid", scope))
                                .build())
                        .build())
                .build();
    }
}

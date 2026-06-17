package org.omnione.did.base.config;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.omnione.did.base.datamodel.data.oid4vci.Oid4vcProperty;
import org.omnione.did.issuer.v1.admin.service.Oid4vciConfigService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.oauth2.core.AuthorizationGrantType;
import org.springframework.security.oauth2.core.ClientAuthenticationMethod;
import org.springframework.security.oauth2.server.authorization.client.InMemoryRegisteredClientRepository;
import org.springframework.security.oauth2.server.authorization.client.RegisteredClient;
import org.springframework.security.oauth2.server.authorization.client.RegisteredClientRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Configuration for the OID4VC Authorization Server.
 * This class provides a RegisteredClientRepository bean that loads client information from the database.
 */
@Slf4j
@RequiredArgsConstructor
@Configuration
public class Oid4vcAuthServerConfig {

    private final Oid4vciConfigService configService;

    @Bean
    public RegisteredClientRepository registeredClientRepository() {
        try {
            Oid4vcProperty properties = configService.getProperties();
            Oid4vcProperty.Clients configClients = properties.getClients();

            List<RegisteredClient> registeredClients = new ArrayList<>();

            if (configClients != null) {
                // issuer-server client
                String clientId = configClients.getClientId();
                if (clientId == null || clientId.isEmpty()) {
                    clientId = "issuer-server";
                }

                RegisteredClient issuerClient = RegisteredClient.withId(UUID.randomUUID().toString())
                        .clientId(clientId)
                        .clientSecret(configClients.getClientSecret() != null ? configClients.getClientSecret() : "{noop}secret")
                        .clientAuthenticationMethod(ClientAuthenticationMethod.CLIENT_SECRET_BASIC)
                        .clientAuthenticationMethod(ClientAuthenticationMethod.CLIENT_SECRET_POST)
                        .authorizationGrantType(AuthorizationGrantType.AUTHORIZATION_CODE)
                        .authorizationGrantType(AuthorizationGrantType.REFRESH_TOKEN)
                        .authorizationGrantType(new AuthorizationGrantType("urn:ietf:params:oauth:grant-type:pre-authorized_code"))
                        .redirectUri(configClients.getRedirectUrl() != null ? configClients.getRedirectUrl() : properties.getIssuerUrl() + "/auth/callback")
                        .scope("openid")
                        .build();

                registeredClients.add(issuerClient);
                log.info("Registered client loaded from DB: {}", clientId);
            } else {
                log.warn("No client configuration found in DB, using default 'issuer-server' client.");
                RegisteredClient devClient = RegisteredClient.withId(UUID.randomUUID().toString())
                        .clientId("issuer-server")
                        .clientSecret("{noop}secret")
                        .clientAuthenticationMethod(ClientAuthenticationMethod.CLIENT_SECRET_BASIC)
                        .authorizationGrantType(AuthorizationGrantType.AUTHORIZATION_CODE)
                        .authorizationGrantType(AuthorizationGrantType.REFRESH_TOKEN)
                        .authorizationGrantType(new AuthorizationGrantType("urn:ietf:params:oauth:grant-type:pre-authorized_code"))
                        .redirectUri("http://localhost:8091/auth/callback")
                        .scope("openid")
                        .build();
                registeredClients.add(devClient);
            }

            return new InMemoryRegisteredClientRepository(registeredClients);
        } catch (Exception e) {
            log.error("Failed to load RegisteredClientRepository from DB, returning default repository: {}", e.getMessage());
            RegisteredClient fallbackClient = RegisteredClient.withId(UUID.randomUUID().toString())
                    .clientId("issuer-server")
                    .clientSecret("{noop}secret")
                    .clientAuthenticationMethod(ClientAuthenticationMethod.CLIENT_SECRET_BASIC)
                    .authorizationGrantType(AuthorizationGrantType.AUTHORIZATION_CODE)
                    .redirectUri("http://localhost:8091/auth/callback")
                    .scope("openid")
                    .build();
            return new InMemoryRegisteredClientRepository(List.of(fallbackClient));
        }
    }

    @Bean
    public org.springframework.security.oauth2.client.registration.ClientRegistrationRepository clientRegistrationRepository() {
        org.springframework.security.oauth2.client.registration.ClientRegistration clientRegistration =
                org.springframework.security.oauth2.client.registration.ClientRegistration.withRegistrationId("dummy")
                        .clientId("dummy")
                        .clientSecret("dummy")
                        .authorizationGrantType(org.springframework.security.oauth2.core.AuthorizationGrantType.AUTHORIZATION_CODE)
                        .redirectUri("{baseUrl}/login/oauth2/code/{registrationId}")
                        .authorizationUri("https://dummy.com/oauth2/authorize")
                        .tokenUri("https://dummy.com/oauth2/token")
                        .build();
        return new org.springframework.security.oauth2.client.registration.InMemoryClientRegistrationRepository(clientRegistration);
    }
}

package org.omnione.did.base.config;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.omnione.did.base.datamodel.data.oid4vci.Oid4vcProperty;
import org.omnione.did.issuer.v1.admin.service.Oid4vciConfigService;
import org.omnione.did.oid4vc.authorization.authorization.oid4vci.CustomTokenResponseHandler;
import org.omnione.did.oid4vc.authorization.authorization.oid4vci.preauthorized.grant.PreAuthorizedCodeGrantAuthenticationConverter;
import org.omnione.did.oid4vc.authorization.authorization.oid4vci.preauthorized.grant.PreAuthorizedCodeGrantAuthenticationProvider;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.oauth2.core.AuthorizationGrantType;
import org.springframework.security.oauth2.core.ClientAuthenticationMethod;
import org.springframework.security.oauth2.server.authorization.authentication.OAuth2ClientAuthenticationToken;
import org.springframework.security.oauth2.server.authorization.client.InMemoryRegisteredClientRepository;
import org.springframework.security.oauth2.server.authorization.client.RegisteredClient;
import org.springframework.security.oauth2.server.authorization.client.RegisteredClientRepository;
import org.springframework.security.oauth2.server.authorization.config.annotation.web.configuration.OAuth2AuthorizationServerConfiguration;
import org.springframework.security.oauth2.server.authorization.config.annotation.web.configurers.OAuth2AuthorizationServerConfigurer;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.AuthenticationConverter;

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

    private static final String PRE_AUTHORIZED_CODE_GRANT_TYPE = "urn:ietf:params:oauth:grant-type:pre-authorized_code";
    private static final String DEFAULT_CLIENT_ID = "issuer-server";

    private final Oid4vciConfigService configService;

    @Bean
    @Order(1)
    public SecurityFilterChain authorizationServerSecurityFilterChain(
            HttpSecurity http,
            RegisteredClientRepository registeredClientRepository,
            PreAuthorizedCodeGrantAuthenticationProvider preAuthorizedCodeGrantAuthenticationProvider,
            CustomTokenResponseHandler customTokenResponseHandler) throws Exception {
        OAuth2AuthorizationServerConfiguration.applyDefaultSecurity(http);

        http.getConfigurer(OAuth2AuthorizationServerConfigurer.class)
                .clientAuthentication(clientAuthentication -> clientAuthentication
                        .authenticationConverters(converters -> converters.add(0, unauthenticatedPreAuthorizedCodeClientConverter()))
                        .authenticationProvider(unauthenticatedPreAuthorizedCodeClientProvider(registeredClientRepository)))
                .tokenEndpoint(tokenEndpoint -> tokenEndpoint
                        .accessTokenRequestConverter(new PreAuthorizedCodeGrantAuthenticationConverter())
                        .authenticationProvider(preAuthorizedCodeGrantAuthenticationProvider)
                        .accessTokenResponseHandler(customTokenResponseHandler));

        return http.build();
    }

    @Bean
    public RegisteredClientRepository registeredClientRepository() {
        try {
            Oid4vcProperty properties = configService.getProperties();
            Oid4vcProperty.Clients configClients = properties.getClients();

            List<RegisteredClient> registeredClients = new ArrayList<>();

            if (configClients != null) {
                // issuer-server client
                String clientId = resolveClientId(configClients);

                RegisteredClient issuerClient = RegisteredClient.withId(UUID.randomUUID().toString())
                        .clientId(clientId)
                        .clientSecret(configClients.getClientSecret() != null ? configClients.getClientSecret() : "{noop}secret")
                        .clientAuthenticationMethod(ClientAuthenticationMethod.NONE)
                        .clientAuthenticationMethod(ClientAuthenticationMethod.CLIENT_SECRET_BASIC)
                        .clientAuthenticationMethod(ClientAuthenticationMethod.CLIENT_SECRET_POST)
                        .authorizationGrantType(AuthorizationGrantType.AUTHORIZATION_CODE)
                        .authorizationGrantType(AuthorizationGrantType.REFRESH_TOKEN)
                        .authorizationGrantType(new AuthorizationGrantType(PRE_AUTHORIZED_CODE_GRANT_TYPE))
                        .redirectUri(configClients.getRedirectUrl() != null ? configClients.getRedirectUrl() : properties.getIssuerUrl() + "/auth/callback")
                        .scope("openid")
                        .build();

                registeredClients.add(issuerClient);
                log.info("Registered client loaded from DB: {}", clientId);
            } else {
                log.warn("No client configuration found in DB, using default 'issuer-server' client.");
                RegisteredClient devClient = RegisteredClient.withId(UUID.randomUUID().toString())
                        .clientId(DEFAULT_CLIENT_ID)
                        .clientSecret("{noop}secret")
                        .clientAuthenticationMethod(ClientAuthenticationMethod.NONE)
                        .clientAuthenticationMethod(ClientAuthenticationMethod.CLIENT_SECRET_BASIC)
                        .authorizationGrantType(AuthorizationGrantType.AUTHORIZATION_CODE)
                        .authorizationGrantType(AuthorizationGrantType.REFRESH_TOKEN)
                        .authorizationGrantType(new AuthorizationGrantType(PRE_AUTHORIZED_CODE_GRANT_TYPE))
                        .redirectUri("http://localhost:8091/auth/callback")
                        .scope("openid")
                        .build();
                registeredClients.add(devClient);
            }

            return new InMemoryRegisteredClientRepository(registeredClients);
        } catch (Exception e) {
            log.error("Failed to load RegisteredClientRepository from DB, returning default repository: {}", e.getMessage());
            RegisteredClient fallbackClient = RegisteredClient.withId(UUID.randomUUID().toString())
                    .clientId(DEFAULT_CLIENT_ID)
                    .clientSecret("{noop}secret")
                    .clientAuthenticationMethod(ClientAuthenticationMethod.NONE)
                    .clientAuthenticationMethod(ClientAuthenticationMethod.CLIENT_SECRET_BASIC)
                    .authorizationGrantType(AuthorizationGrantType.AUTHORIZATION_CODE)
                    .authorizationGrantType(new AuthorizationGrantType(PRE_AUTHORIZED_CODE_GRANT_TYPE))
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

    private AuthenticationConverter unauthenticatedPreAuthorizedCodeClientConverter() {
        return request -> {
            if (!PRE_AUTHORIZED_CODE_GRANT_TYPE.equals(request.getParameter("grant_type"))) {
                return null;
            }

            boolean hasAuthorizationHeader = hasText(request.getHeader("Authorization"));
            boolean hasClientId = hasText(request.getParameter("client_id"));
            boolean hasClientSecret = hasText(request.getParameter("client_secret"));
            boolean hasClientAssertion = hasText(request.getParameter("client_assertion"));
            boolean hasClientAssertionType = hasText(request.getParameter("client_assertion_type"));

            if (hasText(request.getHeader("Authorization"))
                    || hasText(request.getParameter("client_id"))
                    || hasText(request.getParameter("client_secret"))
                    || hasText(request.getParameter("client_assertion"))
                    || hasText(request.getParameter("client_assertion_type"))) {
                log.info("Pre-authorized token request uses explicit client authentication. "
                                + "authorizationHeaderPresent={}, clientIdPresent={}, clientSecretPresent={}, "
                                + "clientAssertionPresent={}, clientAssertionTypePresent={}",
                        hasAuthorizationHeader,
                        hasClientId,
                        hasClientSecret,
                        hasClientAssertion,
                        hasClientAssertionType);
                return null;
            }

            String clientId = resolveConfiguredClientId();
            log.info("Pre-authorized token request has no client credentials. Resolving NONE client_id={}", clientId);

            return new OAuth2ClientAuthenticationToken(
                    clientId,
                    ClientAuthenticationMethod.NONE,
                    null,
                    java.util.Collections.emptyMap());
        };
    }

    private AuthenticationProvider unauthenticatedPreAuthorizedCodeClientProvider(
            RegisteredClientRepository registeredClientRepository) {
        return new AuthenticationProvider() {
            @Override
            public Authentication authenticate(Authentication authentication) throws AuthenticationException {
                OAuth2ClientAuthenticationToken clientAuthentication =
                        (OAuth2ClientAuthenticationToken) authentication;

                if (!ClientAuthenticationMethod.NONE.equals(clientAuthentication.getClientAuthenticationMethod())) {
                    return null;
                }

                String clientId = clientAuthentication.getPrincipal().toString();
                RegisteredClient registeredClient = registeredClientRepository.findByClientId(clientId);
                if (registeredClient == null) {
                    log.warn("Pre-authorized NONE client authentication failed: registered client not found. client_id={}", clientId);
                    return null;
                }

                if (!registeredClient.getClientAuthenticationMethods().contains(ClientAuthenticationMethod.NONE)) {
                    log.warn("Pre-authorized NONE client authentication failed: client does not support NONE. client_id={}, methods={}",
                            clientId, registeredClient.getClientAuthenticationMethods());
                    return null;
                }

                log.info("Pre-authorized NONE client authentication succeeded. client_id={}", clientId);
                return new OAuth2ClientAuthenticationToken(
                        registeredClient,
                        clientAuthentication.getClientAuthenticationMethod(),
                        null);
            }

            @Override
            public boolean supports(Class<?> authentication) {
                return OAuth2ClientAuthenticationToken.class.isAssignableFrom(authentication);
            }
        };
    }

    private String resolveConfiguredClientId() {
        try {
            Oid4vcProperty properties = configService.getProperties();
            if (properties != null && properties.getClients() != null) {
                return resolveClientId(properties.getClients());
            }
        } catch (Exception e) {
            log.warn("Failed to resolve OID4VC client_id for unauthenticated token request. Using default client_id: {}", DEFAULT_CLIENT_ID);
        }
        return DEFAULT_CLIENT_ID;
    }

    private String resolveClientId(Oid4vcProperty.Clients clients) {
        String clientId = clients.getClientId();
        return hasText(clientId) ? clientId : DEFAULT_CLIENT_ID;
    }

    private boolean hasText(String value) {
        return value != null && !value.isBlank();
    }
}

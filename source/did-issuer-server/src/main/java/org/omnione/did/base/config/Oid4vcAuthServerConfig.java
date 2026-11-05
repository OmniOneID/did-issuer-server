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
import org.springframework.security.oauth2.server.authorization.client.RegisteredClient;
import org.springframework.security.oauth2.server.authorization.client.RegisteredClientRepository;
import org.springframework.security.oauth2.server.authorization.config.annotation.web.configuration.OAuth2AuthorizationServerConfiguration;
import org.springframework.security.oauth2.server.authorization.config.annotation.web.configurers.OAuth2AuthorizationServerConfigurer;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.AuthenticationConverter;

import java.util.ArrayList;
import java.util.List;

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
    public RefreshableRegisteredClientRepository registeredClientRepository() {
        try {
            RefreshableRegisteredClientRepository repository =
                    new RefreshableRegisteredClientRepository(configService, this::buildRegisteredClients);
            repository.refresh(configService.getProperties());
            return repository;
        } catch (Exception e) {
            throw new IllegalStateException("Failed to load OID4VCI registered clients", e);
        }
    }

    private List<RegisteredClient> buildRegisteredClients(Oid4vcProperty properties) {
        if (properties == null || properties.getClients() == null) {
            throw new IllegalArgumentException("OID4VCI client properties are missing");
        }
        Oid4vcProperty.Clients clients = properties.getClients();
        RegisteredClient.Builder main = RegisteredClient.withId("oid4vci-main")
                .clientId(resolveClientId(clients))
                .clientSecret(clients.getClientSecret())
                .clientAuthenticationMethod(ClientAuthenticationMethod.NONE)
                .clientAuthenticationMethod(ClientAuthenticationMethod.CLIENT_SECRET_BASIC)
                .clientAuthenticationMethod(ClientAuthenticationMethod.CLIENT_SECRET_POST)
                .authorizationGrantType(AuthorizationGrantType.AUTHORIZATION_CODE)
                .authorizationGrantType(AuthorizationGrantType.REFRESH_TOKEN)
                .authorizationGrantType(new AuthorizationGrantType(PRE_AUTHORIZED_CODE_GRANT_TYPE));
        addValues(clients.getRedirectUris(), main::redirectUri);
        if (hasText(clients.getRedirectUrl())) {
            main.redirectUri(clients.getRedirectUrl());
        }
        addValues(clients.getScopes(), main::scope);
        if (clients.getScopes() == null || clients.getScopes().isEmpty()) {
            main.scope("openid");
        }

        List<RegisteredClient> registeredClients = new ArrayList<>();
        registeredClients.add(main.build());
        addPlatformClient(registeredClients, "android", clients.getAndroid());
        addPlatformClient(registeredClients, "android-openid", clients.getAndroidOpenid());
        addPlatformClient(registeredClients, "ios", clients.getIos());
        addPlatformClient(registeredClients, "ios-openid", clients.getIosOpenid());
        return registeredClients;
    }

    private void addPlatformClient(List<RegisteredClient> registeredClients, String key,
                                   Oid4vcProperty.ClientPlatform platform) {
        if (platform == null || !hasText(platform.getClientId())) {
            return;
        }
        RegisteredClient.Builder client = RegisteredClient.withId("oid4vci-" + key)
                .clientId(platform.getClientId())
                .clientAuthenticationMethod(ClientAuthenticationMethod.NONE)
                .clientAuthenticationMethod(new ClientAuthenticationMethod(
                        "urn:ietf:params:oauth:client-assertion-type:jwt-client-attestation"))
                .authorizationGrantType(AuthorizationGrantType.AUTHORIZATION_CODE)
                .authorizationGrantType(new AuthorizationGrantType(PRE_AUTHORIZED_CODE_GRANT_TYPE));
        addValues(platform.getRedirectUris(), client::redirectUri);
        addValues(platform.getScopes(), client::scope);
        if (platform.getScopes() == null || platform.getScopes().isEmpty()) {
            client.scope("openid");
        }
        registeredClients.add(client.build());
    }

    private void addValues(List<String> values, java.util.function.Consumer<String> add) {
        if (values != null) {
            values.stream().filter(this::hasText).forEach(add);
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

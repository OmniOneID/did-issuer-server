/*
 * Copyright 2024 OmniOne.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.omnione.did.base.config;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.annotation.web.configurers.AuthorizeHttpRequestsConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.DelegatingPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import java.util.HashMap;
import java.util.Map;


/**
 * The SecurityConfig class provides methods for configuring security settings.
 * This class configures the security settings for the application, such as CSRF, form login, HTTP basic authentication,
 * and custom authorization for specific endpoints.
 */
@RequiredArgsConstructor
@Slf4j
@Configuration
@EnableWebSecurity
public class SecurityConfig {
    static {
    }

    private final JwtAuthenticationFilter jwtFilter;

    /**
     * Configures the security filter chain that applies to all HTTP requests.
     * This method disables CSRF protection, basic authentication, form login, and logout functionalities,
     * and it customizes authorization for specific endpoints.
     *
     * @return the configured SecurityFilterChain instance
     * @throws Exception if an error occurs while configuring security
     */
    @Bean
    @org.springframework.core.annotation.Order(2)
    public SecurityFilterChain oid4vciSecurityFilterChain(HttpSecurity http) throws Exception {
        return http
                .securityMatcher("/oid4vci/credential/**", "/oid4vci/credential-offer/**",
                        "/oid4vci/deferred_credential", "/oid4vci/nonce",
                        "/oid4vci/notification", "/.well-known/**",
                        "/qr-data/**", "/oid4vci/**",
                        "/get-credential-identifier", "/metadata-page",
                        "/api/metadata/**", "/claims-page", "/api/claims/**",
                        "/credential/**", "/credential-offer/**", 
                        "/deferred_credential", "/nonce", "/notification")
                .csrf(AbstractHttpConfigurer::disable)
                .sessionManagement(sm -> sm.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(authorize -> authorize
                        .requestMatchers("/credential/**").authenticated()
                        .anyRequest().permitAll())
                .oauth2ResourceServer(oauth2 -> oauth2.jwt(Customizer.withDefaults()))
                .build();
    }

    @Bean
    @org.springframework.core.annotation.Order(3)
    public SecurityFilterChain adminSecurityFilterChain(HttpSecurity http) throws Exception {
        return http
                .csrf(AbstractHttpConfigurer::disable)
                .sessionManagement(sm -> sm.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(this::authorizeHttpRequestsCustomizer)
                .addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class)
                .build();
    }


    /**
     * Customizes authorization for HTTP requests.
     * This method configures which requests are permitted and which require authorization.
     *
     * @param configurer the AuthorizationManagerRequestMatcherRegistry used to configure authorization rules
     */
    private void authorizeHttpRequestsCustomizer(AuthorizeHttpRequestsConfigurer<HttpSecurity>
                                                         .AuthorizationManagerRequestMatcherRegistry configurer) {
        allowedUrlsConfigurer(configurer);
        configurer.anyRequest().permitAll();
    }

    private void allowedUrlsConfigurer(AuthorizeHttpRequestsConfigurer<HttpSecurity>
                                               .AuthorizationManagerRequestMatcherRegistry configurer) {
        // Allowed API
//        configurer
//            .requestMatchers(HttpMethod.GET, UrlConstant.Issuer.V1)
//                .permitAll();
    }


    @Bean
    public PasswordEncoder passwordEncoder() {
        String idForEncode = "bcrypt";
        Map<String, PasswordEncoder> encoders = new HashMap<>();

        encoders.put("bcrypt", new BCryptPasswordEncoder(12));

        DelegatingPasswordEncoder passwordEncoder = new DelegatingPasswordEncoder(idForEncode, encoders);
        @SuppressWarnings("deprecation")
        PasswordEncoder noOpPasswordEncoder = org.springframework.security.crypto.password.NoOpPasswordEncoder.getInstance();
        passwordEncoder.setDefaultPasswordEncoderForMatches(noOpPasswordEncoder);
        return passwordEncoder;
    }
}

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
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.annotation.web.configurers.AuthorizeHttpRequestsConfigurer;
import org.springframework.security.web.SecurityFilterChain;

/**
 * The SecurityConfig class is responsible for configuring the security settings of the web application.
 * It provides the necessary configurations to enable web security and customize the authorization rules for incoming HTTP requests.
 *
 * This configuration class uses the Spring Security framework and provides a securityFilterChain bean that configures the security settings.
 */
@RequiredArgsConstructor
@Slf4j
@Configuration
@EnableWebSecurity
public class SecurityConfig {
    static {
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity httpSecurity)
            throws Exception {

        return httpSecurity
                .csrf(AbstractHttpConfigurer::disable)
                .httpBasic(AbstractHttpConfigurer::disable)
                .formLogin(AbstractHttpConfigurer::disable)
                .logout(AbstractHttpConfigurer::disable)
                .authorizeHttpRequests(this::authorizeHttpRequestsCustomizer)
                .build();
    }

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

}
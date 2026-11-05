/*
 * Copyright 2026 OmniOne.
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
import org.omnione.did.base.constants.Oid4vciEndpointPaths;
import org.omnione.did.issuer.v1.agent.controller.oid4vc.CredentialIssuanceController;
import org.omnione.did.oid4vc.oid4vci.dto.credential.CredentialRequest;
import org.omnione.did.oid4vc.oid4vci.dto.credential.DeferredCredentialRequest;
import org.omnione.did.oid4vc.oid4vci.dto.notification.NotificationRequest;
import org.omnione.did.oid4vc.oid4vci.dto.revocation.CredentialRevocationRequest;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.event.EventListener;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.mvc.method.RequestMappingInfo;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping;

import java.lang.reflect.Method;

@Slf4j
@Configuration
@RequiredArgsConstructor
public class IssuerEndpointConfig {

    private final RequestMappingHandlerMapping requestMappingHandlerMapping;
    private final CredentialIssuanceController credentialIssuanceController;

    @EventListener(ApplicationReadyEvent.class)
    public void registerIssuerEndpoints() {
        log.info("Registering fixed OID4VCI issuer endpoints...");
        registerEndpoint(Oid4vciEndpointPaths.CREDENTIAL_OFFER + "/{request_id}/{configuration_id}", RequestMethod.GET,
                "getCredentialOffer", String.class, String.class);
        registerEndpoint(Oid4vciEndpointPaths.CREDENTIAL, RequestMethod.POST, "issueCredential", CredentialRequest.class, Jwt.class);
        registerEndpoint(Oid4vciEndpointPaths.NONCE, RequestMethod.POST, "handleNonce");
        registerEndpoint(Oid4vciEndpointPaths.CREDENTIAL_REVOCATION, RequestMethod.POST,
                "revokeCredential", CredentialRevocationRequest.class);
        registerEndpoint(Oid4vciEndpointPaths.DEFERRED_CREDENTIAL, RequestMethod.POST,
                "getDeferredCredential", DeferredCredentialRequest.class, Jwt.class);
        registerEndpoint(Oid4vciEndpointPaths.NOTIFICATION, RequestMethod.POST, "handleNotification", NotificationRequest.class);
        log.info("Issuer endpoint registration completed.");
    }

    private void registerEndpoint(String path, RequestMethod httpMethod, String methodName, Class<?>... parameterTypes) {
        try {
            Method method = CredentialIssuanceController.class.getMethod(methodName, parameterTypes);
            
            RequestMappingInfo mappingInfo = RequestMappingInfo
                    .paths(path)
                    .methods(httpMethod)
                    .build();

            requestMappingHandlerMapping.registerMapping(mappingInfo, credentialIssuanceController, method);
            log.info("Registered issuer endpoint: {} {} -> {}", httpMethod, path, methodName);

        } catch (NoSuchMethodException | SecurityException e) {
            log.error("Method {} not found or accessible in CredentialIssuanceController", methodName, e);
        } catch (IllegalArgumentException | IllegalStateException e) {
            log.error("Invalid configuration or mapping conflict for endpoint {} -> {}", path, methodName, e);
        } catch (RuntimeException e) {
            log.error("Unexpected runtime error registering endpoint {} -> {}", path, methodName, e);
        }
    }
}

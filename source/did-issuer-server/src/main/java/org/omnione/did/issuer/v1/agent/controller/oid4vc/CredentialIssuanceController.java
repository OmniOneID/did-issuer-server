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

package org.omnione.did.issuer.v1.agent.controller.oid4vc;

import org.omnione.did.base.constants.ApiType;
import org.omnione.did.base.constants.ActionType;
import org.omnione.did.base.annotation.ArticleLog;
import com.google.gson.Gson;
import lombok.extern.slf4j.Slf4j;
import org.omnione.did.oid4vc.oid4vci.dto.credential.CredentialRequest;
import org.omnione.did.oid4vc.oid4vci.dto.credential.CredentialResponse;
import org.omnione.did.oid4vc.oid4vci.dto.credential.DeferredCredentialRequest;
import org.omnione.did.oid4vc.oid4vci.dto.credential.DeferredIssuanceResponse;
import org.omnione.did.oid4vc.oid4vci.dto.credentialoffer.CredentialOfferRequest;
import org.omnione.did.oid4vc.oid4vci.dto.credentialoffer.CredentialOfferResponse;
import org.omnione.did.oid4vc.oid4vci.dto.credentialoffer.TestCredentialOfferResponse;
import org.omnione.did.oid4vc.oid4vci.dto.nonce.NonceResponse;
import org.omnione.did.oid4vc.oid4vci.dto.notification.NotificationRequest;
import org.omnione.did.oid4vc.oid4vci.dto.revocation.CredentialRevocationRequest;
import org.omnione.did.oid4vc.oid4vci.dto.revocation.CredentialRevocationResponse;
import org.omnione.did.oid4vc.oid4vci.exception.OID4VCIException;
import org.omnione.did.oid4vc.oid4vci.service.CredentialService;
import org.omnione.did.issuer.v1.agent.service.oid4vc.CredentialRevocationService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
public class CredentialIssuanceController {

    private final CredentialService credentialService;
    private final CredentialRevocationService credentialRevocationService;

    public CredentialIssuanceController(
            CredentialService credentialService,
            CredentialRevocationService credentialRevocationService) {
        this.credentialService = credentialService;
        this.credentialRevocationService = credentialRevocationService;
    }

    @ArticleLog(name="[OID4VCI] Get credential offer", apiType=ApiType.AGENT, targetType="OID4VCI_OFFER", actionType=ActionType.NONE)
    public CredentialOfferResponse getCredentialOffer(@PathVariable("request_id") String requestId,
                                                      @PathVariable("configuration_id") String configurationId) throws OID4VCIException {
        CredentialOfferRequest request = new CredentialOfferRequest();
        request.setUserId("test");

        log.info("Retrieving authorization request for request_id: {}, configuration_id: {}", requestId, configurationId);
        CredentialOfferResponse response = credentialService.processCredentialOffer(requestId, request, configurationId);
        if (log.isDebugEnabled()) {
            log.debug("createCredentialOffer response: {}", new Gson().toJson(response));
        }

        return response;
    }

    @ArticleLog(name="[OID4VCI] Issue credential", apiType=ApiType.AGENT, targetType="OID4VCI_CREDENTIAL", actionType=ActionType.ISSUE_VC)
    public ResponseEntity<Object> issueCredential(@RequestBody CredentialRequest request,
            @AuthenticationPrincipal Jwt accessToken) throws OID4VCIException {
        if (log.isDebugEnabled()) {
            log.debug("credential request: {}", new Gson().toJson(request));
        }
        Object response = credentialService.issueCredential(request, accessToken);
        if (log.isDebugEnabled()) {
            log.debug("credential response: {}", new Gson().toJson(response));
        }
        if (response instanceof DeferredIssuanceResponse) {
            return ResponseEntity.accepted().body(response);
        } else {
            return ResponseEntity.ok(response);
        }
    }

    @ArticleLog(name="[OID4VCI] Get deferred credential", apiType=ApiType.AGENT, targetType="OID4VCI_CREDENTIAL", actionType=ActionType.NONE)
    public Object getDeferredCredential(@RequestBody DeferredCredentialRequest request,
            @AuthenticationPrincipal Jwt accessToken) throws OID4VCIException {
        return credentialService.getDeferredCredential(request.getTransactionId(), accessToken);
    }

    @ArticleLog(name="[OID4VCI] Create nonce", apiType=ApiType.AGENT, targetType="OID4VCI_NONCE", actionType=ActionType.CREATE)
    public NonceResponse handleNonce() {
        return credentialService.handleNonce();
    }

    @ArticleLog(name="[OID4VCI] Revoke credential", apiType=ApiType.AGENT, targetType="OID4VCI_CREDENTIAL", actionType=ActionType.REVOKE_VC)
    public CredentialRevocationResponse revokeCredential(
            @RequestBody CredentialRevocationRequest request) {
        return credentialRevocationService.revoke(request);
    }

    @ArticleLog(name="[OID4VCI] Receive notification", apiType=ApiType.AGENT, targetType="OID4VCI_NOTIFICATION", actionType=ActionType.NONE)
    public void handleNotification(@RequestBody NotificationRequest request) {
        credentialService.handleNotification(request);
    }

    @GetMapping("/credential-offer/test")
    @ResponseBody
    @ArticleLog(name="[OID4VCI] Get test credential offer", apiType=ApiType.AGENT, targetType="OID4VCI_OFFER", actionType=ActionType.NONE)
    public TestCredentialOfferResponse getCredentialOfferForTest() throws OID4VCIException {
        log.info("createCredentialOfferForTest request");
        TestCredentialOfferResponse testResponse = credentialService.createTestCredentialOffer("test", "mdoc");
        if (log.isDebugEnabled()) {
            log.debug("createCredentialOfferForTest response: {}", new Gson().toJson(testResponse));
        }
        return testResponse;
    }

}

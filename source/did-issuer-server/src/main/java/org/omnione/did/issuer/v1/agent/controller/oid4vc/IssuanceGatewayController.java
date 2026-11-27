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
import com.google.zxing.WriterException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.omnione.did.issuer.v1.admin.service.Oid4vciConfigService;
import org.omnione.did.oid4vc.oid4vci.exception.OID4VCIErrorCode;
import org.omnione.did.oid4vc.oid4vci.exception.OID4VCIException;
import org.omnione.did.oid4vc.oid4vci.service.IssuanceGatewayService;
import org.omnione.did.oid4vc.oid4vci.util.QrMaker;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.net.URI;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;
import java.util.List;
import java.util.Map;

@Slf4j
@Controller
@RequiredArgsConstructor
public class IssuanceGatewayController {

    private final IssuanceGatewayService issuanceGatewayService;
    private final Oid4vciConfigService oid4vciConfigService;
    private final ObjectMapper objectMapper;

    @GetMapping("/oid4vci/test")
    @ArticleLog(name="[OID4VCI] Open issuance test page", apiType=ApiType.AGENT, targetType="OID4VCI_ISSUANCE", actionType=ActionType.NONE)
    public String issue(Model model) {
        return "issue-credential";
    }

    @PostMapping("/qr-data/generate-qr")
    @ResponseBody
    @ArticleLog(name="[OID4VCI] Generate credential offer QR", apiType=ApiType.AGENT, targetType="OID4VCI_OFFER", actionType=ActionType.CREATE)
    public ResponseEntity<Map<String, Object>> generateQrData(@RequestBody Map<String, String> data) {
        String userId = data.get("userId");
        String grantType = data.get("grantType");
        String offerType = data.get("offerType");
        String scheme = data.get("scheme");
        String credentialConfigurationId = data.get("credentialConfigurationId");


        try {
            String issuerBaseUrl = normalizeIssuerBaseUrl(oid4vciConfigService.getMetadata().getCredentialIssuer());
            Map<String, Object> response = issuanceGatewayService.generateCredentialOfferUri(userId, grantType, offerType, scheme, credentialConfigurationId);
            String qrData = useIssuerBaseUrl((String) response.get("qrData"), offerType, issuerBaseUrl);
            response.put("qrData", qrData);
            response.put("qrImage", Base64.getEncoder().encodeToString(QrMaker.makeQrImage(qrData).getQrImage()));
            return ResponseEntity.ok(response);
        } catch (OID4VCIException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("error", e.getErrorCode(), "message", e.getErrorMsg()));
        } catch (IOException | WriterException | NoSuchAlgorithmException e) {
            log.error("Error generating QR data", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", OID4VCIErrorCode.ERR_CODE_OFFER_GENERATE_FAILED.getCode(),
                            "message", OID4VCIErrorCode.ERR_CODE_OFFER_GENERATE_FAILED.getMsg()));
        } catch (RuntimeException e) {
            log.error("Unexpected runtime error generating QR data", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", OID4VCIErrorCode.ERR_CODE_GENERAL_UNEXPECTED_ERROR.getCode(),
                            "message", OID4VCIErrorCode.ERR_CODE_GENERAL_UNEXPECTED_ERROR.getMsg()));
        }
    }

    private String normalizeIssuerBaseUrl(String value) {
        URI uri = URI.create(value);
        if (!("http".equals(uri.getScheme()) || "https".equals(uri.getScheme()))
                || uri.getHost() == null || uri.getRawUserInfo() != null
                || !(uri.getPath() == null || uri.getPath().isEmpty() || "/".equals(uri.getPath()))
                || uri.getRawQuery() != null || uri.getRawFragment() != null) {
            throw new IllegalArgumentException("Credential Issuer must be a server base URL");
        }
        return uri.toString().replaceAll("/+$", "");
    }

    private String useIssuerBaseUrl(String qrData, String offerType, String issuerBaseUrl) throws IOException {
        String parameter = "value".equals(offerType) ? "credential_offer=" : "credential_offer_uri=";
        int parameterIndex = qrData.indexOf(parameter);
        if (parameterIndex < 0) {
            throw new IllegalStateException("Credential offer QR data is missing " + parameter);
        }
        String prefix = qrData.substring(0, parameterIndex + parameter.length());
        String payload = qrData.substring(parameterIndex + parameter.length());
        if ("value".equals(offerType)) {
            ObjectNode offer = (ObjectNode) objectMapper.readTree(URLDecoder.decode(payload, StandardCharsets.UTF_8));
            offer.put("credential_issuer", issuerBaseUrl);
            return prefix + URLEncoder.encode(objectMapper.writeValueAsString(offer), StandardCharsets.UTF_8);
        }
        URI offerUri = URI.create(payload);
        String path = offerUri.getRawPath();
        if (path == null || !path.startsWith("/credential-offer/")) {
            throw new IllegalStateException("Credential offer URI has an unexpected path");
        }
        return prefix + issuerBaseUrl + path;
    }

    @ResponseBody
    @GetMapping("/get-credential-identifier")
    @ArticleLog(name="[OID4VCI] Get credential identifier", apiType=ApiType.AGENT, targetType="OID4VCI_CREDENTIAL", actionType=ActionType.NONE)
    public List<String> getCredentialIdentifier(
            @RequestParam("credentialConfigurationId") String credentialConfigurationId) {
        List<String> response = issuanceGatewayService.getCredentialIdentifier(credentialConfigurationId);
        log.debug("getCredentialIdentifier response: {}", response);
        return response;
    }

}

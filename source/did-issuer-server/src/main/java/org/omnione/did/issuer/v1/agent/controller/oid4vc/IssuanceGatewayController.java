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

import com.google.zxing.WriterException;
import com.nimbusds.jose.shaded.gson.Gson;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.omnione.did.oid4vc.oid4vci.exception.OID4VCIErrorCode;
import org.omnione.did.oid4vc.oid4vci.exception.OID4VCIException;
import org.omnione.did.oid4vc.oid4vci.service.IssuanceGatewayService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;
import java.util.List;
import java.util.Map;

@Slf4j
@Controller
@RequiredArgsConstructor
public class IssuanceGatewayController {

    private final IssuanceGatewayService issuanceGatewayService;

    @GetMapping("/oid4vci/test")
    public String issue(Model model) {
        return "issue-credential";
    }

    @PostMapping("/qr-data/generate-qr")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> generateQrData(@RequestBody Map<String, String> data) {
        String userId = data.get("userId");
        String grantType = data.get("grantType");
        String offerType = data.get("offerType");
        String scheme = data.get("scheme");
        String credentialConfigurationId = data.get("credentialConfigurationId");


        try {
            Map<String, Object> response = issuanceGatewayService.generateCredentialOfferUri(userId, grantType, offerType, scheme, credentialConfigurationId);
            String s = Base64.getEncoder().encodeToString((byte[])response.get("qrImage"));
            response.put("qrImage", s);
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

    @ResponseBody
    @GetMapping("/get-credential-identifier")
    public List<String> getCredentialIdentifier(
            @RequestParam("credentialConfigurationId") String credentialConfigurationId) {
        List<String> response = issuanceGatewayService.getCredentialIdentifier(credentialConfigurationId);
        log.info("getCredentialIdentifier response : {}", new Gson().toJson(response));
        return response;
    }

}

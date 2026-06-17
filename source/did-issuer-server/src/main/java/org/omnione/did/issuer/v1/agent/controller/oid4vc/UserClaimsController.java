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

import lombok.RequiredArgsConstructor;
import org.omnione.did.oid4vc.oid4vci.config.IssuerSdkProperties;
import org.omnione.did.oid4vc.oid4vci.service.UserClaimsStore;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.io.InputStream;
import java.util.Base64;
import java.util.List;
import java.util.Map;

@Controller
@RequiredArgsConstructor
public class UserClaimsController {

    private final UserClaimsStore userClaimsStore;
    private final IssuerSdkProperties issuerSdkProperties;

    @GetMapping("/claims-page")
    public String claimsPage(Model model) {
        // Pass the full map of configurations to the frontend (configId -> {format, identifiers})
        model.addAttribute("credentialConfigs", issuerSdkProperties.getCredentialConfigurations());

        // Add default claims for mDoc (mDL)
        Map<String, Object> mdocDefaults = new java.util.LinkedHashMap<>();
        mdocDefaults.put("family_name", "Kim");
        mdocDefaults.put("given_name", "Raon");
        mdocDefaults.put("birth_date", "1990-05-15");
        mdocDefaults.put("issue_date", "2026-01-10");
        mdocDefaults.put("expiry_date", "2036-01-10");
        mdocDefaults.put("issuing_country", "KR");
        mdocDefaults.put("issuing_authority", "Korean National Police Agency");
        mdocDefaults.put("document_number", "11-123456-78");
        
        // Load portrait from static resources
        try {
            InputStream is = new ClassPathResource("templates/images/portrait-sample.jpg").getInputStream();
            byte[] bytes = is.readAllBytes();
            String portraitBase64 = Base64.getEncoder().encodeToString(bytes);
            mdocDefaults.put("portrait", portraitBase64);
        } catch (IOException e) {
            mdocDefaults.put("portrait", "IMAGE_NOT_FOUND");
        }

        mdocDefaults.put("driving_privileges", List.of(Map.of(
                "vehicle_category_code", "B",
                "issue_date", "2024-01-10",
                "expiry_date", "2034-01-10"
        )));
        mdocDefaults.put("un_distinguishing_sign", "ROK");

        model.addAttribute("mdocDefaults", mdocDefaults);

        return "claims-editor";
    }

    @PostMapping("/api/claims/save")
    @ResponseBody
    public ResponseEntity<Map<String, String>> saveClaims(@RequestBody ClaimsRequest request) {
        userClaimsStore.saveClaims(request.getUserId(), request.getCredentialType(), request.getClaims());
        return ResponseEntity.ok(Map.of("message", "Claims saved successfully"));
    }

    @GetMapping("/api/claims/list")
    @ResponseBody
    public ResponseEntity<Map<String, Map<String, Object>>> listAllClaims() {
        return ResponseEntity.ok()
                .header("X-Content-Type-Options", "nosniff")
                .body(userClaimsStore.getAllEntries());
    }

    @GetMapping("/api/claims/get")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> getClaims(@RequestParam String userId, @RequestParam String credentialType) {
        Map<String, Object> claims = userClaimsStore.getClaims(userId, credentialType);
        return ResponseEntity.ok()
                .header("X-Content-Type-Options", "nosniff")
                .body(claims);
    }

    @lombok.Data
    public static class ClaimsRequest {
        private String userId;
        private String credentialType;
        private Map<String, Object> claims;
    }
}

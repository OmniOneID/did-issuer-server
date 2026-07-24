/*
 * Copyright 2026 OmniOne.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 */
package org.omnione.did.issuer.v1.agent.controller.oid4vc;

import lombok.RequiredArgsConstructor;
import jakarta.servlet.http.HttpServletResponse;
import org.omnione.did.issuer.v1.agent.dto.oid4vc.WebviewIssuancePage;
import org.omnione.did.issuer.v1.agent.service.oid4vc.WebviewIssuanceException;
import org.omnione.did.issuer.v1.agent.service.oid4vc.WebviewIssuanceService;
import org.springframework.http.CacheControl;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.Map;

@Controller
@RequiredArgsConstructor
@RequestMapping("/oid4vci/issuance")
public class WebviewIssuanceController {
    private final WebviewIssuanceService service;

    @GetMapping("/start")
    public String start(
            @RequestParam("userId") String userId,
            @RequestParam("credential_configuration_id") String configurationId,
            @RequestParam(value = "credential_type", required = false) String credentialType,
            Model model, HttpServletResponse response) {
        securityHeaders(response);
        try {
            model.addAttribute("page", service.start(userId, configurationId, credentialType));
            return "oid4vci-issuance";
        } catch (WebviewIssuanceException e) {
            response.setStatus(e.getStatus().value());
            model.addAttribute("message", e.getMessage());
            return "oid4vci-issuance-error";
        }
    }

    @GetMapping("/sessions/{sessionToken}")
    public String page(@PathVariable String sessionToken, Model model, HttpServletResponse response) {
        securityHeaders(response);
        try {
            model.addAttribute("page", service.getPage(sessionToken, null));
            return "oid4vci-issuance";
        } catch (WebviewIssuanceException e) {
            response.setStatus(e.getStatus().value());
            model.addAttribute("message", e.getMessage());
            return "oid4vci-issuance-error";
        }
    }

    @PostMapping("/sessions/{sessionToken}/confirm")
    public Object confirm(
            @PathVariable String sessionToken,
            @RequestParam("_form_token") String formToken,
            @RequestParam Map<String, String> form,
            Model model, HttpServletResponse response) {
        try {
            String offerUri = service.confirm(sessionToken, formToken, form);
            return ResponseEntity.status(302)
                    .headers(securityHeaders())
                    .location(URI.create(offerUri))
                    .body("");
        } catch (WebviewIssuanceException e) {
            securityHeaders(response);
            response.setStatus(e.getStatus().value());
            try {
                WebviewIssuancePage page = service.getPage(sessionToken, e.getMessage());
                model.addAttribute("page", page);
                return "oid4vci-issuance";
            } catch (WebviewIssuanceException sessionError) {
                model.addAttribute("message", e.getMessage());
                return "oid4vci-issuance-error";
            }
        }
    }

    @PostMapping("/sessions/{sessionToken}/cancel")
    public String cancel(
            @PathVariable String sessionToken,
            @RequestParam("_form_token") String formToken,
            Model model, HttpServletResponse response) {
        securityHeaders(response);
        try {
            service.cancel(sessionToken, formToken);
            return "oid4vci-issuance-canceled";
        } catch (WebviewIssuanceException e) {
            response.setStatus(e.getStatus().value());
            model.addAttribute("message", e.getMessage());
            return "oid4vci-issuance-error";
        }
    }

    private void securityHeaders(HttpServletResponse response) {
        securityHeaders().forEach((name, values) -> values.forEach(value -> response.addHeader(name, value)));
    }

    private HttpHeaders securityHeaders() {
        HttpHeaders headers = new HttpHeaders();
        headers.setCacheControl(CacheControl.noStore());
        headers.setPragma("no-cache");
        headers.set("Referrer-Policy", "no-referrer");
        headers.set("Content-Security-Policy",
                "default-src 'none'; style-src 'unsafe-inline'; form-action 'self'; base-uri 'none'; frame-ancestors 'self'");
        headers.set("X-Content-Type-Options", "nosniff");
        return headers;
    }
}

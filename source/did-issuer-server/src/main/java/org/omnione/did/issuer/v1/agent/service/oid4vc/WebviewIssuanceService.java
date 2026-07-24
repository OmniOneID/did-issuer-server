/*
 * Copyright 2026 OmniOne.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 */
package org.omnione.did.issuer.v1.agent.service.oid4vc;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.omnione.did.base.db.domain.CredentialConfig;
import org.omnione.did.base.db.domain.Oid4vcWebviewIssuanceSessionEntity;
import org.omnione.did.base.db.repository.CredentialConfigRepository;
import org.omnione.did.base.db.repository.Oid4vcWebviewIssuanceSessionRepository;
import org.omnione.did.issuer.v1.agent.dto.oid4vc.WebviewClaimField;
import org.omnione.did.issuer.v1.agent.dto.oid4vc.WebviewIssuancePage;
import org.omnione.did.oid4vc.oid4vci.exception.OID4VCIException;
import org.omnione.did.oid4vc.oid4vci.service.IssuanceGatewayService;
import org.omnione.did.oid4vc.oid4vci.service.UserClaimsStore;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.net.URI;
import java.net.URLEncoder;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.*;

@Service
@RequiredArgsConstructor
public class WebviewIssuanceService {
    private static final int MAX_VALUE_LENGTH = 2048;
    private static final Set<String> SERVER_MANAGED_CLAIMS = Set.of(
            "iss", "sub", "iat", "nbf", "exp", "jti", "vct", "status",
            "credentialStatus", "credential_status", "cnf");

    private final CredentialConfigRepository credentialConfigRepository;
    private final Oid4vcWebviewIssuanceSessionRepository sessionRepository;
    private final UserClaimsStore userClaimsStore;
    private final IssuanceGatewayService issuanceGatewayService;
    private final ObjectMapper objectMapper;
    private final SecureRandom secureRandom = new SecureRandom();

    @Transactional
    public WebviewIssuancePage start(String userId, String configurationId, String requestedCredentialType) {
        requireIdentifier(userId, "userId");
        requireIdentifier(configurationId, "credential_configuration_id");
        CredentialConfig config = loadConfiguration(configurationId);
        String credentialType = resolveCredentialType(config, requestedCredentialType);
        List<ClaimDefinition> definitions = readClaimDefinitions(config);
        if (definitions.isEmpty()) {
            throw error(HttpStatus.BAD_REQUEST, "This credential has no user-editable claims.");
        }

        Oid4vcWebviewIssuanceSessionEntity session = new Oid4vcWebviewIssuanceSessionEntity();
        session.setSessionToken(randomToken());
        session.setCsrfToken(randomToken());
        session.setUserId(userId);
        session.setCredentialConfigurationId(configurationId);
        session.setCredentialType(credentialType);
        session.setStatus("INPUT_REQUIRED");
        session.setExpiresAt(Instant.now().plusSeconds(10 * 60));
        sessionRepository.save(session);

        return page(session, config, definitions, null);
    }

    @Transactional(readOnly = true)
    public WebviewIssuancePage getPage(String sessionToken, String errorMessage) {
        Oid4vcWebviewIssuanceSessionEntity session = loadSession(sessionToken);
        validateActive(session);
        CredentialConfig config = loadConfiguration(session.getCredentialConfigurationId());
        return page(session, config, readClaimDefinitions(config), errorMessage);
    }

    @Transactional
    public String confirm(String sessionToken, String csrfToken, Map<String, String> submitted) {
        Oid4vcWebviewIssuanceSessionEntity session = sessionRepository.findBySessionTokenForUpdate(sessionToken)
                .orElseThrow(() -> error(HttpStatus.NOT_FOUND, "Issuance session was not found."));
        validateNotExpired(session);
        validateCsrf(session, csrfToken);

        if ("OFFER_CREATED".equals(session.getStatus()) && session.getCredentialOfferUri() != null) {
            return session.getCredentialOfferUri();
        }
        if (!"INPUT_REQUIRED".equals(session.getStatus())) {
            throw error(HttpStatus.CONFLICT, "Issuance session cannot be submitted.");
        }

        CredentialConfig config = loadConfiguration(session.getCredentialConfigurationId());
        resolveCredentialType(config, session.getCredentialType());
        List<ClaimDefinition> definitions = readClaimDefinitions(config);
        Map<String, Object> accepted = validateClaims(definitions, submitted);

        Map<String, Object> merged = new LinkedHashMap<>();
        Map<String, Object> existing = userClaimsStore.getClaims(session.getUserId(), session.getCredentialType());
        if (existing != null) {
            merged.putAll(existing);
        }
        merged.putAll(accepted);
        userClaimsStore.saveClaims(session.getUserId(), session.getCredentialType(), merged);
        session.setStatus("CLAIMS_SAVED");

        try {
            Map<String, Object> result = issuanceGatewayService.generateCredentialOfferUri(
                    session.getUserId(), "pre-authorized_code", "reference",
                    "openid-credential-offer://", session.getCredentialConfigurationId(), false);
            Object value = result.get("qrData");
            if (!(value instanceof String rawOfferUri)) {
                throw error(HttpStatus.INTERNAL_SERVER_ERROR, "Credential Offer could not be created.");
            }
            String offerUri = normalizeOfferRedirect(rawOfferUri);
            session.setCredentialOfferUri(offerUri);
            session.setStatus("OFFER_CREATED");
            return offerUri;
        } catch (IOException | NoSuchAlgorithmException | OID4VCIException
                 | com.google.zxing.WriterException e) {
            session.setStatus("FAILED");
            throw error(HttpStatus.BAD_GATEWAY, "Credential Offer could not be created.");
        }
    }

    @Transactional
    public void cancel(String sessionToken, String csrfToken) {
        Oid4vcWebviewIssuanceSessionEntity session = sessionRepository.findBySessionTokenForUpdate(sessionToken)
                .orElseThrow(() -> error(HttpStatus.NOT_FOUND, "Issuance session was not found."));
        validateCsrf(session, csrfToken);
        if ("INPUT_REQUIRED".equals(session.getStatus())) {
            session.setStatus("CANCELED");
        } else if (!"CANCELED".equals(session.getStatus())) {
            throw error(HttpStatus.CONFLICT, "Issuance session cannot be canceled.");
        }
    }

    private WebviewIssuancePage page(Oid4vcWebviewIssuanceSessionEntity session, CredentialConfig config,
                                     List<ClaimDefinition> definitions, String errorMessage) {
        Map<String, Object> existing = userClaimsStore.getClaims(session.getUserId(), session.getCredentialType());
        Map<String, Object> values = existing == null ? Map.of() : existing;
        List<WebviewClaimField> fields = definitions.stream()
                .map(definition -> new WebviewClaimField(
                        definition.name(), definition.label(), definition.valueType(), definition.mandatory(),
                        displayValue(values.get(definition.name()))))
                .toList();
        Display display = readDisplay(config);
        return new WebviewIssuancePage(session.getSessionToken(), session.getCsrfToken(),
                display.issuerName(), display.credentialName(), config.getFormat(), fields, errorMessage);
    }

    private Map<String, Object> validateClaims(List<ClaimDefinition> definitions, Map<String, String> submitted) {
        Map<String, Object> accepted = new LinkedHashMap<>();
        for (ClaimDefinition definition : definitions) {
            String raw = submitted.get("claim." + definition.name());
            if (definition.mandatory() && (raw == null || raw.isBlank())) {
                throw error(HttpStatus.BAD_REQUEST, definition.label() + " is required.");
            }
            if (raw == null || raw.isBlank()) {
                continue;
            }
            if (raw.length() > MAX_VALUE_LENGTH) {
                throw error(HttpStatus.BAD_REQUEST, definition.label() + " is too long.");
            }
            accepted.put(definition.name(), convertValue(definition, raw));
        }
        return accepted;
    }

    private Object convertValue(ClaimDefinition definition, String raw) {
        String type = definition.valueType();
        try {
            if ("full-date".equals(type) || "date".equals(type)) {
                LocalDate.parse(raw);
                return raw;
            }
            if ("uint".equals(type) || "integer".equals(type)) {
                long value = Long.parseLong(raw);
                if ("uint".equals(type) && value < 0) {
                    throw new NumberFormatException();
                }
                return value;
            }
            if ("boolean".equals(type)) {
                if (!"true".equalsIgnoreCase(raw) && !"false".equalsIgnoreCase(raw)) {
                    throw new IllegalArgumentException();
                }
                return Boolean.parseBoolean(raw);
            }
            if ("list".equals(type)) {
                JsonNode node = objectMapper.readTree(raw);
                if (!node.isArray()) {
                    throw new IllegalArgumentException();
                }
                return objectMapper.convertValue(node, List.class);
            }
            if (!definition.enumValues().isEmpty() && !definition.enumValues().contains(raw)) {
                throw new IllegalArgumentException();
            }
            return raw;
        } catch (DateTimeParseException | JsonProcessingException | IllegalArgumentException e) {
            throw error(HttpStatus.BAD_REQUEST, definition.label() + " has an invalid value.");
        }
    }

    private List<ClaimDefinition> readClaimDefinitions(CredentialConfig config) {
        try {
            JsonNode root = objectMapper.readTree(config.getMetadataJson());
            JsonNode claims = root.path("credential_metadata").path("claims");
            if (!claims.isArray()) {
                claims = root.path("claims");
            }
            if (!claims.isArray()) {
                return List.of();
            }
            List<ClaimDefinition> result = new ArrayList<>();
            for (JsonNode claim : claims) {
                JsonNode path = claim.path("path");
                if (!path.isArray() || path.isEmpty()) {
                    continue;
                }
                List<String> parts = new ArrayList<>();
                path.forEach(item -> parts.add(item.asText()));
                String name = isMdoc(config.getFormat())
                        ? parts.get(parts.size() - 1) : String.join(".", parts);
                if (name.isBlank() || SERVER_MANAGED_CLAIMS.contains(name)) {
                    continue;
                }
                String label = claim.path("display").path(0).path("name").asText(name);
                String valueType = claim.path("value_type").asText("string");
                List<String> enumValues = new ArrayList<>();
                claim.path("enum").forEach(value -> enumValues.add(value.asText()));
                result.add(new ClaimDefinition(name, label, valueType,
                        claim.path("mandatory").asBoolean(false), enumValues));
            }
            return result;
        } catch (JsonProcessingException | NullPointerException e) {
            throw error(HttpStatus.BAD_REQUEST, "Credential claim metadata is invalid.");
        }
    }

    private Display readDisplay(CredentialConfig config) {
        String issuerName = "Credential Issuer";
        String credentialName = config.getId();
        try {
            JsonNode root = objectMapper.readTree(config.getMetadataJson());
            credentialName = root.path("display").path(0).path("name").asText(credentialName);
        } catch (JsonProcessingException ignored) {
            // Claim metadata validation reports malformed JSON before this method is reached.
        }
        return new Display(issuerName, credentialName);
    }

    private CredentialConfig loadConfiguration(String configurationId) {
        return credentialConfigRepository.findAllByIdAndEnabledTrue(configurationId).stream()
                .findFirst()
                .filter(config -> config.getFormat() != null && !config.getFormat().isBlank())
                .orElseThrow(() -> error(HttpStatus.BAD_REQUEST,
                        "Credential Configuration is not available."));
    }

    private String resolveCredentialType(CredentialConfig config, String requested) {
        List<String> identifiers = config.getIdentifiers() == null
                ? List.of() : config.getIdentifiers().stream().filter(Objects::nonNull).distinct().toList();
        if (identifiers.isEmpty()) {
            throw error(HttpStatus.BAD_REQUEST, "Credential Configuration has no credential type.");
        }
        if (requested != null && !requested.isBlank()) {
            if (!identifiers.contains(requested)) {
                throw error(HttpStatus.BAD_REQUEST, "Credential type is not allowed.");
            }
            return requested;
        }
        if (identifiers.size() != 1) {
            throw error(HttpStatus.BAD_REQUEST, "credential_type is required for this configuration.");
        }
        return identifiers.get(0);
    }

    private Oid4vcWebviewIssuanceSessionEntity loadSession(String token) {
        return sessionRepository.findBySessionToken(token)
                .orElseThrow(() -> error(HttpStatus.NOT_FOUND, "Issuance session was not found."));
    }

    private void validateActive(Oid4vcWebviewIssuanceSessionEntity session) {
        validateNotExpired(session);
        if (!"INPUT_REQUIRED".equals(session.getStatus())) {
            throw error(HttpStatus.CONFLICT, "Issuance session is no longer active.");
        }
    }

    private void validateNotExpired(Oid4vcWebviewIssuanceSessionEntity session) {
        if (session.getExpiresAt().isBefore(Instant.now())) {
            session.setStatus("EXPIRED");
            throw error(HttpStatus.GONE, "Issuance session has expired.");
        }
    }

    private void validateCsrf(Oid4vcWebviewIssuanceSessionEntity session, String csrfToken) {
        if (csrfToken == null || !java.security.MessageDigest.isEqual(
                session.getCsrfToken().getBytes(java.nio.charset.StandardCharsets.UTF_8),
                csrfToken.getBytes(java.nio.charset.StandardCharsets.UTF_8))) {
            throw error(HttpStatus.FORBIDDEN, "Invalid form token.");
        }
    }

    private void requireIdentifier(String value, String name) {
        if (value == null || value.isBlank() || value.length() > 128
                || !value.matches("[A-Za-z0-9._:@+\\-]+")) {
            throw error(HttpStatus.BAD_REQUEST, name + " is invalid.");
        }
    }

    private String randomToken() {
        byte[] bytes = new byte[32];
        secureRandom.nextBytes(bytes);
        return Base64.getUrlEncoder().withoutPadding().encodeToString(bytes);
    }

    private String displayValue(Object value) {
        if (value == null) {
            return "";
        }
        if (value instanceof String || value instanceof Number || value instanceof Boolean) {
            return value.toString();
        }
        try {
            return objectMapper.writeValueAsString(value);
        } catch (JsonProcessingException e) {
            return "";
        }
    }

    private boolean isMdoc(String format) {
        return "mso_mdoc".equals(format) || "mso-mdoc".equals(format);
    }

    private String normalizeOfferRedirect(String rawRedirect) {
        String prefix = "openid-credential-offer://?credential_offer_uri=";
        if (!rawRedirect.startsWith(prefix)) {
            throw error(HttpStatus.INTERNAL_SERVER_ERROR, "Credential Offer could not be created.");
        }
        String rawValue = rawRedirect.substring(prefix.length());
        if (rawValue.isBlank() || rawValue.contains("&")) {
            throw error(HttpStatus.INTERNAL_SERVER_ERROR, "Credential Offer URI is invalid.");
        }
        String decoded = URLDecoder.decode(rawValue, StandardCharsets.UTF_8);
        URI reference;
        try {
            reference = URI.create(decoded);
        } catch (IllegalArgumentException e) {
            throw error(HttpStatus.INTERNAL_SERVER_ERROR, "Credential Offer URI is invalid.");
        }
        boolean webScheme = "http".equalsIgnoreCase(reference.getScheme())
                || "https".equalsIgnoreCase(reference.getScheme());
        if (!webScheme || reference.getHost() == null
                || reference.getUserInfo() != null || reference.getFragment() != null) {
            throw error(HttpStatus.INTERNAL_SERVER_ERROR,
                    "Credential Offer URI must use HTTP or HTTPS.");
        }
        return prefix + URLEncoder.encode(reference.toASCIIString(), StandardCharsets.UTF_8);
    }

    private WebviewIssuanceException error(HttpStatus status, String message) {
        return new WebviewIssuanceException(status, message);
    }

    private record ClaimDefinition(String name, String label, String valueType,
                                   boolean mandatory, List<String> enumValues) {
    }

    private record Display(String issuerName, String credentialName) {
    }
}

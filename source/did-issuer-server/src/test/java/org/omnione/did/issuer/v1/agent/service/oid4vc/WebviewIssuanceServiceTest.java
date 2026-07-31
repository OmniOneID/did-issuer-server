package org.omnione.did.issuer.v1.agent.service.oid4vc;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.omnione.did.base.db.domain.CredentialConfig;
import org.omnione.did.base.db.domain.Oid4vcWebviewIssuanceSessionEntity;
import org.omnione.did.base.db.repository.CredentialConfigRepository;
import org.omnione.did.base.db.repository.Oid4vcWebviewIssuanceSessionRepository;
import org.omnione.did.issuer.v1.agent.dto.oid4vc.WebviewIssuancePage;
import org.omnione.did.oid4vc.oid4vci.service.IssuanceGatewayService;
import org.omnione.did.oid4vc.oid4vci.service.UserClaimsStore;

import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class WebviewIssuanceServiceTest {
    @Mock CredentialConfigRepository credentialConfigRepository;
    @Mock Oid4vcWebviewIssuanceSessionRepository sessionRepository;
    @Mock UserClaimsStore userClaimsStore;
    @Mock IssuanceGatewayService issuanceGatewayService;

    WebviewIssuanceService service;

    @BeforeEach
    void setUp() {
        service = new WebviewIssuanceService(credentialConfigRepository, sessionRepository,
                userClaimsStore, issuanceGatewayService, new ObjectMapper());
    }

    @Test
    void startsSessionAndPrefillsExistingClaims() {
        CredentialConfig config = config();
        when(credentialConfigRepository.findAllByIdAndEnabledTrue("UniversityDegreeCredential"))
                .thenReturn(List.of(config));
        when(userClaimsStore.getClaims("user123", "UniversityDegree"))
                .thenReturn(Map.of("given_name", "Gilwoo"));

        WebviewIssuancePage page = service.start("user123", "UniversityDegreeCredential", null);

        assertEquals("Gilwoo", page.claims().get(0).value());
        assertEquals("dc+sd-jwt", page.credentialFormat());
        assertFalse(page.sessionToken().isBlank());
        verify(sessionRepository).save(any(Oid4vcWebviewIssuanceSessionEntity.class));
    }

    @Test
    void rejectsUnknownClaimAndPreservesExistingClaim() throws Exception {
        CredentialConfig config = config();
        Oid4vcWebviewIssuanceSessionEntity session = session("INPUT_REQUIRED");
        when(sessionRepository.findBySessionTokenForUpdate("session")).thenReturn(Optional.of(session));
        when(credentialConfigRepository.findAllByIdAndEnabledTrue("UniversityDegreeCredential"))
                .thenReturn(List.of(config));
        when(userClaimsStore.getClaims("user123", "UniversityDegree"))
                .thenReturn(Map.of("server_only", "keep"));
        when(issuanceGatewayService.generateCredentialOfferUri(
                "user123", "pre-authorized_code", "reference",
                "openid-credential-offer://", "UniversityDegreeCredential", false))
                .thenReturn(Map.of("qrData",
                        "openid-credential-offer://?credential_offer_uri=https%3A%2F%2Fissuer.example%2Fcredential-offers%2Foffer"));

        String redirect = service.confirm("session", "csrf", Map.of(
                "claim.given_name", "New",
                "claim.not_allowed", "ignored"));

        assertTrue(redirect.startsWith("openid-credential-offer://"));
        @SuppressWarnings("unchecked")
        ArgumentCaptor<Map<String, Object>> claims = ArgumentCaptor.forClass(Map.class);
        verify(userClaimsStore).saveClaims(eq("user123"), eq("UniversityDegree"), claims.capture());
        assertEquals("keep", claims.getValue().get("server_only"));
        assertEquals("New", claims.getValue().get("given_name"));
        assertFalse(claims.getValue().containsKey("not_allowed"));
        assertEquals("OFFER_CREATED", session.getStatus());
    }

    @Test
    void removesExistingOptionalClaimWhenSubmittedEmpty() throws Exception {
        CredentialConfig config = config();
        Oid4vcWebviewIssuanceSessionEntity session = session("INPUT_REQUIRED");
        when(sessionRepository.findBySessionTokenForUpdate("session")).thenReturn(Optional.of(session));
        when(credentialConfigRepository.findAllByIdAndEnabledTrue("UniversityDegreeCredential"))
                .thenReturn(List.of(config));
        when(userClaimsStore.getClaims("user123", "UniversityDegree"))
                .thenReturn(Map.of("given_name", "Gilwoo", "birth_date", "2000-01-01"));
        when(issuanceGatewayService.generateCredentialOfferUri(
                "user123", "pre-authorized_code", "reference",
                "openid-credential-offer://", "UniversityDegreeCredential", false))
                .thenReturn(Map.of("qrData",
                        "openid-credential-offer://?credential_offer_uri=https%3A%2F%2Fissuer.example%2Foffer"));

        service.confirm("session", "csrf", Map.of(
                "claim.given_name", "Gilwoo",
                "claim.birth_date", ""));

        @SuppressWarnings("unchecked")
        ArgumentCaptor<Map<String, Object>> claims = ArgumentCaptor.forClass(Map.class);
        verify(userClaimsStore).saveClaims(eq("user123"), eq("UniversityDegree"), claims.capture());
        assertFalse(claims.getValue().containsKey("birth_date"));
        assertEquals("Gilwoo", claims.getValue().get("given_name"));
    }

    @Test
    void duplicateConfirmReturnsExistingOfferWithoutCreatingAnother() throws Exception {
        Oid4vcWebviewIssuanceSessionEntity session = session("OFFER_CREATED");
        session.setCredentialOfferUri("openid-credential-offer://?credential_offer_uri=https%3A%2F%2Fissuer.example%2Foffer");
        when(sessionRepository.findBySessionTokenForUpdate("session")).thenReturn(Optional.of(session));

        String result = service.confirm("session", "csrf", Map.of());

        assertEquals(session.getCredentialOfferUri(), result);
        verifyNoInteractions(issuanceGatewayService, userClaimsStore);
    }

    @Test
    void allowsHttpCredentialOfferUriForDevelopmentAndTest() throws Exception {
        CredentialConfig config = config();
        Oid4vcWebviewIssuanceSessionEntity session = session("INPUT_REQUIRED");
        when(sessionRepository.findBySessionTokenForUpdate("session")).thenReturn(Optional.of(session));
        when(credentialConfigRepository.findAllByIdAndEnabledTrue("UniversityDegreeCredential"))
                .thenReturn(List.of(config));
        when(issuanceGatewayService.generateCredentialOfferUri(
                "user123", "pre-authorized_code", "reference",
                "openid-credential-offer://", "UniversityDegreeCredential", false))
                .thenReturn(Map.of("qrData",
                        "openid-credential-offer://?credential_offer_uri=http://10.0.2.2:8091/credential-offer/offer"));

        String redirect = service.confirm("session", "csrf",
                Map.of("claim.given_name", "Gilwoo"));

        assertEquals(
                "openid-credential-offer://?credential_offer_uri=http%3A%2F%2F10.0.2.2%3A8091%2Fcredential-offer%2Foffer",
                redirect);
    }

    @Test
    void rejectsExpiredSession() {
        Oid4vcWebviewIssuanceSessionEntity session = session("INPUT_REQUIRED");
        session.setExpiresAt(Instant.now().minusSeconds(1));
        when(sessionRepository.findBySessionTokenForUpdate("session")).thenReturn(Optional.of(session));

        WebviewIssuanceException error = assertThrows(WebviewIssuanceException.class,
                () -> service.confirm("session", "csrf", Map.of()));

        assertEquals(410, error.getStatus().value());
        verifyNoInteractions(issuanceGatewayService, userClaimsStore);
    }

    @Test
    void rejectsInvalidDateAndMissingMandatoryClaim() {
        CredentialConfig config = config();
        Oid4vcWebviewIssuanceSessionEntity session = session("INPUT_REQUIRED");
        when(sessionRepository.findBySessionTokenForUpdate("session")).thenReturn(Optional.of(session));
        when(credentialConfigRepository.findAllByIdAndEnabledTrue("UniversityDegreeCredential"))
                .thenReturn(List.of(config));

        assertThrows(WebviewIssuanceException.class,
                () -> service.confirm("session", "csrf", Map.of("claim.birth_date", "2025-02-31")));
        verifyNoInteractions(issuanceGatewayService);
    }

    @Test
    void cancelDoesNotSaveClaimsOrCreateOffer() {
        Oid4vcWebviewIssuanceSessionEntity session = session("INPUT_REQUIRED");
        when(sessionRepository.findBySessionTokenForUpdate("session")).thenReturn(Optional.of(session));

        service.cancel("session", "csrf");

        assertEquals("CANCELED", session.getStatus());
        verifyNoInteractions(issuanceGatewayService, userClaimsStore);
    }

    private CredentialConfig config() {
        return CredentialConfig.builder()
                .id("UniversityDegreeCredential")
                .format("dc+sd-jwt")
                .identifiers(List.of("UniversityDegree"))
                .enabled(true)
                .metadataJson("""
                        {
                          "display": [{"name": "University Degree"}],
                          "credential_metadata": {
                            "claims": [
                              {"path":["given_name"],"mandatory":true,"value_type":"string",
                               "display":[{"name":"Given name"}]},
                              {"path":["birth_date"],"mandatory":false,"value_type":"full-date",
                               "display":[{"name":"Birth date"}]}
                            ]
                          }
                        }
                        """)
                .build();
    }

    private Oid4vcWebviewIssuanceSessionEntity session(String status) {
        Oid4vcWebviewIssuanceSessionEntity session = new Oid4vcWebviewIssuanceSessionEntity();
        session.setSessionToken("session");
        session.setCsrfToken("csrf");
        session.setUserId("user123");
        session.setCredentialConfigurationId("UniversityDegreeCredential");
        session.setCredentialType("UniversityDegree");
        session.setStatus(status);
        session.setExpiresAt(Instant.now().plusSeconds(60));
        return session;
    }
}

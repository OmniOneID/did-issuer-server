package org.omnione.did.issuer.v1.agent.controller.oid4vc;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import org.omnione.did.issuer.v1.agent.dto.oid4vc.WebviewClaimField;
import org.omnione.did.issuer.v1.agent.dto.oid4vc.WebviewIssuancePage;
import org.omnione.did.issuer.v1.agent.service.oid4vc.WebviewIssuanceService;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.ui.ExtendedModelMap;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class WebviewIssuanceControllerTest {
    WebviewIssuanceService service;
    WebviewIssuanceController controller;

    @BeforeEach
    void setUp() {
        service = mock(WebviewIssuanceService.class);
        controller = new WebviewIssuanceController(service);
    }

    @Test
    void rendersWebviewStartPage() {
        when(service.start("user123", "UniversityDegreeCredential", null)).thenReturn(page());
        ExtendedModelMap model = new ExtendedModelMap();
        MockHttpServletResponse response = new MockHttpServletResponse();

        String view = controller.start("user123", null,"UniversityDegreeCredential", null, model, response);

        assertEquals("oid4vci-issuance", view);
        assertEquals(page(), model.get("page"));
        assertEquals("private, no-store, no-cache, must-revalidate, max-age=0",
                response.getHeader("Cache-Control"));
        assertEquals(0, response.getDateHeader("Expires"));
        assertEquals("no-store", response.getHeader("Surrogate-Control"));
        assertEquals("no-referrer", response.getHeader("Referrer-Policy"));
    }

    @Test
    void redirectsToCredentialOfferSchemeAfterConfirm() {
        String uri = "openid-credential-offer://?credential_offer_uri=https%3A%2F%2Fissuer.example%2Foffer";
        when(service.confirm(eq("session"), eq("csrf"), anyMap())).thenReturn(uri);

        Object result = controller.confirm("session", "csrf",
                Map.of("claim.given_name", "Gilwoo"), new ExtendedModelMap(),
                new MockHttpServletResponse());

        assertInstanceOf(ResponseEntity.class, result);
        ResponseEntity<?> response = (ResponseEntity<?>) result;
        assertEquals(302, response.getStatusCode().value());
        assertEquals(uri, response.getHeaders().getLocation().toString());
    }

    @Test
    void cancellationDoesNotRedirectToOffer() {
        String view = controller.cancel("session", "csrf", new ExtendedModelMap(),
                new MockHttpServletResponse());

        assertEquals("oid4vci-issuance-canceled", view);
        verify(service).cancel("session", "csrf");
        verify(service, never()).confirm(anyString(), anyString(), anyMap());
    }

    private WebviewIssuancePage page() {
        return new WebviewIssuancePage("session", "csrf", "Issuer", "Degree", "dc+sd-jwt",
                List.of(new WebviewClaimField("given_name", "Given name", "string", true, "Gilwoo")),
                null);
    }
}

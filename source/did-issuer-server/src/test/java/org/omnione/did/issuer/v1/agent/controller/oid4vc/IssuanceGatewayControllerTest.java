package org.omnione.did.issuer.v1.agent.controller.oid4vc;

import org.junit.jupiter.api.Test;
import org.omnione.did.base.datamodel.data.oid4vci.IssuerMetadata;
import org.omnione.did.issuer.v1.admin.service.Oid4vciConfigService;
import org.omnione.did.oid4vc.oid4vci.service.IssuanceGatewayService;
import org.springframework.http.ResponseEntity;

import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class IssuanceGatewayControllerTest {

    @Test
    void passesCurrentDatabaseIssuerUrlToEachQrRequest() throws Exception {
        IssuanceGatewayService gateway = mock(IssuanceGatewayService.class);
        Oid4vciConfigService configService = mock(Oid4vciConfigService.class);
        when(configService.getMetadata()).thenReturn(
                IssuerMetadata.builder().credentialIssuer("http://192.168.3.175:8091").build(),
                IssuerMetadata.builder().credentialIssuer("https://issuer.example").build());
        when(gateway.generateCredentialOfferUri("user", "authorization_code", "reference",
                "openid-credential-offer:", "credential-id", "http://192.168.3.175:8091"))
                .thenReturn(new HashMap<>(Map.of("qrData", "offer-uri", "qrImage", new byte[]{1, 2, 3})));
        when(gateway.generateCredentialOfferUri("user", "authorization_code", "reference",
                "openid-credential-offer:", "credential-id", "https://issuer.example"))
                .thenReturn(new HashMap<>(Map.of("qrData", "updated-offer-uri", "qrImage", new byte[]{1, 2, 3})));

        IssuanceGatewayController controller = new IssuanceGatewayController(gateway, configService);
        Map<String, String> request = Map.of(
                "userId", "user", "grantType", "authorization_code", "offerType", "reference",
                "scheme", "openid-credential-offer:", "credentialConfigurationId", "credential-id");
        ResponseEntity<Map<String, Object>> response = controller.generateQrData(request);
        ResponseEntity<Map<String, Object>> updatedResponse = controller.generateQrData(request);

        assertEquals(200, response.getStatusCode().value());
        assertEquals("offer-uri", response.getBody().get("qrData"));
        assertEquals("AQID", response.getBody().get("qrImage"));
        assertEquals("updated-offer-uri", updatedResponse.getBody().get("qrData"));
        verify(configService, org.mockito.Mockito.times(2)).getMetadata();
    }
}

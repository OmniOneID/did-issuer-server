package org.omnione.did.issuer.v1.agent.controller.oid4vc;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.zxing.BinaryBitmap;
import com.google.zxing.MultiFormatReader;
import com.google.zxing.client.j2se.BufferedImageLuminanceSource;
import com.google.zxing.common.HybridBinarizer;
import org.junit.jupiter.api.Test;
import org.omnione.did.base.datamodel.data.oid4vci.IssuerMetadata;
import org.omnione.did.issuer.v1.admin.service.Oid4vciConfigService;
import org.omnione.did.oid4vc.oid4vci.service.IssuanceGatewayService;
import org.springframework.http.ResponseEntity;

import javax.imageio.ImageIO;
import java.io.ByteArrayInputStream;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class IssuanceGatewayControllerTest {
    private static final String SCHEME = "openid-credential-offer://";
    private static final String REFERENCE_URI = SCHEME
            + "?credential_offer_uri=http://old.example:8091/credential-offer/offer-id/credential-id";

    @Test
    void usesCurrentDatabaseUrlForEachReferenceQrRequest() throws Exception {
        IssuanceGatewayService gateway = mock(IssuanceGatewayService.class);
        Oid4vciConfigService config = mock(Oid4vciConfigService.class);
        when(config.getMetadata()).thenReturn(
                IssuerMetadata.builder().credentialIssuer("http://127.0.0.1:8091").build(),
                IssuerMetadata.builder().credentialIssuer("https://issuer.example/").build());
        when(gateway.generateCredentialOfferUri("user", "authorization_code", "reference",
                SCHEME, "credential-id")).thenAnswer(invocation -> sdkResponse(REFERENCE_URI));
        IssuanceGatewayController controller = new IssuanceGatewayController(gateway, config, new ObjectMapper());

        ResponseEntity<Map<String, Object>> first = controller.generateQrData(request("reference"));
        ResponseEntity<Map<String, Object>> second = controller.generateQrData(request("reference"));

        assertEquals(SCHEME + "?credential_offer_uri=http://127.0.0.1:8091/credential-offer/offer-id/credential-id",
                first.getBody().get("qrData"));
        assertEquals(SCHEME + "?credential_offer_uri=https://issuer.example/credential-offer/offer-id/credential-id",
                second.getBody().get("qrData"));
        assertEquals(first.getBody().get("qrData"), decodedQr(first));
        assertEquals(second.getBody().get("qrData"), decodedQr(second));
    }

    @Test
    void usesDatabaseUrlInValueOfferAndQrImage() throws Exception {
        IssuanceGatewayService gateway = mock(IssuanceGatewayService.class);
        Oid4vciConfigService config = mock(Oid4vciConfigService.class);
        when(config.getMetadata()).thenReturn(IssuerMetadata.builder()
                .credentialIssuer("http://127.0.0.1:8091").build());
        String offer = "{\"credential_issuer\":\"http://old.example:8091\",\"credential_configuration_ids\":[\"credential-id\"]}";
        String qrData = SCHEME + "credential_offer?credential_offer="
                + URLEncoder.encode(offer, StandardCharsets.UTF_8);
        when(gateway.generateCredentialOfferUri("user", "authorization_code", "value",
                SCHEME, "credential-id")).thenReturn(sdkResponse(qrData));
        IssuanceGatewayController controller = new IssuanceGatewayController(gateway, config, new ObjectMapper());

        ResponseEntity<Map<String, Object>> response = controller.generateQrData(request("value"));

        String updatedQrData = (String) response.getBody().get("qrData");
        String updatedOffer = URLDecoder.decode(updatedQrData.substring(updatedQrData.indexOf("credential_offer=")
                + "credential_offer=".length()), StandardCharsets.UTF_8);
        assertEquals("http://127.0.0.1:8091",
                new ObjectMapper().readTree(updatedOffer).get("credential_issuer").asText());
        assertEquals(updatedQrData, decodedQr(response));
    }

    private Map<String, String> request(String offerType) {
        return Map.of("userId", "user", "grantType", "authorization_code", "offerType", offerType,
                "scheme", SCHEME, "credentialConfigurationId", "credential-id");
    }

    private Map<String, Object> sdkResponse(String qrData) {
        return new HashMap<>(Map.of("qrData", qrData, "qrImage", new byte[]{1, 2, 3}));
    }

    private String decodedQr(ResponseEntity<Map<String, Object>> response) throws Exception {
        byte[] image = Base64.getDecoder().decode((String) response.getBody().get("qrImage"));
        BinaryBitmap bitmap = new BinaryBitmap(new HybridBinarizer(new BufferedImageLuminanceSource(
                ImageIO.read(new ByteArrayInputStream(image)))));
        return new MultiFormatReader().decode(bitmap).getText();
    }
}

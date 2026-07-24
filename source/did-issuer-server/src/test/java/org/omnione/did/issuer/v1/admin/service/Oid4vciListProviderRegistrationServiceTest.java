package oid4vci;

import feign.FeignException;
import feign.Request;
import feign.Response;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.omnione.did.base.config.GsonConfig;
import org.omnione.did.base.db.repository.ServerConfigRepository;
import org.omnione.did.base.exception.ErrorCode;
import org.omnione.did.base.exception.OpenDidException;
import org.omnione.did.issuer.v1.admin.api.Oid4vciListProviderFeign;
import org.omnione.did.issuer.v1.admin.dto.oid4vci.ListProviderRegistrationReqDto;
import org.omnione.did.issuer.v1.admin.dto.oid4vci.ListProviderRegistrationResultDto;
import org.omnione.did.issuer.v1.admin.service.Oid4vciListProviderRegistrationService;
import org.omnione.did.issuer.v1.agent.service.query.IssuerInfoQueryService;
import org.omnione.did.base.db.domain.IssuerInfo;
import org.springframework.test.util.ReflectionTestUtils;

import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class Oid4vciListProviderRegistrationServiceTest {
    @Mock Oid4vciListProviderFeign listProviderFeign;
    @Mock IssuerInfoQueryService issuerInfoQueryService;
    @Mock ServerConfigRepository serverConfigRepository;
    private Oid4vciListProviderRegistrationService service;

    @BeforeEach
    void setUp() {
        service = new Oid4vciListProviderRegistrationService(
                listProviderFeign, issuerInfoQueryService,
                serverConfigRepository, new GsonConfig().gson());
        ReflectionTestUtils.setField(service, "listProviderUrl", "http://localhost:8090");
        ReflectionTestUtils.setField(service, "issuerBaseUrl", "http://localhost:8091");
    }

    @Test
    void buildsDefaultsFromIssuerMetadata() {
        when(serverConfigRepository.findByConfigKey(any())).thenReturn(Optional.empty());
        IssuerInfo issuerInfo = new IssuerInfo();
        issuerInfo.setDid("did:example:issuer");
        when(issuerInfoQueryService.getIssuerInfo()).thenReturn(issuerInfo);

        var registration = service.getRegistration();

        assertEquals("did:example:issuer", registration.getCredentialIssuer());
        assertEquals("http://localhost:8091/.well-known/openid-credential-issuer",
                registration.getCredentialIssuerMetadataUri());
        assertEquals("http://localhost:8091/oid4vci/issuance/start",
                registration.getUserInitiationUri());
    }

    @Test
    void savesRequestedRegistration() {
        var request = ListProviderRegistrationReqDto.builder()
                .credentialIssuer("did:example:issuer")
                .credentialIssuerMetadataUri("http://localhost:8091/.well-known/openid-credential-issuer")
                .userInitiationUri("http://localhost:8091/oid4vci/test")
                .build();
        when(listProviderFeign.registerIssuer(request)).thenReturn(
                ListProviderRegistrationResultDto.builder().id(17L).status("REQUESTED").build());
        when(serverConfigRepository.findByConfigKey(any())).thenReturn(Optional.empty());

        var result = service.register(request);

        assertEquals(17L, result.getRegistrationId());
        assertEquals("REQUESTED", result.getStatus());
        verify(serverConfigRepository).save(any());
    }

    @Test
    void mapsListProviderConflict() {
        var request = ListProviderRegistrationReqDto.builder()
                .credentialIssuer("did:example:issuer")
                .credentialIssuerMetadataUri("http://localhost:8091/metadata")
                .userInitiationUri("http://localhost:8091/start")
                .build();
        when(listProviderFeign.registerIssuer(request)).thenThrow(conflict());

        OpenDidException exception = assertThrows(OpenDidException.class,
                () -> service.register(request));

        assertEquals(ErrorCode.OID4VCI_LIST_ISSUER_ALREADY_REGISTERED, exception.getErrorCode());
        verify(serverConfigRepository, never()).save(any());
    }

    private FeignException conflict() {
        Request request = Request.create(
                Request.HttpMethod.POST,
                "http://localhost:8090/list/api/v1/oid4vci/issuers",
                Map.of(),
                null,
                StandardCharsets.UTF_8,
                null);
        Response response = Response.builder()
                .status(409)
                .reason("Conflict")
                .request(request)
                .headers(Map.of())
                .build();
        return FeignException.errorStatus("registerIssuer", response);
    }
}

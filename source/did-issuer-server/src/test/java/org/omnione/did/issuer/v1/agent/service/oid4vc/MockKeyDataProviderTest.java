package org.omnione.did.issuer.v1.agent.service.oid4vc;

import org.junit.jupiter.api.Test;
import org.omnione.did.base.datamodel.data.oid4vci.SdkCredentialConfig;
import org.omnione.did.issuer.v1.admin.service.Oid4vciConfigService;
import org.omnione.did.issuer.v1.agent.service.FileWalletService;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class MockKeyDataProviderTest {

    private final FileWalletService fileWalletService = mock(FileWalletService.class);
    private final Oid4vciConfigService configService = mock(Oid4vciConfigService.class);
    private final MockKeyDataProvider provider =
            new MockKeyDataProvider(fileWalletService, configService);

    @Test
    void resolvesDoctypeForMdocConfiguration() throws IOException {
        when(configService.getSdkCredentialConfigs()).thenReturn(Map.of(
                "mDocPID", credentialConfig("mso_mdoc-did", """
                        {"doctype":"eu.europa.ec.eudi.pid.1"}
                        """)));

        assertEquals(
                "eu.europa.ec.eudi.pid.1",
                provider.resolveCredentialSchemaUrl("mDocPID"));
    }

    @Test
    void resolvesVctForSdJwtConfiguration() throws IOException {
        when(configService.getSdkCredentialConfigs()).thenReturn(Map.of(
                "VerifiableIdSD", credentialConfig("dc+sd-jwt-did", List.of("NationalID"), """
                        {"vct":"urn:eudi:pid:1"}
                        """)));

        assertEquals("urn:eudi:pid:1", provider.resolveCredentialSchemaUrl("NationalID"));
    }

    @Test
    void rejectsUnknownCredentialType() throws IOException {
        when(configService.getSdkCredentialConfigs()).thenReturn(Map.of());

        assertThrows(
                IllegalArgumentException.class,
                () -> provider.resolveCredentialSchemaUrl("unknown"));
    }

    @Test
    void recognizesInternalStatusListCredentialType() {
        assertTrue(provider.isStatusListCredentialType("StatusList"));
    }

    private SdkCredentialConfig credentialConfig(String format, String metadataJson) {
        return credentialConfig(format, List.of(), metadataJson);
    }

    private SdkCredentialConfig credentialConfig(
            String format, List<String> identifiers, String metadataJson) {
        return SdkCredentialConfig.builder()
                .format(format)
                .identifiers(identifiers)
                .metadataJson(metadataJson)
                .build();
    }
}

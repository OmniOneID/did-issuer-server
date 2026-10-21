package org.omnione.did.issuer.v1.admin.service;

import com.google.gson.Gson;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.omnione.did.base.constants.ConfigType;
import org.omnione.did.base.datamodel.data.oid4vci.IssuerMetadata;
import org.omnione.did.base.datamodel.data.oid4vci.SdkCredentialConfig;
import org.omnione.did.base.db.domain.CredentialConfig;
import org.omnione.did.base.db.domain.ServerConfig;
import org.omnione.did.base.db.repository.CredentialConfigRepository;
import org.omnione.did.base.db.repository.ServerConfigRepository;
import org.springframework.test.util.ReflectionTestUtils;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatIllegalArgumentException;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class Oid4vciConfigServiceTest {

    @Mock
    private ServerConfigRepository serverConfigRepository;
    @Mock
    private CredentialConfigRepository credentialConfigRepository;

    private Oid4vciConfigService service;

    @TempDir
    Path tempDir;

    @BeforeEach
    void setUp() {
        service = new Oid4vciConfigService(
                serverConfigRepository, credentialConfigRepository, new Gson());
    }

    @Test
    void replacesConfiguredRevocationEndpointWithFixedIssuerEndpoint() {
        ServerConfig config = ServerConfig.builder()
                .configKey(ConfigType.OID4VCI_METADATA.name())
                .configValue("""
                        {
                          "credentialIssuer": "https://issuer.example.com/",
                          "credentialRevocationEndpoint": "https://attacker.example/revoke"
                        }
                        """)
                .build();
        when(serverConfigRepository.findByConfigKey(ConfigType.OID4VCI_METADATA.name()))
                .thenReturn(Optional.of(config));

        IssuerMetadata metadata = service.getMetadata();

        assertThat(metadata.getCredentialRevocationEndpoint())
                .isEqualTo("https://issuer.example.com/credential-lifecycle/revoke");
    }

    @Test
    void updatesCredentialConfigAndIssuerMetadataInOneOperation() throws Exception {
        Path metadataFile = tempDir.resolve("issuer_meta.json");
        ReflectionTestUtils.setField(service, "metadataPath", metadataFile.toString());
        CredentialConfig entity = CredentialConfig.builder()
                .id("StudentID")
                .format("dc+sd-jwt-did")
                .identifiers(List.of("student_id"))
                .metadataJson("{\"format\":\"old\"}")
                .enabled(true)
                .build();
        ServerConfig metadataConfig = ServerConfig.builder()
                .configKey(ConfigType.OID4VCI_METADATA.name())
                .configValue("{\"credentialIssuer\":\"https://issuer.example.com\"}")
                .build();
        when(credentialConfigRepository.findAll()).thenReturn(List.of(entity));
        when(credentialConfigRepository.findById("StudentID")).thenReturn(Optional.of(entity));
        when(serverConfigRepository.findByConfigKey(ConfigType.OID4VCI_METADATA.name()))
                .thenReturn(Optional.of(metadataConfig));

        service.updateSdkCredentialConfigs(Map.of("StudentID", SdkCredentialConfig.builder()
                .id("StudentID")
                .format("dc+sd-jwt-did")
                .identifiers(List.of("student_id"))
                .metadataJson("{\"format\":\"dc+sd-jwt-did\",\"scope\":\"student\"}")
                .build()));

        assertThat(entity.getMetadataJson()).contains("student");
        IssuerMetadata savedMetadata = new Gson().fromJson(metadataConfig.getConfigValue(), IssuerMetadata.class);
        assertThat(savedMetadata.getCredentialConfigurationsSupported())
                .containsKey("StudentID");
        Map<?, ?> savedCredentialMetadata =
                (Map<?, ?>) savedMetadata.getCredentialConfigurationsSupported().get("StudentID");
        assertThat(savedCredentialMetadata.get("scope")).isEqualTo("student");
        assertThat(Files.readString(metadataFile))
                .contains("\"credential_configurations_supported\"")
                .contains("\"scope\": \"student\"");
        verify(credentialConfigRepository).flush();
        verify(serverConfigRepository).save(metadataConfig);
    }

    @Test
    void rejectsNonObjectCredentialMetadataBeforeWriting() {
        Map<String, SdkCredentialConfig> configs = Map.of("StudentID", SdkCredentialConfig.builder()
                .format("dc+sd-jwt-did")
                .metadataJson("[]")
                .build());

        assertThatIllegalArgumentException()
                .isThrownBy(() -> service.updateSdkCredentialConfigs(configs))
                .withMessageContaining("must be an object");

        verifyNoInteractions(serverConfigRepository, credentialConfigRepository);
    }

    @Test
    void keepsCredentialConfigurationsOwnedByCredentialConfigWhenUpdatingIssuerMetadata() throws Exception {
        CredentialConfig entity = CredentialConfig.builder()
                .id("StudentID")
                .format("dc+sd-jwt-did")
                .metadataJson("{\"format\":\"dc+sd-jwt-did\",\"scope\":\"current\"}")
                .enabled(true)
                .build();
        ServerConfig metadataConfig = ServerConfig.builder()
                .configKey(ConfigType.OID4VCI_METADATA.name())
                .configValue("{}")
                .build();
        when(credentialConfigRepository.findAll()).thenReturn(List.of(entity));
        when(serverConfigRepository.findByConfigKey(ConfigType.OID4VCI_METADATA.name()))
                .thenReturn(Optional.of(metadataConfig));
        IssuerMetadata request = IssuerMetadata.builder()
                .credentialIssuer("https://issuer.example.com")
                .credentialConfigurationsSupported(Map.of("StudentID", Map.of("scope", "stale")))
                .build();

        service.updateMetadata(request);

        Map<?, ?> currentCredentialMetadata =
                (Map<?, ?>) request.getCredentialConfigurationsSupported().get("StudentID");
        assertThat(currentCredentialMetadata.get("scope")).isEqualTo("current");
        verify(serverConfigRepository).save(metadataConfig);
    }
}

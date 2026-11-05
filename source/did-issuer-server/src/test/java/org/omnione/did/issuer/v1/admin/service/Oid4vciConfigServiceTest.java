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
import org.omnione.did.base.datamodel.data.oid4vci.Oid4vcProperty;
import org.omnione.did.base.datamodel.data.oid4vci.SdkCredentialConfig;
import org.omnione.did.base.db.domain.CredentialConfig;
import org.omnione.did.base.db.domain.ServerConfig;
import org.omnione.did.base.db.repository.CredentialConfigRepository;
import org.omnione.did.base.db.repository.ServerConfigRepository;
import org.omnione.did.oid4vc.authorization.authorization.config.Oid4vcAuthServerProperties;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.mock.env.MockEnvironment;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.transaction.support.TransactionSynchronizationManager;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatIllegalArgumentException;
import static org.assertj.core.api.Assertions.assertThatIllegalStateException;
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
                          "credentialEndpoint": "https://attacker.example/credential",
                          "credentialRevocationEndpoint": "https://attacker.example/revoke"
                        }
                        """)
                .build();
        when(serverConfigRepository.findByConfigKey(ConfigType.OID4VCI_METADATA.name()))
                .thenReturn(Optional.of(config));

        IssuerMetadata metadata = service.getMetadata();

        assertThat(metadata.getCredentialRevocationEndpoint())
                .isEqualTo("https://issuer.example.com/credential-lifecycle/revoke");
        assertThat(metadata.getCredentialEndpoint())
                .isEqualTo("https://issuer.example.com/credential");
    }

    @Test
    void initializesNewDatabaseFromSnakeCaseFileAndConfiguredBaseUrl() throws Exception {
        Path metadataFile = tempDir.resolve("issuer_meta.json");
        Files.writeString(metadataFile, """
                {
                  "credential_issuer": "${issuer.base-url}",
                  "authorization_server": ["${issuer.base-url}"],
                  "credential_offer_endpoint": "${issuer.base-url}/credential-offer",
                  "credential_endpoint": "${issuer.base-url}/credential",
                  "nonce_endpoint": "${issuer.base-url}/nonce",
                  "deferred_credential_endpoint": "${issuer.base-url}/deferred_credential",
                  "notification_endpoint": "${issuer.base-url}/notification",
                  "credential_request_encryption": {"enc_values_supported": ["A256GCM"]},
                  "credential_response_encryption": {"alg_values_supported": ["RSA-OAEP"]}
                }
                """);
        ReflectionTestUtils.setField(service, "metadataPath", metadataFile.toString());
        ReflectionTestUtils.setField(service, "env", new MockEnvironment()
                .withProperty("issuer.base-url", "https://issuer.example.com"));
        when(serverConfigRepository.findByConfigKey(ConfigType.OID4VCI_METADATA.name()))
                .thenReturn(Optional.empty());

        var endpointMetadata = service.getIssuerMetadata();
        var publicMetadata = service.getPublicMetadata();

        assertThat(endpointMetadata.getCredentialEndpoint()).isEqualTo("https://issuer.example.com/credential");
        assertThat(endpointMetadata.getAuthorizationServers()).containsExactly("https://issuer.example.com");
        assertThat(publicMetadata.getCredentialRequestEncryption()).isNotNull();
        assertThat(publicMetadata.getCredentialResponseEncryption()).isNotNull();
        verify(serverConfigRepository, atLeastOnce()).save(argThat(config ->
                config.getConfigValue().contains("\"credentialIssuer\":\"https://issuer.example.com\"")
                        && config.getConfigValue().contains("\"credentialEndpoint\":\"https://issuer.example.com/credential\"")));
    }

    @Test
    void doesNotSaveEmptyMetadataWhenInitialFileIsInvalid() throws Exception {
        Path metadataFile = tempDir.resolve("issuer_meta.json");
        Files.writeString(metadataFile, "{}");
        ReflectionTestUtils.setField(service, "metadataPath", metadataFile.toString());
        when(serverConfigRepository.findByConfigKey(ConfigType.OID4VCI_METADATA.name()))
                .thenReturn(Optional.empty());

        assertThatIllegalStateException().isThrownBy(service::getMetadata)
                .withMessageContaining("missing required fields");
        verify(serverConfigRepository, never()).save(any());
    }

    @Test
    void rejectsIssuerUrlWithPathThatWouldConflictWithFixedRoutes() {
        IssuerMetadata metadata = IssuerMetadata.builder()
                .credentialIssuer("https://issuer.example.com/issuer")
                .build();

        assertThatIllegalArgumentException().isThrownBy(() -> service.updateMetadata(metadata))
                .withMessageContaining("without a path");
        verifyNoInteractions(serverConfigRepository, credentialConfigRepository);
    }

    @Test
    void initializesPropertyDefaultsWithoutHardcodedServerAddressOrSharedSecret() throws Exception {
        ReflectionTestUtils.setField(service, "authConfigPath", tempDir.resolve("missing.yml").toString());
        ReflectionTestUtils.setField(service, "env", new MockEnvironment()
                .withProperty("issuer.base-url", "https://issuer.example.com")
                .withProperty("oid4vc.auth.issuer-url", "https://auth.example.com"));
        when(serverConfigRepository.findByConfigKey(ConfigType.OID4VCI_AUTH_PROPERTIES.name()))
                .thenReturn(Optional.empty());

        Oid4vcProperty properties = service.getProperties();

        assertThat(properties.getIssuerUrl()).isEqualTo("https://auth.example.com");
        assertThat(properties.getClients().getClientId()).isEqualTo("oid4vci-client");
        assertThat(properties.getClients().getRedirectUrl()).isEqualTo("https://issuer.example.com/auth/callback");
        assertThat(properties.getClients().getRedirectUris()).containsExactly("https://issuer.example.com/auth/callback");
        assertThat(properties.getClients().getScopes()).containsExactly("openid", "profile", "email");
        assertThat(properties.getClients().getAndroid().getRedirectUris())
                .containsExactly("org.omnione.did.sdk.oid4vc://callback");
        assertThat(properties.getClients().getIosOpenid().getClientId()).isEqualTo("oid4vci-ios-opendid");
        assertThat(properties.getClients().getClientSecret()).startsWith("{noop}").hasSize(49);
        verify(serverConfigRepository).save(argThat(config ->
                config.getConfigValue().contains("${issuer.base-url}/auth/callback")
                        && !config.getConfigValue().contains("10.48.17.128")));
    }

    @Test
    void preservesDynamicDefaultUrlWhenPropertyIsSavedFromAdminConsole() throws Exception {
        ReflectionTestUtils.setField(service, "env", new MockEnvironment()
                .withProperty("issuer.base-url", "https://issuer.example.com"));
        when(serverConfigRepository.findByConfigKey(ConfigType.OID4VCI_AUTH_PROPERTIES.name()))
                .thenReturn(Optional.empty());
        Oid4vcProperty properties = Oid4vcProperty.builder()
                .issuerUrl("https://issuer.example.com")
                .clients(Oid4vcProperty.Clients.builder()
                        .clientId("oid4vci-client")
                        .clientSecret("{noop}test-secret")
                        .redirectUrl("https://issuer.example.com/auth/callback")
                        .redirectUris(List.of("https://issuer.example.com/auth/callback"))
                        .build())
                .build();

        service.updateProperties(properties);

        verify(serverConfigRepository).save(argThat(config ->
                config.getConfigValue().contains("${issuer.base-url}/auth/callback")));
    }

    @Test
    void resolvesStoredPropertyUrlUsingCurrentServerAddress() throws Exception {
        ReflectionTestUtils.setField(service, "env", new MockEnvironment()
                .withProperty("issuer.base-url", "https://new.example.com"));
        ServerConfig config = ServerConfig.builder()
                .configKey(ConfigType.OID4VCI_AUTH_PROPERTIES.name())
                .configValue("""
                        {"issuerUrl":"${issuer.base-url}","clients":{
                          "redirectUrl":"${issuer.base-url}/auth/callback",
                          "redirectUris":["${issuer.base-url}/auth/callback"]}}
                        """)
                .build();
        when(serverConfigRepository.findByConfigKey(ConfigType.OID4VCI_AUTH_PROPERTIES.name()))
                .thenReturn(Optional.of(config));

        Oid4vcProperty properties = service.getProperties();

        assertThat(properties.getIssuerUrl()).isEqualTo("https://new.example.com");
        assertThat(properties.getClients().getRedirectUrl())
                .isEqualTo("https://new.example.com/auth/callback");
        verify(serverConfigRepository, never()).save(any());
    }

    @Test
    void reportsMissingIssuerUrlConfigurationBeforeSecurityFilterCreation() {
        ReflectionTestUtils.setField(service, "env", new MockEnvironment());
        ServerConfig config = ServerConfig.builder()
                .configKey(ConfigType.OID4VCI_AUTH_PROPERTIES.name())
                .configValue("{\"issuerUrl\":\"${issuer.base-url}\"}")
                .build();
        when(serverConfigRepository.findByConfigKey(ConfigType.OID4VCI_AUTH_PROPERTIES.name()))
                .thenReturn(Optional.of(config));

        assertThatIllegalStateException().isThrownBy(service::getProperties)
                .withMessageContaining("Configure oid4vc.auth.issuer-url or issuer.base-url");
    }

    @Test
    void usesProfileIssuerUrlWhenStoredIssuerUrlIsBlankAtStartup() throws Exception {
        ReflectionTestUtils.setField(service, "env", new MockEnvironment()
                .withProperty("oid4vc.auth.issuer-url", "https://auth.example.com"));
        Oid4vcAuthServerProperties authProperties = new Oid4vcAuthServerProperties();
        ReflectionTestUtils.setField(service, "authServerProperties", authProperties);
        ServerConfig config = ServerConfig.builder()
                .configKey(ConfigType.OID4VCI_AUTH_PROPERTIES.name())
                .configValue("{\"issuerUrl\":\"\",\"clients\":{\"clientId\":\"existing-client\"}}")
                .build();
        when(serverConfigRepository.findByConfigKey(ConfigType.OID4VCI_AUTH_PROPERTIES.name()))
                .thenReturn(Optional.of(config));

        service.initSdkProperties();

        assertThat(authProperties.getIssuerUrl()).isEqualTo("https://auth.example.com");
        assertThat(authProperties.getClients().getClientId()).isEqualTo("existing-client");
        verify(serverConfigRepository, never()).save(any());
    }

    @Test
    void savesDefaultIssuerUrlTemplateWhenAdminSubmitsBlankIssuerUrl() throws Exception {
        ReflectionTestUtils.setField(service, "env", new MockEnvironment()
                .withProperty("issuer.base-url", "https://issuer.example.com"));
        when(serverConfigRepository.findByConfigKey(ConfigType.OID4VCI_AUTH_PROPERTIES.name()))
                .thenReturn(Optional.empty());
        Oid4vcProperty properties = Oid4vcProperty.builder()
                .issuerUrl("")
                .clients(Oid4vcProperty.Clients.builder()
                        .clientId("existing-client")
                        .clientSecret("{noop}test-secret")
                        .redirectUrl("https://issuer.example.com/auth/callback")
                        .build())
                .build();

        service.updateProperties(properties);

        verify(serverConfigRepository).save(argThat(config ->
                config.getConfigValue().contains("${oid4vc.auth.issuer-url:${issuer.base-url}}")));
    }

    @Test
    void refreshesRuntimePropertiesOnlyAfterDatabaseCommit() throws Exception {
        ReflectionTestUtils.setField(service, "env", new MockEnvironment()
                .withProperty("issuer.base-url", "https://issuer.example.com"));
        ApplicationEventPublisher publisher = mock(ApplicationEventPublisher.class);
        ReflectionTestUtils.setField(service, "eventPublisher", publisher);
        Oid4vcAuthServerProperties authProperties = new Oid4vcAuthServerProperties();
        ReflectionTestUtils.setField(service, "authServerProperties", authProperties);
        ServerConfig config = ServerConfig.builder()
                .configKey(ConfigType.OID4VCI_AUTH_PROPERTIES.name())
                .build();
        when(serverConfigRepository.findByConfigKey(ConfigType.OID4VCI_AUTH_PROPERTIES.name()))
                .thenReturn(Optional.of(config));
        Oid4vcProperty properties = Oid4vcProperty.builder()
                .issuerUrl("https://issuer.example.com")
                .clients(Oid4vcProperty.Clients.builder()
                        .clientId("updated-client")
                        .clientSecret("{noop}updated-secret")
                        .redirectUrl("https://issuer.example.com/auth/callback")
                        .build())
                .build();

        TransactionSynchronizationManager.initSynchronization();
        try {
            service.updateProperties(properties);
            verifyNoInteractions(publisher);
            assertThat(authProperties.getClients().getClientId()).isEqualTo("oid4vci-client");
            TransactionSynchronizationManager.getSynchronizations()
                    .forEach(synchronization -> synchronization.afterCommit());
            verify(publisher).publishEvent(any(Oid4vciConfigService.PropertiesUpdatedEvent.class));
            assertThat(authProperties.getClients().getClientId()).isEqualTo("updated-client");
            assertThat(authProperties.getClients().getRedirectUrl())
                    .isEqualTo("https://issuer.example.com/auth/callback");
        } finally {
            TransactionSynchronizationManager.clearSynchronization();
        }
    }

    @Test
    void preservesExistingPropertyInsteadOfReapplyingDefaults() throws Exception {
        ServerConfig config = ServerConfig.builder()
                .configKey(ConfigType.OID4VCI_AUTH_PROPERTIES.name())
                .configValue("{\"issuerUrl\":\"https://custom.example.com\",\"clients\":{\"clientId\":\"custom\"}}")
                .build();
        when(serverConfigRepository.findByConfigKey(ConfigType.OID4VCI_AUTH_PROPERTIES.name()))
                .thenReturn(Optional.of(config));

        Oid4vcProperty properties = service.getProperties();

        assertThat(properties.getIssuerUrl()).isEqualTo("https://custom.example.com");
        assertThat(properties.getClients().getClientId()).isEqualTo("custom");
        verify(serverConfigRepository, never()).save(any());
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
                .credentialEndpoint("https://other.example.com/credential")
                .credentialConfigurationsSupported(Map.of("StudentID", Map.of("scope", "stale")))
                .build();

        service.updateMetadata(request);

        Map<?, ?> currentCredentialMetadata =
                (Map<?, ?>) request.getCredentialConfigurationsSupported().get("StudentID");
        assertThat(currentCredentialMetadata.get("scope")).isEqualTo("current");
        assertThat(request.getCredentialEndpoint()).isEqualTo("https://issuer.example.com/credential");
        verify(serverConfigRepository).save(metadataConfig);
    }
}

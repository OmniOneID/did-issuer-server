package org.omnione.did.issuer.v1.admin.service;

import com.google.gson.Gson;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.omnione.did.base.constants.ConfigType;
import org.omnione.did.base.datamodel.data.oid4vci.IssuerMetadata;
import org.omnione.did.base.datamodel.data.oid4vci.Oid4vcProperty;
import org.omnione.did.base.datamodel.data.oid4vci.SdkCredentialConfig;
import org.omnione.did.base.datamodel.data.oid4vci.dto.Oid4vciMetadataResponse;
import org.omnione.did.base.db.domain.CredentialConfig;
import org.omnione.did.base.db.domain.ServerConfig;
import org.omnione.did.base.db.repository.CredentialConfigRepository;
import org.omnione.did.base.db.repository.ServerConfigRepository;
import org.omnione.did.base.util.SerializationUtils;
import org.omnione.did.common.util.JsonUtil;
import org.omnione.did.oid4vc.authorization.authorization.config.Oid4vcAuthServerProperties;
import org.omnione.did.oid4vc.oid4vci.config.IssuerSdkProperties;
import org.omnione.did.oid4vc.oid4vci.dto.metadata.IssuerMetadataResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionSynchronization;
import org.springframework.transaction.support.TransactionSynchronizationManager;
import org.springframework.util.StringUtils;

import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@RequiredArgsConstructor
@Service
public class Oid4vciConfigService {

    public static final String CREDENTIAL_REVOCATION_PATH = "/credential-lifecycle/revoke";

    private final ServerConfigRepository serverConfigRepository;
    private final CredentialConfigRepository credentialConfigRepository;
    private final Gson gson;

    @Autowired(required = false)
    private IssuerSdkProperties issuerSdkProperties;

    @Autowired(required = false)
    private Oid4vcAuthServerProperties authServerProperties;

    @Autowired
    private Environment env;

    private final java.util.concurrent.locks.Lock fileWriteLock = new java.util.concurrent.locks.ReentrantLock();

    @PostConstruct
    public void initSdkProperties() {
        if (issuerSdkProperties != null) {
            try {
                Map<String, SdkCredentialConfig> sdkConfigs = getSdkCredentialConfigs();
                syncProperties(sdkConfigs);
                log.info("Successfully initialized IssuerSdkProperties from DB on startup.");
            } catch (Exception e) {
                log.error("Failed to initialize IssuerSdkProperties from DB: {}", e.getMessage());
            }
        }

        if (authServerProperties != null) {
            try {
                Oid4vcProperty dbProperties = getProperties();
                syncAuthProperties(dbProperties);
                log.info("Successfully initialized Oid4vcAuthServerProperties from DB on startup.");
            } catch (Exception e) {
                log.error("Failed to initialize Oid4vcAuthServerProperties from DB: {}", e.getMessage());
            }
        }
    }

    private void syncProperties(Map<String, SdkCredentialConfig> configs) {
        if (issuerSdkProperties != null) {
            Map<String, IssuerSdkProperties.CredentialConfig> map = issuerSdkProperties.getCredentialConfigurations();
            if (map == null) {
                map = new HashMap<>();
                issuerSdkProperties.setCredentialConfigurations(map);
            }
            for (Map.Entry<String, SdkCredentialConfig> entry : configs.entrySet()) {
                SdkCredentialConfig sdkConfig = entry.getValue();
                IssuerSdkProperties.CredentialConfig config = map.getOrDefault(entry.getKey(), new IssuerSdkProperties.CredentialConfig());
                if (sdkConfig.getFormat() != null) {
                    config.setFormat(sdkConfig.getFormat());
                }
                if (sdkConfig.getIdentifiers() != null) {
                    config.setIdentifiers(new HashSet<>(sdkConfig.getIdentifiers()));
                } else if (config.getIdentifiers() == null) {
                    config.setIdentifiers(new HashSet<>());
                }
                map.put(entry.getKey(), config);
            }
        }
    }

    private String resolvePlaceholder(String text) {
        return text != null ? env.resolvePlaceholders(text) : null;
    }

    private List<String> resolvePlaceholders(List<String> list) {
        if (list == null) return null;
        return list.stream().map(this::resolvePlaceholder).collect(Collectors.toList());
    }

    private void syncClientPlatform(Oid4vcProperty.ClientPlatform source, Oid4vcAuthServerProperties.ClientDetail target) {
        if (source.getClientId() != null && !StringUtils.hasText(target.getClientId())) {
            target.setClientId(resolvePlaceholder(source.getClientId()));
        }
        if (source.getRedirectUris() != null && (target.getRedirectUris() == null || target.getRedirectUris().isEmpty())) {
            target.setRedirectUris(resolvePlaceholders(source.getRedirectUris()));
        }
        if (source.getScopes() != null && (target.getScopes() == null || target.getScopes().isEmpty())) {
            target.setScopes(source.getScopes());
        }
    }

    private void syncAuthProperties(Oid4vcProperty dbProps) {
        if (authServerProperties != null && dbProps != null) {
            if (dbProps.getIssuerUrl() != null && !StringUtils.hasText(authServerProperties.getIssuerUrl())) {
                authServerProperties.setIssuerUrl(resolvePlaceholder(dbProps.getIssuerUrl()));
            }
            if (dbProps.getClients() != null) {
                Oid4vcAuthServerProperties.Clients sdkClients = authServerProperties.getClients();
                if (sdkClients == null) {
                    sdkClients = new Oid4vcAuthServerProperties.Clients();
                    authServerProperties.setClients(sdkClients);
                }
                
                Oid4vcProperty.Clients dbClients = dbProps.getClients();
                if (dbClients.getClientId() != null && !StringUtils.hasText(sdkClients.getClientId())) {
                    sdkClients.setClientId(resolvePlaceholder(dbClients.getClientId()));
                }
                if (dbClients.getClientSecret() != null && !StringUtils.hasText(sdkClients.getClientSecret())) {
                    sdkClients.setClientSecret(resolvePlaceholder(dbClients.getClientSecret()));
                }
                if (dbClients.getRedirectUrl() != null && !StringUtils.hasText(sdkClients.getRedirectUrl())) {
                    sdkClients.setRedirectUrl(resolvePlaceholder(dbClients.getRedirectUrl()));
                }
                if (dbClients.getRedirectUris() != null && (sdkClients.getRedirectUris() == null || sdkClients.getRedirectUris().isEmpty())) {
                    sdkClients.setRedirectUris(resolvePlaceholders(dbClients.getRedirectUris()));
                }
                if (dbClients.getScopes() != null && (sdkClients.getScopes() == null || sdkClients.getScopes().isEmpty())) {
                    sdkClients.setScopes(dbClients.getScopes());
                }
                
                if (dbClients.getAndroid() != null) {
                    if (sdkClients.getAndroid() == null) {
                        sdkClients.setAndroid(new Oid4vcAuthServerProperties.ClientDetail());
                    }
                    syncClientPlatform(dbClients.getAndroid(), sdkClients.getAndroid());
                }
                
                if (dbClients.getAndroidOpenid() != null) {
                    if (sdkClients.getAndroidOpenid() == null) {
                        sdkClients.setAndroidOpenid(new Oid4vcAuthServerProperties.ClientDetail());
                    }
                    syncClientPlatform(dbClients.getAndroidOpenid(), sdkClients.getAndroidOpenid());
                }
                
                if (dbClients.getIos() != null) {
                    if (sdkClients.getIos() == null) {
                        sdkClients.setIos(new Oid4vcAuthServerProperties.ClientDetail());
                    }
                    syncClientPlatform(dbClients.getIos(), sdkClients.getIos());
                }
                
                if (dbClients.getIosOpenid() != null) {
                    if (sdkClients.getIosOpenid() == null) {
                        sdkClients.setIosOpenid(new Oid4vcAuthServerProperties.ClientDetail());
                    }
                    syncClientPlatform(dbClients.getIosOpenid(), sdkClients.getIosOpenid());
                }
                
                if (dbClients.getIssuerServer() != null) {
                    if (sdkClients.getIssuerServer() == null) {
                        sdkClients.setIssuerServer(new Oid4vcAuthServerProperties.Clients.IssuerServer());
                    }
                    if (dbClients.getIssuerServer().getUrl() != null && !StringUtils.hasText(sdkClients.getIssuerServer().getUrl())) {
                        sdkClients.getIssuerServer().setUrl(resolvePlaceholder(dbClients.getIssuerServer().getUrl()));
                    }
                }
            }
        }
    }

    @Value("${issuer.metadata-file-path:${oid4vci.metadata-path:oid4-issuer-server/metadata/issuer_meta_univ_local.json}}")
    private String metadataPath;

    @Value("${oid4vci.sdk-config-path:oid4-issuer-server/src/main/resources/application-issuer-sdk-local.yml}")
    private String sdkConfigPath;

    @Value("${oid4vci.auth-config-path:oid4-issuer-server/src/main/resources/application-authorization-server-local.yml}")
    private String authConfigPath;

    public IssuerMetadata getMetadata() {
        String key = ConfigType.OID4VCI_METADATA.name();
        Optional<ServerConfig> config = serverConfigRepository.findByConfigKey(key);
        try {
            if (config.isPresent()) {
                return applyFixedCredentialRevocationEndpoint(
                        new Gson().fromJson(config.get().getConfigValue(), IssuerMetadata.class));
            } else {
                IssuerMetadata metadata = applyFixedCredentialRevocationEndpoint(
                        JsonUtil.deserializeFromJson(metadataPath, IssuerMetadata.class));
                saveToDb(key, metadata, "OID4VCI Issuer Metadata");
                return metadata;
            }
        } catch (Exception e) {
            log.warn("Failed to load metadata from file {}, returning empty metadata", metadataPath);
            return new IssuerMetadata();
        }
    }
    public IssuerMetadataResponse getIssuerMetadata() {
        String key = ConfigType.OID4VCI_METADATA.name();
        Optional<ServerConfig> config = serverConfigRepository.findByConfigKey(key);
        try {
            if (config.isPresent()) {
                return applyFixedCredentialRevocationEndpoint(
                        new Gson().fromJson(config.get().getConfigValue(), IssuerMetadataResponse.class));
            } else {
                IssuerMetadataResponse metadata = applyFixedCredentialRevocationEndpoint(
                        JsonUtil.deserializeFromJson(metadataPath, IssuerMetadataResponse.class));
                saveToDb(key, metadata, "OID4VCI Issuer Metadata");
                return metadata;
            }
        } catch (Exception e) {
            log.warn("Failed to load metadata from file {}, returning empty metadata", metadataPath);
            return new IssuerMetadataResponse();
        }
    }

    @Transactional(rollbackFor = Exception.class)
    public void updateMetadata(IssuerMetadata metadata) throws IOException {
        applyFixedCredentialRevocationEndpoint(metadata);
        metadata.setCredentialConfigurationsSupported(loadCredentialConfigurationsFromDb());
        saveToDb(ConfigType.OID4VCI_METADATA.name(), metadata, "OID4VCI Issuer Metadata");

        syncMetadataToFileAfterCommit(metadata);
    }

    private void syncMetadataToFileAfterCommit(IssuerMetadata metadata) {
        if (metadataPath == null || metadataPath.isEmpty()) {
            log.warn("Metadata file path is not configured. Skipping file synchronization.");
            return;
        }

        if (TransactionSynchronizationManager.isSynchronizationActive()) {
            TransactionSynchronizationManager.registerSynchronization(new TransactionSynchronization() {
                @Override
                public void afterCommit() {
                    writeCurrentMetadataFile();
                }
            });
        } else {
            writeMetadataFile(serializeMetadata(metadata));
        }
    }

    private void writeCurrentMetadataFile() {
        writeMetadataFile(serializeMetadata(getMetadata()));
    }

    private String serializeMetadata(IssuerMetadata metadata) {
        return new com.google.gson.GsonBuilder()
                .setFieldNamingPolicy(com.google.gson.FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES)
                .setPrettyPrinting()
                .create()
                .toJson(metadata);
    }

    private void writeMetadataFile(String jsonContent) {
        fileWriteLock.lock();
        java.nio.file.Path tempFile = null;
        try {
            java.nio.file.Path path = java.nio.file.Paths.get(metadataPath).toAbsolutePath().normalize();
            java.nio.file.Path parentDir = path.getParent();

            if (parentDir != null) {
                java.nio.file.Files.createDirectories(parentDir);
            }

            tempFile = java.nio.file.Files.createTempFile(parentDir, path.getFileName().toString(), ".tmp");
            java.nio.file.Files.writeString(tempFile, jsonContent, java.nio.charset.StandardCharsets.UTF_8);
            try {
                java.nio.file.Files.move(tempFile, path,
                        java.nio.file.StandardCopyOption.REPLACE_EXISTING,
                        java.nio.file.StandardCopyOption.ATOMIC_MOVE);
            } catch (java.nio.file.AtomicMoveNotSupportedException exception) {
                java.nio.file.Files.move(tempFile, path, java.nio.file.StandardCopyOption.REPLACE_EXISTING);
            }
            log.info("Successfully synchronized OID4VCI Issuer Metadata to file: {}", metadataPath);
        } catch (Exception e) {
            log.error("Failed to synchronize metadata to file: [{}]. Reason: {}", metadataPath, e.getMessage(), e);
        } finally {
            if (tempFile != null) {
                try {
                    java.nio.file.Files.deleteIfExists(tempFile);
                } catch (IOException exception) {
                    log.warn("Failed to clean up temporary metadata file: {}", tempFile, exception);
                }
            }
            fileWriteLock.unlock();
        }
    }

    public Oid4vciMetadataResponse getPublicMetadata() throws IOException {
        IssuerMetadata adminMetadata = getMetadata();
        List<CredentialConfig> configs = credentialConfigRepository.findAll();

        Map<String, Object> credentialConfigurationsSupported = new HashMap<>();
        for (CredentialConfig config : configs) {
            if (config.getEnabled() != null && config.getEnabled()) {
                try {
                    Map<String, Object> metaMap = new Gson().fromJson(config.getMetadataJson(), Map.class);
                    credentialConfigurationsSupported.put(config.getId(), metaMap);
                } catch (Exception e) {
                    log.warn("Failed to parse metadataJson for config {}: {}", config.getId(), e.getMessage());
                }
            }
        }

        return Oid4vciMetadataResponse.builder()
                .credentialIssuer(adminMetadata.getCredentialIssuer())
                .authorizationServers(adminMetadata.getAuthorizationServer())
                .credentialOfferEndpoint(adminMetadata.getCredentialOfferEndpoint())
                .credentialEndpoint(adminMetadata.getCredentialEndpoint())
                .nonceEndpoint(adminMetadata.getNonceEndpoint())
                .credentialRevocationEndpoint(adminMetadata.getCredentialRevocationEndpoint())
                .deferredCredentialEndpoint(adminMetadata.getDeferredCredentialEndpoint())
                .notificationEndpoint(adminMetadata.getNotificationEndpoint())
                .credentialRequestEncryption(Oid4vciMetadataResponse.CredentialRequestEncryptionResponse.builder()
                        .encValuesSupported(adminMetadata.getCredentialRequestEncryption().getEncValuesSupported())
                        .encryptionRequired(adminMetadata.getCredentialRequestEncryption().isEncryptionRequired())
                        .build())
                .credentialResponseEncryption(Oid4vciMetadataResponse.CredentialResponseEncryptionResponse.builder()
                        .algValuesSupported(adminMetadata.getCredentialResponseEncryption().getAlgValuesSupported())
                        .encValuesSupported(adminMetadata.getCredentialResponseEncryption().getEncValuesSupported())
                        .encryptionRequired(adminMetadata.getCredentialResponseEncryption().isEncryptionRequired())
                        .build())
                .credentialConfigurationsSupported(credentialConfigurationsSupported)
                .build();
    }

    private IssuerMetadata applyFixedCredentialRevocationEndpoint(IssuerMetadata metadata) {
        metadata.setCredentialRevocationEndpoint(buildCredentialRevocationEndpoint(metadata.getCredentialIssuer()));
        return metadata;
    }

    private IssuerMetadataResponse applyFixedCredentialRevocationEndpoint(IssuerMetadataResponse metadata) {
        metadata.setCredentialRevocationEndpoint(buildCredentialRevocationEndpoint(metadata.getCredentialIssuer()));
        return metadata;
    }

    private String buildCredentialRevocationEndpoint(String credentialIssuer) {
        if (!StringUtils.hasText(credentialIssuer)) {
            return CREDENTIAL_REVOCATION_PATH;
        }

        String baseUrl = credentialIssuer.trim();
        while (baseUrl.endsWith("/")) {
            baseUrl = baseUrl.substring(0, baseUrl.length() - 1);
        }
        return baseUrl + CREDENTIAL_REVOCATION_PATH;
    }

    public Map<String, SdkCredentialConfig> getSdkCredentialConfigs() throws IOException {
        List<CredentialConfig> entities = credentialConfigRepository.findAll();
        if (!entities.isEmpty()) {
            return entities.stream().collect(Collectors.toMap(
                    CredentialConfig::getId,
                    entity -> SdkCredentialConfig.builder()
                            .id(entity.getId())
                            .format(entity.getFormat())
                            .identifiers(entity.getIdentifiers())
                            .metadataJson(entity.getMetadataJson())
                            .build()
            ));
        } else {
            try {
                Map<String, SdkCredentialConfig> configs = loadSdkConfigsFromFile();
                for (Map.Entry<String, SdkCredentialConfig> entry : configs.entrySet()) {
                    credentialConfigRepository.save(CredentialConfig.builder()
                            .id(entry.getKey())
                            .format(entry.getValue().getFormat())
                            .identifiers(entry.getValue().getIdentifiers())
                            .metadataJson(entry.getValue().getMetadataJson())
                            .enabled(true)
                            .build());
                }
                return configs;
            } catch (Exception e) {
                log.warn("Failed to load SDK configs from file {}, returning empty map", sdkConfigPath);
                return new HashMap<>();
            }
        }
    }

    @Transactional(rollbackFor = Exception.class)
    public void updateSdkCredentialConfigs(Map<String, SdkCredentialConfig> configs) throws IOException {
        validateCredentialConfigurations(configs);
        List<CredentialConfig> existingEntities = credentialConfigRepository.findAll();
        
        // Save or Update
        for (Map.Entry<String, SdkCredentialConfig> entry : configs.entrySet()) {
            CredentialConfig entity = credentialConfigRepository.findById(entry.getKey())
                    .orElse(CredentialConfig.builder().id(entry.getKey()).build());
            entity.setFormat(entry.getValue().getFormat());
            entity.setIdentifiers(entry.getValue().getIdentifiers());
            entity.setMetadataJson(entry.getValue().getMetadataJson());
            credentialConfigRepository.save(entity);
        }
        
        // Delete
        for (CredentialConfig existing : existingEntities) {
            if (!configs.containsKey(existing.getId())) {
                credentialConfigRepository.delete(existing);
            }
        }

        syncProperties(configs);

        credentialConfigRepository.flush();
        Map<String, Object> credentialConfigurations = loadCredentialConfigurationsFromDb();
        IssuerMetadata metadata = loadMetadataForSynchronization();
        metadata.setCredentialConfigurationsSupported(credentialConfigurations);
        saveToDb(ConfigType.OID4VCI_METADATA.name(), metadata, "OID4VCI Issuer Metadata");
        syncMetadataToFileAfterCommit(metadata);
    }

    private IssuerMetadata loadMetadataForSynchronization() {
        String key = ConfigType.OID4VCI_METADATA.name();
        try {
            IssuerMetadata metadata = serverConfigRepository.findByConfigKey(key)
                    .map(config -> gson.fromJson(config.getConfigValue(), IssuerMetadata.class))
                    .orElseGet(() -> JsonUtil.deserializeFromJson(metadataPath, IssuerMetadata.class));
            if (metadata == null) {
                throw new IllegalStateException("Issuer metadata is empty.");
            }
            return applyFixedCredentialRevocationEndpoint(metadata);
        } catch (Exception exception) {
            throw new IllegalStateException(
                    "Credential configurations were not saved because issuer metadata could not be loaded.",
                    exception);
        }
    }

    private Map<String, Object> loadCredentialConfigurationsFromDb() {
        Map<String, Object> credentialConfigurations = new LinkedHashMap<>();
        for (CredentialConfig config : credentialConfigRepository.findAll()) {
            if (Boolean.TRUE.equals(config.getEnabled())) {
                credentialConfigurations.put(config.getId(), parseCredentialMetadata(config.getId(), config.getMetadataJson()));
            }
        }
        return credentialConfigurations;
    }

    private void validateCredentialConfigurations(Map<String, SdkCredentialConfig> configs) {
        if (configs == null) {
            throw new IllegalArgumentException("Credential configurations must not be null.");
        }

        for (Map.Entry<String, SdkCredentialConfig> entry : configs.entrySet()) {
            String id = entry.getKey();
            SdkCredentialConfig config = entry.getValue();
            if (!StringUtils.hasText(id) || config == null) {
                throw new IllegalArgumentException("Credential configuration ID and value must not be empty.");
            }
            if (!StringUtils.hasText(config.getFormat())) {
                throw new IllegalArgumentException("Credential configuration format must not be empty: " + id);
            }
            parseCredentialMetadata(id, config.getMetadataJson());
        }
    }

    @SuppressWarnings("unchecked")
    private Map<String, Object> parseCredentialMetadata(String id, String metadataJson) {
        if (!StringUtils.hasText(metadataJson)) {
            throw new IllegalArgumentException("Credential metadata JSON must not be empty: " + id);
        }

        try {
            Object parsed = gson.fromJson(metadataJson, Object.class);
            if (!(parsed instanceof Map<?, ?>)) {
                throw new IllegalArgumentException("Credential metadata JSON must be an object: " + id);
            }
            return (Map<String, Object>) parsed;
        } catch (com.google.gson.JsonParseException exception) {
            throw new IllegalArgumentException("Invalid credential metadata JSON: " + id, exception);
        }
    }

    public Oid4vcProperty getProperties() throws IOException {
        String key = ConfigType.OID4VCI_AUTH_PROPERTIES.name();
        Optional<ServerConfig> config = serverConfigRepository.findByConfigKey(key);
        if (config.isPresent()) {
            return new Gson().fromJson(config.get().getConfigValue(), Oid4vcProperty.class);
        } else {
            try {
                Oid4vcProperty properties = loadPropertiesFromFile();
                saveToDb(key, properties, "OID4VCI Auth Properties");
                return properties;
            } catch (Exception e) {
                log.warn("Failed to load properties from file {}, returning empty properties", authConfigPath);
                return new Oid4vcProperty();
            }
        }
    }

    @Transactional
    public void updateProperties(Oid4vcProperty properties) throws IOException {
        saveToDb(ConfigType.OID4VCI_AUTH_PROPERTIES.name(), properties, "OID4VCI Auth Properties");
    }

    private void saveToDb(String key, Object value, String description) throws IOException {
        String jsonValue = JsonUtil.serializeToJson(value);
        ServerConfig config = serverConfigRepository.findByConfigKey(key)
                .orElse(ServerConfig.builder()
                        .configKey(key)
                        .description(description)
                        .build());
        config.setConfigValue(jsonValue);
        serverConfigRepository.save(config);
    }

    @SuppressWarnings("unchecked")
    private Map<String, SdkCredentialConfig> loadSdkConfigsFromFile() throws IOException {
        Map<String, Object> yaml = SerializationUtils.readYaml(sdkConfigPath, Map.class);
        Map<String, Object> issuer = (Map<String, Object>) yaml.get("issuer");
        Map<String, Object> sdk = (Map<String, Object>) issuer.get("sdk");
        Map<String, Object> configs = (Map<String, Object>) sdk.get("credential-configurations");

        Map<String, SdkCredentialConfig> result = new HashMap<>();
        for (Map.Entry<String, Object> entry : configs.entrySet()) {
            Map<String, Object> value = (Map<String, Object>) entry.getValue();
            result.put(entry.getKey(), SdkCredentialConfig.builder()
                    .format((String) value.get("format"))
                    .identifiers((List<String>) value.get("identifiers"))
                    .build());
        }
        return result;
    }

    @SuppressWarnings("unchecked")
    private Oid4vcProperty loadPropertiesFromFile() throws IOException {
        Map<String, Object> yaml = SerializationUtils.readYaml(authConfigPath, Map.class);
        Map<String, Object> oid4vc = (Map<String, Object>) yaml.get("oid4vc");
        Map<String, Object> auth = (Map<String, Object>) oid4vc.get("auth");

        Map<String, Object> clientsMap = (Map<String, Object>) auth.get("clients");
        Oid4vcProperty.Clients clients = null;
        if (clientsMap != null) {
            Map<String, Object> androidMap = (Map<String, Object>) clientsMap.get("android");
            Map<String, Object> iosMap = (Map<String, Object>) clientsMap.get("ios");
            Map<String, Object> issuerServerMap = (Map<String, Object>) clientsMap.get("issuer-server");

            clients = Oid4vcProperty.Clients.builder()
                    .clientId((String) clientsMap.get("client-id"))
                    .clientSecret((String) clientsMap.get("client-secret"))
                    .redirectUrl((String) clientsMap.get("redirect-url"))
                    .redirectUris((List<String>) clientsMap.get("redirect-uris"))
                    .scopes((List<String>) clientsMap.get("scopes"))
                    .android(androidMap == null ? null : Oid4vcProperty.ClientPlatform.builder()
                            .clientId((String) androidMap.get("client-id"))
                            .redirectUris((List<String>) androidMap.get("redirect-uris"))
                            .scopes((List<String>) androidMap.get("scopes"))
                            .build())
                    .ios(iosMap == null ? null : Oid4vcProperty.ClientPlatform.builder()
                            .clientId((String) iosMap.get("client-id"))
                            .redirectUris((List<String>) iosMap.get("redirect-uris"))
                            .scopes((List<String>) iosMap.get("scopes"))
                            .build())
                    .issuerServer(issuerServerMap == null ? null : Oid4vcProperty.IssuerServer.builder()
                            .url((String) issuerServerMap.get("url"))
                            .build())
                    .build();
        }

        return Oid4vcProperty.builder()
                .issuerUrl((String) auth.get("issuer-url"))
                .clients(clients)
                .build();
    }

}

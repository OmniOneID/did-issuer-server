package org.omnione.did.issuer.v1.admin.service;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.FieldNamingPolicy;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.omnione.did.base.constants.ConfigType;
import org.omnione.did.base.constants.Oid4vciEndpointPaths;
import org.omnione.did.base.datamodel.data.oid4vci.IssuerMetadata;
import org.omnione.did.base.datamodel.data.oid4vci.Oid4vcProperty;
import org.omnione.did.base.datamodel.data.oid4vci.SdkCredentialConfig;
import org.omnione.did.base.datamodel.data.oid4vci.dto.Oid4vciMetadataResponse;
import org.omnione.did.base.db.domain.CredentialConfig;
import org.omnione.did.base.db.domain.IssuerInfo;
import org.omnione.did.base.db.domain.ServerConfig;
import org.omnione.did.base.db.repository.CredentialConfigRepository;
import org.omnione.did.base.db.repository.IssuerInfoRepository;
import org.omnione.did.base.db.repository.ServerConfigRepository;
import org.omnione.did.base.util.SerializationUtils;
import org.omnione.did.common.util.JsonUtil;
import org.omnione.did.oid4vc.authorization.authorization.config.Oid4vcAuthServerProperties;
import org.omnione.did.oid4vc.oid4vci.config.IssuerSdkProperties;
import org.omnione.did.oid4vc.oid4vci.dto.metadata.IssuerMetadataResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.env.Environment;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionSynchronization;
import org.springframework.transaction.support.TransactionSynchronizationManager;
import org.springframework.util.StringUtils;

import java.io.IOException;
import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.SecureRandom;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@RequiredArgsConstructor
@Service
public class Oid4vciConfigService {

    public static final String CREDENTIAL_REVOCATION_PATH = Oid4vciEndpointPaths.CREDENTIAL_REVOCATION;

    private final ServerConfigRepository serverConfigRepository;
    private final CredentialConfigRepository credentialConfigRepository;
    private final Gson gson;
    private final Gson fileMetadataGson = new GsonBuilder()
            .setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES)
            .create();

    @Autowired(required = false)
    private IssuerSdkProperties issuerSdkProperties;

    @Autowired(required = false)
    private Oid4vcAuthServerProperties authServerProperties;

    @Autowired
    private Environment env;

    @Autowired(required = false)
    private ApplicationEventPublisher eventPublisher;

    @Autowired(required = false)
    private IssuerInfoRepository issuerInfoRepository;

    public record PropertiesUpdatedEvent() {
    }

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
        return text != null && env != null ? env.resolvePlaceholders(text) : text;
    }

    private List<String> resolvePlaceholders(List<String> list) {
        if (list == null) return null;
        return list.stream().map(this::resolvePlaceholder).collect(Collectors.toList());
    }

    private void syncClientPlatform(Oid4vcProperty.ClientPlatform source, Oid4vcAuthServerProperties.ClientDetail target) {
        if (source.getClientId() != null) {
            target.setClientId(resolvePlaceholder(source.getClientId()));
        }
        if (source.getRedirectUris() != null) {
            target.setRedirectUris(resolvePlaceholders(source.getRedirectUris()));
        }
        if (source.getScopes() != null) {
            target.setScopes(source.getScopes());
        }
    }

    private void syncAuthProperties(Oid4vcProperty dbProps) {
        if (authServerProperties != null && dbProps != null) {
            if (dbProps.getIssuerUrl() != null) {
                authServerProperties.setIssuerUrl(resolvePlaceholder(dbProps.getIssuerUrl()));
            }
            if (dbProps.getClients() != null) {
                Oid4vcAuthServerProperties.Clients sdkClients = authServerProperties.getClients();
                if (sdkClients == null) {
                    sdkClients = new Oid4vcAuthServerProperties.Clients();
                    authServerProperties.setClients(sdkClients);
                }
                
                Oid4vcProperty.Clients dbClients = dbProps.getClients();
                if (dbClients.getClientId() != null) {
                    sdkClients.setClientId(resolvePlaceholder(dbClients.getClientId()));
                }
                if (dbClients.getClientSecret() != null) {
                    sdkClients.setClientSecret(resolvePlaceholder(dbClients.getClientSecret()));
                }
                if (dbClients.getRedirectUrl() != null) {
                    sdkClients.setRedirectUrl(resolvePlaceholder(dbClients.getRedirectUrl()));
                }
                if (dbClients.getRedirectUris() != null) {
                    sdkClients.setRedirectUris(resolvePlaceholders(dbClients.getRedirectUris()));
                }
                if (dbClients.getScopes() != null) {
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
                    if (dbClients.getIssuerServer().getUrl() != null) {
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
        if (config.isPresent()) {
            IssuerMetadata metadata = gson.fromJson(config.get().getConfigValue(), IssuerMetadata.class);
            if (metadata == null) {
                throw new IllegalStateException("OID4VCI issuer metadata in DB is empty");
            }
            applyFixedIssuerEndpoints(metadata);
            if (applyRegisteredIssuerBase(metadata, configuredIssuerBaseUrl(), registeredIssuerBaseUrl())) {
                saveIssuerMetadata(metadata);
            }
            return metadata;
        }
        IssuerMetadata metadata = loadMetadataFromFile();
        applyRegisteredIssuerBase(metadata, configuredIssuerBaseUrl(), registeredIssuerBaseUrl());
        saveIssuerMetadata(metadata);
        return metadata;
    }

    @Transactional(rollbackFor = Exception.class)
    public void syncRegisteredIssuerUrl(String previousServerUrl, String currentServerUrl) {
        String currentBase = issuerBaseUrlFromRegistration(currentServerUrl);
        if (currentBase == null) {
            return;
        }
        IssuerMetadata metadata = getMetadata();
        String previousBase = issuerBaseUrlFromRegistration(previousServerUrl);
        if (applyRegisteredIssuerBase(metadata, previousBase, currentBase)
                || applyRegisteredIssuerBase(metadata, configuredIssuerBaseUrl(), currentBase)) {
            saveIssuerMetadata(metadata);
        }
    }

    private boolean applyRegisteredIssuerBase(IssuerMetadata metadata, String previousBase, String currentBase) {
        if (!StringUtils.hasText(previousBase) || !StringUtils.hasText(currentBase)
                || Objects.equals(previousBase, currentBase)
                || !Objects.equals(metadata.getCredentialIssuer(), previousBase)) {
            return false;
        }
        if (metadata.getAuthorizationServer() == null || metadata.getAuthorizationServer().isEmpty()
                || (metadata.getAuthorizationServer().size() == 1
                && Objects.equals(metadata.getAuthorizationServer().get(0), previousBase))) {
            metadata.setAuthorizationServer(List.of(currentBase));
        }
        metadata.setCredentialIssuer(currentBase);
        applyFixedIssuerEndpoints(metadata);
        return true;
    }

    private String configuredIssuerBaseUrl() {
        return env == null ? null : env.getProperty("issuer.base-url");
    }

    private String registeredIssuerBaseUrl() {
        if (issuerInfoRepository == null) {
            return null;
        }
        return issuerInfoRepository.findTop1ByOrderByIdAsc()
                .map(IssuerInfo::getServerUrl)
                .map(this::issuerBaseUrlFromRegistration)
                .orElse(null);
    }

    private String issuerBaseUrlFromRegistration(String serverUrl) {
        if (!StringUtils.hasText(serverUrl)) {
            return null;
        }
        try {
            URI uri = URI.create(serverUrl);
            String path = uri.getPath();
            if (!List.of("http", "https").contains(uri.getScheme()) || uri.getHost() == null
                    || uri.getUserInfo() != null || uri.getQuery() != null || uri.getFragment() != null
                    || !(path == null || path.isEmpty() || "/".equals(path)
                    || "/issuer".equals(path) || "/issuer/".equals(path))) {
                log.warn("Issuer registration URL cannot be used as an OID4VCI base URL: {}", serverUrl);
                return null;
            }
            return new URI(uri.getScheme(), null, uri.getHost(), uri.getPort(), null, null, null).toString();
        } catch (Exception exception) {
            log.warn("Invalid Issuer registration URL for OID4VCI metadata: {}", serverUrl);
            return null;
        }
    }

    private void saveIssuerMetadata(IssuerMetadata metadata) {
        try {
            saveToDb(ConfigType.OID4VCI_METADATA.name(), metadata, "OID4VCI Issuer Metadata");
        } catch (IOException exception) {
            throw new IllegalStateException("Failed to save OID4VCI issuer metadata to DB", exception);
        }
    }
    public IssuerMetadataResponse getIssuerMetadata() {
        IssuerMetadata metadata = getMetadata();
        IssuerMetadataResponse response = gson.fromJson(gson.toJson(metadata), IssuerMetadataResponse.class);
        response.setAuthorizationServers(metadata.getAuthorizationServer());
        return response;
    }

    private IssuerMetadata loadMetadataFromFile() {
        try {
            String json;
            if (StringUtils.hasText(metadataPath) && Files.isRegularFile(Paths.get(metadataPath))) {
                json = Files.readString(Paths.get(metadataPath), StandardCharsets.UTF_8);
            } else {
                log.info("Issuer metadata file is unavailable at {}; using packaged defaults", metadataPath);
                try (var input = new ClassPathResource("metadata/issuer_meta_default.json").getInputStream()) {
                    json = new String(input.readAllBytes(), StandardCharsets.UTF_8);
                }
            }
            if (env != null) {
                json = env.resolveRequiredPlaceholders(json);
            }
            IssuerMetadata metadata = fileMetadataGson.fromJson(json, IssuerMetadata.class);
            if (metadata == null || !StringUtils.hasText(metadata.getCredentialIssuer())
                    || metadata.getCredentialRequestEncryption() == null
                    || metadata.getCredentialResponseEncryption() == null) {
                throw new IllegalStateException("Issuer metadata file is missing required fields: " + metadataPath);
            }
            return applyFixedIssuerEndpoints(metadata);
        } catch (IOException | com.google.gson.JsonParseException e) {
            throw new IllegalStateException("Failed to load issuer metadata file: " + metadataPath, e);
        }
    }

    @Transactional(rollbackFor = Exception.class)
    public void updateMetadata(IssuerMetadata metadata) throws IOException {
        validateCredentialIssuerBaseUrl(metadata.getCredentialIssuer());
        applyFixedIssuerEndpoints(metadata);
        metadata.setCredentialConfigurationsSupported(loadCredentialConfigurationsFromDb());
        saveToDb(ConfigType.OID4VCI_METADATA.name(), metadata, "OID4VCI Issuer Metadata");

        syncMetadataToFileAfterCommit(metadata);
    }

    private void validateCredentialIssuerBaseUrl(String baseUrl) {
        try {
            URI uri = URI.create(baseUrl);
            if (!List.of("http", "https").contains(uri.getScheme()) || uri.getHost() == null
                    || (StringUtils.hasText(uri.getPath()) && !"/".equals(uri.getPath()))
                    || uri.getQuery() != null || uri.getFragment() != null) {
                throw new IllegalArgumentException("Credential Issuer must be a server base URL without a path");
            }
        } catch (NullPointerException | IllegalArgumentException exception) {
            throw new IllegalArgumentException("Credential Issuer must be a server base URL without a path", exception);
        }
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

    private IssuerMetadata applyFixedIssuerEndpoints(IssuerMetadata metadata) {
        String baseUrl = StringUtils.hasText(metadata.getCredentialIssuer())
                ? metadata.getCredentialIssuer().trim().replaceAll("/+$", "") : "";
        metadata.setCredentialOfferEndpoint(baseUrl + Oid4vciEndpointPaths.CREDENTIAL_OFFER);
        metadata.setCredentialEndpoint(baseUrl + Oid4vciEndpointPaths.CREDENTIAL);
        metadata.setNonceEndpoint(baseUrl + Oid4vciEndpointPaths.NONCE);
        metadata.setCredentialRevocationEndpoint(baseUrl + CREDENTIAL_REVOCATION_PATH);
        metadata.setDeferredCredentialEndpoint(baseUrl + Oid4vciEndpointPaths.DEFERRED_CREDENTIAL);
        metadata.setNotificationEndpoint(baseUrl + Oid4vciEndpointPaths.NOTIFICATION);
        return metadata;
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
                    .orElseGet(this::loadMetadataFromFile);
            if (metadata == null) {
                throw new IllegalStateException("Issuer metadata is empty.");
            }
            return applyFixedIssuerEndpoints(metadata);
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
            return resolvePropertyPlaceholders(gson.fromJson(config.get().getConfigValue(), Oid4vcProperty.class));
        }
        Oid4vcProperty properties = Files.isRegularFile(Paths.get(authConfigPath))
                ? loadPropertiesFromFile() : buildDefaultProperties();
        saveToDb(key, properties, "OID4VCI Auth Properties");
        return resolvePropertyPlaceholders(properties);
    }

    private Oid4vcProperty buildDefaultProperties() {
        env.getRequiredProperty("issuer.base-url");
        String clientSecret = env.getProperty("oid4vc.auth.clients.client-secret");
        if (!StringUtils.hasText(clientSecret)) {
            byte[] bytes = new byte[32];
            new SecureRandom().nextBytes(bytes);
            clientSecret = "{noop}" + Base64.getUrlEncoder().withoutPadding().encodeToString(bytes);
        }

        return Oid4vcProperty.builder()
                .issuerUrl("${oid4vc.auth.issuer-url:${issuer.base-url}}")
                .clients(Oid4vcProperty.Clients.builder()
                        .clientId("oid4vci-client")
                        .clientSecret(clientSecret)
                        .redirectUrl("${issuer.base-url}/auth/callback")
                        .redirectUris(List.of("${issuer.base-url}/auth/callback"))
                        .scopes(List.of("openid", "profile", "email"))
                        .android(defaultClientPlatform("oid4vci-android", "org.omnione.did.sdk.oid4vc://callback"))
                        .androidOpenid(defaultClientPlatform("oid4vci-android-opendid", "org.omnione.did.sdk.oid4vc://callback"))
                        .ios(defaultClientPlatform("oid4vci-ios", "oid4vc-app://callback"))
                        .iosOpenid(defaultClientPlatform("oid4vci-ios-opendid", "oid4vc-app://callback"))
                        .issuerServer(Oid4vcProperty.IssuerServer.builder().url("${issuer.base-url}").build())
                        .build())
                .build();
    }

    private Oid4vcProperty resolvePropertyPlaceholders(Oid4vcProperty properties) {
        if (properties == null) {
            return null;
        }
        properties.setIssuerUrl(resolveIssuerUrl(properties.getIssuerUrl()));
        Oid4vcProperty.Clients clients = properties.getClients();
        if (clients != null) {
            clients.setRedirectUrl(resolvePlaceholder(clients.getRedirectUrl()));
            clients.setRedirectUris(resolvePlaceholders(clients.getRedirectUris()));
            if (clients.getIssuerServer() != null) {
                clients.getIssuerServer().setUrl(resolvePlaceholder(clients.getIssuerServer().getUrl()));
            }
        }
        return properties;
    }

    private String resolveIssuerUrl(String configuredUrl) {
        try {
            String issuerUrl = configuredUrl;
            if (!StringUtils.hasText(issuerUrl) && env != null) {
                issuerUrl = env.getProperty("oid4vc.auth.issuer-url");
                if (!StringUtils.hasText(issuerUrl)) {
                    issuerUrl = env.getProperty("issuer.base-url");
                }
            }
            if (env != null && issuerUrl != null) {
                issuerUrl = env.resolveRequiredPlaceholders(issuerUrl);
            }
            String configuredDefault = env == null ? null
                    : env.getProperty("oid4vc.auth.issuer-url", env.getProperty("issuer.base-url"));
            String registeredBase = registeredIssuerBaseUrl();
            if (StringUtils.hasText(registeredBase)
                    && (!StringUtils.hasText(configuredUrl)
                    || Objects.equals(issuerUrl, configuredDefault))) {
                issuerUrl = registeredBase;
            }
            URI uri = URI.create(issuerUrl);
            if (!List.of("http", "https").contains(uri.getScheme()) || uri.getHost() == null) {
                throw new IllegalArgumentException("Issuer URL must be an absolute HTTP URL");
            }
            return issuerUrl;
        } catch (RuntimeException exception) {
            throw new IllegalStateException(
                    "OID4VCI issuer URL is invalid. Configure oid4vc.auth.issuer-url or issuer.base-url.",
                    exception);
        }
    }

    private String preserveDefaultUrlTemplate(String value, String template) {
        return Objects.equals(value, resolvePlaceholder(template)) ? template : value;
    }

    private Oid4vcProperty.ClientPlatform defaultClientPlatform(String clientId, String redirectUri) {
        return Oid4vcProperty.ClientPlatform.builder()
                .clientId(clientId)
                .redirectUris(List.of(redirectUri))
                .scopes(List.of("openid"))
                .build();
    }

    @Transactional
    public void updateProperties(Oid4vcProperty properties) throws IOException {
        validatePropertiesForRuntime(properties);
        if (!StringUtils.hasText(properties.getIssuerUrl())) {
            properties.setIssuerUrl("${oid4vc.auth.issuer-url:${issuer.base-url}}");
        }
        if (properties.getClients() != null) {
            Oid4vcProperty.Clients clients = properties.getClients();
            clients.setRedirectUrl(preserveDefaultUrlTemplate(
                    clients.getRedirectUrl(), "${issuer.base-url}/auth/callback"));
            if (clients.getRedirectUris() != null) {
                clients.setRedirectUris(clients.getRedirectUris().stream()
                        .map(uri -> preserveDefaultUrlTemplate(uri, "${issuer.base-url}/auth/callback"))
                        .collect(Collectors.toList()));
            }
            if (clients.getIssuerServer() != null) {
                clients.getIssuerServer().setUrl(preserveDefaultUrlTemplate(
                        clients.getIssuerServer().getUrl(), "${issuer.base-url}"));
            }
        }
        String issuerUrlTemplate = "${oid4vc.auth.issuer-url:${issuer.base-url}}";
        properties.setIssuerUrl(Objects.equals(properties.getIssuerUrl(), resolveIssuerUrl(issuerUrlTemplate))
                ? issuerUrlTemplate
                : preserveDefaultUrlTemplate(properties.getIssuerUrl(), issuerUrlTemplate));
        saveToDb(ConfigType.OID4VCI_AUTH_PROPERTIES.name(), properties, "OID4VCI Auth Properties");
        if (TransactionSynchronizationManager.isSynchronizationActive()) {
            TransactionSynchronizationManager.registerSynchronization(new TransactionSynchronization() {
                @Override
                public void afterCommit() {
                    refreshRuntimeProperties();
                }
            });
        } else {
            refreshRuntimeProperties();
        }
    }

    private void validatePropertiesForRuntime(Oid4vcProperty properties) {
        if (properties == null || properties.getClients() == null) {
            throw new IllegalArgumentException("OID4VCI client settings are required");
        }
        Oid4vcProperty.Clients clients = properties.getClients();
        if (!StringUtils.hasText(clients.getClientId()) || !StringUtils.hasText(clients.getClientSecret())) {
            throw new IllegalArgumentException("Main client ID and secret are required");
        }
        if (!StringUtils.hasText(clients.getRedirectUrl())
                && (clients.getRedirectUris() == null || clients.getRedirectUris().stream().noneMatch(StringUtils::hasText))) {
            throw new IllegalArgumentException("Main client redirect URL is required");
        }
        Set<String> clientIds = new HashSet<>();
        clientIds.add(clients.getClientId());
        for (Oid4vcProperty.ClientPlatform platform : List.of(
                clients.getAndroid() == null ? new Oid4vcProperty.ClientPlatform() : clients.getAndroid(),
                clients.getAndroidOpenid() == null ? new Oid4vcProperty.ClientPlatform() : clients.getAndroidOpenid(),
                clients.getIos() == null ? new Oid4vcProperty.ClientPlatform() : clients.getIos(),
                clients.getIosOpenid() == null ? new Oid4vcProperty.ClientPlatform() : clients.getIosOpenid())) {
            if (StringUtils.hasText(platform.getClientId())) {
                if (!clientIds.add(platform.getClientId())) {
                    throw new IllegalArgumentException("OID4VCI client IDs must be unique");
                }
                if (platform.getRedirectUris() == null
                        || platform.getRedirectUris().stream().noneMatch(StringUtils::hasText)) {
                    throw new IllegalArgumentException("Redirect URI is required for client: " + platform.getClientId());
                }
            }
        }
    }

    private void refreshRuntimeProperties() {
        if (eventPublisher == null && authServerProperties == null) {
            return;
        }
        try {
            Oid4vcProperty current = getProperties();
            syncAuthProperties(current);
            if (eventPublisher != null) {
                eventPublisher.publishEvent(new PropertiesUpdatedEvent());
            }
        } catch (IOException exception) {
            throw new IllegalStateException("Failed to refresh OID4VCI properties after saving", exception);
        }
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

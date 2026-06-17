package org.omnione.did.base.config;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.omnione.did.base.datamodel.data.oid4vci.IssuerMetadata;
import org.omnione.did.issuer.v1.admin.service.Oid4vciConfigService;
import org.omnione.did.oid4vc.oid4vci.property.IssuerProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

/**
 * Configuration for the OID4VCI SDK properties.
 * This class provides an IssuerProperties bean that loads information from the database or metadata.
 */
@Slf4j
@RequiredArgsConstructor
@Configuration
public class SdkPropertyConfig {

    private final Oid4vciConfigService configService;

    @Primary
    @Bean
    public IssuerProperties issuerProperties() {
        IssuerProperties properties = new IssuerProperties();
        try {
            IssuerMetadata metadata = configService.getMetadata();
            
            // Map baseUrl from the credentialIssuer metadata field
            if (metadata.getCredentialIssuer() != null && !metadata.getCredentialIssuer().isEmpty()) {
                properties.setBaseUrl(metadata.getCredentialIssuer());
            } else {
                properties.setBaseUrl("http://localhost:8091");
            }
            
            // Currently, these paths are often static or from application.yml
            // We can set defaults and let Oid4vciConfigService extend this in the future if needed
            properties.setDataDir(".");
            properties.setMetadataFilePath("metadata/issuer_meta_univ_local.json");

            log.info("IssuerProperties loaded from DB/Metadata: baseUrl={}", properties.getBaseUrl());
            
        } catch (Exception e) {
            log.error("Failed to load IssuerProperties from DB: {}", e.getMessage());
            // Fallback defaults
            properties.setBaseUrl("http://localhost:8091");
            properties.setDataDir(".");
        }
        return properties;
    }

    @Bean
    public org.springframework.boot.ApplicationRunner initializeIssuerSdkPropertiesRunner(
            @org.springframework.beans.factory.annotation.Autowired(required = false) org.omnione.did.oid4vc.oid4vci.config.IssuerSdkProperties issuerSdkProperties,
            Oid4vciConfigService configService) {
        return args -> {
            if (issuerSdkProperties != null) {
                try {
                    java.util.Map<String, org.omnione.did.base.datamodel.data.oid4vci.SdkCredentialConfig> sdkConfigs = configService.getSdkCredentialConfigs();
                    
                    java.util.Map<String, org.omnione.did.oid4vc.oid4vci.config.IssuerSdkProperties.CredentialConfig> map = new java.util.HashMap<>();
                    for (java.util.Map.Entry<String, org.omnione.did.base.datamodel.data.oid4vci.SdkCredentialConfig> entry : sdkConfigs.entrySet()) {
                        org.omnione.did.base.datamodel.data.oid4vci.SdkCredentialConfig sdkConfig = entry.getValue();
                        org.omnione.did.oid4vc.oid4vci.config.IssuerSdkProperties.CredentialConfig config = new org.omnione.did.oid4vc.oid4vci.config.IssuerSdkProperties.CredentialConfig();
                        config.setFormat(sdkConfig.getFormat());
                        if (sdkConfig.getIdentifiers() != null) {
                            config.setIdentifiers(new java.util.HashSet<>(sdkConfig.getIdentifiers()));
                        } else {
                            config.setIdentifiers(new java.util.HashSet<>());
                        }
                        map.put(entry.getKey(), config);
                    }
                    
                    if (!map.isEmpty()) {
                        issuerSdkProperties.setCredentialConfigurations(map);
                        log.info("==> Successfully injected {} credential configurations into IssuerSdkProperties on startup.", map.size());
                    } else {
                        log.warn("==> No credential configurations found to inject into IssuerSdkProperties on startup.");
                    }
                } catch (Exception e) {
                    log.error("Failed to map IssuerSdkProperties from DB on startup: {}", e.getMessage());
                }
            } else {
                log.warn("==> IssuerSdkProperties bean is null, skipping startup injection.");
            }
        };
    }
}

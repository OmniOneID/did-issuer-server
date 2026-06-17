package org.omnione.did.issuer.v1.admin.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.omnione.did.base.constants.UrlConstant;
import org.omnione.did.base.datamodel.data.oid4vci.IssuerMetadata;
import org.omnione.did.base.datamodel.data.oid4vci.Oid4vcProperty;
import org.omnione.did.base.datamodel.data.oid4vci.SdkCredentialConfig;
import org.omnione.did.issuer.v1.admin.api.dto.EmptyResDto;
import org.omnione.did.issuer.v1.admin.service.Oid4vciConfigService;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.Map;

@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping(value = UrlConstant.Admin.V1 + UrlConstant.Admin.OID4VCI)
public class Oid4vciConfigController {
    private final Oid4vciConfigService oid4vciConfigService;

    @GetMapping("/metadata")
    public IssuerMetadata getMetadata() throws IOException {
        return oid4vciConfigService.getMetadata();
    }

    @PostMapping("/metadata")
    public EmptyResDto updateMetadata(@RequestBody IssuerMetadata metadata) throws IOException {
        log.info("Updating OID4VCI Issuer Metadata: {}", metadata.getCredentialIssuer());
        oid4vciConfigService.updateMetadata(metadata);
        return new EmptyResDto();
    }

    @GetMapping("/credential-configs")
    public Map<String, SdkCredentialConfig> getCredentialConfigs() throws IOException {
        return oid4vciConfigService.getSdkCredentialConfigs();
    }

    @PostMapping("/credential-configs")
    public EmptyResDto updateCredentialConfigs(@RequestBody Map<String, SdkCredentialConfig> configs) throws IOException {
        oid4vciConfigService.updateSdkCredentialConfigs(configs);
        return new EmptyResDto();
    }

    @GetMapping("/properties")
    public Oid4vcProperty getProperties() throws IOException {
        return oid4vciConfigService.getProperties();
    }

    @PostMapping("/properties")
    public EmptyResDto updateProperties(@RequestBody Oid4vcProperty properties) throws IOException {
        oid4vciConfigService.updateProperties(properties);
        return new EmptyResDto();
    }
}

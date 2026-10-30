package org.omnione.did.issuer.v1.admin.controller;

import org.omnione.did.base.constants.ActionType;
import org.omnione.did.base.annotation.ArticleLog;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.omnione.did.base.constants.UrlConstant;
import org.omnione.did.base.datamodel.data.oid4vci.IssuerMetadata;
import org.omnione.did.base.datamodel.data.oid4vci.Oid4vcProperty;
import org.omnione.did.base.datamodel.data.oid4vci.SdkCredentialConfig;
import org.omnione.did.issuer.v1.admin.api.dto.EmptyResDto;
import org.omnione.did.issuer.v1.admin.service.Oid4vciConfigService;
import org.omnione.did.issuer.v1.admin.dto.oid4vci.ListProviderRegistrationDto;
import org.omnione.did.issuer.v1.admin.dto.oid4vci.ListProviderRegistrationReqDto;
import org.omnione.did.issuer.v1.admin.service.Oid4vciListProviderRegistrationService;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.Map;

@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping(value = UrlConstant.Admin.V1 + UrlConstant.Admin.OID4VCI)
public class Oid4vciConfigController {
    private final Oid4vciConfigService oid4vciConfigService;
    private final Oid4vciListProviderRegistrationService listProviderRegistrationService;

    @GetMapping("/metadata")
    @ArticleLog(name="[OID4VCI] Read issuer metadata", targetType="OID4VCI_METADATA", actionType=ActionType.NONE)
    public IssuerMetadata getMetadata() throws IOException {
        return oid4vciConfigService.getMetadata();
    }

    @PostMapping("/metadata")
    @ArticleLog(name="[OID4VCI] Update issuer metadata", targetType="OID4VCI_METADATA", actionType=ActionType.UPDATE)
    public EmptyResDto updateMetadata(@RequestBody IssuerMetadata metadata) throws IOException {
        log.info("Updating OID4VCI Issuer Metadata: {}", metadata.getCredentialIssuer());
        oid4vciConfigService.updateMetadata(metadata);
        return new EmptyResDto();
    }

    @GetMapping("/credential-configs")
    @ArticleLog(name="[OID4VCI] Read credential configurations", targetType="OID4VCI_CREDENTIAL_CONFIG", actionType=ActionType.NONE)
    public Map<String, SdkCredentialConfig> getCredentialConfigs() throws IOException {
        return oid4vciConfigService.getSdkCredentialConfigs();
    }

    @PostMapping("/credential-configs")
    @ArticleLog(name="[OID4VCI] Update credential configurations", targetType="OID4VCI_CREDENTIAL_CONFIG", actionType=ActionType.UPDATE)
    public EmptyResDto updateCredentialConfigs(@RequestBody Map<String, SdkCredentialConfig> configs) throws IOException {
        oid4vciConfigService.updateSdkCredentialConfigs(configs);
        return new EmptyResDto();
    }

    @GetMapping("/properties")
    @ArticleLog(name="[OID4VCI] Read client properties", targetType="OID4VCI_PROPERTY", actionType=ActionType.NONE)
    public Oid4vcProperty getProperties() throws IOException {
        return oid4vciConfigService.getProperties();
    }

    @PostMapping("/properties")
    @ArticleLog(name="[OID4VCI] Update client properties", targetType="OID4VCI_PROPERTY", actionType=ActionType.UPDATE)
    public EmptyResDto updateProperties(@RequestBody Oid4vcProperty properties) throws IOException {
        oid4vciConfigService.updateProperties(properties);
        return new EmptyResDto();
    }

    @GetMapping("/list-provider-registration")
    @ArticleLog(name="[OID4VCI] Read list provider registration", targetType="OID4VCI_LIST_PROVIDER", actionType=ActionType.NONE)
    public ListProviderRegistrationDto getListProviderRegistration() {
        return listProviderRegistrationService.getRegistration();
    }

    @PostMapping("/list-provider-registration")
    @ArticleLog(name="[OID4VCI] Register with list provider", targetType="OID4VCI_LIST_PROVIDER", actionType=ActionType.CREATE)
    public ListProviderRegistrationDto registerWithListProvider(
            @RequestBody ListProviderRegistrationReqDto request) {
        return listProviderRegistrationService.register(request);
    }
}

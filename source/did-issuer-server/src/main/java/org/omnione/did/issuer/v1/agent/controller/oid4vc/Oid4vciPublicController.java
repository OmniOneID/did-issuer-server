package org.omnione.did.issuer.v1.agent.controller.oid4vc;

import org.omnione.did.base.constants.ApiType;
import org.omnione.did.base.constants.ActionType;
import org.omnione.did.base.annotation.ArticleLog;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.omnione.did.base.constants.UrlConstant;
import org.omnione.did.base.datamodel.data.oid4vci.dto.Oid4vciMetadataResponse;
import org.omnione.did.issuer.v1.admin.service.Oid4vciConfigService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;

@Slf4j
@RequiredArgsConstructor
@RestController
public class Oid4vciPublicController {

    private final Oid4vciConfigService oid4vciConfigService;

    @GetMapping(UrlConstant.Issuer.WELL_KNOWN_PREFIX + UrlConstant.Issuer.OPENID_CREDENTIAL_ISSUER)
    @ArticleLog(name="[OID4VCI] Get public issuer metadata", apiType=ApiType.AGENT, targetType="OID4VCI_METADATA", actionType=ActionType.NONE)
    public Oid4vciMetadataResponse getIssuerMetadata() throws IOException {
        log.debug("getIssuerMetadata request (overridden)");
        return oid4vciConfigService.getPublicMetadata();
    }
}

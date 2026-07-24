package org.omnione.did.issuer.v1.admin.api;

import org.omnione.did.issuer.v1.admin.dto.oid4vci.ListProviderRegistrationReqDto;
import org.omnione.did.issuer.v1.admin.dto.oid4vci.ListProviderRegistrationResultDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(name = "oid4vciListProvider", url = "${list.url:http://127.0.0.1:8090}")
public interface Oid4vciListProviderFeign {
    @PostMapping("/list/api/v1/oid4vci/issuers")
    ListProviderRegistrationResultDto registerIssuer(
            @RequestBody ListProviderRegistrationReqDto request);
}

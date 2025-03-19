package org.omnione.did.issuer.v1.admin.controller;

import lombok.RequiredArgsConstructor;
import org.omnione.did.base.constants.UrlConstant;
import org.omnione.did.issuer.v1.admin.api.dto.EmptyResDto;
import org.omnione.did.issuer.v1.admin.dto.SendCertificateVcReqDto;
import org.omnione.did.issuer.v1.admin.dto.SendEntityInfoReqDto;
import org.omnione.did.issuer.v1.admin.service.IssuerManagementService;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
@RequestMapping(value = UrlConstant.Admin.V1)
public class IssuerManagementController {
    private final IssuerManagementService issuerManagementService;

    @RequestMapping(value = "/certificate-vc", method = RequestMethod.POST)
    public EmptyResDto createCertificateVc(@RequestBody SendCertificateVcReqDto sendCertificateVcReqDto) {
        return issuerManagementService.createCertificateVc(sendCertificateVcReqDto);
    }

    @RequestMapping(value = "/entity-info", method = RequestMethod.POST)
    public EmptyResDto updateEntityInfo(@RequestBody SendEntityInfoReqDto sendEntityInfoReqDto) {
        return issuerManagementService.updateEntityInfo(sendEntityInfoReqDto);
    }
}

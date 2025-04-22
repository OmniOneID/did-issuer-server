/*
 * Copyright 2025 OmniOne.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.omnione.did.issuer.v1.admin.controller;

import lombok.RequiredArgsConstructor;
import org.omnione.did.base.constants.UrlConstant;
import org.omnione.did.issuer.v1.admin.api.dto.EmptyResDto;
import org.omnione.did.issuer.v1.admin.dto.GetIssuerInfoReqDto;
import org.omnione.did.issuer.v1.admin.dto.SendCertificateVcReqDto;
import org.omnione.did.issuer.v1.admin.dto.SendEntityInfoReqDto;
import org.omnione.did.issuer.v1.admin.dto.issuer.IssuerInfoResDto;
import org.omnione.did.issuer.v1.admin.dto.issuer.RegisterIssuerInfoReqDto;
import org.omnione.did.issuer.v1.admin.dto.issuer.RequestEntityStatusResDto;
import org.omnione.did.issuer.v1.admin.dto.issuer.RequestRegisterDidReqDto;
import org.omnione.did.issuer.v1.admin.service.IssuerManagementService;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RequiredArgsConstructor
@RestController
@RequestMapping(value = UrlConstant.Admin.V1 + UrlConstant.Admin.ISSUER)
public class IssuerAdminController {
    private final IssuerManagementService issuerManagementService;

    @RequestMapping(value = "/info", method = RequestMethod.GET)
    public GetIssuerInfoReqDto getIssuerInfo() {
        return issuerManagementService.getIssuerInfo();
    }
    @RequestMapping(value = "/certificate-vc", method = RequestMethod.POST)
    public EmptyResDto createCertificateVc(@RequestBody SendCertificateVcReqDto sendCertificateVcReqDto) {
        return issuerManagementService.createCertificateVc(sendCertificateVcReqDto);
    }

    @RequestMapping(value = "/entity-info", method = RequestMethod.POST)
    public EmptyResDto updateEntityInfo(@RequestBody SendEntityInfoReqDto sendEntityInfoReqDto) {
        return issuerManagementService.updateEntityInfo(sendEntityInfoReqDto);
    }

    @RequestMapping(value = "/register-issuer-info", method = RequestMethod.POST)
    public IssuerInfoResDto registerIssuerInfo(@RequestBody RegisterIssuerInfoReqDto registerCaInfoReqDto) {
        return issuerManagementService.registerIssuerInfo(registerCaInfoReqDto);
    }

    @RequestMapping(value = "/generate-did-auto", method = RequestMethod.POST)
    public Map<String, Object> generateIssuerDidDocumentAuto() {
        return issuerManagementService.registerIssuerDidDocumentAuto();
    }

    @RequestMapping(value = "/register-did", method = RequestMethod.POST)
    public EmptyResDto requestRegisterDid(@RequestBody RequestRegisterDidReqDto requestRegisterDidReqDto) {
        return issuerManagementService.requestRegisterDid(requestRegisterDidReqDto);
    }

    @GetMapping(value = "/request-status")
    public RequestEntityStatusResDto requestEntityStatus() {
        return issuerManagementService.requestEntityStatus();
    }

    @PostMapping(value = "/request-enroll-entity")
    public Map<String, Object> requestEnrollEntity() {
        return issuerManagementService.enrollEntity();
    }
}

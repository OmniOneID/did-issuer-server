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
import org.omnione.did.issuer.v1.admin.dto.issuer.GetIssuerInfoReqDto;
import org.omnione.did.issuer.v1.admin.dto.vc.SendCertificateVcReqDto;
import org.omnione.did.issuer.v1.admin.dto.admin.SendEntityInfoReqDto;
import org.omnione.did.issuer.v1.admin.dto.issuer.IssuerInfoResDto;
import org.omnione.did.issuer.v1.admin.dto.issuer.RegisterIssuerInfoReqDto;
import org.omnione.did.issuer.v1.admin.dto.issuer.RequestEntityStatusResDto;
import org.omnione.did.issuer.v1.admin.dto.issuer.RequestRegisterDidReqDto;
import org.omnione.did.issuer.v1.admin.service.IssuerManagementService;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * The IssuerAdminController class provides Admin Console endpoints for managing issuer-related operations.
 * It includes functionalities such as registering issuer information, updating entity info, issuing certificate VCs,
 * generating DID documents, and requesting enrollment or status.
 */
@RequiredArgsConstructor
@RestController
@RequestMapping(value = UrlConstant.Admin.V1)
public class IssuerAdminController {

    private final IssuerManagementService issuerManagementService;

    /**
     * Retrieves the current issuer information.
     *
     * @return the issuer information
     */
    @RequestMapping(value = UrlConstant.Admin.ISSUER + "/info", method = RequestMethod.GET)
    public GetIssuerInfoReqDto getIssuerInfo() {
        return issuerManagementService.getIssuerInfo();
    }

    /**
     * Issue a certificate VC.
     *
     * @param sendCertificateVcReqDto the DTO containing certificate VC details
     * @return an empty response on success
     */
    @RequestMapping(value = "/certificate-vc", method = RequestMethod.POST)
    public EmptyResDto createCertificateVc(@RequestBody SendCertificateVcReqDto sendCertificateVcReqDto) {
        return issuerManagementService.createCertificateVc(sendCertificateVcReqDto);
    }

    /**
     * Updates the entity information related to the issuer.
     *
     * @param sendEntityInfoReqDto the DTO containing entity information
     * @return an empty response on success
     */
    @RequestMapping(value = "/entity-info", method = RequestMethod.POST)
    public EmptyResDto updateEntityInfo(@RequestBody SendEntityInfoReqDto sendEntityInfoReqDto) {
        return issuerManagementService.updateEntityInfo(sendEntityInfoReqDto);
    }

    /**
     * Registers issuer information including credential authority data.
     *
     * @param registerCaInfoReqDto the DTO containing issuer registration details
     * @return the registered issuer information
     */
    @RequestMapping(value = UrlConstant.Admin.ISSUER + "/register-issuer-info", method = RequestMethod.POST)
    public IssuerInfoResDto registerIssuerInfo(@RequestBody RegisterIssuerInfoReqDto registerCaInfoReqDto) {
        return issuerManagementService.registerIssuerInfo(registerCaInfoReqDto);
    }

    /**
     * Automatically generates a DID document for the issuer.
     *
     * @return the generated DID document data
     */
    @RequestMapping(value = UrlConstant.Admin.ISSUER + "/generate-did-auto", method = RequestMethod.POST)
    public Map<String, Object> generateIssuerDidDocumentAuto() {
        return issuerManagementService.registerIssuerDidDocumentAuto();
    }

    /**
     * Sends a request to register a new DID.
     *
     * @param requestRegisterDidReqDto the DTO containing DID registration information
     * @return an empty response on success
     */
    @RequestMapping(value = UrlConstant.Admin.ISSUER + "/register-did", method = RequestMethod.POST)
    public EmptyResDto requestRegisterDid(@RequestBody RequestRegisterDidReqDto requestRegisterDidReqDto) {
        return issuerManagementService.requestRegisterDid(requestRegisterDidReqDto);
    }

    /**
     * Requests the status of the issuer’s registered entity.
     *
     * @return the status of the registered entity
     */
    @GetMapping(value = UrlConstant.Admin.ISSUER + "/request-status")
    public RequestEntityStatusResDto requestEntityStatus() {
        return issuerManagementService.requestEntityStatus();
    }

    /**
     * Requests the enrollment of the issuer entity.
     *
     * @return a map containing the result of the enrollment request
     */
    @PostMapping(value = UrlConstant.Admin.ISSUER + "/request-enroll-entity")
    public Map<String, Object> requestEnrollEntity() {
        return issuerManagementService.enrollEntity();
    }
}

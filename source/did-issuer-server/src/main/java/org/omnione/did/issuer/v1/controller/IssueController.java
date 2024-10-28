/*
 * Copyright 2024 OmniOne.
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

package org.omnione.did.issuer.v1.controller;

import jakarta.validation.Valid;
import org.omnione.did.base.constants.UrlConstant;
import org.omnione.did.issuer.v1.dto.vc.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.omnione.did.issuer.v1.helper.IssueServiceHelper;
import org.omnione.did.issuer.v1.service.IssueService;
import org.springframework.web.bind.annotation.*;

/**
 * The IssueController class is a controller that handles requests related to issuing verifiable credentials.
 * It provides endpoints for requesting an offer, inspecting a propose issue, generating an issue profile,
 * issuing a VC, and completing the issuance of a VC.
 */
@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping(value = UrlConstant.Issuer.V1)
public class IssueController {

    private final IssueService issueServiceHelper;

    /**
     * Requests an offer for Issuing Verifiable Credential.
     *
     * @param request the VC to request an offer for
     * @return the offer for the given VC
     */
    @PostMapping(UrlConstant.Issuer.REQUEST_OFFER)
    public OfferIssueVcResDto requestOfferQr(@Valid @RequestBody OfferIssueVcReqDto request) {
        return issueServiceHelper.requestOffer(request);
    }

    /**
     * Inspects the propose issue for a Verifiable Credential.
     *
     * @param request the VC to inspect the propose issue for
     * @return the inspection result for the given VC
     */
    @PostMapping(UrlConstant.Issuer.INSPECT_PROPOSE_ISSUE)
    public InspectIssueProposeResDto inspectIssuePropose(@RequestBody InspectIssueProposeReqDto request) {
        return issueServiceHelper.inspectIssuePropose(request);
    }

    /**
     * Generates an issue profile for a Verifiable Credential.
     *
     * @param request the VC to generate an issue profile for
     * @return the issue profile for the given VC
     */
    @PostMapping(UrlConstant.Issuer.GENERATE_ISSUE_PROFILE)
    public GenerateIssueProfileResDto generateIssueProfile(@RequestBody GenerateIssueProfileReqDto request) {
        return issueServiceHelper.generateIssueProfile(request);
    }

    /**
     * Issues a Verifiable Credential.
     *
     * @param request the VC to issue
     * @return the result of issuing the given VC
     */
    @PostMapping(UrlConstant.Issuer.ISSUE_VC)
    public IssueVcResDto issueVc(@RequestBody IssueVcReqDto request) {
        return issueServiceHelper.issueVc(request);
    }

    /**
     * completing the issuance of a VC.
     *
     * @param request the VC to complete
     * @return the result of completing the given VC
     */
    @PostMapping(UrlConstant.Issuer.COMPLETE_VC)
    public CompleteVcResDto completeVc(@RequestBody CompleteVcReqDto request) {
        return issueServiceHelper.completeVc(request);
    }

    /**
     * Gets the result of issuing a Verifiable Credential.
     *
     * @param offerId the ID of the offer for the VC
     * @return the result of issuing the VC
     */
    @GetMapping(UrlConstant.Issuer.ISSUE_VC + UrlConstant.Issuer.RESULT)
    public IssueVcResultResDto issueVcResult(@RequestParam("offerId") String offerId) {
        return issueServiceHelper.issueVcResult(offerId);
    }
}

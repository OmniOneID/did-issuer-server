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

package org.omnione.did.issuer.v1.service;

import org.omnione.did.issuer.v1.dto.vc.*;


/**
 * Service interface for handling IssuanceService.
 */
public interface IssueService {

    /**
     * Requests an offer for issuing a Verifiable Credential.
     *
     * @param request the VC to request an offer for
     * @return the offer for the given VC
     */
    OfferIssueVcResDto requestOffer(OfferIssueVcReqDto request);

    /**
     * Inspects the issue proposal for a Verifiable Credential.
     *
     * @param request the VC to inspect the issue proposal for
     * @return the inspection result for the given VC
     */
    InspectIssueProposeResDto inspectIssuePropose(InspectIssueProposeReqDto request);

    /**
     * Generates an issue profile for a Verifiable Credential.
     *
     * @param generateIssueProfileReqDto the VC to generate an issue profile for
     * @return the issue profile for the given VC
     */
    GenerateIssueProfileResDto generateIssueProfile(GenerateIssueProfileReqDto generateIssueProfileReqDto);

    /**
     * Issues a Verifiable Credential.
     *
     * @param request the VC to issue
     * @return the result of issuing the given VC
     */
    IssueVcResDto issueVc(IssueVcReqDto request);

    /**
     * Completes the issuance of a Verifiable Credential.
     *
     * @param request the request for completing the issuance of a VC
     * @return the response for completing the issuance of a VC
     */
    CompleteVcResDto completeVc(CompleteVcReqDto request);

    /**
     * Requests the result of issuing a Verifiable Credential.
     *
     * @param offerId the ID of the offer for the Verifiable Credential
     * @return the result of issuing the Verifiable Credential
     */
    IssueVcResultResDto issueVcResult(String offerId);
}


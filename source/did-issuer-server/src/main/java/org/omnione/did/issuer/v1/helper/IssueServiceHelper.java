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

package org.omnione.did.issuer.v1.helper;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.omnione.did.base.constants.VcPlanId;
import org.omnione.did.base.db.domain.Transaction;
import org.omnione.did.data.model.schema.VcSchema;
import org.omnione.did.issuer.v1.dto.vc.*;
import org.omnione.did.issuer.v1.service.MdlIssueServiceImpl;
import org.omnione.did.issuer.v1.service.IssueService;
import org.omnione.did.issuer.v1.service.IssueServiceBase;
import org.omnione.did.issuer.v1.service.NationalIdIssueServiceImpl;
import org.omnione.did.issuer.v1.service.query.TransactionService;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

/**
 * The IssueServiceHelper class provides methods for issuing VCs.
 * It is designed to facilitate the issuance of VCs.
 *
 */
@Profile("!sample")
@Slf4j
@RequiredArgsConstructor
@Service
public class IssueServiceHelper implements IssueService {
    private final NationalIdIssueServiceImpl userInitIssueService;
    private final MdlIssueServiceImpl issueInitIssueService;

    private final TransactionService transactionService;

    /**
     * Request an offer for the given VC.
     *
     * @param request the VC to request an offer for
     * @return the offer for the given VC
     */
    @Override
    public OfferIssueVcResDto requestOffer(OfferIssueVcReqDto request) {

        return issueInitIssueService.requestOffer(request);
    }

    /**
     *
     * Inspects the issue proposal for a Verifiable Credential.
     *
     * @param request the VC to inspect the issue proposal for
     * @return the inspection result for the given VC
     */
    @Override
    public InspectIssueProposeResDto inspectIssuePropose(InspectIssueProposeReqDto request) {
        return issueInitIssueService.inspectIssuePropose(request);
    }

    /**
     * Generates an issue profile for a Verifiable Credential.
     *
     * @param request the VC to generate an issue profile for
     * @return the issue profile for the given VC
     */
    @Override
    public GenerateIssueProfileResDto generateIssueProfile(GenerateIssueProfileReqDto request) {
        return getIssueServiceByTransaction(request.getTxId()).generateIssueProfile(request);
    }

    /**
     * Request an offer for the given VC.
     *
     * @param request the VC to request an offer for
     * @return the offer for the given VC
     */
    @Override
    public IssueVcResDto issueVc(IssueVcReqDto request) {
        return getIssueServiceByTransaction(request.getTxId()).issueVc(request);
    }

    /**
     * completeVc for the given VC.
     *
     * @param request the VC to request an offer for
     * @return the offer for the given VC
     */
    @Override
    public CompleteVcResDto completeVc(CompleteVcReqDto request) {
        return getIssueServiceByTransaction(request.getTxId()).completeVc(request);
    }

    /**
     * issueVcResult for the given offerId
     *
     * @param offerId offerId to issue VC
     * @return Issued VC result for the given offerId
     */
    @Override
    public IssueVcResultResDto issueVcResult(String offerId) {
        return issueInitIssueService.issueVcResult(offerId);
    }

    /**
     *  Method for retrieving the VC Plan ID using the Transaction ID,
     *  and then fetching an implementation of the IssueService interface based on that VC Plan ID
     *
     * @param txId transactionId to IssuerService
     * @return IssuerService to use for the given transactionId
     */
    private IssueServiceBase getIssueServiceByTransaction(String txId) {
        Transaction transaction = transactionService.findByTxId(txId);

        if (VcPlanId.VCPLANID000000000001.getLabel().equals(transaction.getVcPlanId())) {
            return issueInitIssueService;
        }
        return userInitIssueService;
    }
}

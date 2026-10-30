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

package org.omnione.did.issuer.v1.agent.helper;

import org.omnione.did.issuer.v1.admin.service.query.IssueProfileQueryService;

import org.omnione.did.issuer.v1.agent.service.IssueInitIssueService;
import org.omnione.did.issuer.v1.agent.service.TestIssueService;
import org.omnione.did.issuer.v1.agent.service.UserInitIssueService;
import org.omnione.did.issuer.v1.agent.service.UserQueryApiIssueService;
import org.omnione.did.issuer.v1.agent.service.query.TransactionService;
import org.omnione.did.issuer.v1.agent.service.IssueService;
import org.omnione.did.base.datamodel.enums.UserQueryType;
import org.omnione.did.base.db.domain.IssueProfile;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.omnione.did.base.db.domain.Transaction;
import org.omnione.did.sdk.issuer.model.dto.*;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

@Profile("!sample")
@Slf4j
@RequiredArgsConstructor
@Service
public class IssueServiceHelper implements IssueService {
    private final UserInitIssueService userInitIssueService;
    private final IssueInitIssueService issueInitIssueService;
    private final IssueProfileQueryService issueProfileQueryService;
    private final TransactionService transactionService;
    private final UserQueryApiIssueService userQueryApiIssueService;
    private final TestIssueService testIssueService;

    @Override
    public OfferIssueVcResDto requestOffer(OfferIssueVcReqDto request) {

        return issueInitIssueService.requestOffer(request);
    }

    @Override
    public InspectIssueProposeResDto inspectIssuePropose(InspectIssueProposeReqDto request) {
        return issueInitIssueService.inspectIssuePropose(request);
    }

    @Override
    public GenerateIssueProfileResDto generateIssueProfile(GenerateIssueProfileReqDto request) {
        return getIssueServiceByTransaction(request.getTxId()).generateIssueProfile(request);
    }

    @Override
    public IssueVcResDto issueVc(IssueVcReqDto request) {
        return getIssueServiceByTransaction(request.getTxId()).issueVc(request);
    }

    @Override
    public GenerateIssueProfileResDto generateIssueProfileFromProxy(GenerateIssueProfileReqDto request,
            String accessToken) {

        return getIssueServiceByTransaction(request.getTxId()).generateIssueProfileFromProxy(request, accessToken);
    }

    @Override
    public IssueVcResDto issueVcFromProxy(IssueVcReqDto request) {
        return getIssueServiceByTransaction(request.getTxId()).issueVcFromProxy(request);
    }

    @Override
    public CompleteVcResDto completeVc(CompleteVcReqDto request) {
        return getIssueServiceByTransaction(request.getTxId()).completeVc(request);
    }

    @Override
    public IssueVcResultResDto issueVcResult(String offerId) {
        return issueInitIssueService.issueVcResult(offerId);
    }

    private IssueService getIssueServiceByTransaction(String txId) {
        final Transaction tx = transactionService.findByTxId(txId);
        final IssueProfile profile = issueProfileQueryService.findById(tx.getIssueProfileId()); // 예외 발생 가정

        if (UserQueryType.API.equals(profile.getUserQueryType())) {
            return userQueryApiIssueService;
        }
        if (UserQueryType.TEST.equals(profile.getUserQueryType())) {
            return testIssueService;
        }

        return switch (profile.getInitiateType()) {
            case ISSUER_INIT -> issueInitIssueService;
            case USER_INIT -> userInitIssueService;
        };
    }
}

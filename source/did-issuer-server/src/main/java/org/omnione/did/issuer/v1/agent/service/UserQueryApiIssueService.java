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

package org.omnione.did.issuer.v1.agent.service;

// @TODO: External API integration method not yet decided.
//        Implement when API user query is officially supported.

import lombok.extern.slf4j.Slf4j;
import org.omnione.did.base.datamodel.data.Holder;
import org.omnione.did.base.db.domain.User;
import org.omnione.did.base.db.domain.VcProfile;
import org.omnione.did.base.exception.ErrorCode;
import org.omnione.did.base.exception.OpenDidException;
import org.omnione.did.base.property.KycProperty;
import org.omnione.did.issuer.v1.admin.service.query.IssueProfileQueryService;
import org.omnione.did.issuer.v1.admin.service.query.VcSchemaQueryService;
import org.omnione.did.issuer.v1.admin.service.query.ZkpCredentialDefinitionQueryService;
import org.omnione.did.issuer.v1.admin.service.query.ZkpSchemaQueryService;
import org.omnione.did.issuer.v1.agent.dto.vc.*;
import org.omnione.did.issuer.v1.agent.service.query.*;
import org.omnione.did.issuer.v1.common.service.StorageService;
import org.omnione.did.issuer.v1.common.service.ZkpWalletService;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Stub service for API-mode user query. All methods throw USER_QUERY_API_NOT_SUPPORTED
 * until external API integration is officially decided and implemented.
 */
@Slf4j
@Service
@Transactional
@Profile("!sample")
public class UserQueryApiIssueService extends IssueServiceBase {

    public UserQueryApiIssueService(VcProfileQueryService vcProfileQueryService,
                                    VcOfferQueryService vcOfferQueryService,
                                    TransactionService transactionService,
                                    E2EQueryService e2EQueryService,
                                    VcQueryService vcQueryService,
                                    StorageService storageService,
                                    FileWalletService walletService,
                                    IssueProfileQueryService issueProfileQueryService,
                                    VcSchemaService vcSchemaService,
                                    VcSchemaQueryService vcSchemaQueryService,
                                    IssuerInfoQueryService issuerInfoQueryService,
                                    ZkpWalletService zkpWalletService,
                                    ZkpCredentialDefinitionQueryService zkpCredentialDefinitionQueryService,
                                    ZkpSchemaQueryService zkpSchemaQueryService,
                                    KycProperty kycProperty) {
        super(vcProfileQueryService, vcOfferQueryService, transactionService, e2EQueryService,
                vcQueryService, storageService, walletService, issueProfileQueryService,
                vcSchemaService, vcSchemaQueryService, issuerInfoQueryService, zkpWalletService,
                zkpCredentialDefinitionQueryService, zkpSchemaQueryService, kycProperty);
    }

    @Override
    public OfferIssueVcResDto requestOffer(OfferIssueVcReqDto request) {
        throw new OpenDidException(ErrorCode.USER_QUERY_API_NOT_SUPPORTED);
    }

    @Override
    public InspectIssueProposeResDto inspectIssuePropose(InspectIssueProposeReqDto request) {
        throw new OpenDidException(ErrorCode.USER_QUERY_API_NOT_SUPPORTED);
    }

    @Override
    public GenerateIssueProfileResDto generateIssueProfile(GenerateIssueProfileReqDto request) {
        throw new OpenDidException(ErrorCode.USER_QUERY_API_NOT_SUPPORTED);
    }

    @Override
    public IssueVcResDto issueVc(IssueVcReqDto request) {
        throw new OpenDidException(ErrorCode.USER_QUERY_API_NOT_SUPPORTED);
    }

    @Override
    public CompleteVcResDto completeVc(CompleteVcReqDto request) {
        throw new OpenDidException(ErrorCode.USER_QUERY_API_NOT_SUPPORTED);
    }

    @Override
    public IssueVcResultResDto issueVcResult(String offerId) {
        throw new OpenDidException(ErrorCode.USER_QUERY_API_NOT_SUPPORTED);
    }

    @Override
    protected User findUserByVcProfile(VcProfile vcProfile) {
        throw new OpenDidException(ErrorCode.USER_QUERY_API_NOT_SUPPORTED);
    }

    @Override
    protected User findUserByVcProfileAndVcSchemaId(VcProfile vcProfile, Long vcSchemaId) {
        throw new OpenDidException(ErrorCode.USER_QUERY_API_NOT_SUPPORTED);
    }

    @Override
    protected User findUserByHolder(Holder holder) {
        throw new OpenDidException(ErrorCode.USER_QUERY_API_NOT_SUPPORTED);
    }

    @Override
    protected User findUserByHolderAndVcSchemaId(Holder holder, Long vcSchemaId) {
        throw new OpenDidException(ErrorCode.USER_QUERY_API_NOT_SUPPORTED);
    }
}

/*
 * Copyright 2024 - 2025 OmniOne.
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

import lombok.extern.slf4j.Slf4j;
import org.omnione.did.base.datamodel.data.*;
import org.omnione.did.base.db.domain.*;
import org.omnione.did.base.exception.ErrorCode;
import org.omnione.did.base.exception.OpenDidException;
import org.omnione.did.issuer.v1.admin.service.query.IssueProfileQueryService;
import org.omnione.did.issuer.v1.admin.service.query.VcSchemaQueryService;
import org.omnione.did.issuer.v1.admin.service.query.ZkpCredentialDefinitionQueryService;
import org.omnione.did.issuer.v1.admin.service.query.ZkpSchemaQueryService;
import org.omnione.did.issuer.v1.agent.service.query.*;

import org.omnione.did.issuer.v1.common.service.StorageService;
import org.omnione.did.issuer.v1.common.service.ZkpWalletService;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * This service provides methods for issuing a Verifiable Credential (VC) for a national ID.
 */
@Slf4j
@Service
@Transactional
@Profile("!sample")
public class UserInitIssueService extends IssueServiceBase {
    private final UserQueryService userQueryService;

    public UserInitIssueService(VcProfileQueryService vcProfileQueryService, VcOfferQueryService vcOfferQueryService,
                                TransactionService transactionService, E2EQueryService e2EQueryService,
                                VcQueryService vcQueryService, StorageService storageService,
                                FileWalletService walletService, UserQueryService userQueryService, VcSchemaService vcSchemaService,
                                VcSchemaQueryService vcSchemaQueryService, IssueProfileQueryService issueProfileQueryService,
                                IssuerInfoQueryService issuerInfoQueryService, ZkpWalletService zkpWalletService,
                                ZkpCredentialDefinitionQueryService zkpCredentialDefinitionQueryService,
                                ZkpSchemaQueryService zkpSchemaQueryService) {
        super(vcProfileQueryService, vcOfferQueryService, transactionService, e2EQueryService, vcQueryService
                , storageService, walletService, issueProfileQueryService, vcSchemaService,
                vcSchemaQueryService, issuerInfoQueryService, zkpWalletService, zkpCredentialDefinitionQueryService,
                zkpSchemaQueryService);
        this.userQueryService = userQueryService;
    }

    /**
     * Finds a user by a VC profile.
     *
     * @param vcProfile The VC profile.
     * @return The user.
     * @throws OpenDidException if the Holder is not found.
     */
    @Override
    protected User findUserByVcProfile(VcProfile vcProfile) {
        return userQueryService.findByDid(vcProfile.getDid())
                .orElseThrow(() -> new OpenDidException(ErrorCode.HOLDER_NOT_FOUND));
    }

    @Override
    protected User findUserByVcProfileAndVcSchemaId(VcProfile vcProfile, Long vcSchemaId) {
        return userQueryService.findByDidAndVcSchemaId(vcProfile.getDid(), vcSchemaId)
                .orElseThrow(() -> new OpenDidException(ErrorCode.HOLDER_NOT_FOUND));
    }

    /**
     * Finds a user by a Holder.
     *
     * @param holder The Holder.
     * @return The user.
     */
    @Override
    protected User findUserByHolder(Holder holder) {
        // Issue Profile step to return null because the User information is unregistered
        return new User();
    }

    @Override
    protected User findUserByHolderAndVcSchemaId(Holder holder, Long vcSchemaId) {
        // Issue Profile step to return null because the User information is unregistered
        return new User();
    }
}

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

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.omnione.did.base.datamodel.data.*;
import org.omnione.did.base.datamodel.data.claim.NationalIdClaimInfo;
import org.omnione.did.base.db.domain.*;
import org.omnione.did.base.exception.ErrorCode;
import org.omnione.did.base.exception.OpenDidException;
import org.omnione.did.base.property.IssueProperty;
import org.omnione.did.core.data.rest.ClaimInfo;
import org.omnione.did.issuer.v1.dto.demo.InsertUserReqDto;
import org.omnione.did.issuer.v1.service.query.*;
import org.omnione.did.wallet.key.WalletManagerInterface;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

import java.util.HashMap;

/**
 * This service provides methods for issuing a Verifiable Credential (VC) for a national ID.
 */
@Slf4j
@Service
@Profile("!sample")
public class NationalIdIssueServiceImpl extends IssueServiceBase {
    private final UserQueryService userQueryService;
    private final VcSchemaService vcSchemaService;
    public NationalIdIssueServiceImpl(VcProfileQueryService vcProfileQueryService, VcOfferQueryService vcOfferQueryService,
                                      TransactionService transactionService, E2EQueryService e2EQueryService,
                                      VcQueryService vcQueryService, IssueProperty issueProperty, StorageService storageService,
                                      FileWalletService walletService, UserQueryService userQueryService, VcSchemaService vcSchemaService) {
        super(vcProfileQueryService, vcOfferQueryService, transactionService, e2EQueryService, vcQueryService,
                issueProperty, storageService, walletService);
        this.userQueryService = userQueryService;
        this.vcSchemaService = vcSchemaService;
    }

    /**
     * Generates claim information for a national ID VC.
     *
     * @param data The user information.
     * @return The claim information.
     * @throws OpenDidException failed to deserialize the user information.
     */
    @Override
    protected HashMap<String, ClaimInfo> generateClaimInfo(String data) {
        try {
            ObjectMapper mapper = new ObjectMapper();
            InsertUserReqDto userInfo = mapper.readValue(data, InsertUserReqDto.class);

            return new NationalIdClaimInfo.Builder()
                    .userName(userInfo.getUserName())
                    .birthDate(userInfo.getBirthdate())
                    .address(userInfo.getAddress())
                    .issueDate(userInfo.getIssueDate())
                    .pii(userInfo.getPii())
                    .build()
                    .getClaims();
        } catch (JsonProcessingException e) {
            throw new OpenDidException(ErrorCode.JSON_DE_SERIALIZE_FAILED);
        }
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
        return userQueryService.findByDid(vcProfile.getDid()).orElseThrow(()
                -> new OpenDidException(ErrorCode.HOLDER_NOT_FOUND));
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

    /**
     * Gets the VC schema for a national ID.
     *
     * @return The VC schema.
     */
    @Override
    protected String getVcSchema() {
        return vcSchemaService.getNationalIdSchema();
    }
}

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
import org.omnione.did.base.datamodel.data.Holder;
import org.omnione.did.base.datamodel.data.claim.MdlClaimInfo;
import org.omnione.did.base.db.domain.User;
import org.omnione.did.base.db.domain.VcProfile;
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
 * This service provides methods for issuing a Verifiable Credential (VC) for MDL(Mobile Driver License).
 */
@Slf4j
@Service
@Profile("!sample")
public class MdlIssueServiceImpl extends IssueServiceBase {
    private final UserQueryService userQueryService;
    private final VcSchemaService vcSchemaService;


    public MdlIssueServiceImpl(VcProfileQueryService vcProfileQueryService, VcOfferQueryService vcOfferQueryService,
                               TransactionService transactionService, E2EQueryService e2EQueryService,
                               VcQueryService vcQueryService, IssueProperty issueProperty, StorageService storageService,
                               WalletManagerInterface walletManagerInterface, UserQueryService userQueryService, VcSchemaService vcSchemaService) {
        super(vcProfileQueryService, vcOfferQueryService, transactionService, e2EQueryService, vcQueryService,
                issueProperty, storageService, walletManagerInterface);
        this.userQueryService = userQueryService;
        this.vcSchemaService = vcSchemaService;
    }


    /**
     * Generates claim information for MDL.
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

            return new MdlClaimInfo.Builder()
                    .givenName(userInfo.getLastname())
                    .familyName(userInfo.getFirstname())
                    .birthDate(userInfo.getBirthdate())
                    .documentNumber(userInfo.getLicenseNum())
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

        return userQueryService.findById(vcProfile.getUserId());
    }

    /**
     * Finds a user by a Holder.
     *
     * @param holder The Holder.
     * @return The user.
     * @throws OpenDidException if the Holder is not found.
     */
    @Override
    protected User findUserByHolder(Holder holder) {

        return userQueryService.findByPii(holder.getPii())
                .orElseThrow(() -> new OpenDidException(ErrorCode.HOLDER_NOT_FOUND));
    }
    /**
     * Gets the VC schema for MDL.
     *
     * @return The VC schema.
     */
    @Override
    protected String getVcSchema() {

        return vcSchemaService.getMdlVcSchema();
    }
}

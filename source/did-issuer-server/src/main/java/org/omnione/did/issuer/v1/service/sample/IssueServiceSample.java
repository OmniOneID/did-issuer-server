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

package org.omnione.did.issuer.v1.service.sample;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.omnione.did.base.datamodel.data.E2e;
import org.omnione.did.base.datamodel.data.IssueOfferPayload;
import org.omnione.did.base.datamodel.enums.OfferType;
import org.omnione.did.base.exception.ErrorCode;
import org.omnione.did.base.exception.OpenDidException;
import org.omnione.did.data.model.profile.issue.IssueProfile;
import org.omnione.did.issuer.v1.dto.vc.*;
import org.omnione.did.issuer.v1.service.IssueService;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

/**
 * This class is an implementation of the IssueService interface and provides methods for handling issuance services.
 */
@RequiredArgsConstructor
@Profile("sample")
@Service
public class IssueServiceSample implements IssueService {


    /**
     * This method is used to request an offer for issuing a Verifiable Credential.
     *
     * @param request The request object containing the VC plan ID.
     * @return The response object containing the transaction ID and the issue offer payload.
     */
    @Override
    public OfferIssueVcResDto requestOffer(OfferIssueVcReqDto request) {

        return OfferIssueVcResDto.builder()
                .issueOfferPayload(IssueOfferPayload.builder()
                        .offerId("99999999-9999-9999-9999-999999999999")
                        .type(OfferType.ISSUE_OFFER)
                        .vcPlanId("vcplanid000000000001")
                        .issuer("did:omn:issuer")
                        .validUntil("2030-01-01T09:00:00Z")
                        .build())
                .build();
    }

    /**
     * Inspects the propose issue for a Verifiable Credential.
     *
     * @param request the request for inspecting the propose issue
     * @return the response for inspecting the propose issue
     */
    @Override
    public InspectIssueProposeResDto inspectIssuePropose(InspectIssueProposeReqDto request) {

        return InspectIssueProposeResDto.builder()
                .txId("99999999-9999-9999-9999-999999999999")
                .refId("99999999999999999999")
                .build();

    }

    /**
     * Generates the issue profile for a Verifiable Credential.
     *
     * @param generateIssueProfileReqDto The request object containing the necessary information to generate the issue profile.
     * @return The response object containing the transaction ID and the generated issue profile.
     * @throws OpenDidException If there is an error during processing or serialization.
     */
    @Override
    public GenerateIssueProfileResDto generateIssueProfile(GenerateIssueProfileReqDto generateIssueProfileReqDto) {
        String stringResponse = """
                {
                  "description": "National ID",
                  "encoding": "UTF-8",
                  "id": "4d2c9387-7239-43c0-b829-c899d4aeac19",
                  "language": "ko",
                  "profile": {
                    "credentialSchema": {
                      "id": "http://127.0.0.1:8091/issuer/api/v1/vc/vcschema?name=national_id",
                      "type": "OsdSchemaCredential"
                    },
                    "issuer": {
                      "certVcRef": "http://127.0.0.1:8091/issuer/api/v1/certificate-vc",
                      "did": "did:omn:issuer",
                      "name": "issuer"
                    },
                    "process": {
                      "endpoints": [
                        "http://127.0.0.1:8091/issuer"
                      ],
                      "issuerNonce": "mbRz+NJ7fEZEyFiAGIcfktQ",
                      "reqE2e": {
                        "cipher": "AES-256-CBC",
                        "curve": "Secp256r1",
                        "nonce": "mbRz+NJ7fEZEyFiAGIcfktQ",
                        "padding": "PKCS5",
                        "publicKey": "mA+1jfCC06BtbLwUkkAAsiU46i4GWz17SWnaME4yx7g2c"
                      }
                    }
                  },
                  "proof": {
                    "created": "2024-08-29T10:36:10.879887Z",
                    "proofPurpose": "assertionMethod",
                    "proofValue": "mIGTSOOl3ij0TnXd3xnk368PFDNuvaus2SXlWeiyITsZ7RAPvzi0QZqdWbMPMEh5K2OFTJ0FW/vR/njfvPZBkJc0",
                    "type": "Secp256r1Signature2018",
                    "verificationMethod": "did:omn:issuer#assert"
                  },
                  "title": "National ID",
                  "type": "IssueProfile"
                }
                """;
        ObjectMapper mapper = new ObjectMapper();

        try {
            IssueProfile issueProfile = mapper.readValue(stringResponse, IssueProfile.class);

            return GenerateIssueProfileResDto.builder()
                    .txId("99999999-9999-9999-9999-999999999999")
                    .profile(issueProfile)
                    .build();

        } catch (JsonProcessingException e) {
            e.printStackTrace();
            throw new OpenDidException(ErrorCode.JSON_DE_SERIALIZE_FAILED);
        }
    }

    /**
     * Issues a Verifiable Credential (VC).
     *
     * @param request The request object containing the necessary information to issue the VC.
     * @return The response object containing the transaction ID and the encrypted VC.
     */
    @Override
    public IssueVcResDto issueVc(IssueVcReqDto request) {
        final String encVc = "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";

        return IssueVcResDto.builder()
                .txId("99999999-9999-9999-9999-999999999999")
                .e2e(E2e.builder()
                        .encVc(encVc)
                        .iv("mwZPbY3+4RBYwwzuLOM6AFA")
                        .build())
                .build();
    }

    /**
     * Completes the issuance of a Verifiable Credential.
     *
     * @param request The request object containing the necessary information to complete the issuance of the VC.
     * @return The response object containing the transaction ID.
     */
    @Override
    public CompleteVcResDto completeVc(CompleteVcReqDto request) {

        return CompleteVcResDto.builder()
                .txId("99999999-9999-9999-9999-999999999999")
                .build();
    }

    /**
     * Generates a response object for issuing a Verifiable Credential result.
     *
     * @param offerId The offer ID related to the issued Verifiable Credential.
     * @return The response object containing the transaction ID, offer ID, and result status.
     */
    @Override
    public IssueVcResultResDto issueVcResult(String offerId) {
        return IssueVcResultResDto.builder()
                .txId("99999999-9999-9999-9999-999999999999")
                .offerId(offerId)
                .result(true)
                .build();
    }
}

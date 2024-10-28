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

import org.junit.jupiter.api.*;
import org.omnione.did.base.constants.VcPlanId;
import org.omnione.did.base.datamodel.data.AccE2e;
import org.omnione.did.base.datamodel.data.Holder;
import org.omnione.did.base.datamodel.enums.EccCurveType;
import org.omnione.did.base.datamodel.enums.SymmetricCipherType;
import org.omnione.did.base.datamodel.enums.SymmetricPaddingType;
import org.omnione.did.base.util.BaseCryptoUtil;
import org.omnione.did.base.util.BaseMultibaseUtil;
import org.omnione.did.base.util.RandomUtil;
import org.omnione.did.crypto.keypair.KeyPairInterface;
import org.omnione.did.data.model.profile.ReqE2e;
import org.omnione.did.data.model.vc.VerifiableCredential;
import org.omnione.did.issuer.v1.dto.vc.*;
import org.omnione.did.issuer.v1.helper.IssueServiceHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;

import java.nio.charset.StandardCharsets;
import java.security.interfaces.ECPrivateKey;
import java.security.interfaces.ECPublicKey;

//@Transactional
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@AutoConfigureMockMvc
@SpringBootTest
class IssueServiceTest {

    @Autowired
    private IssueServiceHelper issueService;
    private static String txId;
    private static String refId;
    private static String nonce;
    private static String offerId;
    private static String vcId;
    private static String profileId;
    private static ReqE2e reqE2e;
    private static KeyPairInterface keyPair;

    @Test
    @Order(1)
    void offerVc() {
        OfferIssueVcReqDto request = new OfferIssueVcReqDto();
        request.setVcPlanId(VcPlanId.VCPLANID000000000001.getLabel());
        System.out.println("request = " + request);

        OfferIssueVcResDto response = issueService.requestOffer(request);
        System.out.println("response = " + response);
        offerId = response.getIssueOfferPayload().getOfferId();
    }
    @Test
    @Order(2)
    void inspectIssuePropose() {
        System.out.println("INSPECT_ISSUE_PROPOSE");
        InspectIssueProposeReqDto request = new InspectIssueProposeReqDto();
        request.setVcPlanId(VcPlanId.VCPLANID000000000002.getLabel());
        request.setId(RandomUtil.generateMessageId());
        request.setIssuer("did:omn:issuer");
        request.setOfferId(offerId);
        System.out.println("request = " + request);
        InspectIssueProposeResDto response = issueService.inspectIssuePropose(request);
        System.out.println("response = " + response);
        txId = response.getTxId();
        refId = response.getRefId();
    }

    @Test
    @Order(3)
    void generateIssueProfile() {
        System.out.println("GENERATE_ISSUE_PROFILE");
        GenerateIssueProfileReqDto request = new GenerateIssueProfileReqDto();
        request.setTxId(txId);
        request.setHolder(Holder.builder()
                .did("did:omn:user1")
                .pii("f6043e73f3bf4c54864bde2418d1a4fcc617a9319e06d483d57f670c0089fd4d")
                .build());
        System.out.println("request = " + request);
        GenerateIssueProfileResDto response = issueService.generateIssueProfile(request);
        System.out.println("response = " + response);
        System.out.println("response.getProfile().toJson() = " + response.getProfile().toJson());
        nonce = response.getProfile().getProfile().getProcess().getIssuerNonce();
        profileId = response.getProfile().getId();
        reqE2e = response.getProfile().getProfile().getProcess().getReqE2e();

        Assertions.assertEquals(txId, response.getTxId(), "TxId Check");
    }

    @Test
    @Order(4)
    void issueVc() {
        System.out.println("ISSUE_VC");
        String reqVc = """
                {
                    "refId" : "%s",
                    "profile" : {
                        "id" : "%s",
                        "issuerNonce" : "%s"
                    }
                }
                """.formatted(refId, profileId, nonce);
        IssueVcReqDto request = new IssueVcReqDto();
        request.setEncReqVc(encReqVc(reqVc));
        request.setTxId(txId);

        byte[] encoded = ((ECPublicKey) keyPair.getPublicKey()).getEncoded();
        byte[] bytes = BaseCryptoUtil.compressPublicKey(encoded, EccCurveType.SECP_256_R1);


        request.setAccE2e(AccE2e.builder()
                .publicKey(BaseMultibaseUtil.encode(bytes))
                .iv("u9Mytc_E57cDAaAIIuCqfhw")
                .build());
        System.out.println("request = " + request);
        IssueVcResDto response = issueService.issueVc(request);

        System.out.println("response = " + response);
        VerifiableCredential vc = decIssueVc(response.getE2e().getEncVc(), response.getE2e().getIv());
        vcId = vc.getId();

        Assertions.assertEquals(txId, response.getTxId(), "TxId Check");
    }


    @Test
    @Order(5)
    void completeIssue() {
        CompleteVcReqDto request = new CompleteVcReqDto();
        request.setId(RandomUtil.generateMessageId());
        request.setVcId(vcId);
        request.setTxId(txId);
        CompleteVcResDto response = issueService.completeVc(request);
        System.out.println("request = " + request);
        System.out.println("response = " + response);
    }

    public String encReqVc(String reqVc) {
        keyPair = BaseCryptoUtil.generateKeyPair(EccCurveType.SECP_256_R1);
        ECPrivateKey ecPriKey = (ECPrivateKey) keyPair.getPrivateKey();
        System.out.println("BaseMultibaseUtil.encode(ecPriKey.getEncoded()) = " + BaseMultibaseUtil.encode(ecPriKey.getEncoded()));
        byte[] sharedSecret = BaseCryptoUtil.generateSharedSecret(BaseMultibaseUtil.decode(reqE2e.getPublicKey()), ecPriKey.getEncoded(), EccCurveType.SECP_256_R1);
        byte[] mergeSharedSecretAndNonce = BaseCryptoUtil.mergeSharedSecretAndNonce(sharedSecret, BaseMultibaseUtil.decode(reqE2e.getNonce()), SymmetricCipherType.AES_256_CBC);

        byte[] encrypt = BaseCryptoUtil.encrypt(reqVc, mergeSharedSecretAndNonce, BaseMultibaseUtil.decode("u9Mytc_E57cDAaAIIuCqfhw"), SymmetricCipherType.AES_256_CBC, SymmetricPaddingType.PKCS5);
        return BaseMultibaseUtil.encode(encrypt);

    }

    public VerifiableCredential decIssueVc(String endVc, String iv) {
        ECPrivateKey ecPriKey = (ECPrivateKey) keyPair.getPrivateKey();
        System.out.println("BaseMultibaseUtil.encode(ecPriKey.getEncoded()) = " + BaseMultibaseUtil.encode(ecPriKey.getEncoded()));
        byte[] sharedSecret = BaseCryptoUtil.generateSharedSecret(BaseMultibaseUtil.decode(reqE2e.getPublicKey()), ecPriKey.getEncoded(), EccCurveType.SECP_256_R1);
        byte[] mergeSharedSecretAndNonce = BaseCryptoUtil.mergeSharedSecretAndNonce(sharedSecret, BaseMultibaseUtil.decode(reqE2e.getNonce()), SymmetricCipherType.AES_256_CBC);

        byte[] decrypt = BaseCryptoUtil.decrypt(endVc, mergeSharedSecretAndNonce, BaseMultibaseUtil.decode(iv), SymmetricCipherType.AES_256_CBC, SymmetricPaddingType.PKCS5);
        String s = new String(decrypt, StandardCharsets.UTF_8);
        VerifiableCredential verifiableCredential = new VerifiableCredential();
        verifiableCredential.fromJson(s);
        return verifiableCredential;
    }

}
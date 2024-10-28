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
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.omnione.did.base.datamodel.data.ReqRevokeVc;
import org.omnione.did.base.util.BaseDigestUtil;
import org.omnione.did.base.util.BaseMultibaseUtil;
import org.omnione.did.base.util.RandomUtil;
import org.omnione.did.common.util.DateTimeUtil;
import org.omnione.did.common.util.JsonUtil;
import org.omnione.did.data.model.did.Proof;
import org.omnione.did.data.model.enums.did.ProofPurpose;
import org.omnione.did.data.model.enums.did.ProofType;
import org.omnione.did.issuer.v1.dto.vc.CompleteRevokeReqDto;
import org.omnione.did.issuer.v1.dto.vc.InspectProposeRevokeReqDto;
import org.omnione.did.issuer.v1.dto.vc.InspectProposeRevokeResDto;
import org.omnione.did.issuer.v1.dto.vc.RevokeVcReqDto;
import org.omnione.did.wallet.exception.WalletException;
import org.omnione.did.wallet.key.WalletManagerInterface;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;

import java.security.NoSuchAlgorithmException;

/**
 * Description...
 *
 * @author : gwnam
 * @fileName : RevokeServiceTest
 * @since : 8/13/24
 */
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@AutoConfigureMockMvc
@SpringBootTest
public class VcStatusServiceTest {
    @Autowired
    private VcStatusServiceImpl vcStatusService;
    @Autowired
    private WalletManagerInterface walletManagerInterface;

    public static String txId;
    public static String nonce;
    @Order(1)
    @Test
    public void inspectProposeRevoke() throws JsonProcessingException {
        InspectProposeRevokeReqDto request = InspectProposeRevokeReqDto.builder()
                .id(RandomUtil.generateMessageId())
                .vcId("9dd017e5-a8cd-40bf-b67e-a18518e01cb7")
                .build();
        InspectProposeRevokeResDto response = vcStatusService.inspectProposeRevoke(request);
        nonce = response.getIssuerNonce();
        txId = response.getTxId();
        System.out.println("response = " + JsonUtil.serializeAndSort(response));
    }

    @Order(2)
    @Test
    public void revokeVc() throws NoSuchAlgorithmException, WalletException, JsonProcessingException {
        ReqRevokeVc reqRevokeVc = ReqRevokeVc.builder()
                .issuerNonce(nonce)
                .vcId("9dd017e5-a8cd-40bf-b67e-a18518e01cb7")
                .build();
        Proof proof = new Proof();
        proof.setProofPurpose(ProofPurpose.ASSERTION_METHOD.getRawValue());
        proof.setType(ProofType.SECP256R1_SIGNATURE_2018.getRawValue());
        proof.setVerificationMethod("did:omn:issuer?versionId=1#assert");
        proof.setCreated(DateTimeUtil.getCurrentUTCTimeString());

        reqRevokeVc.setProof(proof);

        String source = JsonUtil.serializeAndSort(reqRevokeVc);

        byte[] hashSource = BaseDigestUtil.generateHash(source);
        byte[] signature = walletManagerInterface.generateCompactSignatureFromHash("assert", hashSource);

        proof.setProofValue(BaseMultibaseUtil.encode(signature));

        RevokeVcReqDto request = RevokeVcReqDto.builder()
                .txId(txId)
                .request(reqRevokeVc)
                .build();

        vcStatusService.revokeVc(request);
    }

    @Order(3)
    @Test
    public void completeRevoke() {
        CompleteRevokeReqDto request = CompleteRevokeReqDto.builder()
                .txId(txId)
                .build();
        vcStatusService.completeRevoke(request);
    }


}

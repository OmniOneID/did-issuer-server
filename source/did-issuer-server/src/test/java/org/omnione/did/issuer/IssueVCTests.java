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

package org.omnione.did.issuer;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.*;
import org.omnione.did.IssuerApplication;
import org.omnione.did.base.constants.UrlConstant;
import org.omnione.did.base.datamodel.data.AccE2e;
import org.omnione.did.base.datamodel.data.E2e;
import org.omnione.did.base.datamodel.data.Holder;
import org.omnione.did.base.datamodel.enums.EccCurveType;
import org.omnione.did.base.datamodel.enums.SymmetricCipherType;
import org.omnione.did.base.datamodel.enums.SymmetricPaddingType;
import org.omnione.did.base.util.BaseCryptoUtil;
import org.omnione.did.base.util.BaseMultibaseUtil;
import org.omnione.did.base.util.RandomUtil;
import org.omnione.did.issuer.v1.dto.vc.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.nio.charset.StandardCharsets;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.web.servlet.function.RequestPredicates.param;

@DisplayName("VC Issuance Test")
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@SpringBootTest(classes = IssuerApplication.class)
@ActiveProfiles("sample")
@AutoConfigureMockMvc
class IssueVCTests {

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;

    @Order(1)
    @Test
    @DisplayName("1. Request Offer")
    void testRequestVcOffer() throws Exception {
        OfferIssueVcReqDto request = new OfferIssueVcReqDto();
        request.setVcPlanId("vcplanid000000000001");

        MvcResult result = mockMvc.perform(post(UrlConstant.Issuer.V1 + UrlConstant.Issuer.REQUEST_OFFER)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andReturn();

        String responseBody = result.getResponse().getContentAsString();
        System.out.println("Actual Controller Response: " + responseBody);

        OfferIssueVcResDto response = objectMapper.readValue(responseBody, OfferIssueVcResDto.class);
        System.out.println("Response: " + response.toString());
    }

    @Order(2)
    @Test
    @DisplayName("2. Inspect Issue Propose")
    void testInspectIssuePropose() throws Exception {
        InspectIssueProposeReqDto reqDto = new InspectIssueProposeReqDto();
        reqDto.setId(RandomUtil.generateMessageId());
        reqDto.setIssuer("did:omn:issuer");
        reqDto.setVcPlanId("vcplanid000000000001");
        reqDto.setOfferId("99999999-9999-9999-9999-999999999999");

        MvcResult result = mockMvc.perform(post(UrlConstant.Issuer.V1 + UrlConstant.Issuer.INSPECT_PROPOSE_ISSUE)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(reqDto)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andReturn();

        String responseBody = result.getResponse().getContentAsString();
        System.out.println("Actual Controller Response: " + responseBody);

        InspectIssueProposeResDto response = objectMapper.readValue(responseBody, InspectIssueProposeResDto.class);
        System.out.println("Response: " + response.toString());
    }

    @Order(3)
    @Test
    @DisplayName("3. generate Issue Profile")
    void testGenerateIssueProfile() throws Exception {
        GenerateIssueProfileReqDto reqDto = new GenerateIssueProfileReqDto();
        reqDto.setId("did:example:123456789abcdefghi");
        reqDto.setHolder(Holder.builder()
                        .pii("pii")
                        .did("did:omn:example123")
                        .build());
        reqDto.setTxId("99999999-9999-9999-9999-999999999999");

        MvcResult result = mockMvc.perform(post(UrlConstant.Issuer.V1 + UrlConstant.Issuer.GENERATE_ISSUE_PROFILE)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(reqDto)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andReturn();

        String responseBody = result.getResponse().getContentAsString();
        System.out.println("Actual Controller Response: " + responseBody);

        GenerateIssueProfileResDto response = objectMapper.readValue(responseBody, GenerateIssueProfileResDto.class);
        System.out.println("Response: " + response.toString());
    }

    @Order(4)
    @Test
    @DisplayName("4. Issue VC")
    void testIssueVc() throws Exception {
        IssueVcReqDto reqDto = new IssueVcReqDto();
        reqDto.setTxId("99999999-9999-9999-9999-999999999999");
        reqDto.setAccE2e(AccE2e.builder()
                        .iv("u9Mytc_E57cDAaAIIuCqfhw")
                        .publicKey("mAvuqNcA0akRCgC5anv6fTQFstQynq2WZgYg/9Eh0QkAy")
                        .build());
        reqDto.setEncReqVc("mYMh5+wqo+sFh3oMBpuQCVVNslPLH8juMMBQsUcoJ/NTGzqX4VkLwUML5gWecrHlpVeijasMZFbWVl9prwQApuB0ECzJWXvgJD9c4NwWRrMorMl+uU0eTHMMxbQhu4FARDy98pHpoBj6kP1pQ2Ai3iFgY9qQRowj0B6wW9Xu79Ugu+X+Labl3yqxrA9/5H3Z+b5VTmYZ/TZPKZRQuZRBb8qxz32GX1lCAPxki1ni2XDg");

        MvcResult result = mockMvc.perform(post(UrlConstant.Issuer.V1 + UrlConstant.Issuer.ISSUE_VC)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(reqDto)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andReturn();

        String responseBody = result.getResponse().getContentAsString();
        System.out.println("Actual Controller Response: " + responseBody);

        IssueVcResDto response = objectMapper.readValue(responseBody, IssueVcResDto.class);
        System.out.println("Response: " + response);

        E2e e2e = response.getE2e();
        String plainVC = decode(e2e.getIv(), e2e.getEncVc());
        System.out.println("VC = " + plainVC);
    }

    @Order(5)
    @Test
    @DisplayName("5. Complete VC")
    void testCompleteVc() throws Exception {
        CompleteVcReqDto reqDto = new CompleteVcReqDto();
        reqDto.setId("did:example:123456789abcdefghi");
        reqDto.setTxId("99999999-9999-9999-9999-999999999999");
        reqDto.setVcId("vcid000000000001");

        MvcResult result = mockMvc.perform(post(UrlConstant.Issuer.V1 + UrlConstant.Issuer.COMPLETE_VC)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(reqDto)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andReturn();

        String responseBody = result.getResponse().getContentAsString();
        System.out.println("Actual Controller Response: " + responseBody);

        CompleteVcResDto response = objectMapper.readValue(responseBody, CompleteVcResDto.class);
        System.out.println("Response: " + response.toString());

    }

    @Order(6)
    @Test
    @DisplayName("6. Issuer VC Result")
    void testIssuerVcResult() throws Exception {
        String offerId = "99999999-9999-9999-9999-999999999999";

        MvcResult result = mockMvc.perform(get(UrlConstant.Issuer.V1 + UrlConstant.Issuer.ISSUE_VC + UrlConstant.Issuer.RESULT)
                            .param("offerId", offerId))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andReturn();

        String responseBody = result.getResponse().getContentAsString();
        System.out.println("Actual Controller Response: " + responseBody);

        IssueVcResultResDto response = objectMapper.readValue(responseBody, IssueVcResultResDto.class);
        System.out.println("Response: " + response.toString());
    }


    private String decode(String iv, String encData) {
        byte[] privateKey = BaseMultibaseUtil.decode("mMIGTAgEAMBMGByqGSM49AgEGCCqGSM49AwEHBHkwdwIBAQQgNdy+e2T694f0QbSd/gdLvmdgUjti+RR6wQY4F+kMYfCgCgYIKoZIzj0DAQehRANCAAT7qjXANGpEQoAuWp7+n00BbLUMp6tlmYGIP/RIdEJAMt3uDVWWb54UkVN5RsN4PZ7mnM5ZL9fgEgZTf2nXNpaK");
        byte[] compressPublicKey = BaseMultibaseUtil.decode("mA+1jfCC06BtbLwUkkAAsiU46i4GWz17SWnaME4yx7g2c");

        byte[] sharedSecret = BaseCryptoUtil.generateSharedSecret(compressPublicKey, privateKey, EccCurveType.SECP_256_R1);
        byte[] mergeSharedSecretAndNonce = BaseCryptoUtil.mergeSharedSecretAndNonce(sharedSecret, BaseMultibaseUtil.decode("mbRz+NJ7fEZEyFiAGIcfktQ"), SymmetricCipherType.AES_256_CBC);

        byte[] encrypt = BaseCryptoUtil.decrypt(encData, mergeSharedSecretAndNonce, BaseMultibaseUtil.decode(iv), SymmetricCipherType.AES_256_CBC, SymmetricPaddingType.PKCS5);
        return new String(encrypt, StandardCharsets.UTF_8);
    }
}

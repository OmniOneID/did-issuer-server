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
import org.omnione.did.base.datamodel.data.ReqRevokeVc;
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

@DisplayName("Revoke VC")
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@SpringBootTest(classes = IssuerApplication.class)
@ActiveProfiles("sample")
@AutoConfigureMockMvc
class RevokeVCTests {

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;

    @Order(1)
    @Test
    @DisplayName("1. Inspect Revoke Propose")
    void testInspectRevokePropose() throws Exception {
        // 1. 요청 DTO 설정
        InspectProposeRevokeReqDto request = InspectProposeRevokeReqDto.builder()
                .id("20240904161842958000c9ca1908")
                .vcId("88c2d770-dd40-4209-81ee-53108d365d1d")
                .build();

        //2. 컨트롤러 호출 및 응답 검증
        MvcResult result = mockMvc.perform(post(UrlConstant.Issuer.V1 + UrlConstant.Issuer.INSPECT_PROPOSE_REVOKE)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andReturn();

        // 3. 실제 응답 확인
        String responseBody = result.getResponse().getContentAsString();
        System.out.println("Actual Controller Response: " + responseBody);
    }


    @Order(2)
    @Test
    @DisplayName("2. Revoke VC")
    void testRevokeVc() throws Exception {
        // 1. 요청 DTO 설정
        String strRequest = """
                {
                    "id":"20240904161842958000c9ca1908",
                    "request":{
                        "issuerNonce":"ms8pZ1pDN6vCxGSiwBeyOwQ",
                        "proof":{
                            "created":"2024-09-04T16:18:44.472836Z",
                            "proofPurpose":"assertionMethod",
                            "proofValue":"mIEAVe0v5Bvtw6qXzzE84uQNyq7GNHwbA/67UzejVnFX6M+QiTzGP/PteX43u10SvYm9jf1AhKU1UiQIpd/ix4rU",
                            "type":"Secp256r1Signature2018",
                            "verificationMethod":"did:omn:issuer?versionId=1#assert"
                        },
                        "vcId":"88c2d770-dd40-4209-81ee-53108d365d1d"
                    },
                    "txId":"6886a9d2-0b77-4ff5-bfdd-f6fb87c95fa0"
                }
                """;
        RevokeVcReqDto request = objectMapper.readValue(strRequest, RevokeVcReqDto.class);

        //2. 컨트롤러 호출 및 응답 검증
        MvcResult result = mockMvc.perform(post(UrlConstant.Issuer.V1 + UrlConstant.Issuer.REVOKE_VC)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andReturn();

        // 3. 실제 응답 확인
        String responseBody = result.getResponse().getContentAsString();
        System.out.println("Actual Controller Response: " + responseBody);
    }

    @Order(3)
    @Test
    @DisplayName("3. Complete Revoke")
    void testCompleteRevoke() throws Exception {
        CompleteRevokeReqDto request = CompleteRevokeReqDto.builder()
                .id("20240904161842958000c9ca1908")
                .txId("6886a9d2-0b77-4ff5-bfdd-f6fb87c95fa0")
                .build();

        //2. 컨트롤러 호출 및 응답 검증
        MvcResult result = mockMvc.perform(post(UrlConstant.Issuer.V1 + UrlConstant.Issuer.COMPLETE_REVOKE)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andReturn();

        // 3. 실제 응답 확인
        String responseBody = result.getResponse().getContentAsString();
        System.out.println("Actual Controller Response: " + responseBody);
    }
}

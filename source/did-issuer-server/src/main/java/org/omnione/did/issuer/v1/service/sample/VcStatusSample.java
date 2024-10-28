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

import org.omnione.did.base.datamodel.enums.VerifyAuthType;
import org.omnione.did.issuer.v1.dto.vc.*;
import org.omnione.did.issuer.v1.service.VcStatusService;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

/**
 * This class is an implementation of the VcStatusService interface
 */
@Service
@Profile("sample")
public class VcStatusSample implements VcStatusService {
    @Override
    public InspectProposeRevokeResDto inspectProposeRevoke(InspectProposeRevokeReqDto request) {
        return InspectProposeRevokeResDto.builder()
                .authType(VerifyAuthType.PIN_OR_BIO)
                .issuerNonce("ms8pZ1pDN6vCxGSiwBeyOwQ")
                .txId("6886a9d2-0b77-4ff5-bfdd-f6fb87c95fa0")
                .build();
    }

    @Override
    public RevokeVcResDto revokeVc(RevokeVcReqDto request) {
        return RevokeVcResDto.builder()
                .txId("6886a9d2-0b77-4ff5-bfdd-f6fb87c95fa0")
                .build();
    }

    @Override
    public UpdateVcStatusResDto updateVcStatus(UpdateVcStatusReqDto request) {
        return UpdateVcStatusResDto.builder()
                .txId("6886a9d2-0b77-4ff5-bfdd-f6fb87c95fa0")
                .build();
    }

    @Override
    public CompleteRevokeResDto completeRevoke(CompleteRevokeReqDto request) {
        return CompleteRevokeResDto.builder()
                .txId("6886a9d2-0b77-4ff5-bfdd-f6fb87c95fa0")
                .build();
    }
}

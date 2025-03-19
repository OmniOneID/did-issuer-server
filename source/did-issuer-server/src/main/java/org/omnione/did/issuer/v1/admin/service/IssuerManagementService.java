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

package org.omnione.did.issuer.v1.admin.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.omnione.did.base.db.constant.IssuerStatus;
import org.omnione.did.base.db.domain.CertificateVc;
import org.omnione.did.base.db.domain.IssuerInfo;
import org.omnione.did.base.db.repository.IssuerInfoRepository;
import org.omnione.did.base.util.BaseMultibaseUtil;
import org.omnione.did.data.model.did.DidDocument;
import org.omnione.did.issuer.v1.admin.api.dto.EmptyResDto;
import org.omnione.did.issuer.v1.admin.dto.GetIssuerInfoReqDto;
import org.omnione.did.issuer.v1.admin.dto.SendCertificateVcReqDto;
import org.omnione.did.issuer.v1.admin.dto.SendEntityInfoReqDto;
import org.omnione.did.issuer.v1.agent.service.StorageService;
import org.omnione.did.issuer.v1.agent.service.query.CertificateVcQueryService;
import org.omnione.did.issuer.v1.agent.service.query.IssuerInfoQueryService;
import org.springframework.stereotype.Service;

@Slf4j
@Transactional
@RequiredArgsConstructor
@Service
public class IssuerManagementService {
    private final IssuerInfoQueryService issuerInfoQueryService;
    private final StorageService storageService;
    private final CertificateVcQueryService certificateVcQueryService;


    public GetIssuerInfoReqDto getIssuerInfo() {
        IssuerInfo issuerInfo = issuerInfoQueryService.getIssuerInfo();

        if (issuerInfo.getStatus() != IssuerStatus.ACTIVATE) {
            return GetIssuerInfoReqDto.fromEntity(issuerInfo);
        }

        DidDocument didDocument = storageService.findDidDoc(issuerInfo.getDid());
        return GetIssuerInfoReqDto.fromEntity(issuerInfo, didDocument);
    }

    public EmptyResDto createCertificateVc(SendCertificateVcReqDto sendCertificateVcReqDto) {
        byte[] decodedVc = BaseMultibaseUtil.decode(sendCertificateVcReqDto.getCertificateVc());
        log.debug("Decoded VC: {}", new String(decodedVc));

        certificateVcQueryService.save(CertificateVc.builder()
                .vc(new String(decodedVc))
                .build());

        return new EmptyResDto();
    }

    public EmptyResDto updateEntityInfo(SendEntityInfoReqDto sendEntityInfoReqDto) {
        IssuerInfo existedIssuer = issuerInfoQueryService.getIssuerInfoOrNull();

        if (existedIssuer == null) {
            issuerInfoQueryService.save(IssuerInfo.builder()
                    .name(sendEntityInfoReqDto.getName())
                    .did(sendEntityInfoReqDto.getDid())
                    .status(IssuerStatus.ACTIVATE)
                    .serverUrl(sendEntityInfoReqDto.getServerUrl())
                    .certificateUrl(sendEntityInfoReqDto.getCertificateUrl())
                    .build());
        } else {
            existedIssuer.setName(sendEntityInfoReqDto.getName());
            existedIssuer.setDid(sendEntityInfoReqDto.getDid());
            existedIssuer.setServerUrl(sendEntityInfoReqDto.getServerUrl());
            existedIssuer.setCertificateUrl(sendEntityInfoReqDto.getCertificateUrl());
            existedIssuer.setStatus(IssuerStatus.ACTIVATE);
            issuerInfoQueryService.save(existedIssuer);
        }

        return new EmptyResDto();
    }

}

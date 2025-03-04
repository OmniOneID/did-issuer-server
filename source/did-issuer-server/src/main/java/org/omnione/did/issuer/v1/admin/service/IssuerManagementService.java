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
import org.omnione.did.base.db.constant.IssuerStatus;
import org.omnione.did.base.db.domain.IssuerInfo;
import org.omnione.did.data.model.did.DidDocument;
import org.omnione.did.issuer.v1.admin.dto.GetIssuerInfoReqDto;
import org.omnione.did.issuer.v1.agent.service.StorageService;
import org.omnione.did.issuer.v1.agent.service.query.IssuerInfoQueryService;
import org.springframework.stereotype.Service;

@Transactional
@RequiredArgsConstructor
@Service
public class IssuerManagementService {
    private final IssuerInfoQueryService issuerInfoQueryService;
    private final StorageService storageService;

    public GetIssuerInfoReqDto getIssuerInfo() {
        IssuerInfo issuerInfo = issuerInfoQueryService.getIssuerInfo();

        if (issuerInfo.getStatus() != IssuerStatus.ACTIVATE) {
            return GetIssuerInfoReqDto.fromEntity(issuerInfo);
        }

        DidDocument didDocument = storageService.findDidDoc(issuerInfo.getDid());
        return GetIssuerInfoReqDto.fromEntity(issuerInfo, didDocument);
    }

}

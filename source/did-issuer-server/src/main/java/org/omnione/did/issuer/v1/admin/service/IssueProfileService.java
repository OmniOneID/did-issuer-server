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

import lombok.RequiredArgsConstructor;
import org.omnione.did.base.db.domain.IssueProfile;
import org.omnione.did.base.db.domain.VcSchema;
import org.omnione.did.issuer.v1.admin.dto.CreateIssueProfileReqDto;
import org.omnione.did.issuer.v1.admin.dto.CreateIssueProfileResDto;
import org.omnione.did.issuer.v1.admin.service.query.IssueProfileQueryService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

/**
 * Description...
 *
 */
@RequiredArgsConstructor
@Service
public class IssueProfileService {
    private final IssueProfileQueryService issueProfileQueryService;

    public CreateIssueProfileResDto createIssueProfile(CreateIssueProfileReqDto request) {

        IssueProfile issueProfile = issueProfileQueryService.save(IssueProfile.builder()
                .vcPlainId(request.getVcPlanId())
                .title(request.getTitle())
                .description(request.getDescription())
                .endpoints(request.getEndpoints())
                .cipher(request.getCipher())
                .curve(request.getCurve())
                .padding(request.getPadding())
                .build());

        return CreateIssueProfileResDto.builder()
                .build();
    }

    public Page<IssueProfile> getIssueProfileList(Pageable pageable) {
        return issueProfileQueryService.findAll(pageable);
    }

    public IssueProfile getIssueProfileById(Long id) {
        return issueProfileQueryService.findById(id);
    }
}

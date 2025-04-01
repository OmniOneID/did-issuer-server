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
import org.omnione.did.base.db.domain.IssueProfile;
import org.omnione.did.base.db.domain.VcSchema;
import org.omnione.did.issuer.v1.admin.dto.CreateIssueProfileReqDto;
import org.omnione.did.issuer.v1.admin.dto.CreateIssueProfileResDto;
import org.omnione.did.issuer.v1.admin.dto.GetIssueProfileResDto;
import org.omnione.did.issuer.v1.admin.dto.IssueProfileDto;
import org.omnione.did.issuer.v1.admin.service.query.IssueProfileQueryService;
import org.omnione.did.issuer.v1.admin.service.query.VcSchemaQueryService;
import org.omnione.did.issuer.v1.agent.service.query.IssuerInfoQueryService;
import org.springframework.context.annotation.Profile;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

/**
 * Description...
 *
 */
@Profile("!sample")
@RequiredArgsConstructor
@Transactional
@Service
public class IssueProfileService {
    private final IssueProfileQueryService issueProfileQueryService;
    private final VcSchemaQueryService vcSchemaQueryService;
    private final ListCommunityService listCommunityService;
    public CreateIssueProfileResDto createIssueProfile(CreateIssueProfileReqDto request) {

        IssueProfile issueProfile = issueProfileQueryService.save(IssueProfile.builder()
                .vcPlanId(request.getVcPlanId())
                .title(request.getTitle())
                .description(request.getDescription())
                .endpoints(request.getEndpoints())
                .cipher(request.getCipher())
                .curve(request.getCurve())
                .padding(request.getPadding())
                .vcSchemaId(request.getVcSchemaId())
                .initiateType(request.getInitiateType())
                .language(request.getLanguage())
                .tags(request.getTags())
                .build());

        listCommunityService.registerVcPlan(issueProfile);

        return CreateIssueProfileResDto.builder()
                .build();
    }

    public Page<IssueProfile> getIssueProfileList(Pageable pageable) {
        return issueProfileQueryService.findAll(pageable);
    }

    public GetIssueProfileResDto getIssueProfileById(Long id) {
        IssueProfile issueProfile = issueProfileQueryService.findById(id);
        VcSchema vcSchema = vcSchemaQueryService.findById(issueProfile.getVcSchemaId());

        return GetIssueProfileResDto.builder().issueProfile(issueProfile)
                .vcSchemaName(vcSchema.getVcSchemaId())
                .build();
    }

    public void deleteIssueProfileById(Long id) {
        issueProfileQueryService.deleteIssueProfileById(id);
    }

    public Page<IssueProfileDto> searchIssueProfileList(String searchKey, String searchValue, Pageable pageable) {
        return issueProfileQueryService.searchIssueProfileList(searchKey, searchValue, pageable);
    }

    public void updateIssueProfile(CreateIssueProfileReqDto request) {
        IssueProfile issueProfile = issueProfileQueryService.findById(request.getId());

        issueProfile.setDescription(request.getDescription());
        issueProfile.setTitle(request.getTitle());
        issueProfile.setCipher(request.getCipher());
        issueProfile.setCurve(request.getCurve());
        issueProfile.setPadding(request.getPadding());
        issueProfile.setInitiateType(request.getInitiateType());
        issueProfile.setLanguage(request.getLanguage());
        issueProfile.setEndpoints(request.getEndpoints());
        issueProfile.setVcSchemaId(request.getVcSchemaId());
        issueProfile.setTags(request.getTags());


    }
}

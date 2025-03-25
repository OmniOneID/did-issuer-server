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

package org.omnione.did.issuer.v1.admin.service.query;

import lombok.RequiredArgsConstructor;
import org.omnione.did.base.db.domain.IssueProfile;
import org.omnione.did.base.db.domain.VcSchema;
import org.omnione.did.base.db.repository.IssueProfileRepository;
import org.omnione.did.base.db.repository.VcSchemaRepository;
import org.omnione.did.base.exception.ErrorCode;
import org.omnione.did.base.exception.OpenDidException;
import org.omnione.did.issuer.v1.admin.dto.IssueProfileDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Description...
 */
@RequiredArgsConstructor
@Service
public class IssueProfileQueryService {
    private final IssueProfileRepository issueProfileRepository;
    private final VcSchemaRepository vcSchemaRepository;
    public IssueProfile save(IssueProfile issueProfile) {
        return issueProfileRepository.save(issueProfile);
    }

    public Page<IssueProfile> findAll(Pageable pageable) {
        return issueProfileRepository.findAll(pageable);
    }

    public IssueProfile findById(Long id) {

        return issueProfileRepository.findById(id).orElseThrow(()
                -> new OpenDidException(ErrorCode.VC_PLAN_ID_INVALID));
    }

    public Boolean existsByVcPlanId(String vcPlanId) {
        return issueProfileRepository.existsByVcPlanId(vcPlanId);
    }

    public IssueProfile findByVcPlanId(String vcPlanId) {

        return issueProfileRepository.findByVcPlanId(vcPlanId)
                .orElseThrow(() -> new OpenDidException(ErrorCode.VC_PLAN_ID_INVALID));
    }

    public void deleteIssueProfileById(Long id) {
        if (!issueProfileRepository.existsByVcSchemaId(id)) {
            issueProfileRepository.deleteById(id);
        }
    }

    public Page<IssueProfileDto> searchIssueProfileList(String searchKey, String searchValue, Pageable pageable) {
        Page<IssueProfile> entityPage = issueProfileRepository.searchIssueProfiles(searchKey, searchValue, pageable);

        List<IssueProfileDto> issueProfileDtos = entityPage.getContent().stream()
                .map(issueProfile -> {
                    VcSchema vcSchema = vcSchemaRepository.findById(issueProfile.getVcSchemaId())
                            .orElseThrow(() -> new OpenDidException(ErrorCode.VC_SCHEMA_NOT_FOUND));
                    return IssueProfileDto.fromEntity(issueProfile, vcSchema.getVcSchemaId());
                })
                .collect(Collectors.toList());

        return new PageImpl<>(issueProfileDtos, pageable, entityPage.getTotalElements());
    }
}

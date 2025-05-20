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
import org.omnione.did.issuer.v1.admin.dto.profile.IssueProfileDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Query service for managing and retrieving issue profiles in the Admin Console.
 * <p>
 * Supports CRUD operations and filtered search functionality for VC issue profiles.
 */
@RequiredArgsConstructor
@Service
public class IssueProfileQueryService {

    private final IssueProfileRepository issueProfileRepository;
    private final VcSchemaRepository vcSchemaRepository;

    /**
     * Saves a new or existing IssueProfile to the repository.
     *
     * @param issueProfile the issue profile to persist
     * @return the saved IssueProfile
     */
    public IssueProfile save(IssueProfile issueProfile) {
        return issueProfileRepository.save(issueProfile);
    }

    /**
     * Retrieves all issue profiles with pagination.
     *
     * @param pageable pagination information
     * @return a page of issue profiles
     */
    public Page<IssueProfile> findAll(Pageable pageable) {
        return issueProfileRepository.findAll(pageable);
    }

    /**
     * Finds an IssueProfile by its ID.
     *
     * @param id the ID of the issue profile
     * @return the matching IssueProfile
     * @throws OpenDidException if not found
     */
    public IssueProfile findById(Long id) {
        return issueProfileRepository.findById(id)
                .orElseThrow(() -> new OpenDidException(ErrorCode.VC_PLAN_ID_INVALID));
    }

    /**
     * Checks whether an issue profile exists for the given VC Plan ID.
     *
     * @param vcPlanId the VC Plan ID
     * @return true if exists, false otherwise
     */
    public Boolean existsByVcPlanId(String vcPlanId) {
        return issueProfileRepository.existsByVcPlanId(vcPlanId);
    }

    /**
     * Checks whether an issue profile exists for the given VC Schema ID.
     *
     * @param vcSchemaId the VC Schema ID
     * @return true if exists, false otherwise
     */
    public Boolean existsByVcSchemaId(Long vcSchemaId) {
        return issueProfileRepository.existsByVcSchemaId(vcSchemaId);
    }

    /**
     * Finds an IssueProfile by its VC Plan ID.
     *
     * @param vcPlanId the VC Plan ID
     * @return the matching IssueProfile
     * @throws OpenDidException if not found
     */
    public IssueProfile findByVcPlanId(String vcPlanId) {
        return issueProfileRepository.findByVcPlanId(vcPlanId)
                .orElseThrow(() -> new OpenDidException(ErrorCode.VC_PLAN_ID_INVALID));
    }

    /**
     * Deletes an issue profile by its ID.
     *
     * @param id the ID of the issue profile to delete
     */
    public void deleteIssueProfileById(Long id) {
        issueProfileRepository.deleteById(id);
    }

    /**
     * Searches issue profiles based on a key-value filter with pagination.
     *
     * @param searchKey field to search by
     * @param searchValue value to search for
     * @param pageable pagination information
     * @return page of IssueProfileDto containing profile and VC Schema ID
     */
    public PageImpl<IssueProfileDto> searchIssueProfileList(String searchKey, String searchValue, Pageable pageable) {
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

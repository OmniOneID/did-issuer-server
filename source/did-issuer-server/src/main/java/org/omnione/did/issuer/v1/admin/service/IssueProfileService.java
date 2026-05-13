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
import org.omnione.did.base.datamodel.enums.UserQueryType;
import org.omnione.did.base.db.domain.IssueProfile;
import org.omnione.did.base.db.domain.VcSchema;
import org.omnione.did.base.db.domain.ZkpCredentialDefinition;
import org.omnione.did.base.exception.ErrorCode;
import org.omnione.did.base.exception.OpenDidException;
import org.omnione.did.issuer.v1.admin.dto.profile.CreateIssueProfileReqDto;
import org.omnione.did.issuer.v1.admin.dto.profile.CreateIssueProfileResDto;
import org.omnione.did.issuer.v1.admin.dto.profile.GetIssueProfileResDto;
import org.omnione.did.issuer.v1.admin.dto.profile.IssueProfileDto;
import org.omnione.did.issuer.v1.admin.service.query.IssueProfileQueryService;
import org.omnione.did.issuer.v1.admin.service.query.VcSchemaQueryService;
import org.omnione.did.issuer.v1.admin.service.query.ZkpCredentialDefinitionQueryService;
import org.springframework.context.annotation.Profile;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

/**
 * Service for managing issue profiles in the Admin Console.
 * Provides functionality to create, update, retrieve, delete, and search issue profiles,
 * as well as link them to VC schemas and register them to external community services.
 */
@Profile("!sample")
@RequiredArgsConstructor
@Transactional
@Service
public class IssueProfileService {

    private final IssueProfileQueryService issueProfileQueryService;
    private final VcSchemaQueryService vcSchemaQueryService;
    private final ListCommunityService listCommunityService;
    private final ZkpCredentialDefinitionQueryService zkpCredentialDefinitionQueryService;

    /**
     * Creates a new issue profile and registers it with the external community service.
     *
     * @param request the issue profile creation request
     * @return an empty response DTO
     */
    public CreateIssueProfileResDto createIssueProfile(CreateIssueProfileReqDto request) {
        UserQueryType userQueryType = request.getUserQueryType() != null ? request.getUserQueryType() : UserQueryType.DB;
        validateUserQueryFields(userQueryType, request.getUserQueryUrl());

        String userQueryUrl = UserQueryType.API.equals(userQueryType) ? request.getUserQueryUrl() : null;
        String userQueryHeaders = UserQueryType.API.equals(userQueryType) ? request.getUserQueryHeaders() : null;

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
                .issuanceMode(request.getIssuanceMode())
                .language(request.getLanguage())
                .tags(request.getTags())
                .zkpEnabled(request.getZkpEnabled())
                .definitionId(request.getDefinitionId())
                .userQueryType(userQueryType)
                .userQueryUrl(userQueryUrl)
                .userQueryHeaders(userQueryHeaders)
                .build());

        listCommunityService.registerVcPlan(issueProfile);

        return CreateIssueProfileResDto.builder().build();
    }

    /**
     * Retrieves a paginated list of all issue profiles.
     *
     * @param pageable pagination information
     * @return a page of issue profiles
     */
    public Page<IssueProfile> getIssueProfileList(Pageable pageable) {
        return issueProfileQueryService.findAll(pageable);
    }

    /**
     * Retrieves detailed information for a specific issue profile by ID.
     *
     * @param id the ID of the issue profile
     * @return the detailed response DTO with issue profile and VC schema name
     */
    public GetIssueProfileResDto getIssueProfileById(Long id) {
        IssueProfile issueProfile = issueProfileQueryService.findById(id);
        VcSchema vcSchema = vcSchemaQueryService.findById(issueProfile.getVcSchemaId());
        return GetIssueProfileResDto.builder()
                .issueProfile(issueProfile)
                .vcSchemaName(vcSchema.getVcSchemaId())
                .build();
    }

    /**
     * Deletes an issue profile by its ID.
     *
     * @param id the ID of the issue profile to delete
     */
    public void deleteIssueProfileById(Long id) {
        issueProfileQueryService.deleteIssueProfileById(id);
    }

    /**
     * Searches issue profiles based on search key and value with pagination support.
     *
     * @param searchKey the key to filter by
     * @param searchValue the value to filter with
     * @param pageable pagination information
     * @return a page of matching issue profiles
     */
    public PageImpl<IssueProfileDto> searchIssueProfileList(String searchKey, String searchValue, Pageable pageable) {
        return issueProfileQueryService.searchIssueProfileList(searchKey, searchValue, pageable);
    }

    /**
     * Updates an existing issue profile with the provided data.
     *
     * @param request the DTO containing updated information
     */
    public void updateIssueProfile(CreateIssueProfileReqDto request) {
        UserQueryType userQueryType = request.getUserQueryType() != null ? request.getUserQueryType() : UserQueryType.DB;
        validateUserQueryFields(userQueryType, request.getUserQueryUrl());

        IssueProfile issueProfile = issueProfileQueryService.findById(request.getId());

        issueProfile.setDescription(request.getDescription());
        issueProfile.setTitle(request.getTitle());
        issueProfile.setCipher(request.getCipher());
        issueProfile.setCurve(request.getCurve());
        issueProfile.setPadding(request.getPadding());
        issueProfile.setInitiateType(request.getInitiateType());
        issueProfile.setIssuanceMode(request.getIssuanceMode());
        issueProfile.setLanguage(request.getLanguage());
        issueProfile.setEndpoints(request.getEndpoints());
        issueProfile.setVcSchemaId(request.getVcSchemaId());
        issueProfile.setTags(request.getTags());
        issueProfile.setZkpEnabled(request.getZkpEnabled());
        issueProfile.setDefinitionId(request.getDefinitionId());
        issueProfile.setUserQueryType(userQueryType);

        if (UserQueryType.API.equals(userQueryType)) {
            issueProfile.setUserQueryUrl(request.getUserQueryUrl());
            issueProfile.setUserQueryHeaders(request.getUserQueryHeaders());
        } else {
            issueProfile.setUserQueryUrl(null);
            issueProfile.setUserQueryHeaders(null);
        }

        listCommunityService.registerVcPlan(issueProfile);
    }

    /**
     * Finds an issue profile by its VC schema string ID (e.g., "university-id-v1").
     *
     * @param vcSchemaId the string identifier of the VC schema
     * @return the matching issue profile response
     */
    public GetIssueProfileResDto findByVcSchemaId(String vcSchemaId) {
        VcSchema vcSchema = vcSchemaQueryService.findByVcSchemaId(vcSchemaId);
        IssueProfile issueProfile = issueProfileQueryService.findFirstByVcSchemaId(vcSchema.getId());
        return GetIssueProfileResDto.builder()
                .issueProfile(issueProfile)
                .vcSchemaName(vcSchema.getVcSchemaId())
                .build();
    }

    private void validateUserQueryFields(UserQueryType userQueryType, String userQueryUrl) {
        if (UserQueryType.API.equals(userQueryType) &&
                (userQueryUrl == null || userQueryUrl.isBlank())) {
            throw new OpenDidException(ErrorCode.URL_PING_ERROR);
        }
    }
}

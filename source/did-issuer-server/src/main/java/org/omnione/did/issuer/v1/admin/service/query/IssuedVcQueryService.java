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
import org.omnione.did.base.db.domain.Vc;
import org.omnione.did.base.db.repository.VcRepository;
import org.omnione.did.base.exception.ErrorCode;
import org.omnione.did.base.exception.OpenDidException;
import org.omnione.did.issuer.v1.admin.dto.vc.IssuedVcDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Query service for retrieving issued Verifiable Credentials (VCs) in the Admin Console.
 * <p>
 * Supports searching issued VCs with filters and pagination, and retrieving VCs by ID.
 */
@RequiredArgsConstructor
@Service
public class IssuedVcQueryService {

    private final VcRepository vcRepository;

    /**
     * Searches for issued VCs using the specified key and value with pagination.
     *
     * @param searchKey   the field to search by (e.g., DID, VC ID)
     * @param searchValue the value to match
     * @param pageable    pagination configuration
     * @return a page of IssuedVcDto objects
     */
    public PageImpl<IssuedVcDto> searchIssuedVcList(String searchKey, String searchValue, Pageable pageable) {
        Page<Vc> entityPage = vcRepository.searchIssuedVc(searchKey, searchValue, pageable);
        List<IssuedVcDto> vcs = entityPage.stream()
                .map(IssuedVcDto::fromEntity)
                .collect(Collectors.toList());

        return new PageImpl<>(vcs, pageable, entityPage.getTotalElements());
    }

    /**
     * Finds a VC entity by its unique ID.
     *
     * @param id the ID of the VC
     * @return the VC entity
     * @throws OpenDidException if the VC is not found
     */
    public Vc findById(Long id) {
        return vcRepository.findById(id)
                .orElseThrow(() -> new OpenDidException(ErrorCode.VC_NOT_FOUND));
    }
}

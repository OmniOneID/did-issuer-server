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
import org.omnione.did.base.db.repository.IssueProfileRepository;
import org.omnione.did.base.exception.ErrorCode;
import org.omnione.did.base.exception.OpenDidException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

/**
 * Description...
 *
 */
@RequiredArgsConstructor
@Service
public class IssueProfileQueryService {
    private final IssueProfileRepository issueProfileRepository;
    public IssueProfile save(IssueProfile issueProfile) {
        return issueProfileRepository.save(issueProfile);
    }

    public Page<IssueProfile> findAll(Pageable pageable) {
        return issueProfileRepository.findAll(pageable);
    }

    public IssueProfile findById(Long id) {
        // TODO Error Code
        return issueProfileRepository.findById(id).orElseThrow(()
                -> new OpenDidException(ErrorCode.TODO));
    }
}

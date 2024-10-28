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

package org.omnione.did.issuer.v1.service.query;

import lombok.RequiredArgsConstructor;
import org.omnione.did.base.db.domain.RevokeVc;
import org.omnione.did.base.db.repository.RevokeVcRepository;
import org.omnione.did.base.exception.ErrorCode;
import org.omnione.did.base.exception.OpenDidException;
import org.springframework.stereotype.Service;

/**
 * The RevokeVcQueryService class provides methods for saving and retrieving RevokeVcs.
 * It is designed to facilitate the storage and retrieval of RevokeVcs, ensuring that the data is accurate and up-to-date.
 */
@RequiredArgsConstructor
@Service
public class RevokeVcQueryService {
    private final RevokeVcRepository revokeVcRepository;

    /**
     * Retrieve the RevokeVc with the given transaction ID from the database.
     *
     * @param txId the ID of the RevokeVc to retrieve
     * @return the RevokeVc with the given transaction ID
     * @throws OpenDidException if the RevokeVc cannot be retrieved
     */
    public RevokeVc findByTransactionId(Long txId) {
        return revokeVcRepository.findByTransactionId(txId).orElseThrow(() ->
                new OpenDidException(ErrorCode.TRANSACTION_NOT_FOUND));
    }

    /**
     * Save the given RevokeVc to the database.
     *
     * @param revokeVc the RevokeVc to save
     * @return the saved RevokeVc
     */
    public RevokeVc save(RevokeVc revokeVc) {
        return revokeVcRepository.save(revokeVc);
    }

    public RevokeVc findByVcId(String vcId) {
        return revokeVcRepository.findByVcId(vcId).orElseGet(RevokeVc::new);
    }
}

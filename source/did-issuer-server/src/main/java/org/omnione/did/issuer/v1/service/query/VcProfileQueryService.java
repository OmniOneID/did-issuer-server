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
import org.omnione.did.base.db.domain.VcProfile;
import org.omnione.did.base.db.repository.VcProfileRepository;
import org.omnione.did.base.exception.ErrorCode;
import org.omnione.did.base.exception.OpenDidException;
import org.springframework.stereotype.Service;

/**
 * The VcProfileQueryService class provides methods for saving and retrieving VCs.
 * It is designed to facilitate the storage and retrieval of VCs, ensuring that the data is accurate and up-to-date.
 */

@RequiredArgsConstructor
@Service
public class VcProfileQueryService {
    private final VcProfileRepository vcProfileRepository;

    /**
     * Save the given VC to the database.
     *
     * @param vcProfile vcProfile to save
     * @return the saved VC
     */
    public VcProfile save(VcProfile vcProfile) {

        return vcProfileRepository.save(vcProfile);
    }

    /**
     * Retrieve the VC with the given transaction ID from the database.
     *
     * @param id the transaction ID of the VC to retrieve
     * @return the VC with the given transaction ID
     * @throws OpenDidException if the transaction is invalid
     */
    public VcProfile findByTransactionId(Long id) {
        return vcProfileRepository.findByTransactionId(id)
                .orElseThrow(() -> new OpenDidException(ErrorCode.TRANSACTION_INVALID));
    }
}

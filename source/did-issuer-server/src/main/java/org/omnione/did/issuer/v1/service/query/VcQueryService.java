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
import org.omnione.did.base.db.domain.Vc;
import org.omnione.did.base.db.repository.VcRepository;
import org.omnione.did.base.exception.ErrorCode;
import org.omnione.did.base.exception.OpenDidException;
import org.springframework.stereotype.Service;

import java.util.Optional;

/**
 * The VcQueryService class provides methods for saving and retrieving VCs.
 * It is designed to facilitate the storage and retrieval of VCs, ensuring that the data is accurate and up-to-date.
 */
@RequiredArgsConstructor
@Service
public class VcQueryService {
    private final VcRepository vcRepository;

    /**
     * Retrieve the VC with the given ID from the database.
     *
     * @param vcId the ID of the VC to retrieve
     * @return the VC with the given ID
     * @throws OpenDidException if the VC cannot be retrieved
     */
    public Vc findByVcId(String vcId) {
        return vcRepository.findByVcId(vcRepository)
                .orElseThrow(() -> new OpenDidException(ErrorCode.VC_NOT_FOUND));
    }

    /**
     * Save the given VC to the database.
     *
     * @param vc the VC to save
     * @return the saved VC
     */
    public Vc save(Vc vc) {
        return vcRepository.save(vc);
    }

    /**
     * Retrieve the VC with the given user ID from the database.
     *
     * @param id the ID of the user
     * @return the VC with the given user ID
     */
    public Optional<Vc> findByUserId(Long id) {
        return vcRepository.findByUserId(id);
    }

    /**
     * Retrieve the VC with the given user ID and VC plan ID from the database.
     *
     * @param id the ID of the user
     * @param vcPlanId the ID of the VC plan
     * @return the VC with the given user ID and VC plan ID
     */
    public Optional<Vc> findByUserIdAndVcPlanId(Long id, String vcPlanId) {
        return vcRepository.findByUserIdAndVcPlanId(id, vcPlanId);
    }

    public Vc findByTxId(String txId) {
        return vcRepository.findByTxId(txId).orElseThrow(()
                -> new OpenDidException(ErrorCode.TRANSACTION_INVALID));
    }
}

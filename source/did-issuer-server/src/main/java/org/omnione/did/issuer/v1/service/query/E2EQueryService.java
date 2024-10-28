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
import org.omnione.did.base.db.domain.E2E;
import org.omnione.did.base.db.repository.E2ERepository;
import org.omnione.did.base.exception.ErrorCode;
import org.omnione.did.base.exception.OpenDidException;
import org.springframework.stereotype.Service;

import java.util.Optional;

/**
 * The E2EQueryService class provides methods for saving and retrieving E2Es.
 * It is designed to facilitate the storage and retrieval of E2Es, ensuring that the data is accurate and up-to-date.
 */
@RequiredArgsConstructor
@Service
public class E2EQueryService {

    private final E2ERepository e2ERepository;

    /**
     * Save the given E2E to the database.
     *
     * @param e2E the ID of the E2E to save
     * @return saved E2E
     * @throws OpenDidException if the transaction is invalid
     */
    public E2E save(E2E e2E) {
        return e2ERepository.save(e2E);
    }

    /**
     * Retrieve the E2E with the given transaction ID from the database.
     *
     * @param transactionId the ID of the E2E to retrieve
     * @return the E2E with the given transaction ID
     * @throws OpenDidException if the transaction is invalid
     */
    public E2E findByTransactionId(Long transactionId) {
        return e2ERepository.findByTransactionId(transactionId)
                .orElseThrow(() -> new OpenDidException(ErrorCode.TRANSACTION_INVALID));
    }
}

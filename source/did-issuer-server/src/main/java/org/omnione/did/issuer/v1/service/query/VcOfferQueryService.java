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
import org.omnione.did.base.db.domain.VcOffer;
import org.omnione.did.base.db.repository.VcOfferRepository;
import org.omnione.did.base.exception.ErrorCode;
import org.omnione.did.base.exception.OpenDidException;
import org.springframework.stereotype.Service;

/**
 * The VcOfferQueryService class provides methods for saving and retrieving VcOffers.
 * It is designed to facilitate the storage and retrieval of VcOffers, ensuring that the data is accurate and up-to-date.
 */
@RequiredArgsConstructor
@Service
public class VcOfferQueryService {
    private final VcOfferRepository vcOfferRepository;

    /**
     * Save the given VcOffer to the database.
     *
     * @param vcOffer the ID of the VcOffer to save
     * @return saved VcOffer
     */
    public VcOffer save(VcOffer vcOffer) {
        return vcOfferRepository.save(vcOffer);
    }

    /**
     * Retrieve the VcOffer with the given offer ID from the database.
     *
     * @param offerId the ID of the VcOffer to retrieve
     * @return the VcOffer with the given offer ID
     * @throws OpenDidException if the VcOffer cannot be retrieved
     */
    public VcOffer findByOfferId(String offerId) {
        return vcOfferRepository.findByOfferId(offerId)
                .orElseThrow(() -> new OpenDidException(ErrorCode.VC_OFFER_NOT_FOUND));
    }
}

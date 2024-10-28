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
import org.omnione.did.base.datamodel.data.Holder;
import org.omnione.did.base.db.domain.User;
import org.omnione.did.base.db.repository.UserRepository;
import org.omnione.did.base.exception.ErrorCode;
import org.omnione.did.base.exception.OpenDidException;
import org.springframework.stereotype.Service;

import java.util.Optional;

/**
 * The UserQueryService class provides methods for saving and retrieving users.
 * It is designed to facilitate the storage and retrieval of users, ensuring that the data is accurate and up-to-date.
 */
@RequiredArgsConstructor
@Service
public class UserQueryService {
    private final UserRepository userRepository;

    /**
     * save the given user to the database.
     *
     * @param user the ID of the user to save
     * @return saved user
     */
    public User save(User user) {
        return userRepository.save(user);
    }

    /**
     * Retrieve the user with the given PII from the database.
     *
     * @param pii the PII of the user to retrieve
     * @return the
     * @throws OpenDidException if the user cannot be retrieved
     */
    public Optional<User> findByPii(String pii) {
        return userRepository.findByPii(pii);
    }

    /**
     * Retrieve the user with the given DID from the database.
     *
     * @param did the DID of the user to retrieve
     * @return the user with the given DID
     */
    public Optional<User> findByDid(String did) {
        return userRepository.findByDid(did);
    }

    /**
     * Retrieve the user with the given holder from the database.
     *
     * @param holder the holder of the user to retrieve
     * @return the user with the given holder
     */
    public Optional<User> findByHolder(Holder holder) {
        return findByPii(holder.getPii())
                .or(() -> findByDid(holder.getDid()));
    }

    public User findById(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new OpenDidException(ErrorCode.USER_NOT_FOUND));
    }
}
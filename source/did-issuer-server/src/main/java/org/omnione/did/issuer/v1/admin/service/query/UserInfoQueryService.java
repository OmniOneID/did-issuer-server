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
import org.omnione.did.base.db.domain.User;
import org.omnione.did.base.db.domain.VcSchema;
import org.omnione.did.base.db.repository.UserRepository;
import org.omnione.did.base.db.repository.VcSchemaRepository;
import org.omnione.did.base.exception.ErrorCode;
import org.omnione.did.base.exception.OpenDidException;
import org.omnione.did.issuer.v1.admin.dto.user.UserDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Query service for managing user data in the Admin Console.
 * <p>
 * Provides functionality to search, retrieve, and persist user information,
 * including mapping users to their associated VC schemas.
 */
@RequiredArgsConstructor
@Service
public class UserInfoQueryService {

    private final UserRepository userRepository;
    private final VcSchemaRepository vcSchemaRepository;

    /**
     * Searches user records based on a search key and value with pagination.
     * Includes associated VC schema information in the result.
     *
     * @param searchKey   the field to search by (e.g., DID or PII)
     * @param searchValue the value to match
     * @param pageable    pagination configuration
     * @return a page of UserDto objects
     */
    public PageImpl<UserDto> searchUserInfoList(String searchKey, String searchValue, Pageable pageable) {
        Page<User> entityPage = userRepository.searchUser(searchKey, searchValue, pageable);

        List<UserDto> namespaceDtos = entityPage.getContent().stream()
                .map(user -> {
                    VcSchema vcSchema = vcSchemaRepository.findById(user.getVcSchemaId())
                            .orElseThrow(() -> new OpenDidException(ErrorCode.VC_SCHEMA_NOT_FOUND));
                    return UserDto.fromEntity(user, vcSchema.getVcSchemaId());
                })
                .collect(Collectors.toList());

        return new PageImpl<>(namespaceDtos, pageable, entityPage.getTotalElements());
    }

    /**
     * Persists the given user to the repository.
     *
     * @param user the user entity to save
     */
    public void save(User user) {
        userRepository.save(user);
    }

    /**
     * Finds a user by their ID.
     *
     * @param id the user ID
     * @return the User entity
     * @throws OpenDidException if the user is not found
     */
    public User findById(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new OpenDidException(ErrorCode.USER_NOT_FOUND));
    }

    public User findByPiiAndVcSchemaIdOrNew(String pii, Long vcSchemaId) {
        return userRepository.findByPiiAndVcSchemaId(pii, vcSchemaId)
                .orElse(User.builder().build());
    }

    public User findByDidAndVcSchemaIdOrNew(String did, Long vcSchemaId) {
        return userRepository.findByDidAndVcSchemaId(did, vcSchemaId)
                .orElse(User.builder().build());
    }
}

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

package org.omnione.did.issuer.v1.service.sample;

import com.fasterxml.jackson.core.JsonProcessingException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.omnione.did.base.db.domain.User;
import org.omnione.did.base.exception.ErrorCode;
import org.omnione.did.base.exception.OpenDidException;
import org.omnione.did.common.util.JsonUtil;
import org.omnione.did.issuer.v1.dto.demo.InsertUserReqDto;
import org.omnione.did.issuer.v1.service.query.UserQueryService;
import org.springframework.stereotype.Service;


/**
 * The DemoUserService class is responsible for saving user information in the database.
 * It provides methods to insert user data into the database, ensuring data accuracy and up-to-date information.
 */
@Slf4j
@RequiredArgsConstructor
@Service
public class DemoUserService {
    private final UserQueryService userQueryService;

    /**
     * Saves user information in the database.
     * This method inserts the user data into the database, ensuring data accuracy and up-to-date information.
     *
     * @param request the request object containing the user information to be saved
     * @throws OpenDidException if an error occurs during the saving process
     */
    @Transactional
    public void saveUser(InsertUserReqDto request)  {
        log.debug("Insert User {}", request.toString());

        String pii = request.getPii();
        String userData = JsonUtil.serializeToJson(request);

        User user = userQueryService.findByPii(pii).orElseGet(User::new);
        user.setPii(pii);
        user.setData(userData);

        userQueryService.save(user);
    }


    /**
     * Saves VC information in the database.
     * This method inserts the VC info into the database, ensuring data accuracy and up-to-date information.
     *
     * @param request the request object containing the VC information to be saved
     * @throws OpenDidException if an error occurs during the saving process
     */
    @Transactional
    public void saveVcInfo(InsertUserReqDto request) {
        log.debug("Insert VC Info {}", request.toString());

        String did = request.getDid();
        String userData = JsonUtil.serializeToJson(request);

        User user = userQueryService.findByDid(did).orElseGet(User::new);
        user.setDid(did);
        user.setData(userData);

        userQueryService.save(user);
    }
}

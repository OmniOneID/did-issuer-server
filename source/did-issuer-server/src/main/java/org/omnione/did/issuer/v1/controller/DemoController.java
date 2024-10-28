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

package org.omnione.did.issuer.v1.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.omnione.did.base.constants.UrlConstant;
import org.omnione.did.issuer.v1.dto.demo.InsertUserReqDto;
import org.omnione.did.issuer.v1.service.query.UserQueryService;
import org.omnione.did.issuer.v1.service.sample.DemoUserService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * The DemoController class is a controller that handles requests related to demo.
 * It provides endpoints for inserting user and vc info.
 */

@Slf4j
@RequiredArgsConstructor
@RequestMapping(value = UrlConstant.Issuer.V1)
@RestController
public class DemoController {
    private final DemoUserService demoUserService;

    /**
     * Inserts a user.
     *
     * @param request the user to insert
     */
    @PostMapping("/user")
    public void insertUser(@RequestBody InsertUserReqDto request) {
        demoUserService.saveUser(request);
    }
    /**
     * Inserts a vc info.
     *
     * @param request the vc info to insert
     */
    @PostMapping("/vc")
    public void insertVcInfo(@RequestBody InsertUserReqDto request) {
        demoUserService.saveVcInfo(request);
    }
}

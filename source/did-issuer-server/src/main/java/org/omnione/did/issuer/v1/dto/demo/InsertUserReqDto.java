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

package org.omnione.did.issuer.v1.dto.demo;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * The InsertUserReqDto class is a data transfer object that represents the request to insert a user.
 * This class is used to demonstrate the use of the DID Issuer API.
 */
@ToString
@Getter
@Setter
public class InsertUserReqDto {
    private String did = "did:omn:test";
    private String firstname = "TE";
    private String lastname = "ST";
    private String userName = "TEST";
    private String birthdate = "2024-01-01";
    private String address = "TEST ";
    private String licenseNum = "123123-123123";
    private String issueDate = "2024-01-01";
    private String pii = "TEST";
}

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

package org.omnione.did.base.constants;

/**
 * The UrlConstant class contains constant strings representing the URLs used in the application.
 */
public class UrlConstant {
    public static class Issuer {
        public static final String V1 = "/issuer/api/v1";
        public static final String REQUEST_OFFER = "/request-offer";
        public static final String ISSUE_VC = "/issue-vc";
        public static final String INSPECT_PROPOSE_ISSUE = "/inspect-propose-issue";
        public static final String GENERATE_ISSUE_PROFILE = "/generate-issue-profile";
        public static final String COMPLETE_VC = "/complete-vc";
        public static final String RESULT = "/result";
        public static final String VC = "/vc";
        public static final String STATUS = "/status";
        public static final String INSPECT_PROPOSE_REVOKE = "/inspect-propose-revoke";
        public static final String REVOKE_VC = "/revoke-vc";
        public static final String COMPLETE_REVOKE = "/complete-revoke";
        public static final String SCHEMA = "/vcschema";
    }
}

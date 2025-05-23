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

    public static class Admin {
        public static final String PATH_VARIABLE_ID = "/{id}";
        public static final String V1 = "/issuer/admin/v1";
        public static final String NAMESPACE = "/namespaces";
        public static final String VC_SCHEMA = "/vc-schemas";
        public static final String ISSUE_PROFILE = "/issue-profiles";
        public static final String LIST = "/list";
        public static final String ISSUER = "/issuer";
        public static final String ADMIN = "/admins";

        public static final String USER = "/users";

        public static final String ISSUED_VCS = "/issued-vcs";

        public static final String ZKP_NAMESPACE  = "/zkp/namespaces";
        public static final String CHECK_NAMESPACE_ID = "/check-namespace-id";
        public static final String ZKP_SCHEMA  = "/zkp/schemas";
        public static final String FIND_ALL = "/all";
        public static final String ZKP_ATTRIBUTES  = "/attributes";

        public static final String ZKP_CREDENTIAL_DEFINITION  = "/zkp/definitions";
        public static final String CHECK_CREDENTIAL_DEFINITION_ALIAS = "/check-credential-definition-alias";
        public static final String RE_REGISTER = "/re-register";

    }

    public static class Tas {
        public static final String ADMIN_V1 = "/tas/admin/v1";

        public static final String V1 = "/tas/api/v1";
        public static final String REGISTER_DID_PUBLIC = "/entities/register-did/public";
        public static final String REQUEST_ENTITY_STATUS = "/entities/request-status";
        public static final String PROPOSE_ENROLL_ENTITY = "/propose-enroll-entity";
        public static final String REQUEST_ECDH = "/request-ecdh";
        public static final String REQUEST_ENROLL_ENTITY = "/request-enroll-entity";
        public static final String CONFIRM_ENROLL_ENTITY = "/confirm-enroll-entity";

    }

    public static class List {
        public static final String V1 = "/list/admin/v1";
        public static final String VC_SCHEMA_PUBLIC = "/vc-schemas/public";
        public static final String VC_PLAN_PUBLIC = "/vc-plans/public";
        public static final String CREDENTIAL_SCHEMA_PUBLIC = "/credential-schemas/public";
        public static final String CREDENTIAL_DEFINITION_PUBLIC = "/credential-definitions/public";
    }

}

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

package org.omnione.did.base.datamodel.enums;

/**
 * Defines how the Issuer server resolves user claim data during VC issuance.
 * DB: query from the User table (pre-registered data).
 * API: fetch from an external API endpoint (stub — not yet supported).
 * TEST: auto-generate dummy claim data without any pre-registration.
 */
public enum UserQueryType {
    DB, API, TEST
}

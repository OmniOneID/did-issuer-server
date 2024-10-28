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

package org.omnione.did.base.db.constant;

/**
 * Enum class for the sub_transaction_type table.
 */
public enum SubTransactionType {
    OFFER_ISSUE_VC,
    INSPECT_ISSUE_PROPOSE,
    GENERATE_ISSUE_PROFILE,
    ISSUE_VC,
    COMPLETE_VC,

    INSPECT_REVOKE_PROPOSE,
    REVOKE_VC,
    COMPLETE_REVOKE
}

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

package org.omnione.did.base.datamodel.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import lombok.Getter;

/**
 * Enum class for the verify_auth_type table.
 */
@Getter
public enum VerifyAuthType {
    NO_RESTRICTIONS_AUTHENTICATION(0x00000000),
    NO_AUTHENTICATION(0x00000001),
    PIN(0x00000002),
    BIO(0x00000004),
    PIN_OR_BIO(0x00000006),
    PIN_AND_BIO(0x00008006);

    private final int value;

    VerifyAuthType(int value) {
        this.value = value;
    }

    @JsonValue
    public int toJson() {
        return value;
    }

    @JsonCreator
    public static VerifyAuthType fromJson(String key) {
        for (VerifyAuthType type : VerifyAuthType.values()) {
            if (type.name().equals(key)) {
                return type;
            }
        }
        throw new IllegalArgumentException("Unknown authentication type: " + key);
    }


}

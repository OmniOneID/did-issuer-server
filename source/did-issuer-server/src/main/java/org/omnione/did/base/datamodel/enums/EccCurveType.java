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

import com.fasterxml.jackson.annotation.JsonValue;
import org.omnione.did.base.exception.ErrorCode;
import org.omnione.did.base.exception.OpenDidException;
import org.omnione.did.crypto.enums.DidKeyType;

/**
 * Enum class for the ecc_curve_type table.
 */
public enum EccCurveType {
    SECP_256_K1("Secp256k1"),
    SECP_256_R1("Secp256r1");

    private final String displayName;

    EccCurveType(String displayName) {
        this.displayName = displayName;
    }
    @Override
    @JsonValue
    public String toString() {
        return displayName;
    }

    public static EccCurveType fromValue(String value) {
        for (EccCurveType type : EccCurveType.values()) {
            if (type.displayName.equalsIgnoreCase(value)) {
                return type;
            }
        }
        throw new IllegalArgumentException("Unknown enum value: " + value);

    }

    public org.omnione.did.crypto.enums.EccCurveType toOmnioneEccCurveType() {
        return switch (this) {
            case SECP_256_K1 -> org.omnione.did.crypto.enums.EccCurveType.Secp256k1;
            case SECP_256_R1 -> org.omnione.did.crypto.enums.EccCurveType.Secp256r1;
        };
    }

    public DidKeyType toOmnioneDidKeyType() {
        return switch (this) {
            case SECP_256_K1 -> DidKeyType.SECP256K1_VERIFICATION_KEY_2018;
            case SECP_256_R1 -> DidKeyType.SECP256R1_VERIFICATION_KEY_2018;
        };
    }
}

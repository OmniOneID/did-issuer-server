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

import lombok.Getter;

import java.util.HashMap;
import java.util.Map;

/**
 * Enum class representing the ID of a VC Plan.
 * Each VC Plan ID has a label and a name.
 */
@Getter
public enum VcPlanId {
    VCPLANID000000000001("vcplanid000000000001", "mdl"),
    VCPLANID000000000002("vcplanid000000000002", "national_id")
    ;

    private final String label;
    private final String name;

    private static final Map<String, VcPlanId> PLAN_ID = new HashMap<>();

    static {
        for (VcPlanId vcPlanId : values()) {
            PLAN_ID.put(vcPlanId.label, vcPlanId);
        }
    }
    VcPlanId(String label, String name) {
        this.label = label;
        this.name = name;
    }

    public static VcPlanId valueOfLabel(String label) {
        return PLAN_ID.get(label);
    }
}

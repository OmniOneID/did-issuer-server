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

package org.omnione.did.base.datamodel.data;

import lombok.*;
import org.omnione.did.data.model.vc.CredentialSchema;

import java.util.List;

/**
 * This class represents the VcPlan structure.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
@Builder
public class VcPlan {
    private String vcPlanId;
    private String name;
    private String description;
    private String ref;
    private LogoImage logo;
    private String validFrom;
    private String validUntil;
    private List<String> tags;
    private CredentialSchema credentialSchema;
    private Option option;
    private String delegator;
    private List<String> allowedIssuers;
    private String manager;
}

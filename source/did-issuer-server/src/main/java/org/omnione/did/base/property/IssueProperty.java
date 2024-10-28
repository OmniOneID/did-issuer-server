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

package org.omnione.did.base.property;

import lombok.Getter;
import lombok.Setter;

import org.omnione.did.base.constants.VcPlanId;
import org.omnione.did.base.datamodel.enums.VerifyAuthType;
import org.omnione.did.data.model.profile.issue.IssueProfile;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.Map;
import java.util.Set;


/**
 * Configuration properties class for the Issue.
 * This class maps configuration properties with the prefix "issue" to its fields.
 *
 */
@Getter
@Setter
@ConfigurationProperties(prefix = "issue")
public class IssueProperty {
    private Map<VcPlanId, IssueProfile> profiles;
    private String did;
    private String name;
    private String domain;
    private String assertSignKeyId;
    private String certVcRef;
    private VerifyAuthType revokeVerifyAuthType;
    public IssueProfile getProfileByVcPlanId(String vcPlanId) {
        return profiles.get(VcPlanId.valueOfLabel(vcPlanId));
    }
    public Set<VcPlanId> getPlanIds() {
        return profiles.keySet();
    }
}

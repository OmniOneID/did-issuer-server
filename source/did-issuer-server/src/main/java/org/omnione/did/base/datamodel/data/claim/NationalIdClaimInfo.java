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

package org.omnione.did.base.datamodel.data.claim;

import org.apache.logging.log4j.util.Strings;
import org.omnione.did.core.data.rest.ClaimInfo;

import java.nio.charset.StandardCharsets;
import java.util.HashMap;

/**
 * Represents claim information for a National ID.
 * pattern for creating National ID claim information.
 */
public class NationalIdClaimInfo implements ClaimInfos {

    private final HashMap<String, ClaimInfo> claimInfos;

    /**
     * Constructs a NationalIdClaimInfo object using the provided Builder.
     *
     * @param builder the builder used to construct the NationalIdClaimInfo object
     */
    public NationalIdClaimInfo(Builder builder) {
        this.claimInfos = builder.claimInfos;
    }

    /**
     * The Builder class for creating a NationalIdClaimInfo object.
     * This class uses a step-by-step method to add claim information.
     */
    public static class Builder {

        private final HashMap<String, ClaimInfo> claimInfos = new HashMap<>();

        /**
         * Sets the family name for the national ID claim.
         *
         * @param familyName the family name to be added to the claim
         * @return the Builder object for method chaining
         */
        public Builder userName(String familyName) {
            if (Strings.isEmpty(familyName)) {
                return this;
            }

            String familyNameCode = "org.opendid.v1.national_id.user_name";

            ClaimInfo claimInfo = new ClaimInfo();
            claimInfo.setCode(familyNameCode);
            claimInfo.setValue(familyName.getBytes(StandardCharsets.UTF_8));

            claimInfos.put(familyNameCode, claimInfo);
            return this;
        }

        /**
         * Sets the birth date for the national ID claim.
         *
         * @param birthDate the birth date to be added to the claim in the format "YYYY-MM-DD"
         * @return the Builder object for method chaining
         */
        public Builder birthDate(String birthDate) {
            if (Strings.isEmpty(birthDate)) {
                return this;
            }

            String birthDateCode = "org.opendid.v1.national_id.birth_date";

            ClaimInfo claimInfo = new ClaimInfo();
            claimInfo.setCode(birthDateCode);
            claimInfo.setValue(birthDate.getBytes(StandardCharsets.UTF_8));

            claimInfos.put(birthDateCode, claimInfo);

            return this;
        }

        /**
         * Sets the issue date for the national ID claim.
         *
         * @param issueDate the issue date to be added to the claim in the format "YYYY-MM-DD"
         * @return the Builder object for method chaining
         */
        public Builder issueDate(String issueDate) {
            if (Strings.isEmpty(issueDate)) {
                return this;
            }

            String issueDateCode = "org.opendid.v1.national_id.issue_date";

            ClaimInfo claimInfo = new ClaimInfo();
            claimInfo.setCode(issueDateCode);
            claimInfo.setValue(issueDate.getBytes(StandardCharsets.UTF_8));

            claimInfos.put(issueDateCode, claimInfo);

            return this;
        }

        /**
         * Sets the address for the national ID claim.
         *
         * @param address the address to be added to the claim
         * @return the Builder object for method chaining
         */
        public Builder address(String address) {
            if (Strings.isEmpty(address)) {
                return this;
            }

            String addressCode = "org.opendid.v1.national_id.address";

            ClaimInfo claimInfo = new ClaimInfo();
            claimInfo.setCode(addressCode);
            claimInfo.setValue(address.getBytes(StandardCharsets.UTF_8));

            claimInfos.put(addressCode, claimInfo);

            return this;
        }

        /**
         * Sets the personally identifiable information (PII) for the national ID claim.
         *
         * @param pii the PII to be added to the claim
         * @return the Builder object for method chaining
         */
        public Builder pii(String pii) {
            if (Strings.isEmpty(pii)) {
                return this;
            }

            String piiCode = "org.opendid.v1.pii";

            ClaimInfo claimInfo = new ClaimInfo();
            claimInfo.setCode(piiCode);
            claimInfo.setValue(pii.getBytes(StandardCharsets.UTF_8));

            claimInfos.put(piiCode, claimInfo);

            return this;
        }

        /**
         * Builds the NationalIdClaimInfo object from the added claim information.
         *
         * @return a new NationalIdClaimInfo object
         */
        public NationalIdClaimInfo build() {
            return new NationalIdClaimInfo(this);
        }
    }

    /**
     * Returns the HashMap containing all the claims added to this NationalIdClaimInfo.
     *
     * @return a HashMap where the key is the claim code and the value is the ClaimInfo object
     */
    @Override
    public HashMap<String, ClaimInfo> getClaims() {
        return claimInfos;
    }
}

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
 * Represents claim information for a Mobile Driver's License (MDL).
 * pattern for creating MDL claim information.
 *
 */
public class MdlClaimInfo implements ClaimInfos {
    private final HashMap<String, ClaimInfo> claimInfos;
    /**
     * Constructs a new MdlClaimInfo instance using the provided builder.
     *
     * @param builder The builder containing the claim information
     */
    public MdlClaimInfo(Builder builder) {
        this.claimInfos = builder.claimInfos;
    }

    /**
     * Builder class for creating MdlClaimInfo instances.
     * Provides methods for setting various MDL claim fields.
     */
    public static class Builder {
        private final HashMap<String, ClaimInfo> claimInfos = new HashMap<>();
        /**
         * Sets the family name claim.
         *
         * @param familyName The family name to set
         * @return This Builder instance for method chaining
         */
        public Builder familyName(String familyName) {
            if (Strings.isEmpty(familyName)) {
                return this;
            }

            String familyNameCode = "org.iso.18013.5.family_name";

            ClaimInfo claimInfo = new ClaimInfo();
            claimInfo.setCode(familyNameCode);
            claimInfo.setValue(familyName.getBytes(StandardCharsets.UTF_8));

            claimInfos.put(familyNameCode, claimInfo);
            return this;
        }

        /**
         * Sets the given name claim.
         *
         * @param givenName The given name to set
         * @return This Builder instance for method chaining
         */
        public Builder givenName(String givenName) {
            if (Strings.isEmpty(givenName)) {
                return this;
            }
            String givenNameCode = "org.iso.18013.5.given_name";

            ClaimInfo claimInfo = new ClaimInfo();
            claimInfo.setCode(givenNameCode);
            claimInfo.setValue(givenName.getBytes(StandardCharsets.UTF_8));

            claimInfos.put(givenNameCode, claimInfo);

            return this;
        }

        /**
         * Sets the birth date claim.
         *
         * @param birthDate The birth date to set
         * @return This Builder instance for method chaining
         */
        public Builder birthDate(String birthDate) {
            if (Strings.isEmpty(birthDate)) {
                return this;
            }
            String birthDateCode = "org.iso.18013.5.birth_date";

            ClaimInfo claimInfo = new ClaimInfo();
            claimInfo.setCode(birthDateCode);
            claimInfo.setValue(birthDate.getBytes(StandardCharsets.UTF_8));

            claimInfos.put(birthDateCode, claimInfo);

            return this;
        }

        /**
         * Sets the issue date claim.
         *
         * @param issueDate The issue date to set
         * @return This Builder instance for method chaining
         */
        public Builder issueDate(String issueDate) {
            if (Strings.isEmpty(issueDate)) {
                return this;
            }
            String issueDateCode = "org.iso.18013.5.issue_date";

            ClaimInfo claimInfo = new ClaimInfo();
            claimInfo.setCode(issueDateCode);
            claimInfo.setValue(issueDate.getBytes(StandardCharsets.UTF_8));

            claimInfos.put(issueDateCode, claimInfo);

            return this;
        }

        /**
         * Sets the expiry date claim.
         *
         * @param expiryDate The expiry date to set
         * @return This Builder instance for method chaining
         */
        public Builder expiryDate(String expiryDate) {
            if (Strings.isEmpty(expiryDate)) {
                return this;
            }
            String expiryDateCode = "org.iso.18013.5.expiry_date";

            ClaimInfo claimInfo = new ClaimInfo();
            claimInfo.setCode(expiryDateCode);
            claimInfo.setValue(expiryDate.getBytes(StandardCharsets.UTF_8));

            claimInfos.put(expiryDateCode, claimInfo);

            return this;
        }

        /**
         * Sets the issuing authority claim.
         *
         * @param expiryDate The issuing authority to set
         * @return This Builder instance for method chaining
         */
        public Builder issuingAuthority(String expiryDate) {
            if (Strings.isEmpty(expiryDate)) {
                return this;
            }
            String expiryDateCode = "org.iso.18013.5.issuing_authority";

            ClaimInfo claimInfo = new ClaimInfo();
            claimInfo.setCode(expiryDateCode);
            claimInfo.setValue(expiryDate.getBytes(StandardCharsets.UTF_8));

            claimInfos.put(expiryDateCode, claimInfo);

            return this;
        }
        /**
         * Sets the portrait claim.
         *
         * @param portrait The portrait data to set
         * @return This Builder instance for method chaining
         */
        public Builder portrait(String portrait) {
            if (Strings.isEmpty(portrait)) {
                return this;
            }
            String portraitCode = "org.iso.18013.5.portrait";

            ClaimInfo claimInfo = new ClaimInfo();
            claimInfo.setCode(portraitCode);
            claimInfo.setValue(portrait.getBytes(StandardCharsets.UTF_8));

            claimInfos.put(portraitCode, claimInfo);

            return this;
        }

        /**
         * Sets the address claim.
         *
         * @param address The address to set
         * @return This Builder instance for method chaining
         */
        public Builder address(String address) {
            if (Strings.isEmpty(address)) {
                return this;
            }
            String addressCode = "org.iso.18013.5.address";

            ClaimInfo claimInfo = new ClaimInfo();
            claimInfo.setCode(addressCode);
            claimInfo.setValue(address.getBytes(StandardCharsets.UTF_8));

            claimInfos.put(addressCode, claimInfo);

            return this;
        }
        /**
         * Sets the document number claim.
         *
         * @param documentNumber The document number to set
         * @return This Builder instance for method chaining
         */
        public Builder documentNumber(String documentNumber) {
            if (Strings.isEmpty(documentNumber)) {
                return this;
            }
            String documentNumberCode = "org.iso.18013.5.document_number";

            ClaimInfo claimInfo = new ClaimInfo();
            claimInfo.setCode(documentNumberCode);
            claimInfo.setValue(documentNumber.getBytes(StandardCharsets.UTF_8));

            claimInfos.put(documentNumberCode, claimInfo);

            return this;
        }
        /**
         * Sets the Personally Identifiable Information (PII) claim.
         *
         * @param pii The PII to set
         * @return This Builder instance for method chaining
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
         * Builds and returns a new MdlClaimInfo instance.
         *
         * @return A new MdlClaimInfo instance with the set claims
         */
        public MdlClaimInfo build() {
            return new MdlClaimInfo(this);
        }
    }

    /**
     * Returns the map of claim information.
     *
     * @return A HashMap containing the claim information
     */
    @Override
    public HashMap<String, ClaimInfo> getClaims() {
        return claimInfos;
    }
}

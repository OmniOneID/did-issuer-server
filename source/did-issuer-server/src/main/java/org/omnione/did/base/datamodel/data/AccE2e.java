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

package org.omnione.did.base.datamodel.data;

import lombok.*;
import org.omnione.did.data.model.util.json.GsonWrapper;

/**
 * Represents the Accept E2E (End-to-End) data structure.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
@Builder
public class AccE2e extends RequestProof {
    private String publicKey;
    private String iv;

    public void fromJson(String json) {
        GsonWrapper gson = new GsonWrapper();
        AccE2e accE2e = gson.fromJson(json, AccE2e.class);

        this.publicKey = accE2e.getPublicKey();
        this.iv = accE2e.getIv();
        this.setProof(accE2e.getProof());
    }
}

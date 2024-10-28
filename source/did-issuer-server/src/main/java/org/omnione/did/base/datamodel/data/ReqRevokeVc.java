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

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import org.omnione.did.data.model.util.json.GsonWrapper;

/**
 * Represents the req_revoke_vc data structure.
 */
@Getter
@Setter
@Builder
public class ReqRevokeVc extends RequestProof {
    private String vcId;
    private String issuerNonce;

    public void fromJson(String json) {
        GsonWrapper gson = new GsonWrapper();
        ReqRevokeVc reqRevokeVc = gson.fromJson(json, ReqRevokeVc.class);

        this.vcId = reqRevokeVc.vcId;
        this.issuerNonce = reqRevokeVc.issuerNonce;
        this.setProof(reqRevokeVc.getProof());
    }
}

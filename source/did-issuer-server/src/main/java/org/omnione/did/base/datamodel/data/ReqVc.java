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

import com.google.gson.annotations.Expose;
import lombok.*;
import org.omnione.did.data.model.DataObject;
import org.omnione.did.data.model.did.Controller;
import org.omnione.did.data.model.schema.MetaData;
import org.omnione.did.data.model.util.json.GsonWrapper;
import org.omnione.did.zkp.datamodel.credentialrequest.CredentialRequest;

/**
 * request vc data structure.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
@Builder
public class ReqVc extends DataObject {
    @Expose
    private String refId;
    @Expose
    private ProfileInfo profile;
    @Expose
    private CredentialRequest credentialRequest;

    @Override
    public void fromJson(String data) {
        GsonWrapper gson = new GsonWrapper();
        ReqVc reqVc = gson.fromJson(data, ReqVc.class);

        this.refId = reqVc.refId;
        this.profile = reqVc.profile;
        this.credentialRequest = reqVc.credentialRequest;
    }
}

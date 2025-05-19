package org.omnione.did.base.datamodel.data.zkp;

import com.google.gson.annotations.Expose;
import lombok.*;
import org.omnione.did.data.model.DataObject;
import org.omnione.did.data.model.util.json.GsonWrapper;
import org.omnione.did.data.model.vc.VerifiableCredential;
import org.omnione.did.zkp.datamodel.credential.Credential;

/**
 * Description...
 */
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CredentialInfo extends DataObject {
    @Expose
    private VerifiableCredential vc;
    @Expose
    private Credential credential;

    @Override
    public void fromJson(String val) {
        GsonWrapper gson = new GsonWrapper();
        CredentialInfo data = gson.fromJson(val, CredentialInfo.class);
        this.vc = data.vc;
        this.credential = data.credential;
    }
}

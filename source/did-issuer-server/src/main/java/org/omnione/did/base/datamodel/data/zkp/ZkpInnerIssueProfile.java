package org.omnione.did.base.datamodel.data.zkp;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import lombok.*;
import org.omnione.did.data.model.profile.issue.InnerIssueProfile;
import org.omnione.did.zkp.datamodel.credentialoffer.CredentialOffer;

@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ZkpInnerIssueProfile extends InnerIssueProfile {
    @SerializedName("credentialOffer")
    @Expose
    private CredentialOffer credentialOffer;
}


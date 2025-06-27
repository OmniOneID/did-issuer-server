package org.omnione.did.base.datamodel.data.zkp;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import org.omnione.did.data.model.did.Proof;
import org.omnione.did.data.model.profile.MetaProfile;
import org.omnione.did.data.model.profile.issue.IssueProfile;
import org.omnione.did.data.model.util.json.GsonWrapper;

/**
 * Description...
 */

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ZkpIssueProfile extends MetaProfile {
    @SerializedName("profile")
    @Expose
    private ZkpInnerIssueProfile profile;
    @SerializedName("proof")
    @Expose
    private @NotNull Proof proof;

    public void fromJson(String val) {
        super.fromJson(val);
        GsonWrapper gson = new GsonWrapper();
        ZkpIssueProfile data = gson.fromJson(val, ZkpIssueProfile.class);
        this.profile = data.profile;
        this.proof = data.proof;
    }
}

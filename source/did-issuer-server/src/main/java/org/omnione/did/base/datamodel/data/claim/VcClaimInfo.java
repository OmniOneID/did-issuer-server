package org.omnione.did.base.datamodel.data.claim;

import org.omnione.did.core.data.rest.ClaimInfo;

import java.util.HashMap;

/**
 * Description...
 */
public class VcClaimInfo implements ClaimInfos {
    private final HashMap<String, ClaimInfo> claimInfo;

    public VcClaimInfo() {
        this.claimInfo = new HashMap<>();
    }

    public void put(ClaimInfo claimInfo) {
        this.claimInfo.put(claimInfo.getCode(), claimInfo);
    }

    @Override
    public HashMap<String, ClaimInfo> getClaims() {
        return claimInfo;
    }
}

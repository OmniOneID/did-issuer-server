package org.omnione.did.issuer.v1.agent.service;

import org.omnione.did.sdk.issuer.model.dto.*;

/**
 * Interface for VC Status Service on the server side.
 */
public interface VcStatusService {
    InspectProposeRevokeResDto inspectProposeRevoke(InspectProposeRevokeReqDto request);

    RevokeVcResDto revokeVc(RevokeVcReqDto request);

    CompleteRevokeResDto completeRevoke(CompleteRevokeReqDto request);

    UpdateVcStatusResDto updateVcStatus(UpdateVcStatusReqDto request);
}

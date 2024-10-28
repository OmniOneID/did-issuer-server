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

package org.omnione.did.issuer.v1.service;

import org.omnione.did.issuer.v1.dto.vc.*;

/**
 * Service interface for handling Verifiable Credential (VC) status.
 */
public interface VcStatusService {
    /**
     * Inspects the propose revoke for a Verifiable Credential.
     *
     * @param request the request containing the VC to inspect the propose revoke for
     * @return the inspection result for the given VC
     */
    InspectProposeRevokeResDto inspectProposeRevoke(InspectProposeRevokeReqDto request);
    /**
     * Revokes a Verifiable Credential.
     *
     * @param request the request containing the Verifiable Credential to revoke
     * @return the response indicating the result of revoking the Verifiable Credential
     */
    RevokeVcResDto revokeVc(RevokeVcReqDto request);
    /**
     * Updates the status of a Verifiable Credential.
     *
     * @param request the VC to update the status for
     * @return the result of updating the status for the given VC
     */
    UpdateVcStatusResDto updateVcStatus(UpdateVcStatusReqDto request);
    /**
     * Completes the revocation process for a Verifiable Credential.
     *
     * @param request The completion request containing the transaction ID.
     * @return CompleteRevokeResDto containing the transaction ID of the completed revocation.
     */
    CompleteRevokeResDto completeRevoke(CompleteRevokeReqDto request);
}

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

package org.omnione.did.issuer.v1.controller;

import lombok.RequiredArgsConstructor;
import org.omnione.did.base.constants.UrlConstant;
import org.omnione.did.issuer.v1.dto.vc.*;
import org.omnione.did.issuer.v1.service.VcStatusService;
import org.omnione.did.issuer.v1.service.query.VcSchemaService;
import org.springframework.web.bind.annotation.*;

/**
 * The VcController class is a controller that handles requests related to verifiable credentials.
 * It provides endpoints for inspecting a propose revoke, revoking VC and updating VC status.
 */
@RequiredArgsConstructor
@RequestMapping(UrlConstant.Issuer.V1)
@RestController
public class VcController {
    private final VcStatusService vcStatusService;
    private final VcSchemaService vcSchemaService;

    /**
     * Inspects the propose revoke for a Verifiable Credential.
     *
     * @param request the VC to inspect the propose revoke for
     * @return the inspection result for the given VC
     */
    @PostMapping(UrlConstant.Issuer.INSPECT_PROPOSE_REVOKE)
    public InspectProposeRevokeResDto inspectProposeRevoke(@RequestBody InspectProposeRevokeReqDto request) {

        return vcStatusService.inspectProposeRevoke(request);
    }

    /**
     * revokes a Verifiable Credential.
     * @param request the VC to revoke
     * @return the result of revoking the given VC
     */
    @PostMapping(UrlConstant.Issuer.REVOKE_VC)
    public RevokeVcResDto revokeVc(@RequestBody RevokeVcReqDto request) {

        return vcStatusService.revokeVc(request);
    }

    /**
     * Completes the revocation of a Verifiable Credential.
     *
     * @param request the VC to complete the revocation for
     * @return the result of completing the revocation for the given VC
     */
    @PostMapping(UrlConstant.Issuer.COMPLETE_REVOKE)
    public CompleteRevokeResDto completeRevoke(@RequestBody CompleteRevokeReqDto request) {

        return vcStatusService.completeRevoke(request);
    }

    /**
     * Updates the status of a Verifiable Credential.
     *
     * @param request the VC to update the status for
     * @return the result of updating the status for the given VC
     */
    @PostMapping(UrlConstant.Issuer.STATUS)
    public UpdateVcStatusResDto updateVcStatus(@RequestBody UpdateVcStatusReqDto request) {

        return vcStatusService.updateVcStatus(request);
    }

    /**
     * Gets the schema of a Verifiable Credential.
     *
     * @param name the name of the VC schema
     * @return the schema of the VC
     */
    @GetMapping(UrlConstant.Issuer.VC + UrlConstant.Issuer.SCHEMA)
    public String getVcSchema(@RequestParam(name="name") String name) {
        return vcSchemaService.getVcSchemaByName(name);
    }
}

/*
 * Copyright 2026 OmniOne.
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

package org.omnione.did.issuer.v1.agent.service.oid4vc.status;


import org.omnione.did.issuer.v1.agent.service.oid4vc.status.model.CredentialIssuanceView;
import org.omnione.did.issuer.v1.agent.service.oid4vc.status.model.CredentialStatusHistoryView;
import org.omnione.did.oid4vc.oid4vci.exception.OID4VCIException;
import org.omnione.did.oid4vc.oid4vci.status.model.CredentialStatus;

import java.util.List;

public interface CredentialManagementService {
    CredentialIssuanceView findByIssuanceId(String issuanceId) throws OID4VCIException;
    List<CredentialIssuanceView> findAllByUserId(String userId) throws OID4VCIException;
    List<CredentialStatusHistoryView> findStatusHistory(String issuanceId) throws OID4VCIException;
    CredentialIssuanceView changeStatus(
            String issuanceId,
            CredentialStatus newStatus,
            String reason,
            String changedBy) throws OID4VCIException;
}

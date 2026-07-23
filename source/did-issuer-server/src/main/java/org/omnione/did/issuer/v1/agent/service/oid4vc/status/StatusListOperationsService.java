package org.omnione.did.issuer.v1.agent.service.oid4vc.status;

import org.omnione.did.issuer.v1.agent.service.oid4vc.status.model.StatusListOperationResult;
import org.omnione.did.issuer.v1.agent.service.oid4vc.status.model.StatusListRotationCommand;
import org.omnione.did.oid4vc.oid4vci.exception.OID4VCIException;

import java.time.Instant;
import java.util.List;

public interface StatusListOperationsService {
    StatusListOperationResult rotateSigningKey(StatusListRotationCommand command)
            throws OID4VCIException;
    List<Long> deactivateExpiredLists(Instant now);
}

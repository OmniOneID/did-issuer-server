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

import org.omnione.did.base.db.domain.Oid4vcCredentialIssuanceEntity;
import org.omnione.did.base.db.domain.Oid4vcCredentialStatusHistoryEntity;
import org.omnione.did.base.db.domain.Oid4vcStatusListEntity;
import org.omnione.did.base.db.domain.IssuanceState;
import org.omnione.did.base.db.repository.Oid4vcCredentialIssuanceRepository;
import org.omnione.did.base.db.repository.Oid4vcCredentialStatusHistoryRepository;
import org.omnione.did.base.db.repository.Oid4vcStatusListRepository;
import lombok.RequiredArgsConstructor;
import org.omnione.did.issuer.v1.agent.service.oid4vc.status.model.CredentialIssuanceView;
import org.omnione.did.issuer.v1.agent.service.oid4vc.status.model.CredentialStatusHistoryView;
import org.omnione.did.oid4vc.oid4vci.exception.OID4VCIErrorCode;
import org.omnione.did.oid4vc.oid4vci.exception.OID4VCIException;
import org.omnione.did.oid4vc.oid4vci.status.model.CredentialStatus;
import org.omnione.did.oid4vc.oid4vci.status.model.CredentialStatusTransitionPolicy;
import org.omnione.did.oid4vc.oid4vci.status.model.StatusListValidator;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;

@Service
@Profile("!local")
@RequiredArgsConstructor
public class JpaCredentialManagementService implements CredentialManagementService {

    private final Oid4vcCredentialIssuanceRepository issuanceRepository;
    private final Oid4vcCredentialStatusHistoryRepository historyRepository;
    private final Oid4vcStatusListRepository statusListRepository;

    @Override
    @Transactional(readOnly = true)
    public CredentialIssuanceView findByIssuanceId(String issuanceId) throws OID4VCIException {
        return toView(findIssuance(issuanceId));
    }

    @Override
    @Transactional(readOnly = true)
    public List<CredentialIssuanceView> findAllIssued(String userId) {
        List<Oid4vcCredentialIssuanceEntity> issuances =
                userId == null || userId.isBlank()
                        ? issuanceRepository.findAllByIssuanceStateOrderByCreatedAtDesc(
                                IssuanceState.ISSUED)
                        : issuanceRepository.findAllByUserIdAndIssuanceStateOrderByCreatedAtDesc(
                                userId.trim(), IssuanceState.ISSUED);
        return issuances.stream().map(this::toView).toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<CredentialIssuanceView> findAllByUserId(String userId) throws OID4VCIException {
        requireText(userId, "userId");
        return issuanceRepository.findAllByUserIdOrderByCreatedAtDesc(userId)
                .stream()
                .map(this::toView)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<CredentialStatusHistoryView> findStatusHistory(String issuanceId)
            throws OID4VCIException {
        Oid4vcCredentialIssuanceEntity issuance = findIssuance(issuanceId);
        return historyRepository.findAllByCredentialIssuanceIdOrderByChangedAtAsc(issuance.getId())
                .stream()
                .map(this::toHistoryView)
                .toList();
    }

    @Override
    @Transactional
    public CredentialIssuanceView changeStatus(
            String issuanceId,
            CredentialStatus newStatus,
            String reason,
            String changedBy) throws OID4VCIException {
        if (newStatus == null) {
            throw new OID4VCIException(
                    OID4VCIErrorCode.ERR_CODE_GENERAL_NULL_PARAMETER,
                    "newStatus must not be null");
        }
        requireText(reason, "reason");
        requireText(changedBy, "changedBy");

        Oid4vcCredentialIssuanceEntity issuance = issuanceRepository
                .findByIssuanceIdForUpdate(issuanceId)
                .orElseThrow(() -> credentialNotFound(issuanceId));
        CredentialStatus currentStatus = issuance.getCredentialStatus();
        if (currentStatus == newStatus) {
            return toView(issuance);
        }
        if (!CredentialStatusTransitionPolicy.isAllowed(currentStatus, newStatus)) {
            throw new OID4VCIException(
                    OID4VCIErrorCode.ERR_CODE_STATUS_LIST_INVALID_TRANSITION,
                    currentStatus + " -> " + newStatus);
        }

        Oid4vcStatusListEntity statusList = statusListRepository
                .findByIdForUpdate(issuance.getStatusList().getId())
                .orElseThrow(() -> new OID4VCIException(
                        OID4VCIErrorCode.ERR_CODE_STATUS_LIST_NOT_FOUND,
                        "credential issuance=" + issuanceId));
        try {
            StatusListValidator.validateStatusValue(statusList.getBits(), newStatus.getValue());
        } catch (IllegalArgumentException exception) {
            throw new OID4VCIException(
                    OID4VCIErrorCode.ERR_CODE_STATUS_LIST_INVALID_CONFIGURATION,
                    exception.getMessage(),
                    exception);
        }
        Instant changedAt = Instant.now();

        issuance.setCredentialStatus(newStatus);
        issuance.setStatusChangedAt(changedAt);
        statusList.setListVersion(statusList.getListVersion() + 1);

        Oid4vcCredentialStatusHistoryEntity history = new Oid4vcCredentialStatusHistoryEntity();
        history.setCredentialIssuance(issuance);
        history.setPreviousStatus(currentStatus);
        history.setNewStatus(newStatus);
        history.setReason(reason);
        history.setChangedBy(changedBy);
        history.setChangedAt(changedAt);
        historyRepository.save(history);

        return toView(issuance);
    }

    private Oid4vcCredentialIssuanceEntity findIssuance(String issuanceId)
            throws OID4VCIException {
        requireText(issuanceId, "issuanceId");
        return issuanceRepository.findByIssuanceId(issuanceId)
                .orElseThrow(() -> credentialNotFound(issuanceId));
    }

    private OID4VCIException credentialNotFound(String issuanceId) {
        return new OID4VCIException(
                OID4VCIErrorCode.ERR_CODE_STATUS_LIST_CREDENTIAL_NOT_FOUND,
                "issuanceId=" + issuanceId);
    }

    private void requireText(String value, String name) throws OID4VCIException {
        if (value == null || value.isBlank()) {
            throw new OID4VCIException(
                    OID4VCIErrorCode.ERR_CODE_GENERAL_INVALID_PARAMETER,
                    name + " must not be blank");
        }
    }

    private CredentialIssuanceView toView(Oid4vcCredentialIssuanceEntity issuance) {
        return new CredentialIssuanceView(
                issuance.getIssuanceId(),
                issuance.getUserId(),
                issuance.getConfigId(),
                issuance.getFormat(),
                issuance.getStatusList().getListUri(),
                issuance.getStatusListIndex(),
                issuance.getCredentialStatus(),
                issuance.getIssuanceState(),
                issuance.getIssuedAt(),
                issuance.getExpiresAt(),
                issuance.getStatusChangedAt(),
                issuance.getFailedAt(),
                issuance.getFailureCode(),
                issuance.getFailureMessage(),
                issuance.getCreatedAt(),
                issuance.getUpdatedAt());
    }

    private CredentialStatusHistoryView toHistoryView(Oid4vcCredentialStatusHistoryEntity history) {
        return new CredentialStatusHistoryView(
                history.getPreviousStatus(),
                history.getNewStatus(),
                history.getReason(),
                history.getChangedBy(),
                history.getChangedAt());
    }
}

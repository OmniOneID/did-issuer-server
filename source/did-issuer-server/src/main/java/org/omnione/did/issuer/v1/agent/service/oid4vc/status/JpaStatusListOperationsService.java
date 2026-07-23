package org.omnione.did.issuer.v1.agent.service.oid4vc.status;

import org.omnione.did.base.db.domain.Oid4vcStatusListEntity;
import org.omnione.did.base.db.repository.Oid4vcCredentialIssuanceRepository;
import org.omnione.did.base.db.repository.Oid4vcStatusListRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.omnione.did.issuer.v1.agent.service.oid4vc.status.model.StatusListOperationResult;
import org.omnione.did.issuer.v1.agent.service.oid4vc.status.model.StatusListRotationCommand;
import org.omnione.did.oid4vc.oid4vci.exception.OID4VCIErrorCode;
import org.omnione.did.oid4vc.oid4vci.exception.OID4VCIException;
import org.omnione.did.oid4vc.oid4vci.status.model.StatusListValidator;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
@Profile("!local")
@RequiredArgsConstructor
public class JpaStatusListOperationsService implements StatusListOperationsService {
    private final Oid4vcStatusListRepository statusListRepository;
    private final Oid4vcCredentialIssuanceRepository issuanceRepository;

    @Override
    @Transactional
    public StatusListOperationResult rotateSigningKey(StatusListRotationCommand command)
            throws OID4VCIException {
        validate(command);
        if (statusListRepository.findByListUri(command.listUri()).isPresent()) {
            throw new OID4VCIException(
                    OID4VCIErrorCode.ERR_CODE_STATUS_LIST_CONFLICT,
                    "Status List URI already exists: " + command.listUri());
        }
        List<Oid4vcStatusListEntity> previous =
                statusListRepository.findEnabledByFormatForUpdate(command.format());
        previous.forEach(list -> list.setEnabled(false));
        Oid4vcStatusListEntity replacement = new Oid4vcStatusListEntity();
        replacement.setListUri(command.listUri());
        replacement.setFormat(command.format());
        replacement.setSigningKeyId(command.signingKeyId());
        replacement.setBits(command.bits());
        replacement.setCapacity(command.capacity());
        replacement.setTtlSeconds(command.ttlSeconds());
        Oid4vcStatusListEntity saved = statusListRepository.save(replacement);
        log.info("Rotated Status List key: format={}, oldListIds={}, newListId={}, keyId={}",
                command.format(), previous.stream().map(Oid4vcStatusListEntity::getId).toList(),
                saved.getId(), command.signingKeyId());
        return new StatusListOperationResult(
                saved.getId(), saved.getListUri(), saved.getSigningKeyId());
    }

    @Override
    @Transactional
    public List<Long> deactivateExpiredLists(Instant now) {
        List<Long> deactivated = new ArrayList<>();
        for (Oid4vcStatusListEntity list : statusListRepository.findAllByEnabledTrueOrderByIdAsc()) {
            if (list.getNextIndex() == 0
                    || issuanceRepository.countActiveIssuedByStatusListId(list.getId(), now) > 0) {
                continue;
            }
            statusListRepository.findByIdForUpdate(list.getId()).ifPresent(locked -> {
                locked.setEnabled(false);
                deactivated.add(locked.getId());
                log.info("Deactivated expired Status List: listId={}, format={}",
                        locked.getId(), locked.getFormat());
            });
        }
        return deactivated;
    }

    private void validate(StatusListRotationCommand command) throws OID4VCIException {
        if (command == null) {
            throw invalidConfiguration("command must not be null", null);
        }
        requireText(command.format(), "format");
        requireText(command.listUri(), "listUri");
        requireText(command.signingKeyId(), "signingKeyId");
        try {
            StatusListValidator.validateBits(command.bits());
            StatusListValidator.validateCapacity(command.capacity());
        } catch (IllegalArgumentException exception) {
            throw invalidConfiguration(exception.getMessage(), exception);
        }
        if (command.ttlSeconds() <= 0) {
            throw invalidConfiguration("ttlSeconds must be positive", null);
        }
    }

    private void requireText(String value, String name) throws OID4VCIException {
        if (value == null || value.isBlank()) {
            throw invalidConfiguration(name + " must not be blank", null);
        }
    }

    private OID4VCIException invalidConfiguration(String reason, Throwable cause) {
        return new OID4VCIException(
                OID4VCIErrorCode.ERR_CODE_STATUS_LIST_INVALID_CONFIGURATION,
                reason,
                cause);
    }
}

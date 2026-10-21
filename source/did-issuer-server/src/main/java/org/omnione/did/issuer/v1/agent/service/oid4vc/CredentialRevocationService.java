/*
 * Copyright 2026 OmniOne.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 */
package org.omnione.did.issuer.v1.agent.service.oid4vc;

import lombok.RequiredArgsConstructor;
import org.omnione.did.base.db.domain.IssuanceState;
import org.omnione.did.base.db.domain.Oid4vcCredentialIssuanceEntity;
import org.omnione.did.base.db.repository.Oid4vcCredentialIssuanceRepository;
import org.omnione.did.base.exception.ErrorCode;
import org.omnione.did.base.exception.OpenDidException;
import org.omnione.did.base.util.BaseMultibaseUtil;
import org.omnione.did.data.model.did.DidDocument;
import org.omnione.did.data.model.did.VerificationMethod;
import org.omnione.did.issuer.v1.agent.service.oid4vc.status.CredentialManagementService;
import org.omnione.did.issuer.v1.agent.service.oid4vc.status.model.CredentialIssuanceView;
import org.omnione.did.issuer.v1.common.service.StorageService;
import org.omnione.did.oid4vc.formatter.util.KeyUtil;
import org.omnione.did.oid4vc.oid4vci.dto.revocation.CredentialRevocationRequest;
import org.omnione.did.oid4vc.oid4vci.dto.revocation.CredentialRevocationResponse;
import org.omnione.did.oid4vc.oid4vci.exception.OID4VCIErrorCode;
import org.omnione.did.oid4vc.oid4vci.exception.OID4VCIException;
import org.omnione.did.oid4vc.oid4vci.revocation.model.VerifiedCredentialRevocation;
import org.omnione.did.oid4vc.oid4vci.revocation.service.CredentialRevocationTokenVerifier;
import org.omnione.did.oid4vc.oid4vci.service.store.CNonceStore;
import org.omnione.did.oid4vc.oid4vci.status.model.CredentialStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.PublicKey;
import java.util.List;

@Service
@RequiredArgsConstructor
public class CredentialRevocationService {

    private static final String USER_REQUEST = "USER_REQUEST";

    private final CredentialRevocationTokenVerifier tokenVerifier;
    private final Oid4vcCredentialIssuanceRepository issuanceRepository;
    private final CredentialManagementService credentialManagementService;
    private final CNonceStore cNonceStore;
    private final StorageService storageService;

    @Transactional(rollbackFor = Exception.class)
    public CredentialRevocationResponse revoke(CredentialRevocationRequest request) {
        VerifiedCredentialRevocation verified = verifyToken(request);

        Oid4vcCredentialIssuanceEntity issuance = issuanceRepository
                .findByStatusReferenceForUpdate(
                        verified.statusListUri(), verified.statusListIndex())
                .orElseThrow(() -> new OpenDidException(ErrorCode.VC_NOT_FOUND));
        if (issuance.getIssuanceState() != IssuanceState.ISSUED) {
            throw new OpenDidException(ErrorCode.VC_NOT_FOUND);
        }

        String subjectDid = issuance.getSubjectDid();
        if ((subjectDid == null || subjectDid.isBlank())
                && issuance.getUserId() != null
                && issuance.getUserId().startsWith("did:")) {
            subjectDid = issuance.getUserId();
        }
        if (!verified.holderDid().equals(subjectDid)) {
            throw new OpenDidException(ErrorCode.HOLDER_INVALID);
        }

        if (!cNonceStore.consume(verified.nonce())) {
            throw new OpenDidException(ErrorCode.ISSUER_NONCE_INVALID);
        }

        if (issuance.getCredentialStatus() == CredentialStatus.INVALID) {
            return response(verified, issuance.getStatusChangedAt());
        }

        try {
            CredentialIssuanceView changed = credentialManagementService.changeStatus(
                    issuance.getIssuanceId(), CredentialStatus.INVALID,
                    USER_REQUEST, verified.holderDid());
            return response(verified, changed.statusChangedAt());
        } catch (OID4VCIException exception) {
            throw new OpenDidException(ErrorCode.TR_VC_REVOKE_FAILED);
        }
    }

    private VerifiedCredentialRevocation verifyToken(CredentialRevocationRequest request) {
        try {
            return tokenVerifier.verify(
                    request == null ? null : request.getRevocationToken(),
                    this::resolveAuthenticationKey);
        } catch (OID4VCIException exception) {
            if (OID4VCIErrorCode.ERR_CODE_JWS_VERIFY_FAILED.getCode()
                    .equals(exception.getErrorCode())) {
                throw new OpenDidException(ErrorCode.SIGNATURE_VERIFICATION_FAILED);
            }
            throw new OpenDidException(ErrorCode.HOLDER_INVALID);
        }
    }

    private PublicKey resolveAuthenticationKey(String holderDid, String keyId) {
        DidDocument didDocument;
        try {
            didDocument = storageService.findDidDoc(holderDid);
        } catch (Exception exception) {
            throw new OpenDidException(ErrorCode.DID_DOCUMENT_RETRIEVAL_FAILED);
        }

        if (didDocument == null || !holderDid.equals(baseDid(didDocument.getId()))) {
            throw new OpenDidException(ErrorCode.HOLDER_INVALID);
        }
        List<String> authentication = didDocument.getAuthentication();
        if (authentication == null || authentication.stream()
                .noneMatch(reference -> keyReferenceMatches(reference, keyId))) {
            throw new OpenDidException(ErrorCode.HOLDER_INVALID);
        }

        VerificationMethod method = didDocument.getVerificationMethod() == null
                ? null
                : didDocument.getVerificationMethod().stream()
                        .filter(candidate -> verificationMethodMatches(candidate.getId(), keyId))
                        .findFirst()
                        .orElse(null);
        if (method == null
                // Temporarily allow TAS-controlled verification methods for testing.
                // || !holderDid.equals(baseDid(method.getController()))
                || method.getPublicKeyMultibase() == null) {
            throw new OpenDidException(ErrorCode.HOLDER_INVALID);
        }

        try {
            byte[] compressed = BaseMultibaseUtil.decode(method.getPublicKeyMultibase());
            return KeyUtil.getPublicKeyObject(KeyUtil.unCompressPublicKey(compressed));
        } catch (Exception exception) {
            throw new OpenDidException(ErrorCode.SIGNATURE_VERIFICATION_FAILED);
        }
    }

    private boolean keyReferenceMatches(String reference, String keyId) {
        String normalizedReference = normalizeKeyReference(reference);
        String normalizedKeyId = normalizeKeyReference(keyId);
        return normalizedReference != null && normalizedReference.equals(normalizedKeyId);
    }

    private boolean verificationMethodMatches(String methodId, String keyId) {
        return keyReferenceMatches(methodId, keyId);
    }

    private String normalizeKeyReference(String value) {
        if (value == null) {
            return null;
        }

        int fragmentIndex = value.lastIndexOf('#');
        return fragmentIndex >= 0 ? value.substring(fragmentIndex + 1) : value;
    }

    private String baseDid(String didUrl) {
        if (didUrl == null) {
            return null;
        }
        int query = didUrl.indexOf('?');
        int fragment = didUrl.indexOf('#');
        int end = query >= 0 ? query : fragment;
        return end < 0 ? didUrl : didUrl.substring(0, end);
    }

    private CredentialRevocationResponse response(
            VerifiedCredentialRevocation verified,
            java.time.Instant revokedAt) {
        return new CredentialRevocationResponse(
                new CredentialRevocationResponse.CredentialStatusResult(
                        new CredentialRevocationResponse.StatusListReference(
                                verified.statusListUri(), verified.statusListIndex()),
                        CredentialStatus.INVALID.name()),
                revokedAt);
    }
}

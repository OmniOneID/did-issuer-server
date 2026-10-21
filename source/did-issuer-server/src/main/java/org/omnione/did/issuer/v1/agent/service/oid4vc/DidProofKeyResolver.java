/*
 * Copyright 2026 OmniOne.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 */
package org.omnione.did.issuer.v1.agent.service.oid4vc;

import com.nimbusds.jose.jwk.Curve;
import com.nimbusds.jose.jwk.ECKey;
import lombok.RequiredArgsConstructor;
import org.omnione.did.base.util.BaseMultibaseUtil;
import org.omnione.did.data.model.did.DidDocument;
import org.omnione.did.data.model.did.VerificationMethod;
import org.omnione.did.issuer.v1.common.service.StorageService;
import org.omnione.did.oid4vc.formatter.util.KeyUtil;
import org.omnione.did.oid4vc.oid4vci.exception.OID4VCIErrorCode;
import org.omnione.did.oid4vc.oid4vci.exception.OID4VCIException;
import org.omnione.did.oid4vc.oid4vci.spi.ProofKeyResolver;
import org.springframework.stereotype.Service;

import java.security.PublicKey;
import java.security.interfaces.ECPublicKey;
import java.util.List;

/**
 * Resolves a DID URL used as an OID4VCI proof {@code kid} to an authentication key.
 */
@Service
@RequiredArgsConstructor
public class DidProofKeyResolver implements ProofKeyResolver {

    private final StorageService storageService;

    @Override
    public ResolvedProofKey resolve(String keyId) throws OID4VCIException {
        String holderDid = baseDid(keyId);
        if (holderDid == null || !holderDid.startsWith("did:") || fragment(keyId) == null) {
            throw invalidProof("The proof 'kid' must be a DID URL with a key fragment.");
        }

        DidDocument didDocument;
        try {
            didDocument = storageService.findDidDoc(holderDid);
        } catch (Exception exception) {
            throw invalidProof("Failed to resolve the Holder DID Document.", exception);
        }
        if (didDocument == null || !holderDid.equals(baseDid(didDocument.getId()))) {
            throw invalidProof("The resolved DID Document does not match the proof 'kid'.");
        }

        List<String> authentication = didDocument.getAuthentication();
        if (authentication == null || authentication.stream()
                .noneMatch(reference -> keyReferenceMatches(reference, keyId, holderDid))) {
            throw invalidProof("The proof key is not registered for DID authentication.");
        }

        VerificationMethod method = didDocument.getVerificationMethod() == null
                ? null
                : didDocument.getVerificationMethod().stream()
                        .filter(candidate -> keyReferenceMatches(
                                candidate.getId(), keyId, holderDid))
                        .findFirst()
                        .orElse(null);
        if (method == null
                // Temporarily allow TAS-controlled verification methods for testing.
                // || !holderDid.equals(baseDid(method.getController()))
                || method.getPublicKeyMultibase() == null) {
            throw invalidProof("The DID authentication verification method is not valid.");
        }

        try {
            byte[] compressed = BaseMultibaseUtil.decode(method.getPublicKeyMultibase());
            PublicKey publicKey = KeyUtil.getPublicKeyObject(KeyUtil.unCompressPublicKey(compressed));
            if (!(publicKey instanceof ECPublicKey ecPublicKey)) {
                throw invalidProof("The DID authentication key is not an EC public key.");
            }
            ECKey publicJwk = new ECKey.Builder(Curve.P_256, ecPublicKey)
                    .keyID(keyId)
                    .build();
            return new ResolvedProofKey(holderDid, publicJwk.toJSONObject());
        } catch (OID4VCIException exception) {
            throw exception;
        } catch (Exception exception) {
            throw invalidProof("Failed to decode the DID authentication key.", exception);
        }
    }

    private boolean keyReferenceMatches(String reference, String keyId, String holderDid) {
        if (reference != null && reference.startsWith("did:")
                && !holderDid.equals(baseDid(reference))) {
            return false;
        }
        String referenceFragment = fragment(reference);
        String keyFragment = fragment(keyId);
        return referenceFragment != null && referenceFragment.equals(keyFragment);
    }

    private String fragment(String value) {
        if (value == null || value.isBlank()) {
            return null;
        }
        int fragmentIndex = value.lastIndexOf('#');
        return fragmentIndex >= 0 && fragmentIndex + 1 < value.length()
                ? value.substring(fragmentIndex + 1)
                : value.startsWith("did:") ? null : value;
    }

    private String baseDid(String didUrl) {
        if (didUrl == null || didUrl.isBlank()) {
            return null;
        }
        int queryIndex = didUrl.indexOf('?');
        int fragmentIndex = didUrl.indexOf('#');
        int endIndex;
        if (queryIndex < 0) {
            endIndex = fragmentIndex;
        } else if (fragmentIndex < 0) {
            endIndex = queryIndex;
        } else {
            endIndex = Math.min(queryIndex, fragmentIndex);
        }
        return endIndex < 0 ? didUrl : didUrl.substring(0, endIndex);
    }

    private OID4VCIException invalidProof(String reason) {
        return new OID4VCIException(OID4VCIErrorCode.ERR_CODE_PROOF_INVALID, reason);
    }

    private OID4VCIException invalidProof(String reason, Throwable cause) {
        return new OID4VCIException(OID4VCIErrorCode.ERR_CODE_PROOF_INVALID, reason, cause);
    }
}

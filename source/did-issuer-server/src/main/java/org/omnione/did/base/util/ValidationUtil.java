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

package org.omnione.did.base.util;

import com.fasterxml.jackson.core.JsonProcessingException;
import org.omnione.did.base.datamodel.data.RequestProof;
import org.omnione.did.base.datamodel.enums.EccCurveType;
import org.omnione.did.common.util.DidUtil;
import org.omnione.did.common.util.JsonUtil;
import org.omnione.did.core.manager.DidManager;
import org.omnione.did.data.model.did.DidDocument;
import org.omnione.did.data.model.did.Proof;
import org.omnione.did.data.model.did.VerificationMethod;

import java.nio.charset.StandardCharsets;
import java.time.Instant;

/**
 * Utility class for validation operations.
 * This class provides methods for validating the expiration date of a DID document and verifying a signature.
 */
public class ValidationUtil {
    /**
     * Checks if the expiration date of a DID document has passed.
     *
     * @param expiredAt The expiration date.
     * @return true if the expiration date has passed, false otherwise.
     */
    public static boolean isExpiredDate(Instant expiredAt) {
        return Instant.now().isAfter(expiredAt);
    }

    /**
     * Verifies the signature of a request.
     * if the signature is valid, the method will not throw an exception.
     *
     * @param request The request to verify.
     * @param didDocument The DID document.
     */
    public static void verifySign(RequestProof request, DidDocument didDocument) {
        Proof proof = request.getProof();
        String verificationMethod = proof.getVerificationMethod();

        DidManager didManager = new DidManager();
        didManager.parse(didDocument.toJson());
        String keyId = DidUtil.extractKeyId(verificationMethod);
        VerificationMethod publicKeyByKeyId = didManager.getVerificationMethodByKeyId(keyId);



        Proof tmpProof = new Proof();
        tmpProof.setType(proof.getType());
        tmpProof.setCreated(proof.getCreated());
        tmpProof.setProofPurpose(proof.getProofPurpose());
        tmpProof.setVerificationMethod(proof.getVerificationMethod());

        request.setProof(tmpProof);

        String source = JsonUtil.serializeAndSort(request);

        BaseCryptoUtil.verifySignature(publicKeyByKeyId.getPublicKeyMultibase(), proof.getProofValue(),
                BaseDigestUtil.generateHash(source.getBytes(StandardCharsets.UTF_8)), EccCurveType.SECP_256_R1);
    }
}

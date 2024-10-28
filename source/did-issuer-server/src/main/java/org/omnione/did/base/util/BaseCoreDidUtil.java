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

import lombok.extern.slf4j.Slf4j;
import org.omnione.did.base.exception.ErrorCode;
import org.omnione.did.base.exception.OpenDidException;
import org.omnione.did.core.data.rest.SignatureParams;
import org.omnione.did.core.exception.CoreException;
import org.omnione.did.core.manager.DidManager;
import org.omnione.did.data.model.did.DidDocument;
import org.omnione.did.data.model.did.VerificationMethod;

import java.util.List;


/**
 * Utility class for core DID operations.
 * This class provides methods for parsing DID documents, getting verification methods, and verifying DID document key proofs.
 */
@Slf4j
public class BaseCoreDidUtil {

    /**
     * Parses a DID document from its JSON representation.
     *
     * @param didDocJson The JSON string representing the DID document.
     * @return The parsed DidManager object.
     * @throws OpenDidException if the DID document parsing fails.
     */
    public static DidManager parseDidDoc(String didDocJson) {
        DidManager didManager = new DidManager();
        didManager.parse(didDocJson);

        return didManager;
    }

    /**
     * Parses a DID document.
     *
     * @param didDocument The DID document object.
     * @return The parsed DidManager object.
     * @throws OpenDidException if the DID document parsing fails.
     */
    public static DidManager parseDidDoc(DidDocument didDocument) {
        DidManager didManager = new DidManager();
        didManager.parse(didDocument.toJson());

        return didManager;
    }

    /**
     * Get all sign key ids
     *
     * @param didManager DID manager
     * @return List<String> all sign key ids
     * @throws OpenDidException if failed to get sign key ids
     */
    public static List<String> getAllSignKeyIdList(DidManager didManager) {
        try {
            return didManager.getAllSignKeyIdList();
        } catch (CoreException e) {
            log.error("Failed to get sign key ids: " + e.getMessage());
            throw new OpenDidException(ErrorCode.GET_SIGN_KEY_IDS_FAILED);
        }
    }

    /**
     * Get all sign key ids
     *
     * @param didDocument DID document
     * @return List<String> all sign key ids
     */
    public static List<String> getAllSignKeyIdList(DidDocument didDocument) {
        try {
            DidManager didManager = parseDidDoc(didDocument);
            return didManager.getAllSignKeyIdList();
        } catch (CoreException e) {
            log.error("Failed to get sign key ids: " + e.getMessage());
            throw new OpenDidException(ErrorCode.GET_SIGN_KEY_IDS_FAILED);
        }
    }

    /**
     * Get all sign data list
     * @param didManager DID manager
     * @param keyIdList List of key ids
     * @return List<SignatureParams> all sign data lists
     */
    public static List<SignatureParams> getAllSignDataList(DidManager didManager, List<String> keyIdList) {
        try {
            return didManager.getOriginDataForSign(keyIdList);
        } catch (CoreException e) {
            log.error("Failed to get sign data: " + e.getMessage());
            throw new OpenDidException(ErrorCode.GET_SIGN_DATA_FAILED);
        }
    }

    /**
     * Get all sign data list
     * @param ownerDidDoc DID document
     * @param keyIdList List of key ids
     * @return List<SignatureParams> all sign data list
     */
    public static List<SignatureParams> getAllSignDataList(DidDocument ownerDidDoc, List<String> keyIdList) {
        try {
            DidManager didManager = parseDidDoc(ownerDidDoc.toJson());
            return didManager.getOriginDataForSign(keyIdList);
        } catch (CoreException e) {
            log.error("Failed to get sign data: " + e.getMessage());
            throw new OpenDidException(ErrorCode.GET_SIGN_DATA_FAILED);
        }
    }

    /**
     * Retrieves a verification method from a DID document.
     *
     * @param didDocument The DID document object.
     * @param keyId       The key ID of the verification method.
     * @return The verification method object.
     * @throws OpenDidException if the verification method retrieval fails.
     */
    public static VerificationMethod getVerificationMethod(DidDocument didDocument, String keyId) {
        DidManager didManager = parseDidDoc(didDocument);
        return didManager.getVerificationMethodByKeyId(keyId);
    }
}

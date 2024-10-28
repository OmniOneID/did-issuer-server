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

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.omnione.did.base.exception.OpenDidException;
import org.omnione.did.base.util.BaseBlockChainUtil;
import org.omnione.did.data.model.did.DidDocAndStatus;
import org.omnione.did.data.model.did.DidDocument;
import org.omnione.did.data.model.enums.did.DidDocStatus;
import org.omnione.did.data.model.enums.vc.VcStatus;
import org.omnione.did.data.model.vc.VcMeta;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;
import org.omnione.did.base.exception.ErrorCode;

/**
 * The BlockChainServiceImpl class provides methods for registering and retrieving DID Documents.
 * It is designed to facilitate the storage and retrieval of DID Documents, ensuring that the data is accurate and up-to-date.
 */
@Service
@RequiredArgsConstructor
@Slf4j
@Profile("!repository")
public class BlockChainServiceImpl implements StorageService {

    /**
     * Register the given DID Document to blockchain.
     *
     * @param didKeyUrl the DID key URL
     * @return the retrieved DID Document
     * @throws OpenDidException if the DID Document cannot be retrieved
     */
    @Override
    public DidDocument findDidDoc(String didKeyUrl) {
        try {
            DidDocAndStatus didDocAndStatus = BaseBlockChainUtil.findDidDocument(didKeyUrl);
            isActiveDidDoc(didDocAndStatus.getStatus());
            return didDocAndStatus.getDocument();
        } catch (OpenDidException e) {
            log.error("Failed to find DID Document: " + e.getMessage());
            throw e;
        } catch (Exception e) {
            log.error("Failed to find DID Document: " + e.getMessage());
            throw new OpenDidException(ErrorCode.DID_DOC_FIND_FAILURE);
        }
    }

    /**
     * Register Verifiable Credential Metadata to blockchain.
     *
     * @param vcMeta Verifiable Credential Metadata
     */
    @Override
    public void registerVcMeta(VcMeta vcMeta) {
        BaseBlockChainUtil.registerVcMeta(vcMeta);
    }

    /**
     * Update the status of the Verifiable Credential.
     *
     * @param vcId      the Verifiable Credential ID
     * @param vcStatus  the Verifiable Credential status
     */
    @Override
    public void updateVcStatus(String vcId, VcStatus vcStatus) {
        BaseBlockChainUtil.updateVcStatus(vcId, vcStatus);
    }

    /**
     * Retrieve the Verifiable Credential Metadata by VC ID.
     *
     * @param vcId the Verifiable Credential ID
     * @return the Verifiable Credential Metadata
     */
    @Override
    public VcMeta getVcMetByVcId(String vcId) {
        return BaseBlockChainUtil.findVcMeta(vcId);
    }

    /**
     * Did Document status check
     *
     * @param didDocStatus the status of the DID Document
     * @throws OpenDidException Invalid DID Document version
     */
    private void isActiveDidDoc(DidDocStatus didDocStatus) {
        if (!DidDocStatus.ACTIVATED.equals(didDocStatus)) {
            throw new OpenDidException(ErrorCode.DID_DOC_VERSION_INVALID);
        }
    }
}

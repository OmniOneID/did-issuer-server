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

import feign.FeignException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.omnione.did.base.exception.ErrorCode;
import org.omnione.did.base.exception.OpenDidException;
import org.omnione.did.base.util.BaseCoreDidUtil;
import org.omnione.did.base.util.BaseMultibaseUtil;
import org.omnione.did.common.util.DidUtil;
import org.omnione.did.core.manager.DidManager;
import org.omnione.did.data.model.did.DidDocument;
import org.omnione.did.data.model.enums.vc.VcStatus;
import org.omnione.did.data.model.vc.VcMeta;
import org.omnione.did.issuer.v1.api.RepositoryFeign;
import org.omnione.did.issuer.v1.api.dto.DidDocApiResDto;
import org.omnione.did.issuer.v1.api.dto.UpdateVcStatusApiReqDto;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;

/**
 * Implementation of the StorageService interface.
 * This service provides methods for interacting with the repository service.
 */
@Service
@RequiredArgsConstructor
@Slf4j
@Primary
@Profile("repository")
public class RepositoryServiceImpl implements StorageService {
    private final RepositoryFeign repositoryFeign;

    /**
     * Finds a DID document by DID key URL.
     *
     * @param didKeyUrl The DID key URL.
     * @return The DID document.
     * @throws OpenDidException if the DID document is not found.
     */
    @Override
    public DidDocument findDidDoc(String didKeyUrl) {
        try {
            String did = DidUtil.extractDid(didKeyUrl);

            DidDocApiResDto didDocApiResDto = repositoryFeign.getDid(did);

            byte[] decodedDidDoc = BaseMultibaseUtil.decode(didDocApiResDto.getDidDoc());

            String didDocJson = new String(decodedDidDoc);
            DidManager didManager = BaseCoreDidUtil.parseDidDoc(didDocJson);

            return didManager.getDocument();
        } catch (FeignException e) {
            log.error("Failed to find DID document.", e);
            throw new OpenDidException(ErrorCode.DID_DOC_FIND_FAILURE);
        } catch (Exception e) {
            log.error("Failed to find DID document.", e);
            throw new OpenDidException(ErrorCode.UNKNOWN_SERVER_ERROR);
        }
    }

    /**
     * Registers a verifiable credential metadata.
     *
     * @param vcMeta The VC metadata to register.
     */
    @Override
    public void registerVcMeta(VcMeta vcMeta) {
        repositoryFeign.inputVcMeta(vcMeta);
    }

    /**
     * Updates the status of a verifiable credential (VC).
     *
     * @param vcId     The VC identifier.
     * @param vcStatus The new status of the VC.
     */
    @Override
    public void updateVcStatus(String vcId, VcStatus vcStatus) {

        repositoryFeign.updateVcStatus(UpdateVcStatusApiReqDto.builder()
                .vcId(vcId)
                .vcStatus(vcStatus)
                .build());
    }

    /**
     * Retrieves the metadata of a verifiable credential (VC) by its identifier.
     *
     * @param vcId The VC identifier.
     * @return The found VC metadata.
     */
    @Override
    public VcMeta getVcMetByVcId(String vcId) {
        String encodedVcMeta = repositoryFeign.getVcMetaData(vcId).getVcMeta();
        byte[] decodedVcMeta = BaseMultibaseUtil.decode(encodedVcMeta);
        String jsonVcMeta = new String(decodedVcMeta, StandardCharsets.UTF_8);

        VcMeta vcMeta = new VcMeta();
        vcMeta.fromJson(jsonVcMeta);
        return vcMeta;
    }
}

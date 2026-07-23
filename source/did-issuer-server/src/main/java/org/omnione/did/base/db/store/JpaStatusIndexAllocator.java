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

package org.omnione.did.base.db.store;

import io.micrometer.core.instrument.Metrics;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.omnione.did.base.db.domain.IssuanceState;
import org.omnione.did.base.db.domain.Oid4vcCredentialIssuanceEntity;
import org.omnione.did.base.db.domain.Oid4vcStatusListEntity;
import org.omnione.did.base.db.repository.Oid4vcCredentialIssuanceRepository;
import org.omnione.did.base.db.repository.Oid4vcStatusListRepository;
import org.omnione.did.issuer.v1.agent.service.oid4vc.status.StatusIndexAllocator;
import org.omnione.did.oid4vc.oid4vci.exception.OID4VCIErrorCode;
import org.omnione.did.oid4vc.oid4vci.exception.OID4VCIException;
import org.omnione.did.oid4vc.oid4vci.property.IssuerProperties;
import org.omnione.did.oid4vc.oid4vci.spi.CredentialStatusAllocation;
import org.omnione.did.oid4vc.oid4vci.status.model.CredentialStatus;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Service
@Profile("!local")
@RequiredArgsConstructor
@Slf4j
public class JpaStatusIndexAllocator implements StatusIndexAllocator {

    private static final String DEFAULT_SIGNING_KEY_ID = "assert";

    private final Oid4vcStatusListRepository statusListRepository;
    private final Oid4vcCredentialIssuanceRepository issuanceRepository;
    private final IssuerProperties issuerProperties;

    @Override
    @Transactional
    public CredentialStatusAllocation allocate(
            String userId,
            String configId,
            String format,
            Instant expiresAt) throws OID4VCIException {
        requireText(userId, "userId");
        requireText(configId, "configId");
        requireText(format, "format");

        List<Oid4vcStatusListEntity> allocatableLists = findOrCreateInitialStatusList(format);
        if (allocatableLists.isEmpty()) {
            Metrics.counter("oid4vc.status.allocation.failures", "format", format).increment();
            throw new OID4VCIException(
                    OID4VCIErrorCode.ERR_CODE_STATUS_LIST_UNAVAILABLE,
                    "format=" + format);
        }

        Oid4vcStatusListEntity statusList = allocatableLists.getFirst();
        long allocatedIndex = statusList.getNextIndex();
        statusList.setNextIndex(allocatedIndex + 1);

        Oid4vcCredentialIssuanceEntity issuance = new Oid4vcCredentialIssuanceEntity();
        issuance.setIssuanceId(UUID.randomUUID().toString());
        issuance.setUserId(userId);
        issuance.setConfigId(configId);
        issuance.setFormat(format);
        issuance.setStatusList(statusList);
        issuance.setStatusListIndex(allocatedIndex);
        issuance.setCredentialStatus(CredentialStatus.VALID);
        issuance.setIssuanceState(IssuanceState.ALLOCATED);
        issuance.setStatusChangedAt(Instant.now());
        issuance.setExpiresAt(expiresAt);
        issuanceRepository.save(issuance);
        Metrics.counter("oid4vc.status.allocations", "format", format).increment();
        log.info("Allocated credential status index: issuanceId={}, listId={}, idx={}, format={}",
                issuance.getIssuanceId(), statusList.getId(), allocatedIndex, format);

        return new CredentialStatusAllocation(
                issuance.getIssuanceId(),
                statusList.getListUri(),
                allocatedIndex);
    }

    private List<Oid4vcStatusListEntity> findOrCreateInitialStatusList(String format) {
        List<Oid4vcStatusListEntity> allocatableLists =
                statusListRepository.findAllocatableByFormatForUpdate(format);
        if (!allocatableLists.isEmpty()) {
            return allocatableLists;
        }

        statusListRepository.lockBootstrap();

        allocatableLists = statusListRepository.findAllocatableByFormatForUpdate(format);
        if (!allocatableLists.isEmpty()) {
            return allocatableLists;
        }
        if (!statusListRepository.findAllByFormatAndEnabledTrueOrderByIdAsc(format).isEmpty()) {
            return List.of();
        }

        Oid4vcStatusListEntity initialStatusList = new Oid4vcStatusListEntity();
        initialStatusList.setFormat(format);
        initialStatusList.setSigningKeyId(DEFAULT_SIGNING_KEY_ID);
        initialStatusList.setListUri(temporaryListUri(format));
        initialStatusList = statusListRepository.saveAndFlush(initialStatusList);
        initialStatusList.setListUri(statusListUri(initialStatusList.getId()));
        log.info("Initialized Status List: format={}, listId={}, uri={}, keyId={}",
                format, initialStatusList.getId(), initialStatusList.getListUri(),
                initialStatusList.getSigningKeyId());
        return List.of(initialStatusList);
    }

    private String temporaryListUri(String format) {
        return issuerBaseUrl() + "/status-lists/bootstrap-" + format + "-" + UUID.randomUUID();
    }

    private String statusListUri(Long statusListId) {
        return issuerBaseUrl() + "/status-lists/" + statusListId;
    }

    private String issuerBaseUrl() {
        String baseUrl = issuerProperties.getBaseUrl();
        return baseUrl.endsWith("/") ? baseUrl.substring(0, baseUrl.length() - 1) : baseUrl;
    }

    private void requireText(String value, String name) throws OID4VCIException {
        if (value == null || value.isBlank()) {
            throw new OID4VCIException(
                    OID4VCIErrorCode.ERR_CODE_GENERAL_INVALID_PARAMETER,
                    name + " must not be blank");
        }
    }
}

/*
 * Copyright 2025 OmniOne.
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

package org.omnione.did.issuer.v1.admin.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.omnione.did.base.constants.UrlConstant;
import org.omnione.did.base.db.constant.IssuerStatus;
import org.omnione.did.base.db.domain.CertificateVc;
import org.omnione.did.base.db.domain.EntityDidDocument;
import org.omnione.did.base.db.domain.IssuerInfo;
import org.omnione.did.base.exception.ErrorCode;
import org.omnione.did.base.exception.OpenDidException;
import org.omnione.did.base.response.ErrorResponse;
import org.omnione.did.base.util.BaseCoreDidUtil;
import org.omnione.did.base.util.BaseMultibaseUtil;
import org.omnione.did.common.exception.HttpClientException;
import org.omnione.did.common.util.HttpClientUtil;
import org.omnione.did.common.util.JsonUtil;
import org.omnione.did.core.data.rest.DidKeyInfo;
import org.omnione.did.core.manager.DidManager;
import org.omnione.did.data.model.did.DidDocument;
import org.omnione.did.data.model.enums.did.ProofPurpose;
import org.omnione.did.data.model.enums.vc.RoleType;
import org.omnione.did.data.model.vc.VerifiableCredential;
import org.omnione.did.issuer.v1.admin.api.dto.EmptyResDto;
import org.omnione.did.issuer.v1.admin.constant.EntityStatus;
import org.omnione.did.issuer.v1.admin.dto.issuer.GetIssuerInfoReqDto;
import org.omnione.did.issuer.v1.admin.dto.vc.SendCertificateVcReqDto;
import org.omnione.did.issuer.v1.admin.dto.admin.SendEntityInfoReqDto;
import org.omnione.did.issuer.v1.admin.dto.issuer.*;
import org.omnione.did.issuer.v1.admin.service.query.ApplicationConfigQueryService;
import org.omnione.did.issuer.v1.admin.service.query.DidDocumentQueryService;
import org.omnione.did.issuer.v1.agent.service.EnrollEntityService;
import org.omnione.did.issuer.v1.agent.service.FileWalletService;
import org.omnione.did.issuer.v1.common.service.StorageService;
import org.omnione.did.issuer.v1.agent.service.query.CertificateVcQueryService;
import org.omnione.did.issuer.v1.agent.service.query.IssuerInfoQueryService;
import org.omnione.did.wallet.key.WalletManagerInterface;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

/**
 * Service for managing issuer registration and DID operations in the Admin Console.
 * Handles entity registration, DID document generation and registration, certificate VC management,
 * and communication with the Trusted Authority Service (TAS).
 */
@Slf4j
@Transactional
@Service
public class IssuerManagementService {

    private final IssuerInfoQueryService issuerInfoQueryService;
    private final StorageService storageService;
    private final CertificateVcQueryService certificateVcQueryService;
    private final FileWalletService fileWalletService;
    private final JsonParseService jsonParseService;
    private final DidDocumentQueryService didDocumentQueryService;
    private final EnrollEntityService enrollEntityService;

    @Value(value = "${tas.url}")
    private String TAS_URL;

    public IssuerManagementService(IssuerInfoQueryService issuerInfoQueryService, StorageService storageService,
                                   CertificateVcQueryService certificateVcQueryService, FileWalletService fileWalletService,
                                   JsonParseService jsonParseService, DidDocumentQueryService didDocumentQueryService,
                                   EnrollEntityService enrollEntityService, ApplicationConfigQueryService applicationConfigQueryService) {
        this.issuerInfoQueryService = issuerInfoQueryService;
        this.storageService = storageService;
        this.certificateVcQueryService = certificateVcQueryService;
        this.fileWalletService = fileWalletService;
        this.jsonParseService = jsonParseService;
        this.didDocumentQueryService = didDocumentQueryService;
        this.enrollEntityService = enrollEntityService;
//        this.TAS_URL = applicationConfigQueryService.getApplicationConfig().getTasUrl() + UrlConstant.Tas.ADMIN_V1;
    }

    /**
     * Retrieves current issuer information and DID Document if available.
     *
     * @return issuer info response DTO
     */
    public GetIssuerInfoReqDto getIssuerInfo() {
        IssuerInfo issuerInfo = issuerInfoQueryService.findIssuerOrNull();
        log.debug("\t--> Found Issuer: {}", issuerInfo);

        if (issuerInfo == null || issuerInfo.getStatus() == IssuerStatus.DID_DOCUMENT_REQUIRED) {
            return GetIssuerInfoReqDto.fromEntity(issuerInfo);
        }

        DidDocument didDocument = storageService.findDidDoc(issuerInfo.getDid());
        return GetIssuerInfoReqDto.fromEntity(issuerInfo, didDocument);
    }

    /**
     * Saves a certificate VC to the database.
     *
     * @param sendCertificateVcReqDto encoded certificate VC
     * @return an empty response
     */
    public EmptyResDto createCertificateVc(SendCertificateVcReqDto sendCertificateVcReqDto) {
        byte[] decodedVc = BaseMultibaseUtil.decode(sendCertificateVcReqDto.getCertificateVc());
        log.debug("Decoded VC: {}", new String(decodedVc));

        certificateVcQueryService.save(CertificateVc.builder()
                .vc(new String(decodedVc))
                .build());

        return new EmptyResDto();
    }

    /**
     * Updates or inserts entity information including DID and endpoint metadata.
     *
     * @param sendEntityInfoReqDto entity metadata
     * @return an empty response
     */
    public EmptyResDto updateEntityInfo(SendEntityInfoReqDto sendEntityInfoReqDto) {
        IssuerInfo existedIssuer = issuerInfoQueryService.getIssuerInfoOrNull();

        if (existedIssuer == null) {
            issuerInfoQueryService.save(IssuerInfo.builder()
                    .name(sendEntityInfoReqDto.getName())
                    .did(sendEntityInfoReqDto.getDid())
                    .status(IssuerStatus.ACTIVATE)
                    .serverUrl(sendEntityInfoReqDto.getServerUrl())
                    .certificateUrl(sendEntityInfoReqDto.getCertificateUrl())
                    .build());
        } else {
            existedIssuer.setName(sendEntityInfoReqDto.getName());
            existedIssuer.setDid(sendEntityInfoReqDto.getDid());
            existedIssuer.setServerUrl(sendEntityInfoReqDto.getServerUrl());
            existedIssuer.setCertificateUrl(sendEntityInfoReqDto.getCertificateUrl());
            existedIssuer.setStatus(IssuerStatus.ACTIVATE);
            issuerInfoQueryService.save(existedIssuer);
        }

        return new EmptyResDto();
    }

    /**
     * Register Issuer Info information.
     *
     * @param registerIssuerInfoReqDto Request data transfer object
     * @return Issuer information
     */
    public IssuerInfoResDto registerIssuerInfo(RegisterIssuerInfoReqDto registerIssuerInfoReqDto) {
        log.debug("=== Starting registerIssuerInfo ===");

        IssuerInfo issuerInfo = issuerInfoQueryService.findIssuerOrNull();
        log.debug("\t--> Found Issuer: {}", issuerInfo);

        if (issuerInfo == null) {
            log.debug("\t--> Issuer is not registered yet. Proceeding with new registration.");
            issuerInfo = IssuerInfo.builder()
                    .name(registerIssuerInfoReqDto.getName())
                    .serverUrl(registerIssuerInfoReqDto.getServerUrl())
                    .certificateUrl(registerIssuerInfoReqDto.getServerUrl() + "/api/v1/certificate-vc")
                    .status(IssuerStatus.DID_DOCUMENT_REQUIRED)
                    .build();
            issuerInfoQueryService.save(issuerInfo);

            log.debug("=== Finished registerIssuerInfo ===");
            return buildIssuerInfoResponse(issuerInfo);
        }

        if (issuerInfo.getStatus() == IssuerStatus.ACTIVATE) {
            log.error("Issuer is already registered");
            throw new OpenDidException(ErrorCode.ISSUER_ALREADY_REGISTERED);
        }

        log.debug("\t--> Updating Issuer information");
        issuerInfo.setName(registerIssuerInfoReqDto.getName());
        issuerInfo.setServerUrl(registerIssuerInfoReqDto.getServerUrl());
        issuerInfo.setCertificateUrl(registerIssuerInfoReqDto.getServerUrl() + "/api/v1/certificate-vc");
        issuerInfoQueryService.save(issuerInfo);

        log.debug("=== Finished registerIssuerInfo ===");

        return buildIssuerInfoResponse(issuerInfo);
    }


    /**
     * Builds the Issuer information response.
     *
     * @param issuerInfo Issuer entity
     * @return Issuer information response DTO
     */
    private IssuerInfoResDto buildIssuerInfoResponse(IssuerInfo issuerInfo) {
        if (issuerInfo.getStatus() == IssuerStatus.DID_DOCUMENT_REQUIRED
                || issuerInfo.getStatus() == IssuerStatus.DID_DOCUMENT_REQUESTED) {
            return IssuerInfoResDto.fromEntity(issuerInfo);
        }

        log.debug("\t--> Finding Issuer DID Document");
        DidDocument didDocument = storageService.findDidDoc(issuerInfo.getDid());
        return IssuerInfoResDto.fromEntity(issuerInfo, didDocument);
    }

    /**
     * Register Issuer DID Document automatically.
     * <p>
     * This method creates a wallet, generates keys, and creates a DID Document.
     *
     * @return Map containing the generated DID Document
     */
    public Map<String, Object> registerIssuerDidDocumentAuto() {
        log.debug("=== Starting registerIssuerDidDocumentAuto ===");

        // Finding Issuer
        log.debug("\t--> Finding Issuer");
        IssuerInfo existedIssuer = issuerInfoQueryService.findIssuerInfo();
        log.debug("\t--> Found Issuer: {}", existedIssuer);

        // Check Issuer status
        if (existedIssuer.getStatus() != IssuerStatus.DID_DOCUMENT_REQUIRED) {
            if (existedIssuer.getStatus() == IssuerStatus.DID_DOCUMENT_REQUESTED) {
                log.error("Issuer DID Document is already requested");
                throw new OpenDidException(ErrorCode.ISSUER_DID_DOCUMENT_ALREADY_REQUESTED);
            }
            log.error("Issuer DID Document is already registered");
            throw new OpenDidException(ErrorCode.ISSUER_DID_DOCUMENT_ALREADY_REGISTERED);
        }

        // Step1: Create Wallet and keys
        WalletManagerInterface walletManager = initializeWalletWithKeys();

        // Step2: Create DID Document
        DidDocument didDocument = createDidDocumentAuto(walletManager);

        log.debug("=== Finished registerIssuerDidDocumentAuto ===");

        return jsonParseService.parseDidDocToMap(didDocument.toJson());
    }


    /*
     * Generate Issuer wallet and keys.
     */
    public WalletManagerInterface initializeWalletWithKeys() {
        return fileWalletService.initializeWalletWithKeys();
    }

    /**
     * Create DID Document automatically.
     * <p>
     * This method creates a DID Document using the provided wallet manager.
     *
     * @param walletManager Wallet manager
     * @return Created DID Document
     */
    public DidDocument createDidDocumentAuto(WalletManagerInterface walletManager) {
        String did = "did:omn:issuer";

        Map<String, List<ProofPurpose>> purposes = BaseCoreDidUtil.createDefaultProofPurposes();
        List<DidKeyInfo> keyInfos = BaseCoreDidUtil.getDidKeyInfosFromWallet(walletManager, did, purposes);

        DidManager didManager = new DidManager();
        DidDocument unsignedDoc = BaseCoreDidUtil.createDidDocument(didManager, did, did, keyInfos);

        List<String> signingKeys = BaseCoreDidUtil.getSigningKeyIds(purposes);
        DidDocument signedDoc = BaseCoreDidUtil.signAndAddProof(didManager, walletManager, signingKeys);

        return signedDoc;
    }

    /**
     * Sends a request to register the issuer DID Document to the TAS.
     *
     * @param request request DTO containing the DID Document
     * @return an empty response
     */
    public EmptyResDto requestRegisterDid(RequestRegisterDidReqDto request) throws OpenDidException {
        try {
            log.debug("=== Starting requestRegisterDid ===");

            IssuerInfo issuerInfo = issuerInfoQueryService.findIssuerInfo();
            log.debug("\t--> Found Issuer: {}", issuerInfo);

            if (issuerInfo.getStatus() != IssuerStatus.DID_DOCUMENT_REQUIRED) {
                if (issuerInfo.getStatus() == IssuerStatus.DID_DOCUMENT_REQUESTED) {
                    log.error("Issuer DID Document is already requested");
                    throw new OpenDidException(ErrorCode.ISSUER_DID_DOCUMENT_ALREADY_REQUESTED);
                }
                log.error("Issuer DID Document is already registered");
                throw new OpenDidException(ErrorCode.ISSUER_DID_DOCUMENT_ALREADY_REGISTERED);
            }

            // Send the register DID request to TAS
            log.debug("\t--> Sending register DID request to TAS");
            EmptyResDto resDto = sendRegisterDid(issuerInfo, request);

            // Update didDocument in the database
            log.debug("\t--> Updating DID Document in the database");
            EntityDidDocument entityDidDoc = didDocumentQueryService.findDidDocumentOrNull();
            if (entityDidDoc == null) {
                entityDidDoc = EntityDidDocument.builder()
                        .didDocument(request.getDidDocument())
                        .build();
            } else {
                entityDidDoc.setDidDocument(request.getDidDocument());
            }
            didDocumentQueryService.save(entityDidDoc);

            // Update Issuer status
            log.debug("\t--> Updating Issuer did and status");
            issuerInfo.setStatus(IssuerStatus.DID_DOCUMENT_REQUESTED);
            issuerInfo.setDid(BaseCoreDidUtil.parseDid(request.getDidDocument()));
            issuerInfoQueryService.save(issuerInfo);

            log.debug("=== Finished requestRegisterDid ===");

            return resDto;
        } catch (OpenDidException e) {
            log.error("Failed to register Issuer DID Document: {}", e.getMessage());
            throw e;
        } catch (Exception e) {
            log.error("Failed to register Issuer DID Document", e);
            throw new OpenDidException(ErrorCode.FAILED_TO_REGISTER_ISSUER_DID_DOCUMENT);
        }
    }

    /**
     * Sends the actual HTTP request to the TAS to register a DID.
     *
     * @param issuerInfo               the issuer entity
     * @param requestRegisterDidReqDto request payload
     * @return an empty response
     */
    private EmptyResDto sendRegisterDid(IssuerInfo issuerInfo, RequestRegisterDidReqDto requestRegisterDidReqDto) {
        String url = TAS_URL + UrlConstant.Tas.ADMIN_V1 + UrlConstant.Tas.REGISTER_DID_PUBLIC;

        String encodedDidDocument = BaseMultibaseUtil.encode(requestRegisterDidReqDto.getDidDocument().getBytes());
        RegisterDidToTaReqDto registerDidToTaReqDto = RegisterDidToTaReqDto.builder()
                .didDoc(encodedDidDocument)
                .name(issuerInfo.getName())
                .serverUrl(issuerInfo.getServerUrl())
                .certificateUrl(issuerInfo.getCertificateUrl())
                .role(RoleType.ISSUER)
                .build();
        try {
            String request = JsonUtil.serializeToJson(registerDidToTaReqDto);
            return HttpClientUtil.postData(url, request, EmptyResDto.class);
        } catch (HttpClientException e) {
            log.error("HttpClientException occurred while sending register-did-public request: {}", e.getResponseBody(), e);
            ErrorResponse errorResponse = convertExternalErrorResponse(e.getResponseBody());
            throw new OpenDidException(errorResponse);
        } catch (Exception e) {
            log.error("An unknown error occurred while sending register-did-public request", e);
            throw new OpenDidException(ErrorCode.TAS_COMMUNICATION_ERROR);
        }
    }


    /**
     * Converts an external error response string to an ErrorResponse object.
     * This method attempts to parse the given JSON string into an ErrorResponse instance.
     *
     * @param resBody The JSON string representing the external error response
     * @return An ErrorResponse object parsed from the input string
     * @throws OpenDidException with ErrorCode.ISSUER_UNKNOWN_RESPONSE if parsing fails
     */
    private ErrorResponse convertExternalErrorResponse(String resBody) {
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            return objectMapper.readValue(resBody, ErrorResponse.class);
        } catch (JsonProcessingException e) {
            log.error("Failed to parse external error response: {}", resBody, e);
            throw new OpenDidException(ErrorCode.TAS_UNKNOWN_RESPONSE);
        }
    }

    /**
     * Request the status of the entity.
     *
     * @return RequestEntityStatusResDto containing the status information
     */
    public RequestEntityStatusResDto requestEntityStatus() {
        log.debug("=== Starting requestEntityStatus ===");

        // Finding Issuer
        log.debug("\t--> Finding Issuer");
        IssuerInfo exsitedIssuerInfo = issuerInfoQueryService.findIssuerInfo();

        String did = exsitedIssuerInfo.getDid();
        if (did == null) {
            EntityDidDocument entityDidDocument = didDocumentQueryService.findDidDocument();
            DidDocument didDocument = new DidDocument();
            didDocument.fromJson(entityDidDocument.getDidDocument());
            did = didDocument.getId();
        }

        // Sending request-status request to TAS
        log.debug("\t--> Sending request-status request to TAS");
        RequestEntityStatusResDto requestEntityStatusResDto = sendRequestEntityStatus(did);

        // Update Issuer status based on the response
        if (requestEntityStatusResDto.getStatus() == EntityStatus.NOT_REGISTERED) {
            log.debug("\t--> TA has deleted the entity's registration request. Updating Issuer status accordingly");
            exsitedIssuerInfo.setStatus(IssuerStatus.DID_DOCUMENT_REQUIRED);
            issuerInfoQueryService.save(exsitedIssuerInfo);
        } else if (requestEntityStatusResDto.getStatus() == EntityStatus.CERTIFICATE_VC_REQUIRED) {
            log.debug("\t--> TA has requested a certificate VC. Updating Issuer status accordingly");
            exsitedIssuerInfo.setStatus(IssuerStatus.CERTIFICATE_VC_REQUIRED);
            issuerInfoQueryService.save(exsitedIssuerInfo);
        }

        log.debug("=== Finished requestEntityStatus ===");

        return requestEntityStatusResDto;
    }

    /**
     * Sends the request to TAS for querying the current entity status.
     *
     * @param did the DID to check status for
     * @return response containing entity status
     */

    private RequestEntityStatusResDto sendRequestEntityStatus(String did) {
        String url = TAS_URL + UrlConstant.Tas.ADMIN_V1 + UrlConstant.Tas.REQUEST_ENTITY_STATUS + "?did=" + did;

        try {
            return HttpClientUtil.getData(url, RequestEntityStatusResDto.class);
        } catch (HttpClientException e) {
            log.error("HttpClientException occurred while sending request-status request: {}", e.getResponseBody(), e);
            ErrorResponse errorResponse = convertExternalErrorResponse(e.getResponseBody());
            throw new OpenDidException(errorResponse);
        } catch (Exception e) {
            log.error("An unknown error occurred while sending request-status request", e);
            throw new OpenDidException(ErrorCode.TAS_COMMUNICATION_ERROR);
        }
    }

    /**
     * Enrolls the entity by sending both DID and certificate VC to TAS.
     *
     * @return a map containing the parsed certificate VC
     */
    public Map<String, Object> enrollEntity() {
        try {
            log.debug("=== Starting enrollEntity ===");
            // Register the entity
            log.debug("\t--> Registering the entity");
            enrollEntityService.enrollEntity();

            // Finding Certificate VC
            log.debug("\t--> Finding Certificate VC");
            CertificateVc certificateVc = certificateVcQueryService.findCertificateVc();
            VerifiableCredential verifiableCredential = new VerifiableCredential();
            verifiableCredential.fromJson(certificateVc.getVc());

            log.debug("=== Finished enrollEntity ===");
            return jsonParseService.parseCertificateVcToMap(verifiableCredential.toJson());
        } catch (OpenDidException e) {
            log.error("An OpenDidException occurred while sending requestCertificateVc request", e);
            throw e;
        } catch (Exception e) {
            log.error("An unknown error occurred while sending requestCertificateVc request", e);
            throw new OpenDidException(ErrorCode.FAILED_TO_REQUEST_CERTIFICATE_VC);
        }
    }
}

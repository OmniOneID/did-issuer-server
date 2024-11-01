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

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.logging.log4j.util.Strings;
import org.bouncycastle.jce.interfaces.ECPublicKey;
import org.omnione.did.base.constants.VcPlanId;
import org.omnione.did.base.datamodel.data.*;
import org.omnione.did.base.datamodel.enums.EccCurveType;
import org.omnione.did.base.datamodel.enums.OfferType;
import org.omnione.did.base.datamodel.enums.SymmetricCipherType;
import org.omnione.did.base.datamodel.enums.SymmetricPaddingType;
import org.omnione.did.base.db.constant.SubTransactionStatus;
import org.omnione.did.base.db.constant.SubTransactionType;
import org.omnione.did.base.db.constant.TransactionStatus;
import org.omnione.did.base.db.constant.TransactionType;
import org.omnione.did.base.db.domain.*;
import org.omnione.did.base.exception.ErrorCode;
import org.omnione.did.base.exception.OpenDidException;
import org.omnione.did.base.property.IssueProperty;
import org.omnione.did.base.util.*;
import org.omnione.did.common.util.DateTimeUtil;
import org.omnione.did.common.util.JsonUtil;
import org.omnione.did.core.data.rest.ClaimInfo;
import org.omnione.did.core.data.rest.IssueVcParam;
import org.omnione.did.core.data.rest.SignatureParams;
import org.omnione.did.core.data.rest.SignatureVcParams;
import org.omnione.did.core.exception.CoreException;
import org.omnione.did.core.manager.VcManager;
import org.omnione.did.crypto.keypair.EcKeyPair;
import org.omnione.did.crypto.keypair.KeyPairInterface;
import org.omnione.did.data.model.did.DidDocument;
import org.omnione.did.data.model.did.Proof;
import org.omnione.did.data.model.enums.did.ProofPurpose;
import org.omnione.did.data.model.enums.did.ProofType;
import org.omnione.did.data.model.enums.vc.VcStatus;
import org.omnione.did.data.model.enums.vc.VcType;
import org.omnione.did.data.model.profile.ReqE2e;
import org.omnione.did.data.model.profile.issue.IssueProcess;
import org.omnione.did.data.model.profile.issue.IssueProfile;
import org.omnione.did.data.model.vc.DocumentVerificationEvidence;
import org.omnione.did.data.model.vc.VcMeta;
import org.omnione.did.data.model.vc.VerifiableCredential;
import org.omnione.did.issuer.v1.dto.vc.*;
import org.omnione.did.issuer.v1.service.query.*;
import org.omnione.did.wallet.key.WalletManagerInterface;

import java.security.interfaces.ECPrivateKey;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;

/**
 * Abstract base class for issuing Verifiable Credentials (VCs).
 * This class provides the core functionality for the VC issuance process,
 * including offer generation, inspection, profile generation, VC issuance, and completion.
 */
@Slf4j
@RequiredArgsConstructor
public abstract class IssueServiceBase implements IssueService {
    private final VcProfileQueryService vcProfileQueryService;
    private final VcOfferQueryService vcOfferQueryService;
    private final TransactionService transactionService;
    private final E2EQueryService e2EQueryService;
    private final VcQueryService vcQueryService;
    private final IssueProperty issueProperty;
    private final StorageService storageService;

    private final WalletManagerInterface walletManagerInterface;
    /**
     * Generates an offer for issuing a Verifiable Credential.
     *
     * @param request The request containing the VC plan ID.
     * @return OfferIssueVcResDto containing the issue offer payload.
     * @throws OpenDidException if there's an error in the offer generation process.
     */
    @Override
    public OfferIssueVcResDto requestOffer(OfferIssueVcReqDto request) {
        try {
            validateVcPlanId(request.getVcPlanId());

            String offerId = RandomUtil.generateUUID();
            String issuer = issueProperty.getDid();
            // TODO: validUntil property
            String validUntil = DateTimeUtil.addToCurrentTimeString(3, ChronoUnit.MINUTES);

            IssueOfferPayload issueOfferPayload = IssueOfferPayload.builder()
                    .offerId(offerId)
                    .type(OfferType.ISSUE_OFFER)
                    .vcPlanId(request.getVcPlanId())
                    .issuer(issuer)
                    .validUntil(validUntil)
                    .build();

            // TODO Valid Until 확인, log 추가
            vcOfferQueryService.save(VcOffer.builder()
                    .offerId(offerId)
                    .did(issuer)
                    .validUntil(Instant.now().plusSeconds(86400L))
                    .offerType(OfferType.ISSUE_OFFER)
                    .vcPlanId(request.getVcPlanId())
                    .build());

            return OfferIssueVcResDto.builder()
                    .issueOfferPayload(issueOfferPayload)
                    .build();
        } catch(OpenDidException e) {
            log.error("OpenDidException occurred during requestOffer: {}", e.getErrorCode().getMessage());
            throw e;
        } catch (Exception e) {
            log.error("Exception occurred during requestOffer: {}", e.getMessage(), e);
            throw new OpenDidException(ErrorCode.TR_VC_OFFER_FAILED);
        }

    }
    /**
     * Inspects the issue proposal for a Verifiable Credential.
     *
     * @param request The request containing the VC plan ID and offer ID.
     * @return InspectIssueProposeResDto containing transaction and reference IDs.
     * @throws OpenDidException if there's an error in the inspection process.
     */
    @Override
    public InspectIssueProposeResDto inspectIssuePropose(InspectIssueProposeReqDto request) {
        try {

            String vcPlanId = request.getVcPlanId();
            validateVcPlanId(vcPlanId);

            // todo 용도를 명확하게 알 수 있도록 수정
            VcOffer vcOffer = null;
            if (Strings.isNotBlank(request.getOfferId())) {
                vcOffer = validateOfferId(request.getOfferId());
            }

            String txId = RandomUtil.generateUUID();
            String refId = RandomUtil.generateRefId();

            // TODO: expired at
            Transaction transaction = transactionService.insertTransaction(Transaction.builder()
                    .txId(txId)
                    .refId(refId)
                    .vcPlanId(vcPlanId)
                    .expiredAt(Instant.now().plusSeconds(3600L))
                    .type(TransactionType.ISSUE_VC)
                    .status(TransactionStatus.COMPLETED)
                    .build());

            if (vcOffer != null) {
                // FIXME: Offer에 횟수제한이 없으면 Transaction이 계속 생성되는 문제가 있음
                //  5회 제한
                vcOffer.setTransactionId(transaction.getId());
            }

            transactionService.insertSubTransaction(SubTransaction.builder()
                    .transactionId(transaction.getId())
                    .step(1)
                    .type(SubTransactionType.INSPECT_ISSUE_PROPOSE)
                    .status(SubTransactionStatus.COMPLETED)
                    .build());


            return InspectIssueProposeResDto.builder()
                    .txId(txId)
                    .refId(refId)
                    .build();
        } catch(OpenDidException e) {
            log.error("OpenDidException occurred during inspectIssuePropose: {}", e.getErrorCode().getMessage());
            throw e;
        } catch (Exception e) {
            log.error("Exception occurred during inspectIssuePropose: {}", e.getMessage(), e);
            throw new OpenDidException(ErrorCode.UNKNOWN_SERVER_ERROR);
        }
    }

    /**
     * Generates an issue profile for a Verifiable Credential.
     *
     * @param request The request containing the transaction ID and holder information.
     * @return GenerateIssueProfileResDto containing the transaction ID and generated profile.
     * @throws OpenDidException if there's an error in the profile generation process.
     */
    @Override
    public GenerateIssueProfileResDto generateIssueProfile(GenerateIssueProfileReqDto request) {
        try {
            Transaction transaction = transactionService.validateAndFindTransaction(request.getTxId(), SubTransactionType.INSPECT_ISSUE_PROPOSE);

            Holder holder = request.getHolder();

            User user = findUserByHolder(holder);

            String vcPlanId = transaction.getVcPlanId();
            IssueProfile profile = issueProperty.getProfileByVcPlanId(vcPlanId);
            IssueProcess process = profile.getProfile().getProcess();
            ReqE2e reqE2e = process.getReqE2e();
            profile.setId(RandomUtil.generateUUID());

            EcKeyPair keyPair = generateEcKeyPair(reqE2e.getCurve());

            String nonce = BaseCryptoUtil.generateNonceWithMultibase(16);

            setPublicKeyAndNonce(reqE2e, process, keyPair, nonce);
            signProfile(profile, issueProperty.getAssertSignKeyId());

            String encodedSessionKey = encodedSessionKey((ECPrivateKey) keyPair.getPrivateKey());

            vcProfileQueryService.save(VcProfile.builder()
                    .profileId(profile.getId())
                    .transactionId(transaction.getId())
                    .did(holder.getDid())
                    .nonce(reqE2e.getNonce())
                    .userId(user.getId())
                    .build());

            e2EQueryService.save(E2E.builder()
                    .curve(reqE2e.getCurve())
                    .cipher(reqE2e.getCipher())
                    .padding(reqE2e.getPadding())
                    .nonce(reqE2e.getNonce())
                    .sessionKey(encodedSessionKey)
                    .transactionId(transaction.getId())
                    .build());

            transactionService.insertSubTransaction(SubTransaction.builder()
                    .transactionId(transaction.getId())
                    .step(2)
                    .type(SubTransactionType.GENERATE_ISSUE_PROFILE)
                    .status(SubTransactionStatus.COMPLETED)
                    .build());

            return GenerateIssueProfileResDto.builder()
                    .txId(transaction.getTxId())
                    .profile(profile)
                    .build();
        } catch(OpenDidException e) {
            log.error("OpenDidException occurred during generateIssueProfile: {}", e.getErrorCode().getMessage());
            throw e;
        } catch (Exception e) {
            log.error("Exception occurred during generateIssueProfile: {}", e.getMessage(), e);
            throw new OpenDidException(ErrorCode.TR_VC_ISSUE_PROFILE_FAILED);
        }
    }
    /**
     * Issues a Verifiable Credential.
     *
     * @param request The request containing the transaction ID and E2E information.
     * @return IssueVcResDto containing the transaction ID and encrypted VC.
     * @throws OpenDidException if there's an error in the VC issuance process.
     */
    @Override
    public IssueVcResDto issueVc(IssueVcReqDto request) {
        try {
            Transaction transaction = transactionService.validateAndFindTransaction(request.getTxId(), SubTransactionType.GENERATE_ISSUE_PROFILE);
            AccE2e accE2e = request.getAccE2e();

            validateAccE2e(accE2e);


            E2E e2e = e2EQueryService.findByTransactionId(transaction.getId());

            VcProfile vcProfile = vcProfileQueryService.findByTransactionId(transaction.getId());

            byte[] sharedSecretKey = generateSharedSecretKey(e2e, accE2e);

            byte[] mergeSharedSecretAndNonce = mergeSharedSecretAndNonce(sharedSecretKey, vcProfile.getNonce(), e2e.getCipher());

            String decryptedRequestVc = decryptRequestVc(request, mergeSharedSecretAndNonce,
                    BaseMultibaseUtil.decode(accE2e.getIv()), e2e);

            ReqVc reqVc = parseRequestVc(decryptedRequestVc);
            validateRequestVc(transaction, reqVc);

            User user = findUserByVcProfile(vcProfile);
            VcManager vcManager = new VcManager();

            VerifiableCredential verifiableCredential = issueVerifiableCredential(vcManager,
                    vcProfile.getDid(), user.getData());

            VcMeta vcMeta = vcManager.generateVcMetaData(verifiableCredential, issueProperty.getCertVcRef());

            storageService.registerVcMeta(vcMeta);

            byte[] iv = BaseCryptoUtil.generateInitialVector();

            String encVc = encryptVerifiableCredential(verifiableCredential, mergeSharedSecretAndNonce, iv, e2e);

            Vc vc = handleVcCreationOrUpdate(user, vcProfile.getDid(), transaction, verifiableCredential.getId());

            vcQueryService.save(vc);

            transactionService.insertSubTransaction(SubTransaction.builder()
                    .transactionId(transaction.getId())
                    .step(3)
                    .type(SubTransactionType.ISSUE_VC)
                    .status(SubTransactionStatus.COMPLETED)
                    .build());

            return IssueVcResDto.builder()
                    .txId(transaction.getTxId())
                    .e2e(E2e.builder()
                            .encVc(encVc)
                            .iv(BaseMultibaseUtil.encode(iv))
                            .build())
                    .build();
        } catch(OpenDidException e) {
            log.error("OpenDidException occurred during issueVc: {}", e.getErrorCode().getMessage());
            throw e;
        } catch (Exception e) {
            log.error("Exception occurred during issueVc: {}", e.getMessage(), e);
            throw new OpenDidException(ErrorCode.TR_VC_ISSUE_FAILED);
        }

    }
    /**
     * Completes the Verifiable Credential issuance process.
     *
     * @param request The request containing the transaction ID.
     * @return CompleteVcResDto containing the transaction ID.
     * @throws OpenDidException if there's an error in the completion process.
     */
    @Override
    public CompleteVcResDto completeVc(CompleteVcReqDto request) {
        try {
            Transaction transaction = transactionService.validateAndFindTransaction(request.getTxId(), SubTransactionType.ISSUE_VC);
            String txId = transaction.getTxId();
            Vc vc = vcQueryService.findByTxId(txId);
            if (!vc.getVcId().equals(request.getVcId())) {
                throw new OpenDidException(ErrorCode.VC_ID_NOT_MATCH);
            }
            transactionService.updateTransactionStatus(transaction, TransactionStatus.FINISH);

            transactionService.insertSubTransaction(SubTransaction.builder()
                    .transactionId(transaction.getId())
                    .step(4)
                    .type(SubTransactionType.COMPLETE_VC)
                    .status(SubTransactionStatus.COMPLETED)
                    .build());

            return CompleteVcResDto.builder()
                    .txId(transaction.getTxId())
                    .build();
        } catch(OpenDidException e) {
            log.error("OpenDidException occurred during completeVc: {}", e.getErrorCode().getMessage());
            throw e;
        } catch (Exception e) {
            log.error("Exception occurred during completeVc: {}", e.getMessage(), e);
            throw new OpenDidException(ErrorCode.TR_VC_ISSUE_COMPLETE_FAILED);
        }
    }
    /**
     * Retrieves the result of a Verifiable Credential issuance process.
     *
     * @param offerId The ID of the offer.
     * @return IssueVcResultResDto containing the result of the issuance process.
     * @throws OpenDidException if there's an error in retrieving the result.
     */
    @Override
    public IssueVcResultResDto issueVcResult(String offerId) {
        try {
            VcOffer vcOffer = vcOfferQueryService.findByOfferId(offerId);

            Transaction transaction = transactionService.findById(vcOffer.getTransactionId());
            if (transaction == null) {
                return IssueVcResultResDto.builder()
                        .offerId(offerId)
                        .result(false)
                        .build();
            }
            SubTransaction subTransaction = transactionService
                    .findByTransactionIdOrderByStepDesc(transaction.getId());

            return IssueVcResultResDto.builder()
                    .txId(transaction.getTxId())
                    .offerId(offerId)
                    .result(SubTransactionType.COMPLETE_VC.equals(subTransaction.getType()))
                    .build();
        } catch(OpenDidException e) {
            log.error("OpenDidException occurred during issueVcResult: {}", e.getErrorCode().getMessage());
            throw e;
        } catch (Exception e) {
            log.error("Exception occurred during issueVcResult: {}", e.getMessage(), e);
            throw new OpenDidException(ErrorCode.TR_VC_ISSUE_RESULT_FAILED);
        }
    }

    /**
     * Merge a shared secret and nonce.
     * The merged result is hashed using SHA-256 and the length of the result is determined by the symmetric cipher type.
     *
     * @param sharedSecretKey The shared secret key.
     * @param nonce The nonce.
     * @param cipherType The symmetric cipher type.
     * @return The merged shared secret and nonce.
     */
    private byte[] mergeSharedSecretAndNonce(byte[] sharedSecretKey, String nonce, String cipherType) {
        return BaseCryptoUtil.mergeSharedSecretAndNonce(sharedSecretKey,
                BaseMultibaseUtil.decode(nonce),
                SymmetricCipherType.fromDisplayName(cipherType));
    }


    /**
     * Validate Offer ID
     *
     * @param planId The plan ID to validate
     * @throws OpenDidException The vcPlanId is not valid
     */

    private void validateVcPlanId(String planId) {
        VcPlanId vcPlanId = VcPlanId.valueOfLabel(planId);
        if (!issueProperty.getPlanIds().contains(vcPlanId)) {
            throw new OpenDidException(ErrorCode.VC_PLAN_ID_INVALID);
        }
    }

    /**
     * Validate Offer ID
     *
     * @param offerId The offer ID to validate
     * @return VcOffer The VC offer
     * @throws OpenDidException if the offer as expired.
     */
    private VcOffer validateOfferId(String offerId) {
        VcOffer vcOffer = vcOfferQueryService.findByOfferId(offerId);

        if (ValidationUtil.isExpiredDate(vcOffer.getValidUntil())) {
            throw new OpenDidException(ErrorCode.VC_OFFER_EXPIRED);
        }
        return vcOffer;
    }

    /**
     * ReqE2e's public key and nonce are set.
     *
     * @param reqE2e The ReqE2e object to set
     * @param process The IssueProcess object to set
     * @param keyPair The EcKeyPair object to set
     * @param nonce The nonce to set
     */
    private void setPublicKeyAndNonce(ReqE2e reqE2e, IssueProcess process, EcKeyPair keyPair, String nonce) {
        ECPublicKey publicKey = (ECPublicKey) keyPair.getPublicKey();
        byte[] encodedPublicKey = BaseCryptoUtil.compressPublicKey(publicKey.getEncoded(),
                EccCurveType.fromValue(reqE2e.getCurve()));
        reqE2e.setPublicKey(BaseMultibaseUtil.encode(encodedPublicKey));

        process.setIssuerNonce(nonce);
        reqE2e.setNonce(nonce);
    }

    /**
     * Encodes a session key.
     *
     * @param privateKey The private key to encode
     * @return String The encoded session key
     */
    private String encodedSessionKey(ECPrivateKey privateKey) {
        return BaseMultibaseUtil.encode(privateKey.getEncoded());
    }

    /**
     * handle VC creation or update
     *
     * @param user The user
     * @param holderDid The holder DID
     * @param transaction The transaction
     * @param vcId The VC ID
     * @return The created or updated VC
     *
     */
    private Vc handleVcCreationOrUpdate(User user, String holderDid, Transaction transaction, String vcId) {
        String vcPlanId = transaction.getVcPlanId();
        String txId = transaction.getTxId();
        return vcQueryService.findByUserIdAndVcPlanId(user.getId(), vcPlanId)
                .map(existingVc -> {
                    revokeVc(existingVc);
                    return Vc.builder()
                            .id(existingVc.getId())
                            .issuedAt(Instant.now())
                            .expiredAt(Instant.now())
                            .did(holderDid)
                            .userId(user.getId())
                            .vcPlanId(vcPlanId)
                            .txId(txId)
                            .vcId(vcId)
                            .build();
                })
                .orElseGet(() ->
                        Vc.builder()
                                .issuedAt(Instant.now())
                                .expiredAt(Instant.now())
                                .did(holderDid)
                                .userId(user.getId())
                                .vcPlanId(vcPlanId)
                                .txId(txId)
                                .vcId(vcId)
                                .build()
                );
    }

    /**
     * Encrypts a Verifiable Credential.
     *
     * @param verifiableCredential The Verifiable Credential to encrypt.
     * @param sharedSecretKey The shared secret key.
     * @param iv The initialization vector.
     * @param e2e The end-to-end encryption information.
     * @return The encrypted Verifiable Credential.
     */
    private String encryptVerifiableCredential(VerifiableCredential verifiableCredential, byte[] sharedSecretKey, byte[] iv, E2E e2e) {
        String vcJsonString = verifiableCredential.toJson();
        byte[] encrypt = BaseCryptoUtil.encrypt(vcJsonString,
                sharedSecretKey,
                iv,
                SymmetricCipherType.fromDisplayName(e2e.getCipher()),
                SymmetricPaddingType.fromDisplayName(e2e.getPadding()));

        return BaseMultibaseUtil.encode(encrypt);
    }

    /**
     * if proof is not null, Validate the proof of an AccE2e object.
     *
     * @param accE2e The AccE2e object to verify
     * @throws OpenDidException if Failed to Json serialize
     */
    private void validateAccE2e(AccE2e accE2e) {
        Proof proof = accE2e.getProof();

        if (Objects.nonNull(proof)) {
            String verificationMethod = proof.getVerificationMethod();
            DidDocument holderDidDoc = storageService.findDidDoc(verificationMethod);

            ValidationUtil.verifySign(accE2e, holderDidDoc);
        }
    }

    /**
     * Generates a shared secret key.
     *
     * @param accE2e The AccE2e object containing the public key
     * @param e2e The E2e object containing the session key
     * @return byte[] The generated shared secret key
     */
    private byte[] generateSharedSecretKey(E2E e2e, AccE2e accE2e) {
        byte[] decodePubKey = BaseMultibaseUtil.decode(accE2e.getPublicKey());
        byte[] decodePriKey = BaseMultibaseUtil.decode(e2e.getSessionKey());

        return BaseCryptoUtil.generateSharedSecret(decodePubKey, decodePriKey,
                EccCurveType.fromValue(e2e.getCurve()));
    }

    /**
     * Decrypts the request VC.
     *
     * @param request The request containing the encrypted request VC.
     * @param sharedSecretKey The shared secret key.
     * @param iv The initialization vector.
     * @param e2e The end-to-end encryption information.
     * @return The decrypted request VC.
     */
    private String decryptRequestVc(IssueVcReqDto request, byte[] sharedSecretKey, byte[] iv, E2E e2e) {
        byte[] decrypt = BaseCryptoUtil.decrypt(
                BaseMultibaseUtil.decode(request.getEncReqVc()),
                sharedSecretKey,
                iv,
                SymmetricCipherType.fromDisplayName(e2e.getCipher()),
                SymmetricPaddingType.fromDisplayName(e2e.getPadding())
        );
        return new String(decrypt);
    }

    /**
     * parse request VC
     *
     * @param requestVc request VC
     * @return parsing request VC
     * @throws OpenDidException if there's an error in the parsing process.
     */
    private ReqVc parseRequestVc(String requestVc) {
        try {
            ObjectMapper mapper = new ObjectMapper();
            return mapper.readValue(requestVc, ReqVc.class);
        } catch (JsonProcessingException e) {
            throw new OpenDidException(ErrorCode.PARSE_REQUEST_VC_FAILURE);
        }
    }


    /**
     * Validates the request VC.
     * If the request VC is invalid, the transaction status is updated to FAILED.
     *
     * @param transaction The transaction.
     * @param reqVc The request VC.
     * @throws OpenDidException if the refId is not valid.
     * @throws OpenDidException if the profile nonce is not valid
     * @throws OpenDidException if the profile issuer nonce is not valid
     */
    private void validateRequestVc(Transaction transaction, ReqVc reqVc) {
        if (!transaction.getRefId().equals(reqVc.getRefId())) {
            transactionService.updateTransactionStatus(transaction, TransactionStatus.FAILED);
            throw new OpenDidException(ErrorCode.REF_ID_INVALID);
        }
        VcProfile vcProfile = vcProfileQueryService.findByTransactionId(transaction.getId());

        if (!reqVc.getProfile().getId().equals(vcProfile.getProfileId())) {
            transactionService.updateTransactionStatus(transaction, TransactionStatus.FAILED);
            throw new OpenDidException(ErrorCode.VC_PROFILE_INVALID);
        }

        if (!reqVc.getProfile().getIssuerNonce().equals(vcProfile.getNonce())) {
            transactionService.updateTransactionStatus(transaction, TransactionStatus.FAILED);
            throw new OpenDidException(ErrorCode.VC_PROFILE_ISSUER_NONCE_INVALID);
        }
    }
    /**
     * Issues a Verifiable Credential using the provided VcManager.
     *
     * @param vcManager The VcManager to use for issuing the VC.
     * @param holderDid The DID of the credential holder.
     * @param data The data to include in the credential.
     * @return The issued VerifiableCredential.
     * @throws OpenDidException if there's an error in the VC issuance process.
     */
    private VerifiableCredential issueVerifiableCredential(VcManager vcManager, String holderDid, String data) {
        try {
            IssueVcParam issueVcParam = new IssueVcParam();

            DidDocument didDocument = getDidDocument();

            BaseCoreVcUtil.setVcSchema(issueVcParam, getVcSchema());
            BaseCoreVcUtil.setClaimInfo(issueVcParam, generateClaimInfo(data));
            BaseCoreVcUtil.setIssuer(issueVcParam, didDocument.getId(), issueProperty.getName());
            BaseCoreVcUtil.setVcTypes(issueVcParam, getVcType());
            BaseCoreVcUtil.setEvidence(issueVcParam, getEvidence());
            // TODO Valid until
            BaseCoreVcUtil.setValidateUntil(issueVcParam, 1);

            VerifiableCredential verifiableCredential = vcManager.issueCredential(issueVcParam, holderDid);
            List<SignatureVcParams> signatureParams = vcManager.getOriginDataForSign(issueProperty.getAssertSignKeyId(), didDocument, verifiableCredential);
            signVc(signatureParams);

            verifiableCredential = vcManager.addProof(verifiableCredential, signatureParams);

            return verifiableCredential;
        } catch (CoreException e) {
            log.error("Failed to issue Verifiable Credential: {}", e.getMessage());
            throw new OpenDidException(ErrorCode.VC_ISSUE_FAILED);
        }
    }

    /**
     * Revokes a Verifiable Credential.
     *
     * @param vc The VC to revoke.
     */
    private void revokeVc(Vc vc) {
        VcMeta vcMetByVcId = storageService.getVcMetByVcId(vc.getVcId());
        if (!VcStatus.REVOKED.getRawValue().equals(vcMetByVcId.getStatus())) {
            storageService.updateVcStatus(vc.getVcId(), VcStatus.REVOKED);
        }
    }

    /**
     * Generates Ecc key pair.
     *
     * @param curve The curve to use for generating the key pair.
     * @return The generated key pair.
     */
    private EcKeyPair generateEcKeyPair(String curve) {
        return (EcKeyPair) generateKeyPair(curve);
    }

    /**
     * Generates a key pair.
     *
     * @param curve The curve to use for generating the key pair.
     * @return The generated key pair.
     */
    private KeyPairInterface generateKeyPair(String curve) {
        EccCurveType eccCurveType = EccCurveType.fromValue(curve);
        return BaseCryptoUtil.generateKeyPair(eccCurveType);
    }

    /**
     * Signs a Verifiable Credential profile.
     *
     * @param profile The profile to sign.
     * @param keyId The ID of the key to use for signing.
     */
    private void signProfile(IssueProfile profile, String keyId) {
        Proof proof = new Proof();
        proof.setType(ProofType.SECP256R1_SIGNATURE_2018.getRawValue());
        proof.setProofPurpose(ProofPurpose.ASSERTION_METHOD.getRawValue());
        proof.setCreated(DateTimeUtil.getCurrentUTCTimeString());
        proof.setVerificationMethod(issueProperty.getDid() + "#" + issueProperty.getAssertSignKeyId());
        // TODO: Verification method TAS 참고
        profile.setProof(proof);

        String source = JsonUtil.serializeAndSort(profile);
        byte[] bytes = BaseWalletUtil.generateCompactSignature(walletManagerInterface, keyId, source);

        proof.setProofValue(BaseMultibaseUtil.encode(bytes));
    }
    private List<VcType> getVcType() {
        return List.of(VcType.VERIFIABLE_CREDENTIAL);
    }

    /**
     * Generates a DID Document.
     *
     * @return The generated DID Document.
     */
    private DidDocument getDidDocument() {
        DidDocument didDoc = new DidDocument();
        didDoc.fromJson("""
                {"@context":["https://www.w3.org/ns/did/v1"],"assertionMethod":["assert"],"authentication":["auth"],"controller":"did:omn:tas","created":"2024-07-12T08:35:16Z","deactivated":false,"id":"did:omn:issuer","keyAgreement":["keyagree"],"proofs":[{"created":"2024-07-12T08:35:17Z","proofPurpose":"assertionMethod","proofValue":"signatureValue..1","type":"Secp256r1Signature2018","verificationMethod":"did:omn:issuer?versionId=1#assert"},{"created":"2024-07-12T08:35:17Z","proofPurpose":"authentication","proofValue":"signatureValue..2","type":"Secp256r1Signature2018","verificationMethod":"did:omn:issuer?versionId=1#auth"}],"service":[{"id":"serviceID-1","serviceEndpoint":["https://did.omnione.net"],"type":"LinkedDomains"},{"id":"serviceID-2","serviceEndpoint":["https://did.omnione.net/ld/certificate/1234"],"type":"LinkedDomains"}],"updated":"2024-07-12T08:35:17Z","verificationMethod":[{"authType":1,"controller":"did:omn:issuer","id":"assert","publicKeyMultibase":"zvXsXFNahfw9Cz4KQEdLjBtoUEUiVHoMxWs23j6axNuTP","type":"Secp256r1VerificationKey2018"},{"authType":1,"controller":"did:omn:issuer","id":"auth","publicKeyMultibase":"z21Fy2h5uqmhw8xSVxBXNtBbVVjPTuKMam8ebz2FR7CD62","type":"Secp256r1VerificationKey2018"},{"authType":1,"controller":"did:omn:issuer","id":"keyagree","publicKeyMultibase":"znByKCSPznGAKc48CF7i7BhWuhEnz2U7sU4m5TxTrJVEf","type":"Secp256r1VerificationKey2018"}],"versionId":"1"}
                """);

        return didDoc;
    }

    /**
     * Generates a DocumentVerificationEvidence object.
     *
     * @return The generated DocumentVerificationEvidence object.
     */
    private DocumentVerificationEvidence getEvidence() {
        DocumentVerificationEvidence evidence = new DocumentVerificationEvidence();
        evidence.fromJson("""
                {
                    "evidenceDocument":"BusinessLicense",
                    "subjectPresence":"Physical",
                    "documentPresence":"Physical",
                    "type":"DocumentVerification",
                    "verifier":"did:omn:tas"
                }
                """);

        return evidence;
    }

    /**
     * Signs a Verifiable Credential.
     *
     * @param signatureParams The signature parameters to use for signing the VC.
     */
    private void signVc(List<SignatureVcParams> signatureParams) {
        for (SignatureParams signatureParam : signatureParams) {
            String originData = signatureParam.getOriginData();

            byte[] sign = BaseWalletUtil.generateCompactSignature(walletManagerInterface, signatureParam.getKeyId(), originData);
            String signatureValue = BaseMultibaseUtil.encode(sign);

            signatureParam.setSignatureValue(signatureValue);
        }
    }

    /**
     * Finds a user by their VcProfile.
     *
     * @param vcProfile The VcProfile to use for finding the user.
     * @return The found User.
     */
    protected abstract User findUserByVcProfile(VcProfile vcProfile);
    /**
     * Finds a user by their Holder information.
     *
     * @param holder The Holder information to use for finding the user.
     * @return The found User.
     */
    protected abstract User findUserByHolder(Holder holder);
    /**
     * Generates claim information for a Verifiable Credential.
     *
     * @param data The data to use for generating claim information.
     * @return A HashMap containing the generated claim information.
     */
    protected abstract HashMap<String, ClaimInfo> generateClaimInfo(String data);
    /**
     * Gets the VC schema to use for issuing Verifiable Credentials.
     *
     * @return The VC schema as a String.
     */
    protected abstract String getVcSchema();

}

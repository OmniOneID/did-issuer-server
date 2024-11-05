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
import org.omnione.did.base.datamodel.data.ReqRevokeVc;
import org.omnione.did.base.datamodel.data.RequestProof;
import org.omnione.did.base.datamodel.enums.VerifyAuthType;
import org.omnione.did.base.db.constant.SubTransactionStatus;
import org.omnione.did.base.db.constant.SubTransactionType;
import org.omnione.did.base.db.constant.TransactionStatus;
import org.omnione.did.base.db.constant.TransactionType;
import org.omnione.did.base.db.domain.RevokeVc;
import org.omnione.did.base.db.domain.SubTransaction;
import org.omnione.did.base.db.domain.Transaction;
import org.omnione.did.base.exception.ErrorCode;
import org.omnione.did.base.exception.OpenDidException;
import org.omnione.did.base.property.IssueProperty;
import org.omnione.did.base.util.BaseCryptoUtil;
import org.omnione.did.base.util.BaseMultibaseUtil;
import org.omnione.did.base.util.RandomUtil;
import org.omnione.did.base.util.ValidationUtil;
import org.omnione.did.data.model.did.DidDocument;
import org.omnione.did.data.model.did.Proof;
import org.omnione.did.data.model.enums.vc.VcStatus;
import org.omnione.did.data.model.vc.VcMeta;
import org.omnione.did.issuer.v1.dto.vc.*;
import org.omnione.did.issuer.v1.service.query.RevokeVcQueryService;
import org.omnione.did.issuer.v1.service.query.TransactionService;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.Arrays;
import java.util.Objects;

/**
 * Implementation of the VcStatusService interface.
 * This service manages the lifecycle and status of Verifiable Credentials (VCs),
 * including inspect propose revocation vc, revocation vc, and status updates.
 */
@RequiredArgsConstructor
@Service
@Profile("!sample")
public class VcStatusServiceImpl implements VcStatusService {
    private final StorageService storageService;
    private final TransactionService transactionService;
    private final IssueProperty issueProperty;
    private final RevokeVcQueryService revokeVcQueryService;
    /**
     * Inspects a propose revoke request for a Verifiable Credential.
     *
     * @param request The inspection request containing the VC ID.
     * @return InspectProposeRevokeResDto containing transaction details.
     * @throws OpenDidException if the VC is not found or already revoked.
     * @throws OpenDidException if the VC has already been revoked.
     *
     */
    @Override
    public InspectProposeRevokeResDto inspectProposeRevoke(InspectProposeRevokeReqDto request) {
        try {
            VcMeta vcMeta = storageService.getVcMetByVcId(request.getVcId());
            if (Objects.isNull(vcMeta)) {
                throw new OpenDidException(ErrorCode.VC_NOT_FOUND);
            }
            if (isRevoked(vcMeta)) {
                throw new OpenDidException(ErrorCode.REVOKED_VC);
            }

            String txId = RandomUtil.generateUUID();
            String issuerNonce = BaseCryptoUtil.generateNonceWithMultibase(16);
            VerifyAuthType verifyAuthType = issueProperty.getRevokeVerifyAuthType();

            Transaction transaction = transactionService.insertTransaction(Transaction.builder()
                    .txId(txId)
                    .expiredAt(Instant.now().plusSeconds(3600L))
                    .type(TransactionType.REVOKE_VC)
                    .status(TransactionStatus.COMPLETED)
                    .build());

            transactionService.insertSubTransaction(SubTransaction.builder()
                    .transactionId(transaction.getId())
                    .step(1)
                    .type(SubTransactionType.INSPECT_REVOKE_PROPOSE)
                    .status(SubTransactionStatus.COMPLETED)
                    .build());

            RevokeVc revokeVc = revokeVcQueryService.findByVcId(request.getVcId());
            revokeVcQueryService.save(RevokeVc.builder()
                    .id(revokeVc.getId())
                    .nonce(issuerNonce)
                    .vcId(request.getVcId())
                    .transactionId(transaction.getId())
                    .build());

            return InspectProposeRevokeResDto.builder()
                    .txId(txId)
                    .issuerNonce(issuerNonce)
                    .authType(verifyAuthType)
                    .build();
        } catch (OpenDidException e) {
            throw e;
        } catch (Exception e) {
            throw new OpenDidException(ErrorCode.TR_VC_REVOKE_PROPOSE_FAILED);
        }
    }
    /**
     * Revokes a Verifiable Credential based on the provided request.
     *
     * @param request The revocation request containing transaction ID and revocation details.
     * @return RevokeVcResDto containing the transaction ID of the revoked VC.
     * @throws OpenDidException if the transaction is invalid or the request is not valid.
     */
    @Override
    public RevokeVcResDto revokeVc(RevokeVcReqDto request) {
        try {
            Transaction transaction = transactionService.validateAndFindTransaction(request.getTxId(),
                    SubTransactionType.INSPECT_REVOKE_PROPOSE);

            RevokeVc revokeVc = revokeVcQueryService.findByTransactionId(transaction.getId());
            isValidRequest(revokeVc, request.getRequest());
            verifySign(request.getRequest());
            // TODO: Compare Holder DID

            storageService.updateVcStatus(revokeVc.getVcId(), VcStatus.REVOKED);

            revokeVc.setStatus(VcStatus.REVOKED);
            revokeVcQueryService.save(revokeVc);

            transactionService.insertSubTransaction(SubTransaction.builder()
                    .transactionId(transaction.getId())
                    .step(3)
                    .type(SubTransactionType.REVOKE_VC)
                    .status(SubTransactionStatus.COMPLETED)
                    .build());
            return RevokeVcResDto.builder()
                    .txId(transaction.getTxId())
                    .build();
        } catch (OpenDidException e) {
            throw e;
        } catch (Exception e) {
            throw new OpenDidException(ErrorCode.TR_VC_REVOKE_FAILED);
        }
    }
    /**
     * Updates the status of a Verifiable Credential.
     *
     * @param request The update request containing the VC ID and new status.
     * @return UpdateVcStatusResDto indicating the success of the operation.
     * @throws OpenDidException if the VC is not found or already revoked.
     * @throws OpenDidException if the VC has already been revoked.
     */
    @Override
    public UpdateVcStatusResDto updateVcStatus(UpdateVcStatusReqDto request) {
        try {
            VcMeta vcMeta = storageService.getVcMetByVcId(request.getVcId());
            if (Objects.isNull(vcMeta)) {
                throw new OpenDidException(ErrorCode.VC_NOT_FOUND);
            }
            if (isRevoked(vcMeta)) {
                throw new OpenDidException(ErrorCode.REVOKED_VC);
            }

            storageService.updateVcStatus(request.getVcId(), request.getVcStatus());

            return UpdateVcStatusResDto.builder()
                    .build();
        } catch (OpenDidException e) {
            throw e;
        } catch (Exception e) {
            throw new OpenDidException(ErrorCode.TR_VC_UPDATE_STATUS_FAILED);
        }
    }
    /**
     * Completes the revocation process for a Verifiable Credential.
     *
     * @param request The completion request containing the transaction ID.
     * @return CompleteRevokeResDto containing the transaction ID of the completed revocation.
     * @throws OpenDidException if the transaction is invalid.
     */
    @Override
    public CompleteRevokeResDto completeRevoke(CompleteRevokeReqDto request) {
        try {
            Transaction transaction = transactionService.validateAndFindTransaction(request.getTxId(), SubTransactionType.REVOKE_VC);

            transactionService.insertSubTransaction(SubTransaction.builder()
                    .transactionId(transaction.getId())
                    .step(3)
                    .type(SubTransactionType.COMPLETE_REVOKE)
                    .status(SubTransactionStatus.COMPLETED)
                    .build());

            return CompleteRevokeResDto.builder()
                    .txId(transaction.getTxId())
                    .build();
        } catch (OpenDidException e) {
            throw e;
        } catch (Exception e) {
            throw new OpenDidException(ErrorCode.TR_VC_REVOKE_COMPLETE_FAILED);
        }
    }
    /**
     * Verifies the signature of a request proof.
     *
     * @param request The request proof to verify.
     * @throws OpenDidException if the signature verification fails or JSON serialization fails.
     */
    private void verifySign(RequestProof request) {
        Proof proof = request.getProof();
        String verificationMethod = proof.getVerificationMethod();
        DidDocument holderDidDoc = storageService.findDidDoc(verificationMethod);

        ValidationUtil.verifySign(request, holderDidDoc);
    }
    /**
     * Validates the revocation request against the stored revocation data.
     *
     * @param revokeVc The stored revocation data.
     * @param reqRevokeVc The revocation request to validate.
     * @throws OpenDidException if the VC ID or nonce doesn't match.
     */
    private void isValidRequest(RevokeVc revokeVc, ReqRevokeVc reqRevokeVc) {
        if (!revokeVc.getVcId().equals(reqRevokeVc.getVcId())) {
            throw new OpenDidException(ErrorCode.VC_ID_NOT_MATCH);
        }
        byte[] revokeVcIssuerNonce = BaseMultibaseUtil.decode(revokeVc.getNonce());
        byte[] requestIssuerNonce = BaseMultibaseUtil.decode(reqRevokeVc.getIssuerNonce());

        if (!Arrays.equals(revokeVcIssuerNonce, requestIssuerNonce)) {
            throw new OpenDidException(ErrorCode.ISSUER_NONCE_INVALID);
        }
    }

    /**
     * Checks if a Verifiable Credential is already revoked.
     *
     * @param vcMeta The metadata of the Verifiable Credential.
     * @return true if the VC is revoked, false otherwise.
     */
    private boolean isRevoked(VcMeta vcMeta) {
        return VcStatus.REVOKED.getRawValue().equals(vcMeta.getStatus());
    }
}

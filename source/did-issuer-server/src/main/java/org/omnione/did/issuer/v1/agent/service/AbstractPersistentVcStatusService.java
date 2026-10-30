package org.omnione.did.issuer.v1.agent.service;

import lombok.extern.slf4j.Slf4j;
import org.omnione.did.issuer.v1.agent.service.provider.SpringVcStatusDataProvider;
import org.omnione.did.issuer.v1.agent.service.query.TransactionService;
import org.omnione.did.sdk.issuer.exception.IssuerSdkErrorCode;
import org.omnione.did.sdk.issuer.exception.IssuerSdkException;
import org.omnione.did.sdk.issuer.model.common.enums.SubTransactionStatus;
import org.omnione.did.sdk.issuer.model.common.enums.SubTransactionType;
import org.omnione.did.sdk.issuer.model.common.enums.TransactionStatus;
import org.omnione.did.sdk.issuer.model.common.enums.TransactionType;
import org.omnione.did.sdk.issuer.model.domain.SdkRevokeVc;
import org.omnione.did.sdk.issuer.model.domain.SdkTransaction;
import org.omnione.did.base.db.domain.SubTransaction;
import org.omnione.did.base.db.domain.Transaction;
import org.omnione.did.sdk.issuer.model.dto.*;
import org.omnione.did.sdk.issuer.protocol.vc.VcStatusProtocol;

import java.time.Instant;

@Slf4j
public abstract class AbstractPersistentVcStatusService implements VcStatusService {

    protected final VcStatusProtocol vcStatusProtocol;
    protected final TransactionService transactionService;
    protected final SpringVcStatusDataProvider springVcStatusDataProvider;

    public AbstractPersistentVcStatusService(VcStatusProtocol vcStatusProtocol,
                                             SpringVcStatusDataProvider vcStatusDataProvider,
                                             TransactionService transactionService) {
        this.vcStatusProtocol = vcStatusProtocol;
        this.transactionService = transactionService;
        this.springVcStatusDataProvider = vcStatusDataProvider;
    }

    @Override
    public InspectProposeRevokeResDto inspectProposeRevoke(InspectProposeRevokeReqDto request) {
        try {
            log.debug("=== Server: Starting Inspect Propose Revoke ===");
            Transaction savedTx = transactionService.insertTransaction(Transaction.builder()
                    .txId(java.util.UUID.randomUUID().toString())
                    .expiredAt(Instant.now().plusSeconds(springVcStatusDataProvider.getTransactionExpirationTime()))
                    .type(TransactionType.REVOKE_VC)
                    .status(TransactionStatus.COMPLETED)
                    .build());

            SdkTransaction newTransaction = SdkTransaction.builder()
                    .id(savedTx.getId())
                    .txId(savedTx.getTxId())
                    .expiredAt(savedTx.getExpiredAt())
                    .type(savedTx.getType())
                    .status(savedTx.getStatus())
                    .build();

            InspectProposeRevokeResDto response = vcStatusProtocol.inspectProposeRevoke(request, newTransaction);

            SdkTransaction transaction = newTransaction;

            transactionService.insertSubTransaction(SubTransaction.builder()
                    .transactionId(transaction.getId())
                    .step(1)
                    .type(SubTransactionType.INSPECT_REVOKE_PROPOSE)
                    .status(SubTransactionStatus.COMPLETED)
                    .build());

            SdkRevokeVc revokeVc = springVcStatusDataProvider.findRevokeVcByVcId(request.getVcId());
            if (revokeVc != null) {
                revokeVc.setTransactionId(transaction.getId());
                springVcStatusDataProvider.saveRevokeVc(revokeVc);
            }

            return response;
        } catch (IssuerSdkException e) {
            log.error("SdkException occurred during inspectProposeRevoke: {}", e.getErrorCode().getMessage());
            throw e;
        } catch (Exception e) {
            log.error("Exception occurred during inspectProposeRevoke: {}", e.getMessage(), e);
            throw new IssuerSdkException(IssuerSdkErrorCode.TR_VC_REVOKE_PROPOSE_FAILED);
        }
    }

    @Override
    public RevokeVcResDto revokeVc(RevokeVcReqDto request) {
        try {
            log.debug("=== Server: Starting Revoke VC ===");
            SdkTransaction transaction = springVcStatusDataProvider.validateAndFindTransaction(request.getTxId(),
                    SubTransactionType.INSPECT_REVOKE_PROPOSE);

            // Execute logic in SDK
            RevokeVcResDto response = vcStatusProtocol.revokeVc(request, transaction);

            transactionService.insertSubTransaction(SubTransaction.builder()
                    .transactionId(transaction.getId())
                    .step(3)
                    .type(SubTransactionType.REVOKE_VC)
                    .status(SubTransactionStatus.COMPLETED)
                    .build());

            return response;
        } catch (IssuerSdkException e) {
            log.error("SdkException occurred during revokeVc: {}", e.getErrorCode().getMessage());
            throw e;
        } catch (Exception e) {
            log.error("Exception occurred during revokeVc: {}", e.getMessage(), e);
            throw new IssuerSdkException(IssuerSdkErrorCode.TR_VC_REVOKE_FAILED);
        }
    }

    @Override
    public CompleteRevokeResDto completeRevoke(CompleteRevokeReqDto request) {
        try {
            log.debug("=== Server: Starting Complete Revoke ===");
            SdkTransaction transaction = springVcStatusDataProvider.validateAndFindTransaction(request.getTxId(),
                    SubTransactionType.REVOKE_VC);

            // Execute SDK complete code
            CompleteRevokeResDto response = vcStatusProtocol.completeRevoke(request, transaction);

            transactionService.insertSubTransaction(SubTransaction.builder()
                    .transactionId(transaction.getId())
                    .step(3)
                    .type(SubTransactionType.COMPLETE_REVOKE)
                    .status(SubTransactionStatus.COMPLETED)
                    .build());

            return response;
        } catch (IssuerSdkException e) {
            log.error("SdkException occurred during completeRevoke: {}", e.getErrorCode().getMessage());
            throw e;
        } catch (Exception e) {
            log.error("Exception occurred during completeRevoke: {}", e.getMessage(), e);
            throw new IssuerSdkException(IssuerSdkErrorCode.TR_VC_REVOKE_COMPLETE_FAILED);
        }
    }

    @Override
    public UpdateVcStatusResDto updateVcStatus(UpdateVcStatusReqDto request) {
        return vcStatusProtocol.updateVcStatus(request);
    }
}

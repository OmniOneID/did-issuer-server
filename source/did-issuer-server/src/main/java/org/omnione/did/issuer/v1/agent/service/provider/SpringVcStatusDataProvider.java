package org.omnione.did.issuer.v1.agent.service.provider;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.omnione.did.base.db.domain.RevokeVc;
import org.omnione.did.base.db.domain.SubTransaction;
import org.omnione.did.base.db.domain.Transaction;
import org.omnione.did.base.db.domain.Vc;
import org.omnione.did.data.model.enums.vc.VcStatus;
import org.omnione.did.issuer.v1.admin.service.query.ApplicationConfigQueryService;
import org.omnione.did.issuer.v1.agent.service.query.RevokeVcQueryService;
import org.omnione.did.issuer.v1.agent.service.query.TransactionService;
import org.omnione.did.issuer.v1.agent.service.query.VcQueryService;
import org.omnione.did.issuer.v1.common.service.ServerConfigService;
import org.omnione.did.sdk.issuer.model.common.enums.SubTransactionType;
import org.omnione.did.sdk.issuer.model.common.enums.TransactionStatus;
import org.omnione.did.sdk.issuer.model.domain.SdkRevokeVc;
import org.omnione.did.sdk.issuer.model.domain.SdkSubTransaction;
import org.omnione.did.sdk.issuer.model.domain.SdkTransaction;
import org.omnione.did.sdk.issuer.model.domain.SdkVc;
import org.omnione.did.sdk.issuer.model.enums.VerifyAuthType;
import org.omnione.did.sdk.issuer.provider.VcStatusDataProvider;
import org.springframework.stereotype.Component;

@Slf4j
@RequiredArgsConstructor
@Component
public class SpringVcStatusDataProvider implements VcStatusDataProvider {

    private final RevokeVcQueryService revokeVcQueryService;
    private final TransactionService transactionService;
    private final VcQueryService vcQueryService;
    private final ApplicationConfigQueryService applicationConfigQueryService;
    private final ServerConfigService serverConfigService;

    @Override
    public SdkRevokeVc findRevokeVcByVcId(String vcId) {
        RevokeVc revokeVc = revokeVcQueryService.findByVcId(vcId);
        return toSdk(revokeVc);
    }

    @Override
    public SdkRevokeVc saveRevokeVc(SdkRevokeVc sdkRevokeVc) {
        RevokeVc revokeVc = toEntity(sdkRevokeVc);
        RevokeVc saved = revokeVcQueryService.save(revokeVc);
        return toSdk(saved);
    }

    @Override
    public SdkRevokeVc findRevokeVcByTransactionId(Long transactionId) {
        RevokeVc revokeVc = revokeVcQueryService.findByTransactionId(transactionId);
        return toSdk(revokeVc);
    }

    @Override
    public SdkTransaction validateAndFindTransaction(String txId, SubTransactionType type) {
        Transaction transaction = transactionService.validateAndFindTransaction(txId, type);
        return toSdk(transaction);
    }

    @Override
    public void updateVcStatus(String vcId, VcStatus status) {
        Vc vc = vcQueryService.findByVcId(vcId);
        if (vc != null) {
            vc.setStatus(status.getRawValue());
            vcQueryService.save(vc);
        }
    }

    @Override
    public SdkVc findVcByVcId(String vcId) {
        Vc vc = vcQueryService.findByVcId(vcId);
        return toSdk(vc);
    }

    @Override
    public void saveVc(SdkVc sdkVc) {
        Vc vc = toEntity(sdkVc);
        vcQueryService.save(vc);
    }

    @Override
    public VerifyAuthType getRevokeAuthType() {
        return VerifyAuthType.valueOf(applicationConfigQueryService.getApplicationConfig().getRevokeAuthType().name());
    }

    @Override
    public long getTransactionExpirationTime() {
        return serverConfigService.findTransactionExpirationTime();
    }

    // Mapping Methods

    private SdkRevokeVc toSdk(RevokeVc entity) {
        if (entity == null)
            return new SdkRevokeVc();
        return SdkRevokeVc.builder()
                .id(entity.getId())
                .nonce(entity.getNonce())
                .vcId(entity.getVcId())
                .status(entity.getStatus())
                .transactionId(entity.getTransactionId())
                .build();
    }

    private RevokeVc toEntity(SdkRevokeVc sdk) {
        if (sdk == null)
            return null;
        return RevokeVc.builder()
                .id(sdk.getId())
                .nonce(sdk.getNonce())
                .vcId(sdk.getVcId())
                .status(sdk.getStatus())
                .transactionId(sdk.getTransactionId())
                .build();
    }

    private SdkTransaction toSdk(Transaction entity) {
        if (entity == null)
            return null;
        return SdkTransaction.builder()
                .id(entity.getId())
                .txId(entity.getTxId())
                .refId(entity.getRefId())
                .issueProfileId(entity.getIssueProfileId())
                .vcPlanId(entity.getVcPlanId())
                .type(entity.getType())
                .status(entity.getStatus())
                .expiredAt(entity.getExpiredAt())
                .build();
    }

    private Transaction toEntity(SdkTransaction sdk) {
        if (sdk == null)
            return null;
        return Transaction.builder()
                .id(sdk.getId())
                .txId(sdk.getTxId())
                .refId(sdk.getRefId())
                .issueProfileId(sdk.getIssueProfileId())
                .vcPlanId(sdk.getVcPlanId())
                .type(sdk.getType())
                .status(sdk.getStatus())
                .expiredAt(sdk.getExpiredAt())
                .build();
    }

    private SdkSubTransaction toSdk(SubTransaction entity) {
        if (entity == null)
            return null;
        return SdkSubTransaction.builder()
                .id(entity.getId())
                .transactionId(entity.getTransactionId())
                .step(entity.getStep())
                .type(entity.getType())
                .status(entity.getStatus())
                .build();
    }

    private SubTransaction toEntity(SdkSubTransaction sdk) {
        if (sdk == null)
            return null;
        return SubTransaction.builder()
                .id(sdk.getId())
                .transactionId(sdk.getTransactionId())
                .step(sdk.getStep())
                .type(sdk.getType())
                .status(sdk.getStatus())
                .build();
    }

    private SdkVc toSdk(Vc entity) {
        if (entity == null)
            return null;
        return SdkVc.builder()
                .id(entity.getId())
                .vcId(entity.getVcId())
                .userId(entity.getUserId())
                .vcPlanId(entity.getVcPlanId())
                .vcSchemaId(entity.getVcSchemaId())
                .vcType(entity.getVcType())
                .txId(entity.getTxId())
                .status(entity.getStatus())
                .did(entity.getDid())
                .issuedAt(entity.getIssuedAt())
                .expiredAt(entity.getExpiredAt())
                .build();
    }

    private Vc toEntity(SdkVc sdk) {
        if (sdk == null)
            return null;
        return Vc.builder()
                .id(sdk.getId())
                .vcId(sdk.getVcId())
                .userId(sdk.getUserId())
                .vcPlanId(sdk.getVcPlanId())
                .vcSchemaId(sdk.getVcSchemaId())
                .vcType(sdk.getVcType())
                .txId(sdk.getTxId())
                .status(sdk.getStatus())
                .did(sdk.getDid())
                .issuedAt(sdk.getIssuedAt())
                .expiredAt(sdk.getExpiredAt())
                .build();
    }
}

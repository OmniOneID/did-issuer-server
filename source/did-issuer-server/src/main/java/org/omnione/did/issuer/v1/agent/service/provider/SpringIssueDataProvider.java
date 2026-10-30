package org.omnione.did.issuer.v1.agent.service.provider;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.google.gson.reflect.TypeToken;
import org.omnione.did.common.vo.HeaderEntry;
import lombok.RequiredArgsConstructor;
import org.omnione.did.sdk.issuer.model.enums.InitiateType;
import org.omnione.did.sdk.issuer.model.enums.UserQueryType;
import org.omnione.did.sdk.issuer.model.enums.VcIssuanceMode;
import org.omnione.did.sdk.issuer.model.common.enums.SubTransactionType;
import org.omnione.did.sdk.issuer.model.common.enums.TransactionStatus;
import org.omnione.did.base.db.domain.*;
import org.omnione.did.issuer.v1.admin.service.query.IssueProfileQueryService;
import org.omnione.did.issuer.v1.admin.service.query.ZkpCredentialDefinitionQueryService;
import org.omnione.did.issuer.v1.admin.service.query.ZkpSchemaQueryService;
import org.omnione.did.issuer.v1.agent.service.query.*;
import org.omnione.did.issuer.v1.common.service.ServerConfigService;
import org.omnione.did.sdk.issuer.model.domain.*;
import org.omnione.did.sdk.issuer.provider.IssueDataProvider;
import org.springframework.stereotype.Component;

import java.util.Optional;
import java.util.List;
import java.util.ArrayList;

@Component
@RequiredArgsConstructor
public class SpringIssueDataProvider implements IssueDataProvider {

    private final VcOfferQueryService vcOfferQueryService;
    private final TransactionService transactionService;
    private final VcProfileQueryService vcProfileQueryService;
    private final E2EQueryService e2EQueryService;
    private final VcQueryService vcQueryService;
    private final IssueProfileQueryService issueProfileQueryService;
    private final IssuerInfoQueryService issuerInfoQueryService;
    private final ZkpCredentialDefinitionQueryService zkpCredentialDefinitionQueryService;
    private final ZkpSchemaQueryService zkpSchemaQueryService;
    private final VcSchemaService vcSchemaService;
    private final ServerConfigService serverConfigService;

    @Override
    public void saveVcOffer(SdkVcOffer sdkVcOffer) {
        VcOffer vcOffer = toEntity(sdkVcOffer);
        vcOfferQueryService.save(vcOffer);
    }

    @Override
    public SdkVcOffer findVcOfferByOfferId(String offerId) {
        VcOffer vcOffer = vcOfferQueryService.findByOfferId(offerId);
        return toSdk(vcOffer);
    }

    @Override
    public SdkTransaction validateAndFindTransaction(String txId, SubTransactionType type) {
        Transaction transaction = transactionService.validateAndFindTransaction(txId, type);
        return toSdk(transaction);
    }

    @Override
    public SdkTransaction findTransactionById(Long id) {
        Transaction transaction = transactionService.findById(id);
        return toSdk(transaction);
    }

    @Override
    public SdkSubTransaction findSubTransactionByTransactionIdOrderByStepDesc(Long transactionId) {
        SubTransaction subTransaction = transactionService.findByTransactionIdOrderByStepDesc(transactionId);
        return toSdk(subTransaction);
    }

    @Override
    public long getTransactionExpirationTime() {
        return serverConfigService.findTransactionExpirationTime();
    }

    @Override
    public SdkIssueProfile findIssueProfileByVcPlanId(String vcPlanId) {
        IssueProfile issueProfile = issueProfileQueryService.findByVcPlanId(vcPlanId);
        return toSdk(issueProfile);
    }

    @Override
    public SdkIssueProfile findIssueProfileById(Long id) {
        IssueProfile issueProfile = issueProfileQueryService.findById(id);
        return toSdk(issueProfile);
    }

    @Override
    public SdkIssuerInfo getIssuerInfo() {
        IssuerInfo issuerInfo = issuerInfoQueryService.getIssuerInfo();
        return toSdk(issuerInfo);
    }

    @Override
    public void saveVcProfile(SdkVcProfile sdkVcProfile) {
        VcProfile vcProfile = toEntity(sdkVcProfile);
        vcProfileQueryService.save(vcProfile);
    }

    @Override
    public SdkVcProfile findVcProfileByTransactionId(Long transactionId) {
        VcProfile vcProfile = vcProfileQueryService.findByTransactionId(transactionId);
        return toSdk(vcProfile);
    }

    @Override
    public void saveE2E(SdkE2E sdkE2E) {
        E2E e2E = toEntity(sdkE2E);
        e2EQueryService.save(e2E);
    }

    @Override
    public SdkE2E findE2EByTransactionId(Long transactionId) {
        E2E e2E = e2EQueryService.findByTransactionId(transactionId);
        return toSdk(e2E);
    }

    @Override
    public void saveVc(SdkVc sdkVc) {
        Vc vc = toEntity(sdkVc);
        vcQueryService.save(vc);
    }

    @Override
    public SdkVc findVcByTxId(String txId) {
        Vc vc = vcQueryService.findByTxId(txId);
        return toSdk(vc);
    }

    @Override
    public Optional<SdkVc> findVcByUserIdAndVcPlanId(Long userId, String vcPlanId) {
        return vcQueryService.findByUserIdAndVcPlanId(userId, vcPlanId)
                .map(this::toSdk);
    }

    @Override
    public SdkZkpCredentialDefinition findZkpCredentialDefinitionByDefinitionId(String definitionId) {
        ZkpCredentialDefinition definition = zkpCredentialDefinitionQueryService.findByDefinitionId(definitionId);
        return toSdk(definition);
    }

    @Override
    public SdkZkpSchema findZkpSchemaBySchemaId(String schemaId) {
        ZkpSchema schema = zkpSchemaQueryService.findBySchemaId(schemaId);
        return toSdk(schema);
    }

    @Override
    public org.omnione.did.data.model.schema.VcSchema getVcSchemaById(Long id) {
        return vcSchemaService.getVcSchemaById(id);
    }

    // Mapping Methods

    private SdkVcOffer toSdk(VcOffer entity) {
        if (entity == null)
            return null;
        return SdkVcOffer.builder()
                .id(entity.getId())
                .did(entity.getDid())
                .offerId(entity.getOfferId())
                .transactionId(entity.getTransactionId())
                .vcPlanId(entity.getVcPlanId())
                .offerType(entity.getOfferType())
                .validUntil(entity.getValidUntil())
                .build();
    }

    private VcOffer toEntity(SdkVcOffer sdk) {
        if (sdk == null)
            return null;
        return VcOffer.builder()
                .id(sdk.getId())
                .did(sdk.getDid())
                .offerId(sdk.getOfferId())
                .transactionId(sdk.getTransactionId())
                .vcPlanId(sdk.getVcPlanId())
                .offerType(sdk.getOfferType())
                .validUntil(sdk.getValidUntil())
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

    private SdkIssueProfile toSdk(IssueProfile entity) {
        if (entity == null)
            return null;
        return SdkIssueProfile.builder()
                .id(entity.getId())
                .vcPlanId(entity.getVcPlanId())
                .vcSchemaId(entity.getVcSchemaId())
                .title(entity.getTitle())
                .description(entity.getDescription())
                .language(entity.getLanguage())
                .endpoints(entity.getEndpoints())
                .curve(entity.getCurve())
                .padding(entity.getPadding())
                .cipher(entity.getCipher())
                .definitionId(entity.getDefinitionId())
                .issuanceMode(VcIssuanceMode
                        .valueOf(entity.getIssuanceMode().name()))
                .initiateType(InitiateType
                        .valueOf(entity.getInitiateType().name()))
                .tags(entity.getTags())
                .zkpEnabled(entity.getZkpEnabled())
                .userQueryType(UserQueryType
                        .valueOf(entity.getUserQueryType().name()))
                .userQueryUrl(entity.getUserQueryUrl())
                .userQueryHeaders(parseUserQueryHeaders(entity.getUserQueryHeaders()))
                .build();
    }

    private List<HeaderEntry> parseUserQueryHeaders(String raw) {
        if (raw == null || raw.isBlank()) return null;
        try {
            JsonElement json = JsonParser.parseString(raw);
            if (json.isJsonArray()) {
                return new Gson().fromJson(json, new TypeToken<List<HeaderEntry>>() {}.getType());
            }
            if (json.isJsonObject()) {
                List<HeaderEntry> entries = new ArrayList<>();
                json.getAsJsonObject().entrySet().forEach(entry -> {
                    HeaderEntry header = new HeaderEntry();
                    header.setKey(entry.getKey());
                    header.setValue(entry.getValue().getAsString());
                    entries.add(header);
                });
                return entries;
            }
            raw = json.getAsString();
        } catch (RuntimeException ignored) {
            // Existing profiles may contain a plain header value.
        }
        HeaderEntry header = new HeaderEntry();
        header.setKey("Authorization");
        header.setValue(raw);
        return List.of(header);
    }

    private SdkIssuerInfo toSdk(IssuerInfo entity) {
        if (entity == null)
            return null;
        return SdkIssuerInfo.builder()
                .id(entity.getId())
                .did(entity.getDid())
                .name(entity.getName())
                .status(entity.getStatus())
                .serverUrl(entity.getServerUrl())
                .publishedCertificateUrl(entity.getPublishedCertificateUrl())
                .certificateUrl(entity.getCertificateUrl())
                .build();
    }

    private SdkVcProfile toSdk(VcProfile entity) {
        if (entity == null)
            return null;
        return SdkVcProfile.builder()
                .id(entity.getId())
                .userId(entity.getUserId())
                .profileId(entity.getProfileId())
                .transactionId(entity.getTransactionId())
                .did(entity.getDid())
                .nonce(entity.getNonce())
                .zkpNonce(entity.getZkpNonce())
                .build();
    }

    private VcProfile toEntity(SdkVcProfile sdk) {
        if (sdk == null)
            return null;
        return VcProfile.builder()
                .id(sdk.getId())
                .userId(sdk.getUserId())
                .profileId(sdk.getProfileId())
                .transactionId(sdk.getTransactionId())
                .did(sdk.getDid())
                .nonce(sdk.getNonce())
                .zkpNonce(sdk.getZkpNonce())
                .build();
    }

    private SdkE2E toSdk(E2E entity) {
        if (entity == null)
            return null;
        return SdkE2E.builder()
                .id(entity.getId())
                .transactionId(entity.getTransactionId())
                .curve(entity.getCurve())
                .cipher(entity.getCipher())
                .padding(entity.getPadding())
                .sessionKey(entity.getSessionKey())
                .nonce(entity.getNonce())
                .build();
    }

    private E2E toEntity(SdkE2E sdk) {
        if (sdk == null)
            return null;
        return E2E.builder()
                .id(sdk.getId())
                .transactionId(sdk.getTransactionId())
                .curve(sdk.getCurve())
                .cipher(sdk.getCipher())
                .padding(sdk.getPadding())
                .sessionKey(sdk.getSessionKey())
                .nonce(sdk.getNonce()) // Assuming SdkE2E has nonce now or we just removed iv
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

    private SdkZkpCredentialDefinition toSdk(ZkpCredentialDefinition entity) {
        if (entity == null)
            return null;
        return SdkZkpCredentialDefinition.builder()
                .id(entity.getId())
                .definitionId(entity.getDefinitionId())
                .schemaId(entity.getSchemaId())
                .alias(entity.getAlias())
                .definition(entity.getDefinition())
                .build();
    }

    private SdkZkpSchema toSdk(ZkpSchema entity) {
        if (entity == null)
            return null;
        return SdkZkpSchema.builder()
                .id(entity.getId())
                .schemaId(entity.getSchemaId())
                .schema(entity.getSchema())
                .build();
    }
}

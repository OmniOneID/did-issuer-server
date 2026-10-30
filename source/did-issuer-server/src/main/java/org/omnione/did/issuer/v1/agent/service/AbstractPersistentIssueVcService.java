package org.omnione.did.issuer.v1.agent.service;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import lombok.extern.slf4j.Slf4j;
import org.apache.logging.log4j.util.Strings;
import org.omnione.did.core.manager.DidManager;
import org.omnione.did.data.model.did.DidDocument;
import org.omnione.did.data.model.did.VerificationMethod;
import org.omnione.did.issuer.v1.agent.service.provider.SpringIssueDataProvider;
import org.omnione.did.issuer.v1.agent.service.query.TransactionService;
import org.omnione.did.issuer.v1.common.service.StorageService;
import org.omnione.did.sdk.issuer.exception.IssuerSdkErrorCode;
import org.omnione.did.sdk.issuer.exception.IssuerSdkException;
import org.omnione.did.sdk.issuer.model.common.enums.*;
import org.omnione.did.sdk.issuer.model.data.Holder;
import org.omnione.did.sdk.issuer.model.domain.*;
import org.omnione.did.sdk.issuer.model.dto.*;
import org.omnione.did.sdk.issuer.protocol.vc.IssueVcProtocol;
import org.omnione.did.base.db.domain.SubTransaction;
import org.omnione.did.base.db.domain.Transaction;
import org.omnione.did.common.util.crypto.BaseCryptoUtil;
import org.omnione.did.common.util.digest.BaseDigestUtil;

import java.net.URI;
import java.net.URISyntaxException;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.Base64;

@Slf4j
public abstract class AbstractPersistentIssueVcService implements IssueService {

    protected final IssueVcProtocol issueVcProtocol;
    protected final TransactionService transactionService;
    protected final SpringIssueDataProvider springIssueDataProvider;
    protected final StorageService storageService;

    public AbstractPersistentIssueVcService(IssueVcProtocol issueVcProtocol,
                                            SpringIssueDataProvider issueDataProvider,
                                            TransactionService transactionService,
                                            StorageService storageService) {
        this.issueVcProtocol = issueVcProtocol;
        this.transactionService = transactionService;
        this.springIssueDataProvider = issueDataProvider;
        this.storageService = storageService;
    }

    protected abstract UserInfoDto findUserByVcProfileAndVcSchemaId(SdkVcProfile vcProfile,
                                                                    SdkIssueProfile issueProfile);

    protected abstract SdkUser findUserByHolderAndVcSchemaId(Holder holder, Long vcSchemaId);

    @Override
    public OfferIssueVcResDto requestOffer(OfferIssueVcReqDto request) {
        return issueVcProtocol.requestOffer(request);
    }

    @Override
    public IssueVcResultResDto issueVcResult(String offerId) {
        return issueVcProtocol.issueVcResult(offerId);
    }

    @Override
    public InspectIssueProposeResDto inspectIssuePropose(InspectIssueProposeReqDto request) {
        try {
            log.debug("=== Server: Starting Inspect Issue Propose ===");
            String vcPlanId = request.getVcPlanId();
            SdkIssueProfile issueProfile = springIssueDataProvider.findIssueProfileByVcPlanId(vcPlanId);

            SdkVcOffer vcOffer = null;
            if (request.getOfferId() != null && !request.getOfferId().isBlank()) {
                vcOffer = springIssueDataProvider.findVcOfferByOfferId(request.getOfferId());
            }

            // Execute non-transaction logic in SDK Wrapper
            InspectIssueProposeResDto response = issueVcProtocol.inspectIssuePropose(request);

            String txId = response.getTxId();
            String refId = response.getRefId();

            Transaction savedTx = transactionService.insertTransaction(Transaction.builder()
                    .txId(txId)
                    .refId(refId)
                    .vcPlanId(vcPlanId)
                    .expiredAt(Instant.now().plusSeconds(springIssueDataProvider.getTransactionExpirationTime()))
                    .type(TransactionType.ISSUE_VC)
                    .status(TransactionStatus.COMPLETED)
                    .issueProfileId(issueProfile.getId())
                    .build());

            SdkTransaction sdkTx = SdkTransaction.builder()
                    .id(savedTx.getId())
                    .txId(savedTx.getTxId())
                    .refId(savedTx.getRefId())
                    .vcPlanId(savedTx.getVcPlanId())
                    .expiredAt(savedTx.getExpiredAt())
                    .type(savedTx.getType())
                    .status(savedTx.getStatus())
                    .issueProfileId(savedTx.getIssueProfileId())
                    .build();

            if (vcOffer != null) {
                vcOffer.setTransactionId(sdkTx.getId());
                springIssueDataProvider.saveVcOffer(vcOffer);
            }

            transactionService.insertSubTransaction(SubTransaction.builder()
                    .transactionId(sdkTx.getId())
                    .step(1)
                    .type(SubTransactionType.INSPECT_ISSUE_PROPOSE)
                    .status(SubTransactionStatus.COMPLETED)
                    .build());

            return response;
        } catch (IssuerSdkException e) {
            log.error("SdkException occurred during inspectIssuePropose: {}", e.getErrorCode().getMessage());
            throw e;
        } catch (Exception e) {
            log.error("Exception occurred during inspectIssuePropose: {}", e.getMessage(), e);
            throw new IssuerSdkException(IssuerSdkErrorCode.UNKNOWN_SERVER_ERROR);
        }
    }

    @Override
    public GenerateIssueProfileResDto generateIssueProfile(GenerateIssueProfileReqDto request) {
        try {
            log.debug("=== Server: Starting Generate Issue Profile ===");
            SdkTransaction transaction = springIssueDataProvider.validateAndFindTransaction(request.getTxId(),
                    SubTransactionType.INSPECT_ISSUE_PROPOSE);

            SdkIssueProfile byVcPlanId = springIssueDataProvider.findIssueProfileByVcPlanId(transaction.getVcPlanId());
            SdkUser user = findUserByHolderAndVcSchemaId(request.getHolder(), byVcPlanId.getVcSchemaId());

            // Execute Business Logic in SDK
            GenerateIssueProfileResDto response = issueVcProtocol.generateIssueProfile(request, transaction, user);

            // Save SubTransaction
            transactionService.insertSubTransaction(SubTransaction.builder()
                    .transactionId(transaction.getId())
                    .step(2)
                    .type(SubTransactionType.GENERATE_ISSUE_PROFILE)
                    .status(SubTransactionStatus.COMPLETED)
                    .build());

            return response;
        } catch (IssuerSdkException e) {
            log.error("SdkException occurred during generateIssueProfile: {}", e.getErrorCode().getMessage());
            throw e;
        } catch (Exception e) {
            log.error("Exception occurred during generateIssueProfile: {}", e.getMessage(), e);
            throw new IssuerSdkException(IssuerSdkErrorCode.TR_VC_ISSUE_PROFILE_FAILED);
        }
    }

    @Override
    public IssueVcResDto issueVc(IssueVcReqDto request) {
        try {
            log.debug("=== Server: Starting Issue VC ===");
            SdkTransaction transaction = springIssueDataProvider.validateAndFindTransaction(request.getTxId(),
                    SubTransactionType.GENERATE_ISSUE_PROFILE);

            SdkVcProfile vcProfile = springIssueDataProvider.findVcProfileByTransactionId(transaction.getId());
            SdkIssueProfile issueProfile = springIssueDataProvider
                    .findIssueProfileById(transaction.getIssueProfileId());
            UserInfoDto user = findUserByVcProfileAndVcSchemaId(vcProfile, issueProfile);

            // Execute Business Logic in SDK
            IssueVcResDto response = issueVcProtocol.issueVc(request, transaction, user);

            transactionService.insertSubTransaction(SubTransaction.builder()
                    .transactionId(transaction.getId())
                    .step(3)
                    .type(SubTransactionType.ISSUE_VC)
                    .status(SubTransactionStatus.COMPLETED)
                    .build());

            return response;
        } catch (IssuerSdkException e) {
            log.error("SdkException occurred during issueVc: {}", e.getErrorCode().getMessage());
            throw e;
        } catch (Exception e) {
            log.error("Exception occurred during issueVc: {}", e.getMessage(), e);
            throw new IssuerSdkException(IssuerSdkErrorCode.TR_VC_ISSUE_FAILED);
        }
    }

    @Override
    public CompleteVcResDto completeVc(CompleteVcReqDto request) {
        try {
            log.debug("=== Server: Starting Complete VC ===");
            SdkTransaction transaction = springIssueDataProvider.validateAndFindTransaction(request.getTxId(),
                    SubTransactionType.ISSUE_VC);

            // Execute logic
            CompleteVcResDto response = issueVcProtocol.completeVc(request, transaction);

            Transaction transactionEntity = transactionService.findById(transaction.getId());
            transactionService.updateTransactionStatus(transactionEntity, TransactionStatus.FINISH);

            transactionService.insertSubTransaction(SubTransaction.builder()
                    .transactionId(transaction.getId())
                    .step(4)
                    .type(SubTransactionType.COMPLETE_VC)
                    .status(SubTransactionStatus.COMPLETED)
                    .build());

            return response;
        } catch (IssuerSdkException e) {
            log.error("SdkException occurred during completeVc: {}", e.getErrorCode().getMessage());
            throw e;
        } catch (Exception e) {
            log.error("Exception occurred during completeVc: {}", e.getMessage(), e);
            throw new IssuerSdkException(IssuerSdkErrorCode.TR_VC_ISSUE_COMPLETE_FAILED);
        }
    }

    @Override
    public GenerateIssueProfileResDto generateIssueProfileFromProxy(GenerateIssueProfileReqDto request,
                                                                    String accessToken) {
        try {
            log.debug("=== Server: Starting Generate Issue Profile Proxy ===");
            SdkTransaction transaction = springIssueDataProvider.validateAndFindTransaction(request.getTxId(),
                    SubTransactionType.INSPECT_ISSUE_PROPOSE);

            SdkIssueProfile byVcPlanId = springIssueDataProvider.findIssueProfileByVcPlanId(transaction.getVcPlanId());

            String sub = request.getUserId() != null && !request.getUserId().isBlank()
                    ? request.getUserId() : verifyAccessTokenGetSub(accessToken);
            Holder holder = Holder.builder().pii(sub).build();
            request.setHolder(holder);

            SdkUser user = findUserByHolderAndVcSchemaId(request.getHolder(), byVcPlanId.getVcSchemaId());

            GenerateIssueProfileResDto response = issueVcProtocol.generateIssueProfile(request, transaction, user);

            transactionService.insertSubTransaction(SubTransaction.builder()
                    .transactionId(transaction.getId())
                    .step(2)
                    .type(SubTransactionType.GENERATE_ISSUE_PROFILE)
                    .status(SubTransactionStatus.COMPLETED)
                    .build());

            return response;
        } catch (IssuerSdkException e) {
            log.error("SdkException occurred during generateIssueProfileFromProxy: {}", e.getErrorCode().getMessage());
            throw e;
        } catch (Exception e) {
            log.error("Exception occurred during generateIssueProfileFromProxy: {}", e.getMessage(), e);
            throw new IssuerSdkException(IssuerSdkErrorCode.TR_VC_ISSUE_PROFILE_FAILED);
        }
    }

    @Override
    public IssueVcResDto issueVcFromProxy(IssueVcReqDto request) {
        try {
            log.debug("=== Server: Starting Issue VC Proxy ===");
            SdkTransaction transaction = springIssueDataProvider.validateAndFindTransaction(request.getTxId(),
                    SubTransactionType.GENERATE_ISSUE_PROFILE);

            SdkVcProfile vcProfile = springIssueDataProvider.findVcProfileByTransactionId(transaction.getId());
            SdkIssueProfile issueProfile = springIssueDataProvider
                    .findIssueProfileById(transaction.getIssueProfileId());
            UserInfoDto user = findUserByVcProfileAndVcSchemaId(vcProfile, issueProfile);

            IssueVcResDto response = issueVcProtocol.issueVcFromProxy(request, transaction, user);

            transactionService.insertSubTransaction(SubTransaction.builder()
                    .transactionId(transaction.getId())
                    .step(3)
                    .type(SubTransactionType.ISSUE_VC)
                    .status(SubTransactionStatus.COMPLETED)
                    .build());

            return response;
        } catch (IssuerSdkException e) {
            log.error("SdkException occurred during issueVcFromProxy: {}", e.getErrorCode().getMessage());
            throw e;
        } catch (Exception e) {
            log.error("Exception occurred during issueVcFromProxy: {}", e.getMessage(), e);
            throw new IssuerSdkException(IssuerSdkErrorCode.TR_VC_ISSUE_FAILED);
        }
    }


    private String verifyAccessTokenGetSub(String accessToken) {
        JwtTokenParts parts = parseJwtTokenParts(accessToken);
        String headerJson = new String(Base64.getUrlDecoder().decode(parts.unsignedJwt().split("\\.")[0]));
        String payloadJson = new String(Base64.getUrlDecoder().decode(parts.unsignedJwt().split("\\.")[1]));

        JsonObject header = JsonParser.parseString(headerJson).getAsJsonObject();
        JsonObject payload = JsonParser.parseString(payloadJson).getAsJsonObject();

        String kid = header.has("kid") ? header.get("kid").getAsString() : null;
        String alg = header.has("alg") ? header.get("alg").getAsString() : null;
        long exp = payload.has("exp") ? payload.get("exp").getAsLong() : null;

        String did = payload.get("iss").getAsString();

        if (Strings.isBlank(did)) {
            throw new IssuerSdkException(IssuerSdkErrorCode.INVALID_TOKEN);
        }

        DidDocument didDocument = storageService.findDidDoc(did);
        if (didDocument == null) {
            throw new IssuerSdkException(IssuerSdkErrorCode.DID_DOC_NOT_FOUND);
        }

        DidManager didManager = new DidManager();
        try {
            didManager.parse(didDocument.toJson());
        } catch (Exception e) {
            throw new IssuerSdkException(IssuerSdkErrorCode.DID_DOC_NOT_FOUND);
        }
        VerificationMethod foundMethod = null;

        if (Strings.isNotBlank(kid)) {
            String keyId = extractKeyIdFromUri(kid);
            foundMethod = didManager.getVerificationMethodByKeyId(keyId);
        } else {
            throw new IssuerSdkException(IssuerSdkErrorCode.INVALID_TOKEN);
        }

        if (foundMethod == null) {
            throw new IssuerSdkException(IssuerSdkErrorCode.DID_DOC_NOT_FOUND);
        }

        byte[] signingInputBytes = parts.unsignedJwt().getBytes(StandardCharsets.UTF_8);

        EccCurveType curveType = null;

        if ("ES256".equals(alg)) {
            curveType = EccCurveType.SECP_256_R1;
        } else if ("RS256".equals(alg)) {
            throw new IssuerSdkException(IssuerSdkErrorCode.INVALID_KEY_TYPE);
        } else {
            curveType = EccCurveType.SECP_256_R1;
        }

        validateTokenExpiration(exp);
        BaseCryptoUtil.verifySignature(foundMethod.getPublicKeyMultibase(),
                Base64.getUrlDecoder().decode(parts.signature()),
                BaseDigestUtil.generateHash(signingInputBytes), curveType.toCommonSdkEccCurveType());

        return payload.get("sub").getAsString();
    }

    private void validateTokenExpiration(long exp) {
        long currentTime = Instant.now().getEpochSecond();
        if (exp <= currentTime) {
            log.error("Token has expired. Current time: {}, Expiration time: {}", currentTime, exp);
            throw new IssuerSdkException(IssuerSdkErrorCode.JWT_TOKEN_EXPIRED);
        }
    }

    private String extractKeyIdFromUri(String kid) {
        try {
            URI uri = new URI(kid);
            String keyId = uri.getFragment();
            if (keyId == null || keyId.trim().isEmpty()) {
                throw new IssuerSdkException(IssuerSdkErrorCode.INVALID_TOKEN);
            }
            return keyId;
        } catch (URISyntaxException e) {
            throw new IssuerSdkException(IssuerSdkErrorCode.INVALID_TOKEN);
        }
    }

    private JwtTokenParts parseJwtTokenParts(String token) {
        if (token != null && token.startsWith("Bearer ")) {
            token = token.substring(7).trim();
        }

        int lastDotIndex = token.lastIndexOf('.');
        if (lastDotIndex == -1) {
            throw new IssuerSdkException(IssuerSdkErrorCode.INVALID_DATE_FORMAT);
        }

        String unsignedJwt = token.substring(0, lastDotIndex);
        String signature = token.substring(lastDotIndex + 1);

        return new JwtTokenParts(unsignedJwt, signature);
    }

    private record JwtTokenParts(String unsignedJwt, String signature) {
    }
}

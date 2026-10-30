package org.omnione.did.issuer.v1.agent.service;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import lombok.extern.slf4j.Slf4j;
import org.omnione.did.base.db.domain.*;
import org.omnione.did.base.db.repository.ZkpAttributeRepository;
import org.omnione.did.base.db.repository.ZkpSchemaAttributeRepository;
import org.omnione.did.base.util.RandomUtil;
import org.omnione.did.common.util.DateTimeUtil;
import org.omnione.did.data.model.enums.vc.ClaimFormat;
import org.omnione.did.data.model.enums.vc.ClaimType;
import org.omnione.did.data.model.schema.ClaimDef;
import org.omnione.did.data.model.schema.SchemaClaims;
import org.omnione.did.data.model.schema.VcSchema;
import org.omnione.did.issuer.v1.admin.service.query.IssueProfileQueryService;
import org.omnione.did.issuer.v1.admin.service.query.ZkpCredentialDefinitionQueryService;
import org.omnione.did.issuer.v1.admin.service.query.ZkpSchemaQueryService;
import org.omnione.did.issuer.v1.agent.service.provider.SpringIssueDataProvider;
import org.omnione.did.issuer.v1.agent.service.query.TransactionService;
import org.omnione.did.issuer.v1.agent.service.query.UserQueryService;
import org.omnione.did.issuer.v1.agent.service.query.VcSchemaService;
import org.omnione.did.issuer.v1.common.service.StorageService;
import org.omnione.did.sdk.issuer.model.data.Holder;
import org.omnione.did.sdk.issuer.model.domain.SdkIssueProfile;
import org.omnione.did.sdk.issuer.model.domain.SdkUser;
import org.omnione.did.sdk.issuer.model.domain.SdkVcProfile;
import org.omnione.did.sdk.issuer.model.dto.UserInfoDto;
import org.omnione.did.sdk.issuer.protocol.vc.IssueVcProtocol;
import org.omnione.did.zkp.datamodel.schema.AttributeDef;
import org.omnione.did.zkp.datamodel.schema.CredentialSchema;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.ZonedDateTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/** Uses the SDK protocol while preserving the existing TEST claim generation mode. */
@Slf4j
@Service
@Transactional
@Profile("!sample")
public class TestIssueService extends AbstractPersistentIssueVcService {
    private static final String DUMMY_PNG_BASE64 =
            "data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAAAAEAAAABCAYAAAAfFcSJAAAADUlEQVR42mNkYPhfDwAChwGA60e6kgAAAABJRU5ErkJggg==";
    private static final String DUMMY_JPG_BASE64 =
            "data:image/jpeg;base64,/9j/4AAQSkZJRgABAQAASABIAAD/wAARCAABAAEDASIAAhEBAxEB/8QAHwAAAQUBAQEBAQEAAAAAAAAAAAECAwQFBgcICQoL/8QAtRAAAgEDAwIEAwUFBAQAAAF9AQIDAAQRBRIhMUEGE1FhByJxFDKBkaEII0KxwRVS0fAkM2JyggkKFhcYGRolJicoKSo0NTY3ODk6Q0RFRkdISUpTVFVWV1hZWmNkZWZnaGlqc3R1dnd4eXqDhIWGh4iJipKTlJWWl5iZmqKjpKWmp6ipqrKztLW2t7i5usLDxMXGx8jJytLT1NXW19jZ2uHi4+Tl5ufo6erx8vP09fb3+Pn6/8QAHwEAAwEBAQEBAQEBAQAAAAAAAAECAwQFBgcICQoL/8QAtREAAgECBAQDBAcFBAQAAQJ3AAECAxEEBSExBhJBUQdhcRMiMoEIFEKRobHBCSMzUvAVYnLRChYkNOEl8RcYGRomJygpKjU2Nzg5OkNERUZHSElKU1RVVldYWVpjZGVmZ2hpanN0dXZ3eHl6goOEhYaHiImKkpOUlZaXmJmaoqOkpaanqKmqsrO0tba3uLm6wsPExcbHyMnK0tPU1dbX2Nna4uPk5ebn6Onq8vP09fb3+Pn6/9sAQwAEBAQEBAQGBAQGCQYGBgkMCQkJCQwPDAwMDAwPEg8PDw8PDxISEhISEhISFRUVFRUVGRkZGRkcHBwcHBwcHBwc/9sAQwEEBQUHBwcMBwcMHRQQFB0dHR0dHR0dHR0dHR0dHR0dHR0dHR0dHR0dHR0dHR0dHR0dHR0dHR0dHR0dHR0dHR0d/90ABAAB/9oADAMBAAIRAxEAPwD4booor+gDyz//2Q==";
    private static final String DUMMY_GIF_BASE64 =
            "data:image/gif;base64,R0lGODlhAQABAIAAAAAAAP///yH5BAEAAAAALAAAAAABAAEAAAIBRAA7";


    private final UserQueryService userQueryService;
    private final VcSchemaService vcSchemaService2;
    private final IssueProfileQueryService issueProfileQueryService2;
    private final ZkpSchemaQueryService zkpSchemaQueryService2;
    private final ZkpCredentialDefinitionQueryService zkpCredentialDefinitionQueryService2;
    private final ZkpSchemaAttributeRepository zkpSchemaAttributeRepository;
    private final ZkpAttributeRepository zkpAttributeRepository;
    private final TransactionService transactionService2;

    public TestIssueService(IssueVcProtocol protocol, SpringIssueDataProvider dataProvider,
            TransactionService transactionService, StorageService storageService,
            UserQueryService userQueryService, VcSchemaService vcSchemaService,
            IssueProfileQueryService issueProfileQueryService,
            ZkpSchemaQueryService zkpSchemaQueryService,
            ZkpCredentialDefinitionQueryService zkpCredentialDefinitionQueryService,
            ZkpSchemaAttributeRepository zkpSchemaAttributeRepository,
            ZkpAttributeRepository zkpAttributeRepository) {
        super(protocol, dataProvider, transactionService, storageService);
        this.userQueryService = userQueryService;
        this.vcSchemaService2 = vcSchemaService;
        this.issueProfileQueryService2 = issueProfileQueryService;
        this.zkpSchemaQueryService2 = zkpSchemaQueryService;
        this.zkpCredentialDefinitionQueryService2 = zkpCredentialDefinitionQueryService;
        this.zkpSchemaAttributeRepository = zkpSchemaAttributeRepository;
        this.zkpAttributeRepository = zkpAttributeRepository;
        this.transactionService2 = transactionService;
    }

    @Override
    protected SdkUser findUserByHolderAndVcSchemaId(Holder holder, Long vcSchemaId) {
        String dummyData = generateVcClaimDummyData(vcSchemaId);
        User user = null;
        if (holder.getDid() != null) {
            user = userQueryService.findByDidAndVcSchemaId(holder.getDid(), vcSchemaId).orElse(null);
        }
        if (user == null) {
            String pii = holder.getPii() != null ? holder.getPii() : "test-pii-" + RandomUtil.generateUUID();
            user = userQueryService.findByPiiAndVcSchemaId(pii, vcSchemaId).orElse(null);
            if (user == null) {
                user = User.builder().pii(pii).did(holder.getDid()).vcSchemaId(vcSchemaId).build();
            }
        }
        user = updateUserData(user, dummyData);
        return SdkUser.builder().id(user.getId()).pii(user.getPii()).did(user.getDid())
                .vcSchemaId(user.getVcSchemaId()).data(user.getData()).build();
    }

    @Override
    protected UserInfoDto findUserByVcProfileAndVcSchemaId(SdkVcProfile vcProfile, SdkIssueProfile issueProfile) {
        User user = userQueryService.findByIdAndVcSchemaId(vcProfile.getUserId(), issueProfile.getVcSchemaId());
        String data = generateVcClaimDummyData(issueProfile.getVcSchemaId());
        String definitionId = resolveDefinitionId(vcProfile.getTransactionId());
        if (definitionId != null) data = generateFullDummyData(issueProfile.getVcSchemaId(), definitionId, data);
        user = updateUserData(user, data);
        return UserInfoDto.builder().id(user.getId()).pii(user.getPii()).did(user.getDid())
                .vcSchemaId(user.getVcSchemaId()).data(user.getData())
                .validFrom(DateTimeUtil.getCurrentUTCTime()).validUntil(DateTimeUtil.addYearsToCurrentTime(2)).build();
    }

    private String generateVcClaimDummyData(Long vcSchemaId) {
        try {
            VcSchema vcSchema = vcSchemaService2.getVcSchemaById(vcSchemaId);
            JsonObject data = new JsonObject();
            for (SchemaClaims schemaClaims : vcSchema.getCredentialSubject().getClaims()) {
                String namespaceId = schemaClaims.getNamespace().getId();
                for (ClaimDef claimDef : schemaClaims.getItems()) {
                    String key = namespaceId + "." + claimDef.getId();
                    String value = dummyValueForClaim(claimDef);
                    data.addProperty(key, value);
                }
            }
            return new Gson().toJson(data);
        } catch (Exception e) {
            log.warn("[TEST] Failed to generate VC claim dummy data for vcSchemaId={}: {}", vcSchemaId, e.getMessage());
            return "{}";
        }
    }

    private String generateFullDummyData(Long vcSchemaId, String definitionId, String existingData) {
        try {
            JsonObject data = new Gson().fromJson(existingData, JsonObject.class);
            if (data == null) data = new JsonObject();

            ZkpCredentialDefinition definition = zkpCredentialDefinitionQueryService2.findByDefinitionId(definitionId);
            ZkpSchema zkpSchema = zkpSchemaQueryService2.findBySchemaId(definition.getSchemaId());
            CredentialSchema credentialSchema = new Gson().fromJson(zkpSchema.getSchema(), CredentialSchema.class);

            List<ZkpSchemaAttribute> schemaAttributes = zkpSchemaAttributeRepository.findByZkpSchemaId(zkpSchema.getId());
            Map<String, Long> labelToAttrId = schemaAttributes.stream()
                    .collect(Collectors.toMap(ZkpSchemaAttribute::getAttributeLabel, ZkpSchemaAttribute::getZkpAttributeId, (a, b) -> a));

            for (String attrName : credentialSchema.getAttrNames()) {
                if (!data.has(attrName)) {
                    String value = "test";
                    Long attrId = labelToAttrId.get(attrName);
                    if (attrId != null) {
                        ZkpAttribute zkpAttribute = zkpAttributeRepository.findById(attrId).orElse(null);
                        if (zkpAttribute != null && AttributeDef.ATTR_TYPE.NUMBER.equals(zkpAttribute.getType())) {
                            value = "0";
                        }
                    }
                    data.addProperty(attrName, value);
                }
            }
            return new Gson().toJson(data);
        } catch (Exception e) {
            log.warn("[TEST] Failed to enrich ZKP dummy data for definitionId={}: {}", definitionId, e.getMessage());
            return existingData;
        }
    }

    private String dummyValueForClaim(ClaimDef claimDef) {
        String typeRaw = claimDef.getType();
        String formatRaw = claimDef.getFormat();

        ClaimType type = safeParseClaimType(typeRaw);
        ClaimFormat format = safeParseClaimFormat(formatRaw);

        if (type == null) return "test";

        return switch (type) {
            case TEXT -> switch (format != null ? format : ClaimFormat.PLAIN) {
                case HTML -> "<p>test</p>";
                case XML  -> "<root>test</root>";
                default   -> "test";
            };
            case IMAGE -> switch (format != null ? format : ClaimFormat.PNG) {
                case PNG  -> DUMMY_PNG_BASE64;
                case JPG  -> DUMMY_JPG_BASE64;
                case GIF  -> DUMMY_GIF_BASE64;
                default   -> DUMMY_PNG_BASE64;
            };
            case DOCUMENT -> "test";
        };
    }

    private ClaimType safeParseClaimType(String raw) {
        if (raw == null) return null;
        try { return ClaimType.fromString(raw); } catch (Exception e) { return null; }
    }

    private ClaimFormat safeParseClaimFormat(String raw) {
        if (raw == null) return null;
        try { return ClaimFormat.fromString(raw); } catch (Exception e) { return null; }
    }

    private String resolveDefinitionId(Long transactionId) {
        try {
            Transaction transaction = transactionService2.findById(transactionId);
            IssueProfile profile = issueProfileQueryService2.findById(transaction.getIssueProfileId());
            return profile.getDefinitionId();
        } catch (Exception e) {
            log.debug("[TEST] Could not resolve definitionId from transactionId={}: {}", transactionId, e.getMessage());
            return null;
        }
    }

    private User updateUserData(User user, String data) {
        user.setData(data);
        return userQueryService.save(user);
    }
}

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

package org.omnione.did.issuer.v1.agent.service;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import lombok.extern.slf4j.Slf4j;
import org.omnione.did.base.datamodel.data.Holder;
import org.omnione.did.base.db.domain.*;
import org.omnione.did.base.db.repository.ZkpAttributeRepository;
import org.omnione.did.base.db.repository.ZkpSchemaAttributeRepository;
import org.omnione.did.base.exception.ErrorCode;
import org.omnione.did.base.exception.OpenDidException;
import org.omnione.did.base.property.KycProperty;
import org.omnione.did.base.util.RandomUtil;
import org.omnione.did.data.model.enums.vc.ClaimFormat;
import org.omnione.did.data.model.enums.vc.ClaimType;
import org.omnione.did.data.model.schema.ClaimDef;
import org.omnione.did.data.model.schema.SchemaClaims;
import org.omnione.did.data.model.schema.VcSchema;
import org.omnione.did.issuer.v1.admin.service.query.IssueProfileQueryService;
import org.omnione.did.issuer.v1.admin.service.query.VcSchemaQueryService;
import org.omnione.did.issuer.v1.admin.service.query.ZkpCredentialDefinitionQueryService;
import org.omnione.did.issuer.v1.admin.service.query.ZkpSchemaQueryService;
import org.omnione.did.issuer.v1.agent.service.query.*;
import org.omnione.did.issuer.v1.common.service.StorageService;
import org.omnione.did.issuer.v1.common.service.ZkpWalletService;
import org.omnione.did.zkp.datamodel.schema.AttributeDef;
import org.omnione.did.zkp.datamodel.schema.CredentialSchema;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * TEST mode issue service. Generates dummy claim data for all claim fields so that
 * VC issuance succeeds without any pre-registered user data.
 *
 * Dummy generation rules (ClaimType × ClaimFormat):
 *   TEXT/PLAIN, TEXT/TXT, TEXT/CSV → "test"
 *   TEXT/HTML → "<p>test</p>"
 *   TEXT/XML  → "<root>test</root>"
 *   IMAGE/PNG → 1×1 transparent PNG base64
 *   IMAGE/JPG → minimal JPEG base64
 *   IMAGE/GIF → 1×1 GIF base64
 *   DOCUMENT/PDF, DOCUMENT/WORD → "test"
 *   (default) → "test"
 *
 * ZKP attribute rules:
 *   NUMBER → "0", STRING → "test"
 */
@Slf4j
@Service
@Transactional
@Profile("!sample")
public class TestIssueService extends IssueServiceBase {

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

    public TestIssueService(VcProfileQueryService vcProfileQueryService,
                            VcOfferQueryService vcOfferQueryService,
                            TransactionService transactionService,
                            E2EQueryService e2EQueryService,
                            VcQueryService vcQueryService,
                            StorageService storageService,
                            FileWalletService walletService,
                            IssueProfileQueryService issueProfileQueryService,
                            VcSchemaService vcSchemaService,
                            VcSchemaQueryService vcSchemaQueryService,
                            IssuerInfoQueryService issuerInfoQueryService,
                            ZkpWalletService zkpWalletService,
                            ZkpCredentialDefinitionQueryService zkpCredentialDefinitionQueryService,
                            ZkpSchemaQueryService zkpSchemaQueryService,
                            UserQueryService userQueryService,
                            ZkpSchemaAttributeRepository zkpSchemaAttributeRepository,
                            ZkpAttributeRepository zkpAttributeRepository,
                            KycProperty kycProperty) {
        super(vcProfileQueryService, vcOfferQueryService, transactionService, e2EQueryService,
                vcQueryService, storageService, walletService, issueProfileQueryService,
                vcSchemaService, vcSchemaQueryService, issuerInfoQueryService, zkpWalletService,
                zkpCredentialDefinitionQueryService, zkpSchemaQueryService, kycProperty);
        this.userQueryService = userQueryService;
        this.vcSchemaService2 = vcSchemaService;
        this.issueProfileQueryService2 = issueProfileQueryService;
        this.zkpSchemaQueryService2 = zkpSchemaQueryService;
        this.zkpCredentialDefinitionQueryService2 = zkpCredentialDefinitionQueryService;
        this.zkpSchemaAttributeRepository = zkpSchemaAttributeRepository;
        this.zkpAttributeRepository = zkpAttributeRepository;
        this.transactionService2 = transactionService;
    }

    /**
     * For USER_INIT: upsert dummy user by (did, vcSchemaId).
     * For ISSUER_INIT: upsert dummy user by (pii, vcSchemaId); auto-generate PII if null.
     */
    @Override
    protected User findUserByHolderAndVcSchemaId(Holder holder, Long vcSchemaId) {
        log.debug("[TEST] findUserByHolderAndVcSchemaId did={} pii={}", holder.getDid(), holder.getPii());

        String dummyData = generateVcClaimDummyData(vcSchemaId);

        // DID가 있으면 항상 DID로 먼저 조회 (데모 저장 여부와 무관하게 기존 레코드 재사용)
        if (holder.getDid() != null) {
            Optional<User> byDid = userQueryService.findByDidAndVcSchemaId(holder.getDid(), vcSchemaId);
            if (byDid.isPresent()) {
                return updateUserData(byDid.get(), dummyData);
            }
        }

        String pii = (holder.getPii() != null) ? holder.getPii() : "test-pii-" + RandomUtil.generateUUID();
        return userQueryService.findByPiiAndVcSchemaId(pii, vcSchemaId)
                .map(user -> updateUserData(user, dummyData))
                .orElseGet(() -> userQueryService.save(User.builder()
                        .pii(pii)
                        .did(holder.getDid())
                        .vcSchemaId(vcSchemaId)
                        .data(dummyData)
                        .build()));
    }

    /**
     * Finds the user by userId stored in VcProfile and regenerates all dummy claim data immediately
     * before issuance. TEST mode must not depend on user data that may have been registered or updated
     * after the issue profile was generated.
     */
    @Override
    protected User findUserByVcProfileAndVcSchemaId(VcProfile vcProfile, Long vcSchemaId) {
        log.debug("[TEST] findUserByVcProfileAndVcSchemaId userId={}", vcProfile.getUserId());

        User user = userQueryService.findByIdAndVcSchemaId(vcProfile.getUserId(), vcSchemaId);
        String dummyData = generateVcClaimDummyData(vcSchemaId);

        String definitionId = resolveDefinitionId(vcProfile.getTransactionId());
        if (definitionId != null) {
            dummyData = generateFullDummyData(vcSchemaId, definitionId, dummyData);
        }

        // Persist the exact data used for issuance so TEST mode behaves identically in
        // direct and proxy flows, regardless of any intervening user-info update.
        return updateUserData(user, dummyData);
    }

    @Override
    protected User findUserByHolder(Holder holder) {
        return new User();
    }

    @Override
    protected User findUserByVcProfile(VcProfile vcProfile) {
        if (vcProfile.getUserId() != null) {
            return userQueryService.findById(vcProfile.getUserId());
        }
        return userQueryService.findByDid(vcProfile.getDid())
                .orElseThrow(() -> new OpenDidException(ErrorCode.HOLDER_NOT_FOUND));
    }

    // -------------------------------------------------------------------------
    // Dummy data generation helpers
    // -------------------------------------------------------------------------

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

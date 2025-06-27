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

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializer;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.omnione.did.base.db.constant.ZkpCredentialDefinitionStatus;
import org.omnione.did.base.db.constant.ZkpSchemaStatus;
import org.omnione.did.base.db.domain.*;
import org.omnione.did.base.exception.ErrorCode;
import org.omnione.did.base.exception.OpenDidException;
import org.omnione.did.issuer.v1.admin.api.dto.EmptyResDto;
import org.omnione.did.issuer.v1.admin.dto.zkp.namespace.ZkpAttributeSaveDto;
import org.omnione.did.issuer.v1.admin.dto.zkp.schema.ZkpSchemaDto;
import org.omnione.did.issuer.v1.admin.dto.zkp.schema.ZkpSchemaInfoDto;
import org.omnione.did.issuer.v1.admin.service.query.ZkpNamespaceQueryService;
import org.omnione.did.issuer.v1.admin.service.query.ZkpSchemaQueryService;
import org.omnione.did.issuer.v1.agent.service.query.IssuerInfoQueryService;
import org.omnione.did.issuer.v1.common.service.StorageService;
import org.omnione.did.zkp.datamodel.credential.Credential;
import org.omnione.did.zkp.datamodel.definition.CredentialDefinition;
import org.omnione.did.zkp.datamodel.schema.AttributeDef;
import org.omnione.did.zkp.datamodel.schema.AttributeDef.ATTR_TYPE;
import org.omnione.did.zkp.datamodel.schema.AttributeType;
import org.omnione.did.zkp.datamodel.schema.CredentialSchema;
import org.omnione.did.zkp.datamodel.schema.Namespace;
import org.omnione.did.zkp.datamodel.util.GsonWrapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@Transactional
@RequiredArgsConstructor
@Service
public class ZkpSchemaService {
    private final ZkpSchemaQueryService zkpSchemaQueryService;
    private final ZkpNamespaceQueryService zkpNamespaceQueryService;
    private final IssuerInfoQueryService issuerInfoQueryService;
    private final ListCommunityService listCommunityService;
    private final StorageService storageService;

    public PageImpl<ZkpSchemaDto> searchZkpSchemaList(String searchKey, String searchValue, Pageable pageable) {
        return zkpSchemaQueryService.searchZkpSchemaList(searchKey, searchValue, pageable);
    }

    public EmptyResDto createZkpSchema(ZkpSchemaInfoDto request) {
        // Find Issuer Info
        log.debug("Finding Issuer Info");
        IssuerInfo issuerInfo = issuerInfoQueryService.getIssuerInfo();
        log.debug("Found Issuer Info: {}", issuerInfo);

        // Generate Schema ID
        log.debug("Generating Schema ID");
        String schemaId = generateSchemaId(request, issuerInfo);
        log.debug("Generated Schema ID: {}", schemaId);

        // Check if schema ID already exists
        log.debug("Checking if Schema ID already exists: {}", schemaId);
        if (zkpSchemaQueryService.existsBySchemaId(schemaId)) {
            log.error("Schema ID already exists: {}", schemaId);
            throw new OpenDidException(ErrorCode.ZKP_SCHEMA_ID_ALREADY_EXISTS);
        }

        // Generate Credential Schema
        log.debug("Generating Credential Schema");
        CredentialSchema credentialSchema = generateCredentialSchema(request, schemaId);
        log.debug("Generated Credential Schema: {}", credentialSchema);

        // Save ZKP Schema
        log.debug("Saving ZKP Schema: {}", request.getName());
        ZkpSchema savedZkpSchema = saveZkpSchemaAndAttributes(request, credentialSchema);

        try {
            // Register to Blockchain and List Provider
            log.debug("Registering to Blockchain and List Provider: {}", savedZkpSchema.getSchemaId());
            registerToBlockchain(savedZkpSchema, credentialSchema);

            // Register to List Provider
            log.debug("Registering to List Provider: {}", savedZkpSchema.getSchemaId());
            registerToListProvider(savedZkpSchema, credentialSchema);
        } catch (OpenDidException e) {
            log.error("Failed to register to Blockchain or List Provider: {}", e.getMessage(), e);
        } catch (Exception e) {
            log.error("Failed to register to Blockchain or List Provider: {}", e.getMessage(), e);
        }

        log.debug("ZKP Schema registration completed: {}", savedZkpSchema.getSchemaId());
        return new EmptyResDto();
    }

    private ZkpSchema saveZkpSchemaAndAttributes(ZkpSchemaInfoDto request, CredentialSchema credentialSchema) {
        try {
            Gson gson = new GsonBuilder()
                    .registerTypeAdapter(AttributeDef.ATTR_TYPE.class,
                            (JsonSerializer<AttributeDef.ATTR_TYPE>) (src, typeOfSrc, context) ->
                                    new JsonPrimitive(
                                            // 예: STRING → "String", NUMBER → "Number"
                                            src.name().charAt(0) + src.name().substring(1).toLowerCase()
                                    )
                    )
                    .create();

            ZkpSchema zkpSchema = ZkpSchema.builder()
                    .name(request.getName())
                    .version(request.getVersion())
                    .tag(request.getTag())
                    .schemaId(credentialSchema.getId())
                    .status(ZkpSchemaStatus.NEED_BLOCKCHAIN_REGISTRATION)
                    .schema(gson.toJson(credentialSchema))
                    .build();

            ZkpSchema savedZkpSchema = zkpSchemaQueryService.saveZkpSchema(zkpSchema);

            List<ZkpSchemaAttribute> schemaAttributes = request.getAttributes().stream().map(dto -> {
                ZkpNamespace namespace = zkpNamespaceQueryService.findNamespaceById(dto.getNamespaceId());
                ZkpAttribute attribute = zkpNamespaceQueryService.findAttributeById(dto.getId());

                return ZkpSchemaAttribute.builder()
                        .zkpSchemaId(savedZkpSchema.getId())
                        .schemaId(savedZkpSchema.getSchemaId())
                        .namespaceId(namespace.getNamespaceId())
                        .attributeLabel(dto.getLabel())
                        .zkpAttributeId(attribute.getId())
                        .sortOrder(dto.getSortOrder())
                        .build();
            }).toList();

            zkpSchemaQueryService.saveAllSchemaAttributes(schemaAttributes);
            return savedZkpSchema;

        } catch (OpenDidException e) {
            log.error("Failed to save ZKP schema and attributes: {}", e.getMessage(), e);
            throw new OpenDidException(ErrorCode.ZKP_SCHEMA_SAVE_FAILED);
        }
    }

    private void registerToBlockchain(ZkpSchema zkpSchema, CredentialSchema credentialSchema) {
        try {
            log.debug("Registering to Blockchain: {}", zkpSchema.getSchemaId());
            registerCredentialSchemaToBlockchain(credentialSchema);

            zkpSchema.setStatus(ZkpSchemaStatus.NEED_LIST_PROVIDER_REGISTRATION);
            zkpSchemaQueryService.updateZkpSchemaStatusById(zkpSchema.getId(), zkpSchema.getStatus());
        } catch (OpenDidException e) {
            log.error("Failed to register to Blockchain: {}", e.getMessage(), e);
            throw e;
        } catch (Exception e) {
            log.error("Failed to register to Blockchain: {}", e.getMessage(), e);
            throw new OpenDidException(ErrorCode.ZKP_SCHEMA_REGISTRATION_FAILED);
        }
    }

    // @TODO: List Provider registration
    private void registerToListProvider(ZkpSchema zkpSchema, CredentialSchema credentialSchema) {

        try {
            log.debug("Registering to List Provider: {}", zkpSchema.getSchemaId());
            registerCredentialSchemaToListProvider(credentialSchema);

            zkpSchema.setStatus(ZkpSchemaStatus.ACTIVATE);
            zkpSchemaQueryService.updateZkpSchemaStatusById(zkpSchema.getId(), zkpSchema.getStatus());
        } catch (OpenDidException e) {
            log.error("Failed to register to List Provider: {}", e.getMessage(), e);
            throw e;
        } catch (Exception e) {
            log.error("Failed to register to List Provider: {}", e.getMessage(), e);
            throw new OpenDidException(ErrorCode.ZKP_SCHEMA_REGISTRATION_FAILED);
        }
    }

    private CredentialSchema generateCredentialSchema(ZkpSchemaInfoDto zkpSchemaInfoDto, String schemaId) {
        CredentialSchema credentialSchema = new CredentialSchema();

        // 기본 정보 설정
        credentialSchema.setName(zkpSchemaInfoDto.getName());
        credentialSchema.setVersion(zkpSchemaInfoDto.getVersion());
        credentialSchema.setTag(zkpSchemaInfoDto.getTag());
        credentialSchema.setId(schemaId);

        List<ZkpAttributeSaveDto> attributeList = zkpSchemaInfoDto.getAttributes();

        // 1. Sort the entire list based on sortOrder
        List<ZkpAttributeSaveDto> sortedList = attributeList.stream()
                .sorted((a, b) -> {
                    Integer orderA = a.getSortOrder();
                    Integer orderB = b.getSortOrder();
                    if (orderA == null && orderB == null) return 0;
                    if (orderA == null) return 1;
                    if (orderB == null) return -1;
                    return orderA.compareTo(orderB);
                })
                .toList();

        // 2. Set the order of attrNames
        List<String> attrNames = sortedList.stream()
                .map(attr -> attr.getNamespaceIdentifier() + "." + attr.getLabel())
                .toList();
        credentialSchema.setAttrNames(attrNames);

        // 3. Group by namespace (preserve order → use LinkedHashMap)
        Map<Long, List<ZkpAttributeSaveDto>> groupedByNamespace = sortedList.stream()
                .collect(Collectors.groupingBy(
                        ZkpAttributeSaveDto::getNamespaceId,
                        LinkedHashMap::new,
                        Collectors.toList()
                ));

        // 4. Construct AttributeType
        List<AttributeType> attrTypes = groupedByNamespace.entrySet().stream()
                .map(entry -> {
                    List<ZkpAttributeSaveDto> groupAttributes = entry.getValue();
                    ZkpAttributeSaveDto firstAttr = groupAttributes.get(0);

                    // Build namespace
                    Namespace namespace = new Namespace();
                    namespace.setId(firstAttr.getNamespaceIdentifier());
                    namespace.setName(firstAttr.getNamespaceName());
                    if (firstAttr.getNamespaceRef() != null && !firstAttr.getNamespaceRef().isEmpty()) {
                        namespace.setRef(firstAttr.getNamespaceRef());
                    }

                    // Build AttributeDef
                    List<AttributeDef> defs = groupAttributes.stream()
                            .map(attr -> {
                                AttributeDef def = new AttributeDef();
                                def.setLabel(attr.getLabel());
                                def.setCaption(attr.getCaption());
                                try {
                                    def.setType(getAttrTypeFromString(attr.getType()));
                                } catch (IllegalArgumentException e) {
                                    def.setType(ATTR_TYPE.STRING);
                                }
                                return def;
                            })
                            .toList();

                    AttributeType attrType = new AttributeType();
                    attrType.setNamespace(namespace);
                    attrType.setItems(defs);
                    return attrType;
                })
                .toList();

        credentialSchema.setAttrTypes(attrTypes);
        return credentialSchema;
    }

    // TODO:
    private void registerCredentialSchemaToBlockchain(CredentialSchema credentialSchema) {
        storageService.registerCredentialSchema(credentialSchema);

    }

    // TODO:
    private void registerCredentialSchemaToListProvider(CredentialSchema credentialSchema) {
        listCommunityService.registerCredentialSchema(credentialSchema);

    }

    private String generateSchemaId(ZkpSchemaInfoDto zkpSchemaInfoDto, IssuerInfo issuerInfo) {
        return String.format("%s:2:%s:%s",
                issuerInfo.getDid(),
                zkpSchemaInfoDto.getName(),
                zkpSchemaInfoDto.getVersion());
    }

    private static AttributeDef.ATTR_TYPE getAttrTypeFromString(String typeStr) {
        if (typeStr == null) {
            return AttributeDef.ATTR_TYPE.STRING; // 기본값
        }

        if (typeStr.equalsIgnoreCase("String")) {
            return AttributeDef.ATTR_TYPE.STRING;
        } else if (typeStr.equalsIgnoreCase("Number")) {
            return ATTR_TYPE.NUMBER;
        } else {
            throw new IllegalArgumentException("Invalid attribute type: " + typeStr);
        }
    }

    public ZkpSchemaInfoDto getZkpSchemaInfo(Long id) {
        // Find ZkpSchema by ID
        log.debug("Finding ZkpSchema by ID: {}", id);
        ZkpSchema zkpSchema = zkpSchemaQueryService.findById(id);

        // Fetch ZkpSchema attributes
        log.debug("Fetching ZkpSchema attributes for ID: {}", id);
        List<ZkpSchemaAttribute> zkpSchemaAttributes = zkpNamespaceQueryService.findZkpSchemaAttributesBySchemaId(zkpSchema.getId());

        // Convert to List<ZkpAttributeSaveDto>
        List<ZkpAttributeSaveDto> attributes = zkpSchemaAttributes.stream()
                .map(attribute -> {
                    ZkpAttribute zkpAttribute = zkpNamespaceQueryService.findAttributeById(attribute.getZkpAttributeId());
                    ZkpNamespace zkpNamespace = zkpNamespaceQueryService.findNamespaceByNamespaceId(attribute.getNamespaceId());
                    return ZkpAttributeSaveDto.builder()
                            .id(zkpAttribute.getId())
                            .label(attribute.getAttributeLabel())
                            .namespaceId(zkpNamespace.getId())
                            .namespaceIdentifier(zkpNamespace.getNamespaceId())
                            .namespaceName(zkpNamespace.getName())
                            .namespaceRef(zkpNamespace.getRef())
                            .caption(zkpAttribute.getCaption())
                            .type(zkpAttribute.getType().toString())
                            .sortOrder(attribute.getSortOrder())
                            .build();
                })
                .toList();

        return ZkpSchemaInfoDto.builder()
                .id(zkpSchema.getId())
                .name(zkpSchema.getName())
                .version(zkpSchema.getVersion())
                .status(zkpSchema.getStatus())
                .tag(zkpSchema.getTag())
                .schema(zkpSchema.getSchema())
                .schemaId(zkpSchema.getSchemaId())
                .attributes(attributes)
                .build();
    }

    public List<ZkpSchemaDto> getAllZkpSchemas() {
        List<ZkpSchema> zkpSchemaList = zkpSchemaQueryService.findAllSchemas();
        return zkpSchemaList.stream()
                .map(ZkpSchemaDto::fromEntity)
                .toList();
    }

    public EmptyResDto reRegisterZkpCredentialSchema() {
        var credentialSchemaList = zkpSchemaQueryService.findByStatusNot(ZkpSchemaStatus.ACTIVATE);

        credentialSchemaList.forEach(zkpCredentialSchema -> {
            try {
                var status = zkpCredentialSchema.getStatus();
                var credentialSchema = GsonWrapper.getGson()
                        .fromJson(zkpCredentialSchema.getSchema(), CredentialSchema.class);

                if (ZkpSchemaStatus.NEED_BLOCKCHAIN_REGISTRATION.equals(status)) {
                    register(zkpCredentialSchema, credentialSchema);
                } else if (ZkpSchemaStatus.NEED_LIST_PROVIDER_REGISTRATION.equals(status)) {
                    registerToListProvider(zkpCredentialSchema, credentialSchema);
                }
            } catch (OpenDidException e) {
                log.error("Failed to re-register for schema {}: {}", zkpCredentialSchema.getId(), e.getMessage(), e);
            } catch (Exception e) {
                log.error("Unexpected error for schema {}: {}", zkpCredentialSchema.getId(), e.getMessage(), e);
            }
        });

        return new EmptyResDto();
    }

    private void register(ZkpSchema zkpCredentialDefinition, CredentialSchema credentialDefinition) {
        try {
            // Register to Blockchain
            log.debug("Registering Credential Schema to Blockchain");
            registerToBlockchain(zkpCredentialDefinition, credentialDefinition);

            // Register to List Provider
            log.debug("Registering Credential Schema to List Provider");
            registerToListProvider(zkpCredentialDefinition, credentialDefinition);
        } catch (OpenDidException e) {
            log.error("Failed to register to Blockchain or List Provider: {}", e.getMessage(), e);
        } catch (Exception e) {
            log.error("Failed to register to Blockchain or List Provider: {}", e.getMessage(), e);
        }
    }
}

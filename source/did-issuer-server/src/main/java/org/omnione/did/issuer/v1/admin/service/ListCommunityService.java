package org.omnione.did.issuer.v1.admin.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.omnione.did.base.constants.UrlConstant;
import org.omnione.did.base.datamodel.data.Option;
import org.omnione.did.base.datamodel.data.VcPlan;
import org.omnione.did.base.datamodel.enums.InitiateType;
import org.omnione.did.base.db.domain.IssueProfile;
import org.omnione.did.base.db.domain.ZkpCredentialDefinition;
import org.omnione.did.base.exception.ErrorCode;
import org.omnione.did.base.exception.OpenDidException;
import org.omnione.did.base.response.ErrorResponse;
import org.omnione.did.base.util.BaseMultibaseUtil;
import org.omnione.did.common.exception.HttpClientException;
import org.omnione.did.common.util.HttpClientUtil;
import org.omnione.did.common.util.JsonUtil;
import org.omnione.did.data.model.schema.VcSchema;
import org.omnione.did.data.model.vc.CredentialSchema;
import org.omnione.did.issuer.v1.admin.api.dto.*;
import org.omnione.did.issuer.v1.admin.service.query.ApplicationConfigQueryService;
import org.omnione.did.issuer.v1.admin.service.query.ZkpCredentialDefinitionQueryService;
import org.omnione.did.issuer.v1.agent.service.query.IssuerInfoQueryService;
import org.omnione.did.issuer.v1.agent.service.query.VcSchemaService;
import org.omnione.did.zkp.datamodel.definition.CredentialDefinition;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.util.List;

/**
 * Service for interacting with the List Community endpoints in the Admin Console.
 * <p>
 * This service allows registration and deletion of VC Schemas and VC Plans with the Trusted Authority Service (TAS).
 * It handles encoding, request formatting, and error handling for communication with TAS list APIs.
 */
@Slf4j
@Profile("!sample")
@Service
public class ListCommunityService {
    private final ZkpCredentialDefinitionQueryService zkpCredentialDefinitionQueryService;
    private final VcSchemaService vcSchemaService;
    @Value(value = "${tas.url}")
    private String TAS_URL;
    private final String ISSUER_DID;

    public ListCommunityService(ApplicationConfigQueryService applicationConfigQueryService,
                                ZkpCredentialDefinitionQueryService zkpCredentialDefinitionQueryService,
                                VcSchemaService vcSchemaService, IssuerInfoQueryService issuerInfoQueryService) {
        this.vcSchemaService = vcSchemaService;
        this.zkpCredentialDefinitionQueryService = zkpCredentialDefinitionQueryService;
//        this.TAS_URL = applicationConfigQueryService.getApplicationConfig().getTasUrl() + UrlConstant.List.V1;
        this.ISSUER_DID = issuerInfoQueryService.getIssuerInfo().getDid();
    }

    /**
     * Registers a VC Schema to the TAS List API.
     *
     * @param vcSchemaId the ID of the VC Schema to register
     * @throws OpenDidException if the request to TAS fails
     */
    public void registerVcSchema(Long vcSchemaId) {
        VcSchema vcSchema = vcSchemaService.getVcSchemaById(vcSchemaId);
        String vcSchemaEncode = BaseMultibaseUtil.encode(vcSchema.toJson().getBytes(StandardCharsets.UTF_8));
        RegisterVcSchemaReqDto request = RegisterVcSchemaReqDto.builder()
                .vcSchema(vcSchemaEncode)
                .issuerDid(ISSUER_DID)
                .build();
        try {
            HttpClientUtil.postData(TAS_URL + UrlConstant.List.V1 + UrlConstant.List.VC_SCHEMA_PUBLIC,
                    JsonUtil.serializeToJson(request), EmptyResDto.class);
        } catch (HttpClientException e) {
            log.error("HttpClientException occurred while sending post vc schema request: {}", e.getResponseBody(), e);
            ErrorResponse errorResponse = convertExternalErrorResponse(e.getResponseBody());
            throw new OpenDidException(errorResponse);
        }
    }

    /**
     * Deletes a VC Schema from the TAS List API.
     *
     * @param request the request containing schema deletion data
     * @throws OpenDidException if the request to TAS fails
     */
    public void deleteVcSchema(DeleteVcSchemaReqDto request) {
        try {
            HttpClientUtil.postData(TAS_URL + UrlConstant.List.V1 + UrlConstant.List.VC_SCHEMA_PUBLIC,
                    JsonUtil.serializeToJson(request), EmptyResDto.class);
        } catch (HttpClientException e) {
            log.error("HttpClientException occurred while sending delete vc schema request: {}", e.getResponseBody(), e);
            ErrorResponse errorResponse = convertExternalErrorResponse(e.getResponseBody());
            throw new OpenDidException(errorResponse);
        }
    }

    /**
     * Registers a VC Plan to the TAS List API.
     *
     * @param issueProfile the issue profile data used to create the VC Plan
     * @throws OpenDidException if the request to TAS fails
     */
    public void registerVcPlan(IssueProfile issueProfile) {
        VcSchema vcSchema = vcSchemaService.getVcSchemaById(issueProfile.getVcSchemaId());
        CredentialSchema credentialSchema = new CredentialSchema();
        credentialSchema.setId(vcSchema.getId());
        credentialSchema.setType("OsdSchemaCredential");


        boolean isIssuerInit = InitiateType.ISSUER_INIT.equals(issueProfile.getInitiateType());

        VcPlan vcPlan = VcPlan.builder()
                .vcPlanId(issueProfile.getVcPlanId())
                .name(issueProfile.getTitle())
                .description(issueProfile.getDescription())
                .credentialSchema(credentialSchema)
                .option(Option.builder()
                        .allowIssuerInit(isIssuerInit)
                        .allowUserInit(!isIssuerInit)
                        .delegatedIssuance(false)
                        .build())
                .allowedIssuers(List.of(ISSUER_DID))
                .manager(ISSUER_DID)
                .tags(issueProfile.getTags())
                .build();

        if (issueProfile.getZkpEnabled()) {
            ZkpCredentialDefinition definition = zkpCredentialDefinitionQueryService.findByDefinitionId(issueProfile.getDefinitionId());

            org.omnione.did.base.datamodel.data.zkp.CredentialDefinition credentialDefinition = new org.omnione.did.base.datamodel.data.zkp.CredentialDefinition();
            credentialDefinition.setId(issueProfile.getDefinitionId());
            credentialDefinition.setSchemaId(definition.getSchemaId());

            vcPlan.setCredentialDefinition(credentialDefinition);
        }

        String vcPlanEncode = BaseMultibaseUtil.encode(
                JsonUtil.serializeToJson(vcPlan).getBytes(StandardCharsets.UTF_8));

        RegisterVcPlanReqDto request = RegisterVcPlanReqDto.builder()
                .vcPlan(vcPlanEncode)
                .issuerDid(ISSUER_DID)
                .initiate(issueProfile.getInitiateType().getType())
                .build();

        try {
            HttpClientUtil.postData(TAS_URL + UrlConstant.List.V1 + UrlConstant.List.VC_PLAN_PUBLIC,
                    JsonUtil.serializeToJson(request), EmptyResDto.class);
        } catch (HttpClientException e) {
            log.error("HttpClientException occurred while sending post vc plan request: {}", e.getResponseBody(), e);
            ErrorResponse errorResponse = convertExternalErrorResponse(e.getResponseBody());
            throw new OpenDidException(errorResponse);
        }
    }

    /**
     * Deletes a VC Plan from the TAS List API.
     *
     * @param request the request containing VC Plan ID to be deleted
     * @throws OpenDidException if the request to TAS fails
     */
    public void deleteVcPlan(DeleteIssuePlanIdReqDto request) {
        try {
            HttpClientUtil.postData(TAS_URL + UrlConstant.List.V1 + UrlConstant.List.VC_PLAN_PUBLIC,
                    JsonUtil.serializeToJson(request), EmptyResDto.class);
        } catch (HttpClientException e) {
            log.error("HttpClientException occurred while sending delete vc plan request: {}", e.getResponseBody(), e);
            ErrorResponse errorResponse = convertExternalErrorResponse(e.getResponseBody());
            throw new OpenDidException(errorResponse);
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
     * Registers a Credential Schema to the TAS List API.
     *
     * @param credentialSchema CredentialSchema to register
     * @throws OpenDidException if the request to TAS fails
     */
    public void registerCredentialSchema(org.omnione.did.zkp.datamodel.schema.CredentialSchema credentialSchema) {

        String credentialSchemaJson = BaseMultibaseUtil.encode(credentialSchema.toJson().getBytes(StandardCharsets.UTF_8));
        RegisterCredentialSchemaReqDto request = RegisterCredentialSchemaReqDto.builder()
                .credentialSchema(credentialSchemaJson)
                .issuerDid(ISSUER_DID)
                .build();
        try {
            HttpClientUtil.postData(TAS_URL + UrlConstant.List.V1 + UrlConstant.List.CREDENTIAL_SCHEMA_PUBLIC,
                    JsonUtil.serializeToJson(request), EmptyResDto.class);
        } catch (HttpClientException e) {
            log.error("HttpClientException occurred while sending post credential schema request: {}", e.getResponseBody(), e);
            ErrorResponse errorResponse = convertExternalErrorResponse(e.getResponseBody());
            throw new OpenDidException(errorResponse);
        }
    }

    /**
     * Registers a Credential Definition to the TAS List API.
     *
     * @param credentialDefinition Credential Definition to register
     * @throws OpenDidException if the request to TAS fails
     */
    public void registerCredentialDefinition(CredentialDefinition credentialDefinition) {
        String credentialDefinitionJson = BaseMultibaseUtil.encode(credentialDefinition.toJson().getBytes(StandardCharsets.UTF_8));
        RegisterCredentialDefinitionReqDto request = RegisterCredentialDefinitionReqDto.builder()
                .credentialDefinition(credentialDefinitionJson)
                .issuerDid(ISSUER_DID)
                .build();
        try {
            HttpClientUtil.postData(TAS_URL + UrlConstant.List.V1 + UrlConstant.List.CREDENTIAL_DEFINITION_PUBLIC,
                    JsonUtil.serializeToJson(request), EmptyResDto.class);
        } catch (HttpClientException e) {
            log.error("HttpClientException occurred while sending post credential definition request: {}", e.getResponseBody(), e);
            ErrorResponse errorResponse = convertExternalErrorResponse(e.getResponseBody());
            throw new OpenDidException(errorResponse);
        }
    }
}

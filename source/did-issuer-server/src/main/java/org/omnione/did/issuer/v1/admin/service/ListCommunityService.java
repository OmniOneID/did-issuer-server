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
import org.omnione.did.issuer.v1.agent.service.query.IssuerInfoQueryService;
import org.omnione.did.issuer.v1.agent.service.query.VcSchemaService;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.util.List;

/**
 * Description...
 */

@Slf4j
@Profile("!sample")
@Service
public class ListCommunityService {
    private final VcSchemaService vcSchemaService;
    private final String TAS_URL;
    private final String ISSUER_DID;

    public ListCommunityService(ApplicationConfigQueryService applicationConfigQueryService,
                                VcSchemaService vcSchemaService, IssuerInfoQueryService issuerInfoQueryService) {
        this.vcSchemaService = vcSchemaService;
        this.TAS_URL = applicationConfigQueryService.getApplicationConfig().getTasUrl() + UrlConstant.List.V1;
        this.ISSUER_DID = issuerInfoQueryService.getIssuerInfo().getDid();
    }

    public void registerVcSchema(Long vcSchemaId) {
        VcSchema vcSchema = vcSchemaService.getVcSchemaById(vcSchemaId);
        String vcSchemaEncode = BaseMultibaseUtil.encode(vcSchema.toJson().getBytes(StandardCharsets.UTF_8));
        RegisterVcSchemaReqDto request = RegisterVcSchemaReqDto.builder()
                .vcSchema(vcSchemaEncode)
                .issuerDid(ISSUER_DID)
                .build();
        try {
            HttpClientUtil.postData(TAS_URL + UrlConstant.List.VC_SCHEMA_PUBLIC,
                    JsonUtil.serializeToJson(request), EmptyResDto.class);
        } catch (HttpClientException e) {
            log.error("HttpClientException occurred while sending post vc schema request: {}", e.getResponseBody(), e);
            ErrorResponse errorResponse = convertExternalErrorResponse(e.getResponseBody());
            throw new OpenDidException(errorResponse);
        }
    }

    public void deleteVcSchema(DeleteVcSchemaReqDto request) {
        try {
            HttpClientUtil.postData(TAS_URL + UrlConstant.List.VC_SCHEMA_PUBLIC,
                    JsonUtil.serializeToJson(request), EmptyResDto.class);
        } catch (HttpClientException e) {
            log.error("HttpClientException occurred while sending delete vc schema request: {}", e.getResponseBody(), e);
            ErrorResponse errorResponse = convertExternalErrorResponse(e.getResponseBody());
            throw new OpenDidException(errorResponse);
        }
    }

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
                        .allowUserInit(isIssuerInit)
                        .delegatedIssuance(false)
                        .build())
                .allowedIssuers(List.of(ISSUER_DID))
                .manager(ISSUER_DID)
                .tags(issueProfile.getTags())
                .build();

        String vcPlanEncode = BaseMultibaseUtil.encode(
                JsonUtil.serializeToJson(vcPlan).getBytes(StandardCharsets.UTF_8));

        RegisterVcPlanReqDto request = RegisterVcPlanReqDto.builder()
                .vcPlan(vcPlanEncode)
                .issuerDid(ISSUER_DID)
                .build();
        try {
            HttpClientUtil.postData(TAS_URL + UrlConstant.List.VC_PLAN_PUBLIC,
                    JsonUtil.serializeToJson(request), EmptyResDto.class);
        } catch (HttpClientException e) {
            log.error("HttpClientException occurred while sending post vc plan request: {}", e.getResponseBody(), e);
            ErrorResponse errorResponse = convertExternalErrorResponse(e.getResponseBody());
            throw new OpenDidException(errorResponse);
        }
    }

    public void deleteVcPlan(DeleteIssuePlanIdReqDto request) {
        try {
            HttpClientUtil.postData(TAS_URL + UrlConstant.List.VC_PLAN_PUBLIC,
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
}

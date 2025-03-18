package org.omnione.did.issuer.v1.admin.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.omnione.did.base.constants.UrlConstant;
import org.omnione.did.base.exception.ErrorCode;
import org.omnione.did.base.exception.OpenDidException;
import org.omnione.did.base.response.ErrorResponse;
import org.omnione.did.common.exception.HttpClientException;
import org.omnione.did.common.util.HttpClientUtil;
import org.omnione.did.common.util.JsonUtil;
import org.omnione.did.issuer.v1.admin.api.dto.*;
import org.omnione.did.issuer.v1.admin.service.query.ApplicationConfigQueryService;
import org.springframework.stereotype.Service;

/**
 * Description...
 */

@Slf4j
@RequiredArgsConstructor
@Service
public class ListCommunityService {
    private final ApplicationConfigQueryService applicationConfigQueryService;
    private String TAS_URL;
    @PostConstruct
    public void loadData() {
        this.TAS_URL = applicationConfigQueryService.getApplicationConfig().getTasUrl() + UrlConstant.List.V1;
    }
    public void postVcSchema(PostVcSchemaReqDto request) {
//        try {
//            HttpClientUtil.postData(TAS_URL + UrlConstant.List.VC_SCHEMA_PUBLIC,
//                    JsonUtil.serializeToJson(request), EmptyResDto.class);
//        } catch (HttpClientException e) {
//            log.error("HttpClientException occurred while sending post vc schema request: {}", e.getResponseBody(), e);
//            ErrorResponse errorResponse = convertExternalErrorResponse(e.getResponseBody());
//            throw new OpenDidException(errorResponse);
//        }
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
    public void postVcPlan(PostIssuePlanIdReqDto request) {
//        try {
//            HttpClientUtil.postData(TAS_URL + UrlConstant.List.VC_PLAN_PUBLIC,
//                    JsonUtil.serializeToJson(request), EmptyResDto.class);
//        } catch (HttpClientException e) {
//            log.error("HttpClientException occurred while sending post vc plan request: {}", e.getResponseBody(), e);
//            ErrorResponse errorResponse = convertExternalErrorResponse(e.getResponseBody());
//            throw new OpenDidException(errorResponse);
//        }
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

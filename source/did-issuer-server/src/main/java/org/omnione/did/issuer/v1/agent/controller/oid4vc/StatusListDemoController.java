/*
 * Copyright 2026 OmniOne.
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

package org.omnione.did.issuer.v1.agent.controller.oid4vc;

import lombok.RequiredArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.omnione.did.issuer.v1.agent.service.oid4vc.status.CredentialManagementService;
import org.omnione.did.issuer.v1.agent.service.oid4vc.status.model.CredentialIssuanceView;
import org.omnione.did.oid4vc.oid4vci.exception.OID4VCIErrorCode;
import org.omnione.did.oid4vc.oid4vci.exception.OID4VCIException;
import org.omnione.did.oid4vc.oid4vci.status.model.CredentialStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.context.annotation.Profile;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;
import java.util.Map;

@Controller
@Profile("!local")
@RequestMapping("/oid4vci/status-list-demo")
@RequiredArgsConstructor
public class StatusListDemoController {

    private static final String DEMO_ACTOR = "status-list-demo";

    private final CredentialManagementService credentialManagementService;

    @GetMapping
    public String page() {
        return "status-list-demo";
    }

    @GetMapping("/api/credentials")
    @ResponseBody
    public List<CredentialIssuanceView> findIssuedCredentials(
            @RequestParam(required = false) String userId) {
        return credentialManagementService.findAllIssued(userId);
    }

    @PatchMapping("/api/credentials/{issuanceId}/status")
    @ResponseBody
    public CredentialIssuanceView changeStatus(
            @PathVariable String issuanceId,
            @RequestBody ChangeStatusRequest request) throws OID4VCIException {
        return credentialManagementService.changeStatus(
                issuanceId, request.getStatus(), request.getReason(), DEMO_ACTOR);
    }

    @ExceptionHandler(OID4VCIException.class)
    @ResponseBody
    public ResponseEntity<Map<String, String>> handleOid4vciException(OID4VCIException exception) {
        return ResponseEntity.status(resolveHttpStatus(exception))
                .body(Map.of(
                        "code", exception.getErrorCode(),
                        "message", exception.getErrorMsg(),
                        "reason", exception.getErrorReason() == null
                                ? "" : exception.getErrorReason()));
    }

    private HttpStatus resolveHttpStatus(OID4VCIException exception) {
        String code = exception.getErrorCode();
        if (OID4VCIErrorCode.ERR_CODE_STATUS_LIST_INVALID_TRANSITION.getCode().equals(code)
                || OID4VCIErrorCode.ERR_CODE_STATUS_LIST_INVALID_CONFIGURATION.getCode().equals(code)
                || OID4VCIErrorCode.ERR_CODE_GENERAL_INVALID_PARAMETER.getCode().equals(code)
                || OID4VCIErrorCode.ERR_CODE_GENERAL_NULL_PARAMETER.getCode().equals(code)) {
            return HttpStatus.BAD_REQUEST;
        }
        return HttpStatus.resolve(exception.getHttpStatus()) == null
                ? HttpStatus.INTERNAL_SERVER_ERROR
                : HttpStatus.valueOf(exception.getHttpStatus());
    }

    @Getter
    @Setter
    @NoArgsConstructor
    public static class ChangeStatusRequest {
        private CredentialStatus status;
        private String reason;
    }
}

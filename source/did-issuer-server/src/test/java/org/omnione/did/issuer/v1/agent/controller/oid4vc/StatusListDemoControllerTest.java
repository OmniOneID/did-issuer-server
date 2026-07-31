package org.omnione.did.issuer.v1.agent.controller.oid4vc;

import com.google.gson.Gson;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.omnione.did.base.db.domain.IssuanceState;
import org.omnione.did.issuer.v1.agent.service.oid4vc.status.CredentialManagementService;
import org.omnione.did.issuer.v1.agent.service.oid4vc.status.model.CredentialIssuanceView;
import org.omnione.did.oid4vc.oid4vci.exception.OID4VCIErrorCode;
import org.omnione.did.oid4vc.oid4vci.exception.OID4VCIException;
import org.omnione.did.oid4vc.oid4vci.status.model.CredentialStatus;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Instant;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class StatusListDemoControllerTest {

    private MockMvc mockMvc;
    private StatusListDemoController controller;

    @Mock
    private CredentialManagementService credentialManagementService;

    @BeforeEach
    void setUp() {
        controller = new StatusListDemoController(credentialManagementService);
        mockMvc = MockMvcBuilders.standaloneSetup(controller).build();
    }

    @Test
    void rendersDemoPage() {
        assertEquals("status-list-demo", controller.page());
    }

    @Test
    void listsIssuedCredentialsByUser() throws Exception {
        when(credentialManagementService.findAllIssued("demo-user"))
                .thenReturn(List.of(issuanceView(CredentialStatus.VALID)));

        mockMvc.perform(get("/oid4vci/status-list-demo/api/credentials")
                        .param("userId", "demo-user"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].issuanceId").value("issuance-1"))
                .andExpect(jsonPath("$[0].credentialStatus").value("VALID"));
    }

    @Test
    void changesCredentialStatus() throws Exception {
        when(credentialManagementService.changeStatus(
                "issuance-1", CredentialStatus.SUSPENDED, "demo test", "status-list-demo"))
                .thenReturn(issuanceView(CredentialStatus.SUSPENDED));

        mockMvc.perform(patch("/oid4vci/status-list-demo/api/credentials/issuance-1/status")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"status":"SUSPENDED","reason":"demo test"}
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.credentialStatus").value("SUSPENDED"));

        verify(credentialManagementService).changeStatus(
                "issuance-1", CredentialStatus.SUSPENDED, "demo test", "status-list-demo");
    }

    @Test
    void deserializesChangeRequestWithProjectGsonVersion() {
        StatusListDemoController.ChangeStatusRequest request = new Gson().fromJson(
                """
                        {"status":"SUSPENDED","reason":"demo test"}
                        """,
                StatusListDemoController.ChangeStatusRequest.class);

        assertEquals(CredentialStatus.SUSPENDED, request.getStatus());
        assertEquals("demo test", request.getReason());
    }

    @Test
    void returnsBadRequestForInvalidTransition() throws Exception {
        when(credentialManagementService.changeStatus(
                "issuance-1", CredentialStatus.RESERVED, "demo test", "status-list-demo"))
                .thenThrow(new OID4VCIException(
                        OID4VCIErrorCode.ERR_CODE_STATUS_LIST_INVALID_TRANSITION,
                        "VALID -> RESERVED"));

        mockMvc.perform(patch("/oid4vci/status-list-demo/api/credentials/issuance-1/status")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"status":"RESERVED","reason":"demo test"}
                                """))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.reason").value("VALID -> RESERVED"));
    }

    private CredentialIssuanceView issuanceView(CredentialStatus status) {
        Instant now = Instant.parse("2026-07-29T00:00:00Z");
        return new CredentialIssuanceView(
                "issuance-1", "demo-user", "config-1", "vc+sd-jwt",
                "https://issuer.example/status-lists/1", 3, status,
                IssuanceState.ISSUED, now, null, now, null,
                null, null, now, now);
    }
}

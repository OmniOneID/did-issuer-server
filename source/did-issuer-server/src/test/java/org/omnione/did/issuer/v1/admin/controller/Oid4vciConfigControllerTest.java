package org.omnione.did.issuer.v1.admin.controller;

import org.junit.jupiter.api.Test;
import org.omnione.did.base.datamodel.data.oid4vci.IssuerMetadata;
import org.omnione.did.issuer.v1.admin.service.Oid4vciConfigService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = Oid4vciConfigController.class)
@AutoConfigureMockMvc(addFilters = false)
class Oid4vciConfigControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private Oid4vciConfigService oid4vciConfigService;

    @Test
    void should_updateMetadata_when_postMetadata() throws Exception {
        mockMvc.perform(post("/issuer/admin/v1/oid4vci/metadata")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "credentialIssuer": "https://issuer.example.com",
                                  "authorizationServer": [],
                                  "credentialConfigurationsSupported": {}
                                }
                                """))
                .andExpect(status().isOk());

        verify(oid4vciConfigService).updateMetadata(any(IssuerMetadata.class));
    }
}

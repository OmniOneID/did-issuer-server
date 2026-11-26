package org.omnione.did.base.config;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.omnione.did.base.annotation.ArticleLog;
import org.omnione.did.base.aop.ApiLogInterceptor;
import org.omnione.did.base.db.domain.ApiLog;
import org.omnione.did.issuer.v1.admin.service.JwtService;
import org.omnione.did.issuer.v1.common.service.ApiLogService;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class JwtAuthenticationFilterAuditTest {

    @SuppressWarnings("unchecked")
    @Test
    void auditLogUsesLoginIdInsteadOfNumericTokenSubject() throws Exception {
        JwtService jwtService = mock(JwtService.class);
        Jws<Claims> token = mock(Jws.class);
        Claims claims = mock(Claims.class);
        when(jwtService.parse("access-token")).thenReturn(token);
        when(jwtService.isCurrentSession(token)).thenReturn(true);
        when(token.getPayload()).thenReturn(claims);
        when(claims.get("typ", String.class)).thenReturn("access");
        when(claims.get("role", String.class)).thenReturn("ROOT");
        when(claims.get("loginId", String.class)).thenReturn("admin@opendid.omnione.net");
        when(claims.getSubject()).thenReturn("1");

        ApiLogService logService = mock(ApiLogService.class);
        var mvc = MockMvcBuilders.standaloneSetup(new AuditController())
                .addFilters(new JwtAuthenticationFilter(jwtService))
                .addInterceptors(new ApiLogInterceptor(logService))
                .build();

        mvc.perform(get("/issuer/admin/v1/audit-test")
                        .header("Authorization", "Bearer access-token")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        ArgumentCaptor<ApiLog> logs = ArgumentCaptor.forClass(ApiLog.class);
        verify(logService).saveAsync(logs.capture());
        assertEquals("admin@opendid.omnione.net", logs.getValue().getRequesterId());
    }

    @RestController
    static class AuditController {
        @GetMapping("/issuer/admin/v1/audit-test")
        @ArticleLog(name = "Audit test")
        public String handle() {
            return "ok";
        }
    }
}

package org.omnione.did.issuer.v1.admin.controller;

import com.google.gson.Gson;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import jakarta.servlet.http.HttpServletRequest;
import org.junit.jupiter.api.Test;
import org.omnione.did.issuer.v1.admin.dto.admin.AdminDto;
import org.omnione.did.base.aop.ApiLogInterceptor;
import org.omnione.did.base.db.domain.ApiLog;
import org.omnione.did.issuer.v1.admin.service.JwtService;
import org.omnione.did.issuer.v1.admin.service.SessionService;
import org.omnione.did.issuer.v1.common.service.ApiLogService;
import org.mockito.ArgumentCaptor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.converter.json.GsonHttpMessageConverter;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.server.ResponseStatusException;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class SessionControllerLogoutTest {
    @Test
    void gsonReadsRefreshTokenRequest() {
        SessionController.RefreshReq request = new Gson().fromJson(
                "{\"refreshToken\":\"refresh\"}", SessionController.RefreshReq.class);
        assertEquals("refresh", request.refreshToken());
    }

    @SuppressWarnings("unchecked")
    @Test
    void httpLogoutWithGsonWritesSuccessfulAuditLog() throws Exception {
        JwtService jwt = mock(JwtService.class);
        Jws<Claims> token = mock(Jws.class);
        Claims claims = mock(Claims.class);
        when(jwt.parse("refresh")).thenReturn(token);
        when(jwt.isRefreshToken(token)).thenReturn(true);
        when(token.getPayload()).thenReturn(claims);
        when(claims.getSubject()).thenReturn("7");
        when(jwt.isCurrentSession(token)).thenReturn(true);
        SessionService sessionService = mock(SessionService.class);
        AdminDto admin = mock(AdminDto.class);
        when(admin.getLoginId()).thenReturn("admin@opendid.omnione.com");
        when(sessionService.requestAdminLoginById(7L)).thenReturn(admin);

        ApiLogService logService = mock(ApiLogService.class);
        GsonHttpMessageConverter converter = new GsonHttpMessageConverter();
        converter.setGson(new Gson());
        var mvc = MockMvcBuilders.standaloneSetup(new SessionController(sessionService, jwt))
                .setMessageConverters(converter)
                .addInterceptors(new ApiLogInterceptor(logService))
                .build();

        mvc.perform(post("/issuer/admin/v1/logout")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"refreshToken\":\"refresh\"}"))
                .andExpect(status().isOk());

        verify(jwt).revokeAllSessions(7L);
        ArgumentCaptor<ApiLog> logs = ArgumentCaptor.forClass(ApiLog.class);
        verify(logService).saveAsync(logs.capture());
        assertEquals("SUCCESS", logs.getValue().getResult());
        assertEquals("admin@opendid.omnione.com", logs.getValue().getRequesterId());
        assertEquals("admin@opendid.omnione.com", logs.getValue().getTargetId());
    }

    @SuppressWarnings("unchecked")
    @Test
    void repeatedLogoutSucceedsWithoutRevokingAgain() {
        JwtService jwt = mock(JwtService.class);
        Jws<Claims> token = mock(Jws.class);
        Claims claims = mock(Claims.class);
        HttpServletRequest request = mock(HttpServletRequest.class);
        when(jwt.parse("refresh")).thenReturn(token);
        when(jwt.isRefreshToken(token)).thenReturn(true);
        when(token.getPayload()).thenReturn(claims);
        when(claims.getSubject()).thenReturn("7");
        when(jwt.isCurrentSession(token)).thenReturn(false);
        SessionService sessionService = mock(SessionService.class);
        AdminDto admin = mock(AdminDto.class);
        when(admin.getLoginId()).thenReturn("admin@opendid.omnione.com");
        when(sessionService.requestAdminLoginById(7L)).thenReturn(admin);

        SessionController controller = new SessionController(sessionService, jwt);
        assertDoesNotThrow(() -> controller.logout(new SessionController.RefreshReq("refresh"), request));
        verify(jwt, never()).revokeAllSessions(7L);
        verify(request).setAttribute("targetId", "admin@opendid.omnione.com");
        verify(request).setAttribute("requesterId", "admin@opendid.omnione.com");
    }

    @SuppressWarnings("unchecked")
    @Test
    void nonRefreshTokenStillFails() {
        JwtService jwt = mock(JwtService.class);
        Jws<Claims> token = mock(Jws.class);
        when(jwt.parse("invalid")).thenReturn(token);
        when(jwt.isRefreshToken(token)).thenReturn(false);

        SessionController controller = new SessionController(mock(SessionService.class), jwt);
        ResponseStatusException error = assertThrows(ResponseStatusException.class,
                () -> controller.logout(new SessionController.RefreshReq("invalid"), mock(HttpServletRequest.class)));
        assertEquals(HttpStatus.UNAUTHORIZED, error.getStatusCode());
    }
}

package org.omnione.did.base.controller;

import org.junit.jupiter.api.Test;
import org.omnione.did.base.exception.ErrorCode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpStatus;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


/**
 * Check the behavior of {@link GlobalControllerAdvice}
 * @author birariro
 */
@WithMockUser
@WebMvcTest(controllers = GlobalControllerAdviceTestController.class)
@Import(GlobalControllerAdvice.class)
class GlobalControllerAdviceTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void should_badRequest_when_hasErrorResponseByOpenDidException() throws Exception {
        mockMvc.perform(post("/test/exception/OpenDidException/ErrorResponse")
                        .with(SecurityMockMvcRequestPostProcessors.csrf()))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").isNotEmpty())
                .andExpect(jsonPath("$.description").isNotEmpty());
    }

    @Test
    void should_internalServerError_when_emptyErrorResponseByOpenDidException() throws Exception {
        mockMvc.perform(post("/test/exception/OpenDidException/ErrorCode")
                        .with(SecurityMockMvcRequestPostProcessors.csrf()))
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.code").value(ErrorCode.UNKNOWN_SERVER_ERROR.getCode()))
                .andExpect(jsonPath("$.description").value(ErrorCode.UNKNOWN_SERVER_ERROR.getMessage()));
    }

    @Test
    void should_internalServerError_when_MethodArgumentNotValidException() throws Exception {
        int unknown_error_code = 9999;
        mockMvc.perform(post("/test/exception/MethodArgumentNotValidException")
                        .with(SecurityMockMvcRequestPostProcessors.csrf()))
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.code").value(unknown_error_code))
                .andExpect(jsonPath("$.description").isNotEmpty());
    }

    @Test
    void should_internalServerError_when_HttpMessageNotReadableException() throws Exception {
        mockMvc.perform(post("/test/exception/HttpMessageNotReadableException")
                        .with(SecurityMockMvcRequestPostProcessors.csrf()))
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.code").value(ErrorCode.REQUEST_BODY_UNREADABLE.getCode()))
                .andExpect(jsonPath("$.description").value(ErrorCode.REQUEST_BODY_UNREADABLE.getMessage()));
    }

    @Test
    void should_internalServerError_when_GenericException() throws Exception {
        mockMvc.perform(post("/test/exception/Exception")
                        .with(SecurityMockMvcRequestPostProcessors.csrf()))
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.code").value(HttpStatus.INTERNAL_SERVER_ERROR.value()))
                .andExpect(jsonPath("$.description").isNotEmpty());
    }
}


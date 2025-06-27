package org.omnione.did.base.controller;

import org.omnione.did.base.exception.ErrorCode;
import org.omnione.did.base.exception.OpenDidException;
import org.omnione.did.base.response.ErrorResponse;
import org.springframework.core.MethodParameter;
import org.springframework.http.HttpInputMessage;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.mock.http.MockHttpInputMessage;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

/**
 * something for {@link GlobalControllerAdviceTest}
 * @author birariro
 */
@RestController
class GlobalControllerAdviceTestController {

    @PostMapping("/test/exception/OpenDidException/ErrorResponse")
    public void throwOpenDidException() {
        throw new OpenDidException(new ErrorResponse("999", "message"));
    }

    @PostMapping("/test/exception/OpenDidException/ErrorCode")
    public void throwOpenDidExceptionAndErrorCode() {
        throw new OpenDidException(ErrorCode.UNKNOWN_SERVER_ERROR);
    }

    @PostMapping("/test/exception/MethodArgumentNotValidException")
    public void throwMethodArgumentNotValidException() throws MethodArgumentNotValidException {

        FieldError fieldError = new FieldError("dummyRequest", "name", "Name must not be blank");
        BindingResult bindingResult = new BeanPropertyBindingResult(Map.of(), "dummyRequest");
        bindingResult.addError(fieldError);

        throw new MethodArgumentNotValidException(
                new MethodParameter(this.getClass().getDeclaredMethods()[0], -1),
                bindingResult
        );
    }

    @PostMapping("/test/exception/HttpMessageNotReadableException")
    public void throwHttpMessageNotReadableException() {
        HttpInputMessage dummyInputMessage = new MockHttpInputMessage("dummy body".getBytes());
        throw new HttpMessageNotReadableException("json error", dummyInputMessage);
    }

    @PostMapping("/test/exception/Exception")
    public void throwRuntimeException() {
        throw new RuntimeException("Generic exception");
    }
}

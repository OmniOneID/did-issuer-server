/*
 * Copyright 2024 OmniOne.
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

package org.omnione.did.base.controller;


import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.omnione.did.base.exception.ErrorCode;
import org.omnione.did.base.exception.OpenDidException;
import org.omnione.did.base.response.ErrorResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;

import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.stream.Collectors;

/**
 * GlobalControllerAdvice is a class that provides global exception handling for controllers
 * It handles different types of exceptions and returns appropriate error responses.
 */
@Slf4j
@RequiredArgsConstructor
@RestControllerAdvice(basePackages = {"org.omnione.did"})
public class GlobalControllerAdvice {

    @ExceptionHandler(OpenDidException.class)
    public ResponseEntity<ErrorResponse> handleTasException(OpenDidException ex) {
        if (ex.getErrorResponse() != null) {
            ex.printStackTrace();
            return new ResponseEntity<>(ex.getErrorResponse(), HttpStatus.valueOf(400));
        }

        log.error("Error", ex);
        int httpStatus = ex.getErrorCode().getHttpStatus();
        ErrorResponse errorResponse = new ErrorResponse(ex.getErrorCode().getCode(), ex.getErrorCode().getMessage());
        return new ResponseEntity<>(errorResponse, HttpStatus.valueOf(httpStatus));
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleValidationException(MethodArgumentNotValidException ex) {
        int httpStatus = 500;

        String errorMessages = ex.getBindingResult()
                .getFieldErrors()
                .stream()
                .map(error -> error.getDefaultMessage())
                .collect(Collectors.joining("; "));

        log.error("Error", ex);

        ErrorResponse errorResponse = new ErrorResponse("9999", errorMessages);
        return new ResponseEntity<>(errorResponse, HttpStatus.valueOf(httpStatus));
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ErrorResponse> handleValidationException(HttpMessageNotReadableException ex) {
        log.error("Error", ex);

        ErrorResponse errorResponse = new ErrorResponse(ErrorCode.REQUEST_BODY_UNREADABLE);
        return new ResponseEntity<>(errorResponse, HttpStatus.valueOf(500));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleValidationException(Exception ex) {
        ErrorResponse errorResponse = new ErrorResponse("500", ex.getMessage());

        return new ResponseEntity<>(errorResponse, HttpStatus.valueOf(500));
    }


}

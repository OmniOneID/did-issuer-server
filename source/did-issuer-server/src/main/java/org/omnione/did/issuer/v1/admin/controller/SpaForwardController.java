package org.omnione.did.issuer.v1.admin.controller;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.resource.NoResourceFoundException;

@ControllerAdvice
@Order(Ordered.HIGHEST_PRECEDENCE)
public class SpaForwardController {

    @ExceptionHandler(NoResourceFoundException.class)
    public Object handleNoResourceFoundException(NoResourceFoundException ex, HttpServletRequest request) throws Exception {
        String uri = request.getRequestURI();

        // 1. 프론트엔드 라우팅 경로 (API, 정적 파싱 예외)인 경우 브라우저 뷰(index.html)로 포워드
        // api, oid4vci 등의 코어 백엔드 경로가 아니고, 확장자(.)가 없는 경로이면 SPA 요청으로 간주
        if (!uri.startsWith("/api") 
                && !uri.startsWith("/oid4vci/") 
                && !uri.equals("/oid4vci")
                && !uri.contains(".")) {
            return "forward:/index.html";
        }

        // 2. 그 외(API 호출 등)에서 발생한 순수 404 에러의 경우
        // 기존 ControllerAdvice(SDK나 기타 에러 핸들러)가 처리할 수 있도록, 
        // 404 상태코드 매핑 후 CustomWebErrorController로 넘깁니다. 
        // 만약 원래의 예외를 그대로 되돌려주고자 한다면, 주석 해제 후 throw ex; 를 사용해도 됩니다.
        request.setAttribute("jakarta.servlet.error.status_code", 404);
        request.setAttribute("jakarta.servlet.error.request_uri", uri);
        return "forward:/error-custom";
    }
}

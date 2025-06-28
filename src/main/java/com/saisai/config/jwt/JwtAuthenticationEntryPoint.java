package com.saisai.config.jwt;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.saisai.domain.common.exception.ExceptionResponse;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class JwtAuthenticationEntryPoint implements AuthenticationEntryPoint {

    private final ObjectMapper objectMapper;

    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response,
        AuthenticationException authException) throws IOException, ServletException {

        // JwtAuthenticationException에서 HTTP 상태 코드와 에러 코드를 가져옴
        int status;
        String code;
        String message;

        if (authException instanceof JwtAuthenticationException jwtEx) {
            status = jwtEx.getHttpStatus().value();
            code = jwtEx.getCode();
            message = jwtEx.getMessage();
            log.info(message);
        } else {
            log.info("왜 인증 안됨");
            status = HttpServletResponse.SC_UNAUTHORIZED;
            code = "UNAUTHORIZED";
            message = "인증되지 않은 사용자입니다.";
        }

        response.setStatus(status);
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setCharacterEncoding("UTF-8");

        ExceptionResponse errorResponse = new ExceptionResponse(code, message);

        PrintWriter out = response.getWriter();
        log.info(out.toString());
        objectMapper.writeValue(out, errorResponse);
        out.flush();

    }
}

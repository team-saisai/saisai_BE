package com.saisai.config.jwt;

import static com.saisai.domain.common.exception.ExceptionCode.ADMIN_REQUIRED;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.saisai.domain.common.exception.ExceptionResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import org.springframework.http.MediaType;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.stereotype.Component;

@Component
public class JwtAccessDeniedHandler implements AccessDeniedHandler {

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public void handle(HttpServletRequest request, HttpServletResponse response,
        AccessDeniedException accessDeniedException)
        throws IOException {
        response.setStatus(HttpServletResponse.SC_FORBIDDEN);
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setCharacterEncoding(StandardCharsets.UTF_8.name());

        ExceptionResponse body = new ExceptionResponse(ADMIN_REQUIRED.getCode(),
            ADMIN_REQUIRED.getMessage());
        String json = objectMapper.writeValueAsString(body);

        response.getWriter().write(json);
        response.getWriter().flush();
    }
}


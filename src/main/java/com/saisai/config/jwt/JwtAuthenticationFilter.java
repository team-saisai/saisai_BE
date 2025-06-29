package com.saisai.config.jwt;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.saisai.domain.common.exception.ExceptionResponse;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Arrays;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

@Component
@Slf4j
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtProvider jwtProvider;
    private final ObjectMapper objectMapper;

    private static final String HEADER_AUTHORIZATION = "Authorization";
    private static final String TOKEN_PREFIX = "Bearer ";

    private static final String[] EXCLUDE_URLS = {
        "/api/auth/reissue"
    };

    @Override
    protected void doFilterInternal(
        HttpServletRequest request,
        HttpServletResponse response,
        FilterChain filterChain) throws ServletException, IOException {

        // 필터 제외 경로 확인
        String requestUri = request.getRequestURI();
        if (Arrays.stream(EXCLUDE_URLS).anyMatch(requestUri::startsWith)) {
            filterChain.doFilter(request, response);
            return;
        }

        String authorizationHeader = request.getHeader(HEADER_AUTHORIZATION);

        if (authorizationHeader != null && authorizationHeader.startsWith(TOKEN_PREFIX)) {
            try {
                String token = jwtProvider.substringToken(authorizationHeader);

                if (jwtProvider.validToken(token)) {
                    AuthUserDetails userDetails = jwtProvider.getAuthentication(token);

                    UsernamePasswordAuthenticationToken authentication =
                        new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());

                    SecurityContextHolder.getContext().setAuthentication(authentication);
                }
            } catch (JwtAuthenticationException e ) {
                setErrorResponse(response, e);
                return;
            } catch (Exception e) {
                log.error("Internal server error", e);
                response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
                return;
            }
        }

        filterChain.doFilter(request, response);
    }

    private void setErrorResponse(HttpServletResponse response, JwtAuthenticationException e) throws IOException {
        response.setStatus(e.getHttpStatus().value());
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setCharacterEncoding("UTF-8");

        ExceptionResponse errorResponse = new ExceptionResponse(e.getCode(), e.getMessage());

        response.getWriter().write(objectMapper.writeValueAsString(errorResponse));
        response.getWriter().flush();
    }
}

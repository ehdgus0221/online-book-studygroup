package com.project.bookstudy.security.filter.handler;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.project.bookstudy.common.dto.ErrorResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

import static com.project.bookstudy.common.dto.ErrorCode.LOGIN_REQUIRED;

@RequiredArgsConstructor
@Slf4j
@Component
public class ApiAuthenticationEntryPoint implements AuthenticationEntryPoint {
    private final ObjectMapper objectMapper;

    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException authenticationException) throws IOException {
        ErrorResponse errorResponse = ErrorResponse.builder()
                .code(String.valueOf(HttpStatus.UNAUTHORIZED.value()))
                .message(String.valueOf(LOGIN_REQUIRED))
                .build();

        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setCharacterEncoding(StandardCharsets.UTF_8.name());
        response.setStatus(HttpStatus.UNAUTHORIZED.value());
        response.getWriter().write(objectMapper.writeValueAsString(errorResponse));

        log.error("ApiAuthenticationEntryPoint.handle: ", authenticationException);
    }
}

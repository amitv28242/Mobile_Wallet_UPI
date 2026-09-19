// FILE: src/main/java/com/mobilewallet/security/JwtAuthenticationEntryPoint.java
package com.mobilewallet.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mobilewallet.exception.ApiError;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.time.LocalDateTime;

@Component
public class JwtAuthenticationEntryPoint implements AuthenticationEntryPoint {

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response,
                         AuthenticationException authException) throws IOException, ServletException {
        
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.setContentType("application/json");

        ApiError apiError = ApiError.builder()
                .success(false)
                .message("Authentication failed: " + authException.getMessage())
                .code("UNAUTHORIZED")
                .timestamp(LocalDateTime.now())
                .status(HttpStatus.UNAUTHORIZED.value())
                .path(request.getRequestURI())
                .build();

        String jsonResponse = objectMapper.writeValueAsString(apiError);
        response.getWriter().write(jsonResponse);
    }
}
package com.lerado.drivee.exceptions.handlers;

import java.io.IOException;

import org.springframework.security.authentication.InsufficientAuthenticationException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

public class SecurityExceptionsHandler implements AuthenticationEntryPoint {

    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response,
            AuthenticationException authException) throws IOException, ServletException {

        int httpResponseStatus = HttpServletResponse.SC_FORBIDDEN;
        if (authException.getClass().getTypeName() == InsufficientAuthenticationException.class.getTypeName()) {
            httpResponseStatus = HttpServletResponse.SC_UNAUTHORIZED;
        }
        response.sendError(httpResponseStatus, authException.getMessage());
    }
}

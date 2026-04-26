package com.app.config.security.configuration;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class SecurityAuditHandlers implements AuthenticationEntryPoint, AccessDeniedHandler {

    private static final Logger log = LoggerFactory.getLogger(SecurityAuditHandlers.class);

    @Override
    public void commence(
            HttpServletRequest request,
            HttpServletResponse response,
            AuthenticationException authException
    ) throws IOException {
        log.warn("AUDIT acceso_no_autenticado metodo={} path={} detalle={}",
                request.getMethod(), request.getRequestURI(), authException.getMessage());
        response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "No autenticado");
    }

    @Override
    public void handle(
            HttpServletRequest request,
            HttpServletResponse response,
            AccessDeniedException accessDeniedException
    ) throws IOException {
        Authentication authentication = (Authentication) request.getUserPrincipal();
        String principal = authentication != null ? authentication.getName() : "anonimo";
        log.warn("AUDIT acceso_denegado usuario={} metodo={} path={} detalle={}",
                principal, request.getMethod(), request.getRequestURI(), accessDeniedException.getMessage());
        response.sendError(HttpServletResponse.SC_FORBIDDEN, "Acceso denegado");
    }
}

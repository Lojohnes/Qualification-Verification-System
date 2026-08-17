package com.aqvp.platform.identity.security;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.stereotype.Component;

/**
 * Handles access denied errors by returning a 403 response.
 *
 * <p>Writes the response directly instead of calling {@code response.sendError(...)}, which would
 * trigger the servlet container's error-page forward to {@code /error}. That internal forward
 * re-enters the Spring Security filter chain and, since {@code /error} is not permitted, would
 * incorrectly overwrite this 403 with a 401 from {@link JwtAuthenticationEntryPoint}.
 */
@Component
@Slf4j
public class JwtAccessDeniedHandler implements AccessDeniedHandler {

    @Override
    public void handle(HttpServletRequest request, HttpServletResponse response,
                       AccessDeniedException accessDeniedException) throws IOException {
        log.warn("Access denied to {}: {}", request.getRequestURI(), accessDeniedException.getMessage());
        response.setStatus(HttpServletResponse.SC_FORBIDDEN);
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.getWriter().write("{\"status\":403,\"error\":\"Forbidden\",\"message\":\"Access is denied\"}");
    }
}

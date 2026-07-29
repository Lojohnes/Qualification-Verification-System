package com.aqvp.platform.identity.security;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import org.junit.jupiter.api.Test;
import org.springframework.security.access.AccessDeniedException;

/**
 * Unit tests for {@link JwtAccessDeniedHandler}.
 */
class JwtAccessDeniedHandlerTest {

    private final JwtAccessDeniedHandler handler = new JwtAccessDeniedHandler();

    @Test
    void shouldSendForbiddenError() throws IOException {
        final HttpServletRequest request = mock(HttpServletRequest.class);
        final HttpServletResponse response = mock(HttpServletResponse.class);

        when(request.getRequestURI()).thenReturn("/api/v1/users");

        handler.handle(request, response, new AccessDeniedException("Forbidden"));

        verify(response).sendError(HttpServletResponse.SC_FORBIDDEN, "Forbidden");
    }
}

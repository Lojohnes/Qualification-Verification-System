package com.aqvp.platform.identity.security;

import com.aqvp.platform.identity.config.ApiClientConfig;
import com.aqvp.platform.identity.service.ApiClientService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.lang.NonNull;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

/**
 * Extracts API client credentials from request headers and authenticates machine clients.
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class ApiClientAuthenticationFilter extends OncePerRequestFilter {

    private final ApiClientConfig apiClientConfig;
    private final ApiClientService apiClientService;

    @Override
    protected void doFilterInternal(@NonNull HttpServletRequest request,
                                    @NonNull HttpServletResponse response,
                                    @NonNull FilterChain filterChain) throws ServletException, IOException {
        final String clientId = request.getHeader(apiClientConfig.headerClientId());
        final String clientSecret = request.getHeader(apiClientConfig.headerClientSecret());

        if (!StringUtils.hasText(clientId) || !StringUtils.hasText(clientSecret)) {
            filterChain.doFilter(request, response);
            return;
        }

        try {
            final Authentication authentication = apiClientService.authenticate(clientId, clientSecret);
            SecurityContextHolder.getContext().setAuthentication(authentication);
            log.debug("Authenticated API client '{}'", clientId);
        } catch (Exception ex) {
            log.warn("API client authentication failed: {}", ex.getMessage());
        }

        filterChain.doFilter(request, response);
    }
}

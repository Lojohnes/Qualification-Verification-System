package com.aqvp.platform.identity.security;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.aqvp.platform.identity.config.ApiClientConfig;
import com.aqvp.platform.identity.service.ApiClientService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.util.List;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;

/**
 * Unit tests for {@link ApiClientAuthenticationFilter}.
 */
@ExtendWith(MockitoExtension.class)
class ApiClientAuthenticationFilterTest {

    @Mock
    private ApiClientConfig apiClientConfig;

    @Mock
    private ApiClientService apiClientService;

    @InjectMocks
    private ApiClientAuthenticationFilter filter;

    @AfterEach
    void tearDown() {
        SecurityContextHolder.clearContext();
    }

    @Test
    void shouldAuthenticateApiClientWhenHeadersPresent() throws Exception {
        final HttpServletRequest request = mock(HttpServletRequest.class);
        final HttpServletResponse response = mock(HttpServletResponse.class);
        final FilterChain chain = mock(FilterChain.class);
        final Authentication authentication = new ApiClientAuthenticationToken(
            "client-1",
            null,
            List.of(new SimpleGrantedAuthority("role:read"))
        );

        when(apiClientConfig.headerClientId()).thenReturn("X-Api-Client-Id");
        when(apiClientConfig.headerClientSecret()).thenReturn("X-Api-Client-Secret");
        when(request.getHeader("X-Api-Client-Id")).thenReturn("client-1");
        when(request.getHeader("X-Api-Client-Secret")).thenReturn("secret");
        when(apiClientService.authenticate("client-1", "secret")).thenReturn(authentication);

        filter.doFilterInternal(request, response, chain);

        assertThat(SecurityContextHolder.getContext().getAuthentication()).isEqualTo(authentication);
        verify(chain).doFilter(request, response);
    }

    @Test
    void shouldContinueWhenClientIdHeaderMissing() throws Exception {
        final HttpServletRequest request = mock(HttpServletRequest.class);
        final HttpServletResponse response = mock(HttpServletResponse.class);
        final FilterChain chain = mock(FilterChain.class);

        when(apiClientConfig.headerClientId()).thenReturn("X-Api-Client-Id");
        when(apiClientConfig.headerClientSecret()).thenReturn("X-Api-Client-Secret");
        when(request.getHeader("X-Api-Client-Id")).thenReturn(null);

        filter.doFilterInternal(request, response, chain);

        assertThat(SecurityContextHolder.getContext().getAuthentication()).isNull();
        verify(chain).doFilter(request, response);
    }
}

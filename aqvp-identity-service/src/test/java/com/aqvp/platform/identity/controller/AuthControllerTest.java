package com.aqvp.platform.identity.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.aqvp.platform.identity.dto.AuthenticationRequest;
import com.aqvp.platform.identity.dto.AuthenticationResponse;
import com.aqvp.platform.identity.dto.ChangePasswordRequest;
import com.aqvp.platform.identity.dto.ForgotPasswordRequest;
import com.aqvp.platform.identity.dto.RefreshTokenRequest;
import com.aqvp.platform.identity.dto.ResetPasswordRequest;
import com.aqvp.platform.identity.dto.UserResponseDto;
import com.aqvp.platform.identity.service.AuthService;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.Set;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.boot.autoconfigure.security.servlet.SecurityFilterAutoConfiguration;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

/**
 * Web layer tests for {@link AuthController}.
 */
@WebMvcTest(
    controllers = AuthController.class,
    excludeAutoConfiguration = {
        SecurityAutoConfiguration.class,
        SecurityFilterAutoConfiguration.class
    },
    excludeFilters = @ComponentScan.Filter(
        type = FilterType.REGEX,
        pattern = "com\\.aqvp\\.platform\\.identity\\.(security|config)\\..*"
    )
)
@AutoConfigureMockMvc(addFilters = false)
@ActiveProfiles("test")
class AuthControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private AuthService authService;

    @Test
    void shouldReturnAuthenticationResponseOnLogin() throws Exception {
        final AuthenticationRequest request = new AuthenticationRequest("johndoe", "Password123!");
        final AuthenticationResponse response = new AuthenticationResponse("access", "refresh", "Bearer", 900L);

        when(authService.login(any(AuthenticationRequest.class))).thenReturn(response);

        mockMvc.perform(post("/api/v1/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.accessToken").value("access"))
            .andExpect(jsonPath("$.refreshToken").value("refresh"));
    }

    @Test
    void shouldReturnNoContentOnLogout() throws Exception {
        final RefreshTokenRequest request = new RefreshTokenRequest("refresh-token-123");

        mockMvc.perform(post("/api/v1/auth/logout")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isNoContent());
    }

    @Test
    void shouldReturnAuthenticationResponseOnRefresh() throws Exception {
        final RefreshTokenRequest request = new RefreshTokenRequest("refresh-token-123");
        final AuthenticationResponse response = new AuthenticationResponse("new-access", "new-refresh", "Bearer", 900L);

        when(authService.refresh(any(RefreshTokenRequest.class))).thenReturn(response);

        mockMvc.perform(post("/api/v1/auth/refresh")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.accessToken").value("new-access"))
            .andExpect(jsonPath("$.refreshToken").value("new-refresh"));
    }

    @Test
    void shouldReturnNoContentOnChangePassword() throws Exception {
        final ChangePasswordRequest request = new ChangePasswordRequest(
            "Current123!", "NewPass123!", "NewPass123!"
        );

        mockMvc.perform(post("/api/v1/auth/change-password")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isNoContent());
    }

    @Test
    void shouldReturnNoContentOnForgotPassword() throws Exception {
        final ForgotPasswordRequest request = new ForgotPasswordRequest("user@aqvp.local");

        mockMvc.perform(post("/api/v1/auth/forgot-password")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isAccepted());
    }

    @Test
    void shouldReturnNoContentOnResetPassword() throws Exception {
        final ResetPasswordRequest request = new ResetPasswordRequest(
            "reset-token", "NewPass123!", "NewPass123!"
        );

        mockMvc.perform(post("/api/v1/auth/reset-password")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isAccepted());
    }

    @Test
    void shouldReturnCurrentUserDetails() throws Exception {
        final UserResponseDto response = new UserResponseDto(
            UUID.randomUUID(), "johndoe", "john@aqvp.local", "John", "Doe",
            true, true, false, Set.of("USER"), Set.of("user:read")
        );

        when(authService.getCurrentUser()).thenReturn(response);

        mockMvc.perform(get("/api/v1/auth/me"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.username").value("johndoe"))
            .andExpect(jsonPath("$.email").value("john@aqvp.local"))
            .andExpect(jsonPath("$.roles[0]").value("USER"))
            .andExpect(jsonPath("$.permissions[0]").value("user:read"));
    }
}

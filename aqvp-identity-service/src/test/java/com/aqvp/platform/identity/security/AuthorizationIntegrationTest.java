package com.aqvp.platform.identity.security;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

/**
 * Integration tests for role-based access control on identity endpoints.
 */
@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class AuthorizationIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void shouldRequireAuthenticationForProtectedEndpoints() throws Exception {
        mockMvc.perform(get("/api/v1/users"))
            .andExpect(status().isUnauthorized());
    }

    @Test
    @WithMockUser(authorities = "user:read")
    void shouldAllowUserReadWithCorrectAuthority() throws Exception {
        mockMvc.perform(get("/api/v1/users"))
            .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(authorities = "user:read")
    void shouldDenyUserWriteWithReadOnlyAuthority() throws Exception {
        final String body = objectMapper.writeValueAsString(Map.of(
            "username", "testuser",
            "email", "test@aqvp.local",
            "password", "Password123!",
            "firstName", "Test",
            "lastName", "User",
            "roleIds", Set.of(UUID.randomUUID())
        ));

        mockMvc.perform(post("/api/v1/users")
                .contentType(MediaType.APPLICATION_JSON)
                .content(body))
            .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(authorities = "user:write")
    void shouldAllowUserWriteWithCorrectAuthority() throws Exception {
        final String body = objectMapper.writeValueAsString(Map.of(
            "username", "writeuser",
            "email", "write@aqvp.local",
            "password", "Password123!",
            "firstName", "Write",
            "lastName", "User",
            "roleIds", Set.of(UUID.randomUUID())
        ));

        mockMvc.perform(post("/api/v1/users")
                .contentType(MediaType.APPLICATION_JSON)
                .content(body))
            .andExpect(status().isNotFound());
    }
}

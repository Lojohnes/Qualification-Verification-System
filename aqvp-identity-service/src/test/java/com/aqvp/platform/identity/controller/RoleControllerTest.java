package com.aqvp.platform.identity.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.aqvp.platform.identity.dto.RoleRequestDto;
import com.aqvp.platform.identity.dto.RoleResponseDto;
import com.aqvp.platform.identity.service.RoleService;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.List;
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
 * Web layer tests for {@link RoleController}.
 */
@WebMvcTest(
    controllers = RoleController.class,
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
class RoleControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private RoleService roleService;

    @Test
    void shouldReturnAllRoles() throws Exception {
        final RoleResponseDto role = new RoleResponseDto(
            UUID.randomUUID(), "USER", "Standard user", Set.of("user:read")
        );
        when(roleService.findAll()).thenReturn(List.of(role));

        mockMvc.perform(get("/api/v1/roles"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$[0].name").value("USER"));
    }

    @Test
    void shouldReturnRoleById() throws Exception {
        final UUID id = UUID.randomUUID();
        final RoleResponseDto role = new RoleResponseDto(id, "ADMIN", "Administrator", Set.of("user:read"));
        when(roleService.findById(id)).thenReturn(role);

        mockMvc.perform(get("/api/v1/roles/{id}", id))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.name").value("ADMIN"));
    }

    @Test
    void shouldCreateRole() throws Exception {
        final UUID id = UUID.randomUUID();
        final RoleRequestDto request = new RoleRequestDto("MANAGER", "Manager", Set.of());
        final RoleResponseDto response = new RoleResponseDto(id, "MANAGER", "Manager", Set.of());

        when(roleService.createRole(any(RoleRequestDto.class))).thenReturn(response);

        mockMvc.perform(post("/api/v1/roles")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isCreated())
            .andExpect(header().string("Location", "http://localhost/api/v1/roles/" + id))
            .andExpect(jsonPath("$.name").value("MANAGER"));
    }

    @Test
    void shouldUpdateRole() throws Exception {
        final UUID id = UUID.randomUUID();
        final RoleRequestDto request = new RoleRequestDto("MANAGER", "Updated", Set.of());
        final RoleResponseDto response = new RoleResponseDto(id, "MANAGER", "Updated", Set.of());

        when(roleService.updateRole(any(UUID.class), any(RoleRequestDto.class))).thenReturn(response);

        mockMvc.perform(put("/api/v1/roles/{id}", id)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.description").value("Updated"));
    }
}

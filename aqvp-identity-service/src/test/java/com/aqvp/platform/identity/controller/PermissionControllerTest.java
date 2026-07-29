package com.aqvp.platform.identity.controller;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.aqvp.platform.identity.dto.PermissionDto;
import com.aqvp.platform.identity.service.PermissionService;
import java.util.List;
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
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

/**
 * Web layer tests for {@link PermissionController}.
 */
@WebMvcTest(
    controllers = PermissionController.class,
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
class PermissionControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private PermissionService permissionService;

    @Test
    void shouldReturnAllPermissions() throws Exception {
        final PermissionDto permission = new PermissionDto(
            UUID.randomUUID(), "user:read", "user", "read", "Read users"
        );
        when(permissionService.findAll()).thenReturn(List.of(permission));

        mockMvc.perform(get("/api/v1/permissions"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$[0].name").value("user:read"));
    }
}

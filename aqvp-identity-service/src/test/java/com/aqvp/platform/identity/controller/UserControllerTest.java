package com.aqvp.platform.identity.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.aqvp.platform.identity.dto.UserRequestDto;
import com.aqvp.platform.identity.dto.UserResponseDto;
import com.aqvp.platform.identity.dto.UserUpdateRequestDto;
import com.aqvp.platform.identity.repository.UserRepository;
import com.aqvp.platform.identity.service.UserService;
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
 * Web layer tests for {@link UserController}.
 */
@WebMvcTest(
    controllers = UserController.class,
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
class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private UserService userService;

    @MockBean
    private UserRepository userRepository;

    @Test
    void shouldReturnAllUsers() throws Exception {
        final UserResponseDto user = new UserResponseDto(
            UUID.randomUUID(), "johndoe", "john@aqvp.local", "John", "Doe",
            true, false, false, Set.of("USER")
        );
        when(userService.findAll()).thenReturn(List.of(user));

        mockMvc.perform(get("/api/v1/users"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$[0].username").value("johndoe"));
    }

    @Test
    void shouldCreateUser() throws Exception {
        final UUID id = UUID.randomUUID();
        final UserRequestDto request = new UserRequestDto(
            "johndoe", "john@aqvp.local", "Password123!", "John", "Doe", Set.of(UUID.randomUUID())
        );
        final UserResponseDto response = new UserResponseDto(
            id, request.username(), request.email(), request.firstName(), request.lastName(),
            true, false, false, Set.of("USER")
        );

        when(userRepository.existsByUsername(request.username())).thenReturn(false);
        when(userRepository.existsByEmail(request.email())).thenReturn(false);
        when(userService.createUser(any(UserRequestDto.class))).thenReturn(response);

        mockMvc.perform(post("/api/v1/users")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isCreated())
            .andExpect(header().string("Location", "http://localhost/api/v1/users/" + id))
            .andExpect(jsonPath("$.id").value(id.toString()));
    }

    @Test
    void shouldUpdateUser() throws Exception {
        final UUID id = UUID.randomUUID();
        final UserUpdateRequestDto request = new UserUpdateRequestDto(
            "john@aqvp.local", "John", "Doe", true, Set.of(UUID.randomUUID())
        );
        final UserResponseDto response = new UserResponseDto(
            id, "johndoe", request.email(), request.firstName(), request.lastName(),
            true, false, false, Set.of("USER")
        );

        when(userService.updateUser(any(UUID.class), any(UserUpdateRequestDto.class))).thenReturn(response);

        mockMvc.perform(put("/api/v1/users/{id}", id)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.email").value("john@aqvp.local"));
    }

    @Test
    void shouldDeleteUser() throws Exception {
        final UUID id = UUID.randomUUID();

        mockMvc.perform(delete("/api/v1/users/{id}", id))
            .andExpect(status().isNoContent());
    }
}

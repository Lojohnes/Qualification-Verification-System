package com.aqvp.platform.identity.exception;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.setup.MockMvcBuilders.standaloneSetup;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.ResponseEntity;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Tests for {@link GlobalExceptionHandler} using a standalone MockMvc setup.
 */
class GlobalExceptionHandlerTest {

    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        mockMvc = standaloneSetup(new TestController())
            .setControllerAdvice(new GlobalExceptionHandler())
            .build();
    }

    @Test
    void shouldReturnNotFoundForResourceNotFound() throws Exception {
        mockMvc.perform(get("/test/not-found"))
            .andExpect(status().isNotFound())
            .andExpect(jsonPath("$.message").value("Missing"));
    }

    @Test
    void shouldReturnConflictForDuplicateResource() throws Exception {
        mockMvc.perform(get("/test/conflict"))
            .andExpect(status().isConflict())
            .andExpect(jsonPath("$.message").value("Duplicate"));
    }

    @Test
    void shouldReturnUnauthorizedForInvalidCredentials() throws Exception {
        mockMvc.perform(get("/test/unauthorized"))
            .andExpect(status().isUnauthorized())
            .andExpect(jsonPath("$.message").value("Invalid credentials"));
    }

    @Test
    void shouldReturnBadRequestForBusinessException() throws Exception {
        mockMvc.perform(get("/test/bad-request"))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.message").value("Business error"));
    }

    @RestController
    @RequestMapping("/test")
    static class TestController {

        @GetMapping("/not-found")
        ResponseEntity<String> notFound() {
            throw new EntityNotFoundException("Missing");
        }

        @GetMapping("/conflict")
        ResponseEntity<String> conflict() {
            throw new DuplicateResourceException("Duplicate");
        }

        @GetMapping("/unauthorized")
        ResponseEntity<String> unauthorized() {
            throw new InvalidCredentialsException("Bad credentials");
        }

        @GetMapping("/bad-request")
        ResponseEntity<String> badRequest() {
            throw new PasswordMismatchException("Business error");
        }
    }
}

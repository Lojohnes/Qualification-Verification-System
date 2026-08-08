package com.aqvp.platform.identity.dto;

import jakarta.validation.constraints.NotBlank;

/**
 * DTO for user authentication requests.
 */
public record AuthenticationRequest(
    @NotBlank(message = "Username or email is required")
    String usernameOrEmail,

    @NotBlank(message = "Password is required")
    String password
) {
}

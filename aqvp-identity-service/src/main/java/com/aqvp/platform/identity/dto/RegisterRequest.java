package com.aqvp.platform.identity.dto;

import com.aqvp.platform.identity.validator.StrongPassword;
import com.aqvp.platform.identity.validator.UniqueEmail;
import com.aqvp.platform.identity.validator.UniqueUsername;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

/**
 * DTO for self-registering a new account. Only the very first account created in the
 * system may register this way; it is automatically granted administrator privileges.
 */
public record RegisterRequest(
    @NotBlank(message = "Username is required")
    @UniqueUsername
    String username,

    @NotBlank(message = "Email is required")
    @Email(message = "Email must be valid")
    @UniqueEmail
    String email,

    @NotBlank(message = "Password is required")
    @StrongPassword
    String password,

    String firstName,

    String lastName
) {
}

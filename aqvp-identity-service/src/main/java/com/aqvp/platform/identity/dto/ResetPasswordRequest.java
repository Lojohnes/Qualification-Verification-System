package com.aqvp.platform.identity.dto;

import com.aqvp.platform.identity.validator.StrongPassword;
import jakarta.validation.constraints.NotBlank;

/**
 * DTO for reset-password requests.
 */
public record ResetPasswordRequest(
    @NotBlank(message = "Token is required")
    String token,

    @NotBlank(message = "New password is required")
    @StrongPassword
    String newPassword,

    @NotBlank(message = "Password confirmation is required")
    String confirmPassword
) {
}

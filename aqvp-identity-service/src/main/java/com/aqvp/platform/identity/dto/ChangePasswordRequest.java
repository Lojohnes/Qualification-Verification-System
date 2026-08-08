package com.aqvp.platform.identity.dto;

import com.aqvp.platform.identity.validator.StrongPassword;
import jakarta.validation.constraints.NotBlank;

/**
 * DTO for changing the authenticated user's password.
 */
public record ChangePasswordRequest(
    @NotBlank(message = "Current password is required")
    String currentPassword,

    @NotBlank(message = "New password is required")
    @StrongPassword
    String newPassword,

    @NotBlank(message = "Password confirmation is required")
    String confirmPassword
) {
}

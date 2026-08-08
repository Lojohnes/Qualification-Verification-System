package com.aqvp.platform.identity.dto;

import com.aqvp.platform.identity.validator.StrongPassword;
import com.aqvp.platform.identity.validator.UniqueEmail;
import com.aqvp.platform.identity.validator.UniqueUsername;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import java.util.Set;

/**
 * DTO for creating a new user.
 */
public record UserRequestDto(
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

    String lastName,

    @NotEmpty(message = "At least one role is required")
    Set<java.util.UUID> roleIds
) {
    public UserRequestDto {
        roleIds = roleIds == null ? java.util.Set.of() : java.util.Set.copyOf(roleIds);
    }
}

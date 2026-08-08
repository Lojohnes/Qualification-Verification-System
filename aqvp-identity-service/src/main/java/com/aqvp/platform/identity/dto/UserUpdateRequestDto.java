package com.aqvp.platform.identity.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import java.util.Set;

/**
 * DTO for updating an existing user.
 */
public record UserUpdateRequestDto(
    @NotBlank(message = "Email is required")
    @Email(message = "Email must be valid")
    String email,

    String firstName,

    String lastName,

    Boolean enabled,

    Set<java.util.UUID> roleIds
) {
    public UserUpdateRequestDto {
        roleIds = roleIds == null ? java.util.Set.of() : java.util.Set.copyOf(roleIds);
    }
}

package com.aqvp.platform.identity.dto;

import jakarta.validation.constraints.NotBlank;
import java.util.Set;
import java.util.UUID;

/**
 * DTO for creating or updating a role.
 */
public record RoleRequestDto(
    @NotBlank(message = "Role name is required")
    String name,

    String description,

    Set<UUID> permissionIds
) {
    public RoleRequestDto {
        permissionIds = permissionIds == null ? Set.of() : Set.copyOf(permissionIds);
    }
}

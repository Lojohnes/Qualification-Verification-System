package com.aqvp.platform.identity.dto;

import java.util.Set;
import java.util.UUID;

/**
 * DTO representing a role in API responses.
 */
public record RoleResponseDto(
    UUID id,
    String name,
    String description,
    Set<String> permissions
) {
    public RoleResponseDto {
        permissions = permissions == null ? Set.of() : Set.copyOf(permissions);
    }
}

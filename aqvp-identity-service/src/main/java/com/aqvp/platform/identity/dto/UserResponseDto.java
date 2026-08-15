package com.aqvp.platform.identity.dto;

import java.util.Set;
import java.util.UUID;

/**
 * DTO representing a user in API responses.
 */
public record UserResponseDto(
    UUID id,
    String username,
    String email,
    String firstName,
    String lastName,
    Boolean enabled,
    Boolean emailVerified,
    Boolean mfaEnabled,
    Set<String> roles,
    Set<String> permissions
) {
    public UserResponseDto {
        roles = roles == null ? Set.of() : Set.copyOf(roles);
        permissions = permissions == null ? Set.of() : Set.copyOf(permissions);
    }
}

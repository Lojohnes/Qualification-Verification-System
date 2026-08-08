package com.aqvp.platform.identity.dto;

import java.util.UUID;

/**
 * DTO representing a permission in API responses.
 */
public record PermissionDto(
    UUID id,
    String name,
    String resource,
    String action,
    String description
) {
}

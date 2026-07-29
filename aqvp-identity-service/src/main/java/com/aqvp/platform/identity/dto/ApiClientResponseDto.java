package com.aqvp.platform.identity.dto;

import java.util.Set;
import java.util.UUID;

/**
 * DTO representing an API client in API responses.
 */
public record ApiClientResponseDto(
    UUID id,
    String clientId,
    String name,
    String description,
    Boolean enabled,
    Set<String> authorities
) {
    public ApiClientResponseDto {
        authorities = authorities == null ? Set.of() : Set.copyOf(authorities);
    }
}

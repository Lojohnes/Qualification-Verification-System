package com.aqvp.platform.identity.dto;

import jakarta.validation.constraints.NotBlank;
import java.util.Set;

/**
 * DTO for creating or updating an API client.
 */
public record ApiClientRequestDto(
    @NotBlank(message = "Client id is required")
    String clientId,

    @NotBlank(message = "Client secret is required")
    String clientSecret,

    @NotBlank(message = "Client name is required")
    String name,

    String description,

    Boolean enabled,

    Set<String> authorities
) {
    public ApiClientRequestDto {
        authorities = authorities == null ? Set.of() : Set.copyOf(authorities);
    }
}

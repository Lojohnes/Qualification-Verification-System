package com.aqvp.platform.identity.dto;

import jakarta.validation.constraints.NotBlank;

/**
 * DTO for API key authentication requests.
 */
public record ApiKeyAuthenticationRequest(
    @NotBlank(message = "Client id is required")
    String clientId,

    @NotBlank(message = "Client secret is required")
    String clientSecret
) {
}

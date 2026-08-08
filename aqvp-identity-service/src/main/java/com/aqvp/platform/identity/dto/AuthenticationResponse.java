package com.aqvp.platform.identity.dto;

/**
 * DTO returned on successful authentication or token refresh.
 */
public record AuthenticationResponse(
    String accessToken,
    String refreshToken,
    String tokenType,
    Long expiresIn
) {
}

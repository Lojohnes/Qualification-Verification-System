package com.aqvp.platform.identity.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * Externalized JWT configuration properties.
 */
@ConfigurationProperties(prefix = "aqvp.security.jwt")
public record JwtConfig(String secret, Long accessTokenExpirationMs, Long refreshTokenExpirationMs) {
}

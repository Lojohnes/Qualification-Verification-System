package com.aqvp.platform.identity.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * Externalized API client authentication header configuration.
 */
@ConfigurationProperties(prefix = "aqvp.security.api-client")
public record ApiClientConfig(String headerClientId, String headerClientSecret) {
}

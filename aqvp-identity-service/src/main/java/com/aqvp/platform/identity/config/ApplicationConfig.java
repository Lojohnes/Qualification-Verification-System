package com.aqvp.platform.identity.config;

import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

/**
 * Application-level bean definitions for the identity module.
 */
@Configuration
@EnableConfigurationProperties({JwtConfig.class, ApiClientConfig.class})
public class ApplicationConfig {

    /**
     * Configures BCrypt as the password encoder.
     *
     * @return a {@link BCryptPasswordEncoder} instance
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}

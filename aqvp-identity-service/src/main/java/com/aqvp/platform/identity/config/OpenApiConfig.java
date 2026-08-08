package com.aqvp.platform.identity.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * OpenAPI/Swagger configuration for the identity module.
 */
@Configuration
public class OpenApiConfig {

    /**
     * Configures the OpenAPI documentation metadata.
     *
     * @return the OpenAPI bean
     */
    @Bean
    public OpenAPI identityOpenApi() {
        return new OpenAPI()
            .info(new Info()
                .title("AQVP Identity & Access API")
                .description("Authentication, authorization and user management endpoints.")
                .version("1.0.0"));
    }
}

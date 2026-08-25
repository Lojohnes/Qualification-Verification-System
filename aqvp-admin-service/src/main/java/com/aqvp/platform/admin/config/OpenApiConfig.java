package com.aqvp.platform.admin.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI adminOpenApi() {
        return new OpenAPI()
            .info(new Info()
                .title("AQVP Admin API")
                .description("Administrative audit and notification endpoints")
                .version("1.0.0"));
    }
}

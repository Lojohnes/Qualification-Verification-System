package com.aqvp.platform.verification;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

/**
 * Bootstrap for the VerificationService service.
 */
@SpringBootApplication
@EnableJpaRepositories(basePackages = "com.aqvp.platform.verification.repository")
@EntityScan(basePackages = "com.aqvp.platform.verification.domain")
public class VerificationServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(VerificationServiceApplication.class, args);
    }
}


package com.aqvp.platform.identity.integration;

import static org.assertj.core.api.Assertions.assertThat;

import com.aqvp.platform.identity.support.EnabledIfDocker;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import javax.sql.DataSource;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

/**
 * Integration test that verifies Flyway migrations and seed data against a real PostgreSQL instance.
 */
@SpringBootTest
@Testcontainers
@EnabledIfDocker
@ActiveProfiles("test")
class FlywayMigrationIntegrationTest {

    @Container
    private static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:16-alpine")
        .withDatabaseName("identity_test")
        .withUsername("test")
        .withPassword("test");

    @DynamicPropertySource
    static void configure(final DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", postgres::getJdbcUrl);
        registry.add("spring.datasource.username", postgres::getUsername);
        registry.add("spring.datasource.password", postgres::getPassword);
        registry.add("spring.datasource.driver-class-name", postgres::getDriverClassName);
        registry.add("spring.flyway.enabled", () -> "true");
        registry.add("spring.jpa.hibernate.ddl-auto", () -> "none");
    }

    @Autowired
    private DataSource dataSource;

    @Test
    void shouldApplyMigrationsAndSeedReferenceData() throws SQLException {
        assertThat(dataSource.getConnection().isValid(5)).isTrue();

        final JdbcTemplate jdbcTemplate = new JdbcTemplate(dataSource);

        final List<String> tables = jdbcTemplate.query(
            "SELECT table_name FROM information_schema.tables WHERE table_schema = 'public'",
            new RowMapper<String>() {
                @Override
                public String mapRow(final ResultSet rs, final int rowNum) throws SQLException {
                    return rs.getString("table_name");
                }
            }
        );
        assertThat(tables).contains(
            "users", "roles", "permissions", "users_roles", "roles_permissions",
            "refresh_tokens", "api_clients", "api_client_authorities"
        );

        final Integer roleCount = jdbcTemplate.queryForObject(
            "SELECT count(*) FROM roles", Integer.class);
        assertThat(roleCount).isEqualTo(2);

        final Integer permissionCount = jdbcTemplate.queryForObject(
            "SELECT count(*) FROM permissions", Integer.class);
        assertThat(permissionCount).isEqualTo(5);

        final Integer adminCount = jdbcTemplate.queryForObject(
            "SELECT count(*) FROM users WHERE username = 'admin'", Integer.class);
        assertThat(adminCount).isEqualTo(1);
    }
}

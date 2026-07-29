package com.aqvp.platform.identity.testing;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

/**
 * Utility test that generates BCrypt hashes and a development seed SQL file.
 * Run manually with:
 * mvn -pl aqvp-identity-service -Dtest=PasswordHashGenerator test
 */
@Disabled("Run manually to regenerate the dev seed SQL file")
class PasswordHashGenerator {

    private static final String SEED_TEMPLATE = """
        -- Development seed data for the AQVP Identity Service.
        -- Run against a PostgreSQL database after migrations have been applied.

        INSERT INTO roles (id, name, description, version)
        VALUES
            (gen_random_uuid(), 'SYSTEM_ADMIN', 'Full platform access', 0),
            (gen_random_uuid(), 'INSTITUTION_ADMIN', 'Institution-level administration', 0),
            (gen_random_uuid(), 'REGISTRAR', 'Manages academic records', 0),
            (gen_random_uuid(), 'VERIFIER', 'Verifies qualifications', 0),
            (gen_random_uuid(), 'AUDITOR', 'Read-only audit access', 0)
        ON CONFLICT (name) DO NOTHING;

        WITH role_ids AS (
            SELECT id, name FROM roles WHERE name IN (
                'SYSTEM_ADMIN', 'INSTITUTION_ADMIN', 'REGISTRAR', 'VERIFIER', 'AUDITOR', 'ADMIN', 'USER'
            )
        ),
        perms AS (
            SELECT id, name FROM permissions WHERE name IN (
                'user:read', 'user:write', 'user:delete', 'role:read', 'role:write'
            )
        )
        INSERT INTO roles_permissions (role_id, permission_id)
        SELECT r.id, p.id
        FROM role_ids r
        CROSS JOIN perms p
        WHERE r.name = 'SYSTEM_ADMIN'
          AND NOT EXISTS (
              SELECT 1 FROM roles_permissions rp WHERE rp.role_id = r.id AND rp.permission_id = p.id
          );

        INSERT INTO roles_permissions (role_id, permission_id)
        SELECT r.id, p.id
        FROM role_ids r
        JOIN perms p ON p.name IN ('user:read', 'user:write', 'role:read')
        WHERE r.name = 'INSTITUTION_ADMIN'
          AND NOT EXISTS (
              SELECT 1 FROM roles_permissions rp WHERE rp.role_id = r.id AND rp.permission_id = p.id
          );

        INSERT INTO roles_permissions (role_id, permission_id)
        SELECT r.id, p.id
        FROM role_ids r
        JOIN perms p ON p.name IN ('user:read', 'user:write')
        WHERE r.name = 'REGISTRAR'
          AND NOT EXISTS (
              SELECT 1 FROM roles_permissions rp WHERE rp.role_id = r.id AND rp.permission_id = p.id
          );

        INSERT INTO roles_permissions (role_id, permission_id)
        SELECT r.id, p.id
        FROM role_ids r
        JOIN perms p ON p.name = 'user:read'
        WHERE r.name IN ('VERIFIER', 'AUDITOR')
          AND NOT EXISTS (
              SELECT 1 FROM roles_permissions rp WHERE rp.role_id = r.id AND rp.permission_id = p.id
          );

        INSERT INTO users (id, username, email, password, first_name, last_name, enabled,
                           account_non_expired, account_non_locked, credentials_non_expired,
                           email_verified, mfa_enabled, version)
        SELECT gen_random_uuid(),
               'system_admin',
               'system_admin@aqvp.local',
               '%s',
               'System', 'Administrator', true, true, true, true, true, false, 0
        WHERE NOT EXISTS (SELECT 1 FROM users WHERE username = 'system_admin');

        INSERT INTO users (id, username, email, password, first_name, last_name, enabled,
                           account_non_expired, account_non_locked, credentials_non_expired,
                           email_verified, mfa_enabled, version)
        SELECT gen_random_uuid(),
               'institution_admin',
               'institution_admin@aqvp.local',
               '%s',
               'Institution', 'Administrator', true, true, true, true, true, false, 0
        WHERE NOT EXISTS (SELECT 1 FROM users WHERE username = 'institution_admin');

        INSERT INTO users (id, username, email, password, first_name, last_name, enabled,
                           account_non_expired, account_non_locked, credentials_non_expired,
                           email_verified, mfa_enabled, version)
        SELECT gen_random_uuid(),
               'registrar',
               'registrar@aqvp.local',
               '%s',
               'Registry', 'Officer', true, true, true, true, true, false, 0
        WHERE NOT EXISTS (SELECT 1 FROM users WHERE username = 'registrar');

        INSERT INTO users (id, username, email, password, first_name, last_name, enabled,
                           account_non_expired, account_non_locked, credentials_non_expired,
                           email_verified, mfa_enabled, version)
        SELECT gen_random_uuid(),
               'verifier',
               'verifier@aqvp.local',
               '%s',
               'Verifier', 'User', true, true, true, true, true, false, 0
        WHERE NOT EXISTS (SELECT 1 FROM users WHERE username = 'verifier');

        INSERT INTO users (id, username, email, password, first_name, last_name, enabled,
                           account_non_expired, account_non_locked, credentials_non_expired,
                           email_verified, mfa_enabled, version)
        SELECT gen_random_uuid(),
               'auditor',
               'auditor@aqvp.local',
               '%s',
               'Audit', 'User', true, true, true, true, true, false, 0
        WHERE NOT EXISTS (SELECT 1 FROM users WHERE username = 'auditor');

        -- Update admin password in case the default seed changed
        UPDATE users SET password = '%s' WHERE username = 'admin';

        INSERT INTO users_roles (user_id, role_id)
        SELECT u.id, r.id
        FROM users u
        JOIN roles r ON r.name = UPPER(u.username)
        WHERE u.username IN ('system_admin', 'institution_admin', 'registrar', 'verifier', 'auditor')
          AND NOT EXISTS (
              SELECT 1 FROM users_roles ur WHERE ur.user_id = u.id AND ur.role_id = r.id
          );
        """;

    @Test
    void generateDevSeedSql() throws IOException {
        final BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();

        final String adminPassword = encoder.encode("Admin123!");
        final String userPassword = encoder.encode("Password123!");
        final String sql = SEED_TEMPLATE.formatted(
            adminPassword, userPassword, userPassword, userPassword, userPassword, adminPassword);

        final Path path = Paths.get("src/test/resources/testing/sql/dev_seed.sql");
        Files.createDirectories(path.getParent());
        Files.writeString(path, sql);
    }
}

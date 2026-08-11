CREATE EXTENSION IF NOT EXISTS pgcrypto;

WITH inserted_permissions AS (
    INSERT INTO permissions (id, name, resource, action, description, version)
    VALUES
        (gen_random_uuid(), 'user:read',  'user',  'read',  'Read users',        0),
        (gen_random_uuid(), 'user:write', 'user',  'write', 'Create or update users', 0),
        (gen_random_uuid(), 'user:delete','user',  'delete','Delete users',      0),
        (gen_random_uuid(), 'role:read',  'role',  'read',  'Read roles and permissions', 0),
        (gen_random_uuid(), 'role:write', 'role',  'write', 'Create or update roles', 0)
    ON CONFLICT (name) DO NOTHING
    RETURNING id, name
), existing_permissions AS (
    SELECT id, name FROM permissions
), all_permissions AS (
    SELECT id, name FROM inserted_permissions
    UNION ALL
    SELECT id, name FROM existing_permissions
    WHERE existing_permissions.name IN (
        'user:read', 'user:write', 'user:delete', 'role:read', 'role:write'
    )
), inserted_roles AS (
    INSERT INTO roles (id, name, description, version)
    VALUES
        (gen_random_uuid(), 'ADMIN', 'Administrator with full access', 0),
        (gen_random_uuid(), 'USER',  'Standard application user',      0)
    ON CONFLICT (name) DO NOTHING
    RETURNING id, name
), existing_roles AS (
    SELECT id, name FROM roles
), all_roles AS (
    SELECT id, name FROM inserted_roles
    UNION ALL
    SELECT id, name FROM existing_roles
    WHERE existing_roles.name IN ('ADMIN', 'USER')
)
INSERT INTO roles_permissions (role_id, permission_id)
SELECT r.id, p.id
FROM all_roles r
CROSS JOIN all_permissions p
WHERE r.name = 'ADMIN'
  AND NOT EXISTS (
      SELECT 1 FROM roles_permissions rp WHERE rp.role_id = r.id AND rp.permission_id = p.id
  );

INSERT INTO roles_permissions (role_id, permission_id)
SELECT r.id, p.id
FROM roles r
JOIN permissions p ON p.name = 'user:read'
WHERE r.name = 'USER'
  AND NOT EXISTS (
      SELECT 1 FROM roles_permissions rp WHERE rp.role_id = r.id AND rp.permission_id = p.id
  );

INSERT INTO users (id, username, email, password, first_name, last_name, enabled,
                   account_non_expired, account_non_locked, credentials_non_expired,
                   email_verified, mfa_enabled, version)
SELECT gen_random_uuid(),
       'admin',
       'admin@aqvp.local',
       crypt('Admin123!', gen_salt('bf')),
       'System',
       'Administrator',
       true,
       true,
       true,
       true,
       true,
       false,
       0
WHERE NOT EXISTS (SELECT 1 FROM users WHERE username = 'admin');

INSERT INTO users_roles (user_id, role_id)
SELECT u.id, r.id
FROM users u
JOIN roles r ON r.name = 'ADMIN'
WHERE u.username = 'admin'
  AND NOT EXISTS (
      SELECT 1 FROM users_roles ur WHERE ur.user_id = u.id AND ur.role_id = r.id
  );

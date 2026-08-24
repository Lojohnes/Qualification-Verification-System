WITH inserted_permissions AS (
    INSERT INTO permissions (id, name, resource, action, description, version)
    VALUES
        (gen_random_uuid(), 'verification:read',  'verification', 'read',  'Read verification history', 0),
        (gen_random_uuid(), 'verification:write', 'verification', 'write', 'Verify a qualification', 0)
    ON CONFLICT (name) DO NOTHING
    RETURNING id, name
), existing_permissions AS (
    SELECT id, name FROM permissions
    WHERE name IN ('verification:read', 'verification:write')
), all_new_permissions AS (
    SELECT id, name FROM inserted_permissions
    UNION ALL
    SELECT id, name FROM existing_permissions
    WHERE existing_permissions.name NOT IN (SELECT name FROM inserted_permissions)
)
INSERT INTO roles_permissions (role_id, permission_id)
SELECT r.id, p.id
FROM roles r
CROSS JOIN all_new_permissions p
WHERE r.name = 'ADMIN'
  AND NOT EXISTS (
      SELECT 1 FROM roles_permissions rp WHERE rp.role_id = r.id AND rp.permission_id = p.id
  );

INSERT INTO roles_permissions (role_id, permission_id)
SELECT r.id, p.id
FROM roles r
JOIN permissions p ON p.name IN ('verification:read', 'verification:write')
WHERE r.name = 'USER'
  AND NOT EXISTS (
      SELECT 1 FROM roles_permissions rp WHERE rp.role_id = r.id AND rp.permission_id = p.id
  );

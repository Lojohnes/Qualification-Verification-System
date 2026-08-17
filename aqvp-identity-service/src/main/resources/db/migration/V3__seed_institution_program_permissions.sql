WITH inserted_permissions AS (
    INSERT INTO permissions (id, name, resource, action, description, version)
    VALUES
        (gen_random_uuid(), 'institution:read',  'institution', 'read',  'Read institutions', 0),
        (gen_random_uuid(), 'institution:write', 'institution', 'write', 'Create or update institutions', 0),
        (gen_random_uuid(), 'program:read',      'program',     'read',  'Read academic programs', 0),
        (gen_random_uuid(), 'program:write',     'program',     'write', 'Create or update academic programs', 0)
    ON CONFLICT (name) DO NOTHING
    RETURNING id, name
), existing_permissions AS (
    SELECT id, name FROM permissions
    WHERE name IN ('institution:read', 'institution:write', 'program:read', 'program:write')
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
JOIN permissions p ON p.name IN ('institution:read', 'program:read')
WHERE r.name = 'USER'
  AND NOT EXISTS (
      SELECT 1 FROM roles_permissions rp WHERE rp.role_id = r.id AND rp.permission_id = p.id
  );

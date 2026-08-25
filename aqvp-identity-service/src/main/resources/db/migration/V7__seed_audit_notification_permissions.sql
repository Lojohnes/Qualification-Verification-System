WITH inserted_permissions AS (
    INSERT INTO permissions (id, name, resource, action, description, version)
    VALUES
        (gen_random_uuid(), 'audit:read', 'audit', 'read', 'Read audit events', 0),
        (gen_random_uuid(), 'notification:write', 'notification', 'write', 'Send notifications', 0)
    ON CONFLICT (name) DO NOTHING
    RETURNING id, name
), existing_permissions AS (
    SELECT id, name FROM permissions
    WHERE name IN ('audit:read', 'notification:write')
), all_permissions AS (
    SELECT id, name FROM inserted_permissions
    UNION ALL
    SELECT id, name FROM existing_permissions
    WHERE existing_permissions.name NOT IN (SELECT name FROM inserted_permissions)
)
INSERT INTO roles_permissions (role_id, permission_id)
SELECT r.id, p.id
FROM roles r
CROSS JOIN all_permissions p
WHERE r.name = 'ADMIN'
  AND NOT EXISTS (
      SELECT 1 FROM roles_permissions rp WHERE rp.role_id = r.id AND rp.permission_id = p.id
  );

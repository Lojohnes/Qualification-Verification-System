import { useCallback, useEffect, useState } from 'react';
import { Box, Button, Chip, IconButton, Paper, Typography } from '@mui/material';
import AddIcon from '@mui/icons-material/Add';
import EditIcon from '@mui/icons-material/Edit';

import { DataTable } from '@/components/ui/DataTable';
import { LoadingSpinner } from '@/components/ui/LoadingSpinner';
import { PermissionGate } from '@/components/auth/PermissionGate';
import { usePermission } from '@/hooks/usePermission';
import { useSnackbar } from '@/hooks/useSnackbar';
import { identityService } from '@/features/identity/services/identityService';
import { RoleFormDialog } from '@/features/identity/components/RoleFormDialog';
import { getApiErrorMessage } from '@/utils/errors';
import type { Permission, Role, RoleRequest } from '@/types/identity';

export function RolesPage() {
  const { showSnackbar } = useSnackbar();
  const { hasPermission } = usePermission();
  const [roles, setRoles] = useState<Role[]>([]);
  const [permissions, setPermissions] = useState<Permission[]>([]);
  const [loading, setLoading] = useState(true);
  const [formOpen, setFormOpen] = useState(false);
  const [editing, setEditing] = useState<Role | null>(null);
  const [submitting, setSubmitting] = useState(false);

  const loadRoles = useCallback(() => {
    setLoading(true);
    identityService
      .getRoles()
      .then(setRoles)
      .catch(() => showSnackbar('Failed to load roles.', 'error'))
      .finally(() => setLoading(false));
  }, [showSnackbar]);

  useEffect(() => {
    identityService
      .getPermissions()
      .then(setPermissions)
      .catch(() => showSnackbar('Failed to load permissions.', 'error'));
  }, [showSnackbar]);

  useEffect(() => {
    loadRoles();
  }, [loadRoles]);

  const handleCreate = () => {
    setEditing(null);
    setFormOpen(true);
  };

  const handleEdit = (role: Role) => {
    setEditing(role);
    setFormOpen(true);
  };

  const handleSubmit = async (data: RoleRequest) => {
    setSubmitting(true);
    try {
      if (editing) {
        await identityService.updateRole(editing.id, data);
        showSnackbar('Role updated successfully.', 'success');
      } else {
        await identityService.createRole(data);
        showSnackbar('Role created successfully.', 'success');
      }
      setFormOpen(false);
      loadRoles();
    } catch (error) {
      showSnackbar(getApiErrorMessage(error, 'Failed to save role.'), 'error');
    } finally {
      setSubmitting(false);
    }
  };

  return (
    <>
      <Box display="flex" justifyContent="space-between" alignItems="center" mb={2}>
        <Typography variant="h4" fontWeight={600}>
          Roles
        </Typography>
        <PermissionGate permission="role:write">
          <Button variant="contained" startIcon={<AddIcon />} onClick={handleCreate}>
            New Role
          </Button>
        </PermissionGate>
      </Box>
      <Paper elevation={2} sx={{ p: 3 }}>
        {loading ? (
          <LoadingSpinner />
        ) : (
          <DataTable
            data={roles}
            keyExtractor={(row) => row.id}
            columns={[
              { key: 'name', header: 'Name' },
              { key: 'description', header: 'Description' },
              {
                key: 'permissions',
                header: 'Permissions',
                render: (row) => (
                  <Box display="flex" gap={0.5} flexWrap="wrap">
                    {row.permissions.map((permission) => (
                      <Chip key={permission} label={permission} size="small" />
                    ))}
                  </Box>
                ),
              },
              {
                key: 'actions',
                header: 'Actions',
                align: 'right',
                render: (row) =>
                  hasPermission('role:write') ? (
                    <IconButton size="small" onClick={() => handleEdit(row)} aria-label="Edit">
                      <EditIcon fontSize="small" />
                    </IconButton>
                  ) : (
                    '-'
                  ),
              },
            ]}
          />
        )}
      </Paper>
      <RoleFormDialog
        open={formOpen}
        role={editing}
        permissions={permissions}
        submitting={submitting}
        onSubmit={handleSubmit}
        onClose={() => setFormOpen(false)}
      />
    </>
  );
}

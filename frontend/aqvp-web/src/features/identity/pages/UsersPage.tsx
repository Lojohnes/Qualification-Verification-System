import { useCallback, useEffect, useState } from 'react';
import { Box, Button, Chip, IconButton, Paper, Typography } from '@mui/material';
import AddIcon from '@mui/icons-material/Add';
import EditIcon from '@mui/icons-material/Edit';
import DeleteIcon from '@mui/icons-material/Delete';

import { DataTable } from '@/components/ui/DataTable';
import { LoadingSpinner } from '@/components/ui/LoadingSpinner';
import { SearchBar } from '@/components/ui/SearchBar';
import { ConfirmDialog } from '@/components/ui/ConfirmDialog';
import { PermissionGate } from '@/components/auth/PermissionGate';
import { usePermission } from '@/hooks/usePermission';
import { useSnackbar } from '@/hooks/useSnackbar';
import { identityService } from '@/features/identity/services/identityService';
import { UserFormDialog } from '@/features/identity/components/UserFormDialog';
import { getApiErrorMessage } from '@/utils/errors';
import type {
  Role,
  UserCreateRequest,
  UserListItem,
  UserUpdateRequest,
} from '@/types/identity';

export function UsersPage() {
  const { showSnackbar } = useSnackbar();
  const { permissions, hasPermission } = usePermission();
  const [users, setUsers] = useState<UserListItem[]>([]);
  const [roles, setRoles] = useState<Role[]>([]);
  const [loading, setLoading] = useState(true);
  const [search, setSearch] = useState('');
  const [formOpen, setFormOpen] = useState(false);
  const [editing, setEditing] = useState<UserListItem | null>(null);
  const [submitting, setSubmitting] = useState(false);
  const [deleteTarget, setDeleteTarget] = useState<UserListItem | null>(null);

  const loadUsers = useCallback(() => {
    setLoading(true);
    identityService
      .getUsers()
      .then(setUsers)
      .catch(() => showSnackbar('Failed to load users.', 'error'))
      .finally(() => setLoading(false));
  }, [showSnackbar]);

  useEffect(() => {
    if (!hasPermission('role:read')) {
      return;
    }
    identityService
      .getRoles()
      .then(setRoles)
      .catch(() => showSnackbar('Failed to load roles.', 'error'));
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, [permissions, showSnackbar]);

  useEffect(() => {
    loadUsers();
  }, [loadUsers]);

  const filtered = users.filter(
    (u) =>
      u.username.toLowerCase().includes(search.toLowerCase()) ||
      u.email.toLowerCase().includes(search.toLowerCase())
  );

  const handleCreate = () => {
    setEditing(null);
    setFormOpen(true);
  };

  const handleEdit = (user: UserListItem) => {
    setEditing(user);
    setFormOpen(true);
  };

  const handleSubmit = async (data: UserCreateRequest | UserUpdateRequest) => {
    setSubmitting(true);
    try {
      if (editing) {
        await identityService.updateUser(editing.id, data as UserUpdateRequest);
        showSnackbar('User updated successfully.', 'success');
      } else {
        await identityService.createUser(data as UserCreateRequest);
        showSnackbar('User created successfully.', 'success');
      }
      setFormOpen(false);
      loadUsers();
    } catch (error) {
      showSnackbar(getApiErrorMessage(error, 'Failed to save user.'), 'error');
    } finally {
      setSubmitting(false);
    }
  };

  const handleDelete = async () => {
    if (!deleteTarget) return;
    try {
      await identityService.deleteUser(deleteTarget.id);
      showSnackbar('User deleted successfully.', 'success');
      loadUsers();
    } catch (error) {
      showSnackbar(getApiErrorMessage(error, 'Failed to delete user.'), 'error');
    } finally {
      setDeleteTarget(null);
    }
  };

  return (
    <>
      <Box display="flex" justifyContent="space-between" alignItems="center" mb={2}>
        <Typography variant="h4" fontWeight={600}>
          Users
        </Typography>
        <PermissionGate permission="user:write">
          <Button variant="contained" startIcon={<AddIcon />} onClick={handleCreate}>
            New User
          </Button>
        </PermissionGate>
      </Box>
      <Box mb={2}>
        <SearchBar value={search} onChange={setSearch} placeholder="Search users..." />
      </Box>
      <Paper elevation={2} sx={{ p: 3 }}>
        {loading ? (
          <LoadingSpinner />
        ) : (
          <DataTable
            data={filtered}
            keyExtractor={(row) => row.id}
            columns={[
              { key: 'username', header: 'Username' },
              { key: 'email', header: 'Email' },
              { key: 'firstName', header: 'First Name' },
              { key: 'lastName', header: 'Last Name' },
              {
                key: 'roles',
                header: 'Roles',
                render: (row) => (
                  <Box display="flex" gap={0.5} flexWrap="wrap">
                    {row.roles.map((role) => (
                      <Chip key={role} label={role} size="small" />
                    ))}
                  </Box>
                ),
              },
              {
                key: 'enabled',
                header: 'Status',
                render: (row) => (row.enabled ? 'Active' : 'Inactive'),
              },
              {
                key: 'actions',
                header: 'Actions',
                align: 'right',
                render: (row) =>
                  hasPermission('user:write') || hasPermission('user:delete') ? (
                    <>
                      <PermissionGate permission="user:write">
                        <IconButton size="small" onClick={() => handleEdit(row)} aria-label="Edit">
                          <EditIcon fontSize="small" />
                        </IconButton>
                      </PermissionGate>
                      <PermissionGate permission="user:delete">
                        <IconButton
                          size="small"
                          onClick={() => setDeleteTarget(row)}
                          aria-label="Delete"
                        >
                          <DeleteIcon fontSize="small" />
                        </IconButton>
                      </PermissionGate>
                    </>
                  ) : (
                    '-'
                  ),
              },
            ]}
          />
        )}
      </Paper>
      <UserFormDialog
        open={formOpen}
        user={editing}
        roles={roles}
        submitting={submitting}
        onSubmit={handleSubmit}
        onClose={() => setFormOpen(false)}
      />
      <ConfirmDialog
        open={!!deleteTarget}
        title="Delete User"
        message={`Are you sure you want to delete "${deleteTarget?.username}"?`}
        confirmLabel="Delete"
        onConfirm={handleDelete}
        onCancel={() => setDeleteTarget(null)}
      />
    </>
  );
}

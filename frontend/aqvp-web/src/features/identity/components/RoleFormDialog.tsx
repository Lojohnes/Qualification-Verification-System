import { useEffect, useMemo } from 'react';
import { Controller, useForm, type SubmitHandler } from 'react-hook-form';
import { yupResolver } from '@hookform/resolvers/yup';
import * as yup from 'yup';
import {
  Box,
  Button,
  Checkbox,
  Dialog,
  DialogActions,
  DialogContent,
  DialogTitle,
  FormControlLabel,
  FormGroup,
  FormHelperText,
  TextField,
  Typography,
} from '@mui/material';

import type { Permission, Role, RoleRequest } from '@/types/identity';

interface RoleFormValues {
  name: string;
  description: string;
  permissionIds: string[];
}

const schema = yup.object({
  name: yup.string().required('Role name is required'),
  description: yup.string(),
  permissionIds: yup.array(yup.string().required()).min(1, 'Select at least one permission').required(),
});

interface RoleFormDialogProps {
  open: boolean;
  role?: Role | null;
  permissions: Permission[];
  submitting?: boolean;
  onSubmit: (data: RoleRequest) => void;
  onClose: () => void;
}

export function RoleFormDialog({
  open,
  role,
  permissions,
  submitting,
  onSubmit,
  onClose,
}: RoleFormDialogProps) {
  const {
    register,
    control,
    handleSubmit,
    reset,
    formState: { errors },
  } = useForm<RoleFormValues>({
    resolver: yupResolver(schema) as never,
    defaultValues: { name: '', description: '', permissionIds: [] },
  });

  useEffect(() => {
    if (open) {
      const currentPermissionIds = role
        ? permissions.filter((p) => role.permissions.includes(p.name)).map((p) => p.id)
        : [];
      reset({
        name: role?.name ?? '',
        description: role?.description ?? '',
        permissionIds: currentPermissionIds,
      });
    }
  }, [open, role, permissions, reset]);

  const groupedPermissions = useMemo(() => {
    const groups = new Map<string, Permission[]>();
    permissions.forEach((permission) => {
      const list = groups.get(permission.resource) ?? [];
      list.push(permission);
      groups.set(permission.resource, list);
    });
    return Array.from(groups.entries());
  }, [permissions]);

  const submitHandler: SubmitHandler<RoleFormValues> = (data) => {
    onSubmit(data);
  };

  return (
    <Dialog open={open} onClose={onClose} maxWidth="sm" fullWidth>
      <DialogTitle>{role ? 'Edit Role' : 'New Role'}</DialogTitle>
      <Box component="form" onSubmit={handleSubmit(submitHandler)} noValidate>
        <DialogContent>
          <TextField
            {...register('name')}
            label="Role Name"
            fullWidth
            margin="normal"
            error={!!errors.name}
            helperText={errors.name?.message}
          />
          <TextField
            {...register('description')}
            label="Description"
            fullWidth
            multiline
            minRows={2}
            margin="normal"
            error={!!errors.description}
            helperText={errors.description?.message}
          />
          <Typography variant="subtitle2" sx={{ mt: 2 }}>
            Permissions
          </Typography>
          <Controller
            name="permissionIds"
            control={control}
            render={({ field }) => (
              <Box>
                {groupedPermissions.map(([resource, perms]) => (
                  <Box key={resource} mb={1}>
                    <Typography
                      variant="caption"
                      color="text.secondary"
                      textTransform="uppercase"
                    >
                      {resource}
                    </Typography>
                    <FormGroup row>
                      {perms.map((permission) => (
                        <FormControlLabel
                          key={permission.id}
                          control={
                            <Checkbox
                              checked={field.value.includes(permission.id)}
                              onChange={(e) => {
                                if (e.target.checked) {
                                  field.onChange([...field.value, permission.id]);
                                } else {
                                  field.onChange(
                                    field.value.filter((id) => id !== permission.id)
                                  );
                                }
                              }}
                            />
                          }
                          label={permission.action}
                        />
                      ))}
                    </FormGroup>
                  </Box>
                ))}
              </Box>
            )}
          />
          {errors.permissionIds && (
            <FormHelperText error>{errors.permissionIds.message as string}</FormHelperText>
          )}
        </DialogContent>
        <DialogActions>
          <Button onClick={onClose} color="inherit" disabled={submitting}>
            Cancel
          </Button>
          <Button type="submit" variant="contained" color="primary" disabled={submitting}>
            {submitting ? 'Saving...' : 'Save'}
          </Button>
        </DialogActions>
      </Box>
    </Dialog>
  );
}

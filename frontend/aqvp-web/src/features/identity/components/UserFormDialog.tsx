import { useEffect } from 'react';
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
  Switch,
  TextField,
  Typography,
} from '@mui/material';

import type { Role, UserCreateRequest, UserListItem, UserUpdateRequest } from '@/types/identity';
import { PASSWORD_PATTERN, PASSWORD_HELP_TEXT } from '@/utils/passwordValidation';

interface UserFormValues {
  username: string;
  email: string;
  password: string;
  firstName: string;
  lastName: string;
  enabled: boolean;
  roleIds: string[];
}

const createSchema = yup.object({
  username: yup.string().required('Username is required'),
  email: yup.string().email('Must be a valid email').required('Email is required'),
  password: yup
    .string()
    .required('Password is required')
    .matches(PASSWORD_PATTERN, PASSWORD_HELP_TEXT),
  firstName: yup.string(),
  lastName: yup.string(),
  enabled: yup.boolean(),
  roleIds: yup.array(yup.string().required()).min(1, 'Select at least one role').required(),
});

const editSchema = yup.object({
  username: yup.string(),
  email: yup.string().email('Must be a valid email').required('Email is required'),
  password: yup.string(),
  firstName: yup.string(),
  lastName: yup.string(),
  enabled: yup.boolean(),
  roleIds: yup.array(yup.string().required()).min(1, 'Select at least one role').required(),
});

interface UserFormDialogProps {
  open: boolean;
  user?: UserListItem | null;
  roles: Role[];
  submitting?: boolean;
  onSubmit: (data: UserCreateRequest | UserUpdateRequest) => void;
  onClose: () => void;
}

export function UserFormDialog({
  open,
  user,
  roles,
  submitting,
  onSubmit,
  onClose,
}: UserFormDialogProps) {
  const isEdit = !!user;
  const {
    register,
    control,
    handleSubmit,
    reset,
    formState: { errors },
  } = useForm<UserFormValues>({
    resolver: yupResolver(isEdit ? editSchema : createSchema) as never,
    defaultValues: {
      username: '',
      email: '',
      password: '',
      firstName: '',
      lastName: '',
      enabled: true,
      roleIds: [],
    },
  });

  useEffect(() => {
    if (open) {
      const currentRoleIds = user
        ? roles.filter((role) => user.roles.includes(role.name)).map((role) => role.id)
        : [];
      reset({
        username: user?.username ?? '',
        email: user?.email ?? '',
        password: '',
        firstName: user?.firstName ?? '',
        lastName: user?.lastName ?? '',
        enabled: user?.enabled ?? true,
        roleIds: currentRoleIds,
      });
    }
  }, [open, user, roles, reset]);

  const submitHandler: SubmitHandler<UserFormValues> = (data) => {
    if (isEdit) {
      const payload: UserUpdateRequest = {
        email: data.email,
        firstName: data.firstName,
        lastName: data.lastName,
        enabled: data.enabled,
        roleIds: data.roleIds,
      };
      onSubmit(payload);
    } else {
      const payload: UserCreateRequest = {
        username: data.username,
        email: data.email,
        password: data.password,
        firstName: data.firstName,
        lastName: data.lastName,
        roleIds: data.roleIds,
      };
      onSubmit(payload);
    }
  };

  return (
    <Dialog open={open} onClose={onClose} maxWidth="sm" fullWidth>
      <DialogTitle>{isEdit ? 'Edit User' : 'New User'}</DialogTitle>
      <Box component="form" onSubmit={handleSubmit(submitHandler)} noValidate>
        <DialogContent>
          <TextField
            {...register('username')}
            label="Username"
            fullWidth
            margin="normal"
            disabled={isEdit}
            error={!!errors.username}
            helperText={errors.username?.message}
          />
          <TextField
            {...register('email')}
            label="Email"
            fullWidth
            margin="normal"
            error={!!errors.email}
            helperText={errors.email?.message}
          />
          {!isEdit && (
            <TextField
              {...register('password')}
              label="Password"
              type="password"
              fullWidth
              margin="normal"
              error={!!errors.password}
              helperText={errors.password?.message ?? PASSWORD_HELP_TEXT}
            />
          )}
          <TextField
            {...register('firstName')}
            label="First Name"
            fullWidth
            margin="normal"
            error={!!errors.firstName}
            helperText={errors.firstName?.message}
          />
          <TextField
            {...register('lastName')}
            label="Last Name"
            fullWidth
            margin="normal"
            error={!!errors.lastName}
            helperText={errors.lastName?.message}
          />
          {isEdit && (
            <Controller
              name="enabled"
              control={control}
              render={({ field }) => (
                <FormControlLabel
                  control={
                    <Switch
                      checked={!!field.value}
                      onChange={(e) => field.onChange(e.target.checked)}
                    />
                  }
                  label="Active"
                />
              )}
            />
          )}
          <Typography variant="subtitle2" sx={{ mt: 2 }}>
            Roles
          </Typography>
          <Controller
            name="roleIds"
            control={control}
            render={({ field }) => (
              <FormGroup>
                {roles.map((role) => (
                  <FormControlLabel
                    key={role.id}
                    control={
                      <Checkbox
                        checked={field.value.includes(role.id)}
                        onChange={(e) => {
                          if (e.target.checked) {
                            field.onChange([...field.value, role.id]);
                          } else {
                            field.onChange(field.value.filter((id) => id !== role.id));
                          }
                        }}
                      />
                    }
                    label={role.name}
                  />
                ))}
              </FormGroup>
            )}
          />
          {errors.roleIds && (
            <FormHelperText error>{errors.roleIds.message as string}</FormHelperText>
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

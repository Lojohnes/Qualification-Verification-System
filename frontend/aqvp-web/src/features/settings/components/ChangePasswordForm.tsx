import { useForm, type SubmitHandler } from 'react-hook-form';
import { yupResolver } from '@hookform/resolvers/yup';
import * as yup from 'yup';
import { Box, Button, TextField } from '@mui/material';

import type { ChangePasswordRequest } from '@/types/identity';
import { PASSWORD_PATTERN, PASSWORD_HELP_TEXT } from '@/utils/passwordValidation';

const schema = yup.object({
  currentPassword: yup.string().required('Current password is required'),
  newPassword: yup
    .string()
    .required('New password is required')
    .matches(PASSWORD_PATTERN, PASSWORD_HELP_TEXT),
  confirmPassword: yup
    .string()
    .required('Please confirm your new password')
    .oneOf([yup.ref('newPassword')], 'Passwords must match'),
});

interface ChangePasswordFormProps {
  onSubmit: (data: ChangePasswordRequest) => void;
  submitting?: boolean;
}

export function ChangePasswordForm({ onSubmit, submitting }: ChangePasswordFormProps) {
  const {
    register,
    handleSubmit,
    reset,
    formState: { errors },
  } = useForm<ChangePasswordRequest>({
    resolver: yupResolver(schema),
    defaultValues: { currentPassword: '', newPassword: '', confirmPassword: '' },
  });

  const submitHandler: SubmitHandler<ChangePasswordRequest> = (data) => {
    onSubmit(data);
    reset();
  };

  return (
    <Box component="form" onSubmit={handleSubmit(submitHandler)} noValidate>
      <TextField
        {...register('currentPassword')}
        label="Current Password"
        type="password"
        fullWidth
        margin="normal"
        error={!!errors.currentPassword}
        helperText={errors.currentPassword?.message}
      />
      <TextField
        {...register('newPassword')}
        label="New Password"
        type="password"
        fullWidth
        margin="normal"
        error={!!errors.newPassword}
        helperText={errors.newPassword?.message ?? PASSWORD_HELP_TEXT}
      />
      <TextField
        {...register('confirmPassword')}
        label="Confirm New Password"
        type="password"
        fullWidth
        margin="normal"
        error={!!errors.confirmPassword}
        helperText={errors.confirmPassword?.message}
      />
      <Button type="submit" variant="contained" color="primary" disabled={submitting} sx={{ mt: 2 }}>
        {submitting ? 'Updating...' : 'Update Password'}
      </Button>
    </Box>
  );
}

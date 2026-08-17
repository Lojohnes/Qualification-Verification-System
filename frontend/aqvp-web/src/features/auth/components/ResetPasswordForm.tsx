import { useForm, type SubmitHandler } from 'react-hook-form';
import { yupResolver } from '@hookform/resolvers/yup';
import * as yup from 'yup';
import { Box, Button, TextField, Typography } from '@mui/material';

import { PASSWORD_PATTERN, PASSWORD_HELP_TEXT } from '@/utils/passwordValidation';

export interface ResetPasswordFormValues {
  newPassword: string;
  confirmPassword: string;
}

const schema = yup.object({
  newPassword: yup
    .string()
    .required('New password is required')
    .matches(PASSWORD_PATTERN, PASSWORD_HELP_TEXT),
  confirmPassword: yup
    .string()
    .required('Please confirm your password')
    .oneOf([yup.ref('newPassword')], 'Passwords must match'),
});

interface ResetPasswordFormProps {
  onSubmit: (data: ResetPasswordFormValues) => void;
  loading?: boolean;
  successMessage?: string | null;
  error?: string | null;
}

export function ResetPasswordForm({
  onSubmit,
  loading,
  successMessage,
  error,
}: ResetPasswordFormProps) {
  const {
    register,
    handleSubmit,
    formState: { errors },
  } = useForm<ResetPasswordFormValues>({
    resolver: yupResolver(schema),
  });

  const submitHandler: SubmitHandler<ResetPasswordFormValues> = (data) => {
    onSubmit(data);
  };

  return (
    <Box component="form" onSubmit={handleSubmit(submitHandler)} noValidate sx={{ width: '100%' }}>
      {error && (
        <Typography color="error" variant="body2" sx={{ mb: 2 }}>
          {error}
        </Typography>
      )}
      {successMessage && (
        <Typography color="success.main" variant="body2" sx={{ mb: 2 }}>
          {successMessage}
        </Typography>
      )}
      <TextField
        {...register('newPassword')}
        label="New Password"
        type="password"
        fullWidth
        margin="normal"
        error={!!errors.newPassword}
        helperText={errors.newPassword?.message ?? PASSWORD_HELP_TEXT}
        disabled={!!successMessage}
      />
      <TextField
        {...register('confirmPassword')}
        label="Confirm Password"
        type="password"
        fullWidth
        margin="normal"
        error={!!errors.confirmPassword}
        helperText={errors.confirmPassword?.message}
        disabled={!!successMessage}
      />
      <Button
        type="submit"
        variant="contained"
        color="primary"
        fullWidth
        size="large"
        disabled={loading || !!successMessage}
        sx={{ mt: 3 }}
      >
        {loading ? 'Resetting...' : 'Reset Password'}
      </Button>
    </Box>
  );
}

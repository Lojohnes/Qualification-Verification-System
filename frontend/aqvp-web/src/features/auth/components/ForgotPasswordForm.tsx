import { useForm, type SubmitHandler } from 'react-hook-form';
import { yupResolver } from '@hookform/resolvers/yup';
import * as yup from 'yup';
import { Box, Button, TextField, Typography } from '@mui/material';

import type { ForgotPasswordRequest } from '@/types/auth';

const schema = yup.object({
  email: yup.string().required('Email is required').email('Enter a valid email address'),
});

interface ForgotPasswordFormProps {
  onSubmit: (data: ForgotPasswordRequest) => void;
  loading?: boolean;
  successMessage?: string | null;
  error?: string | null;
}

export function ForgotPasswordForm({
  onSubmit,
  loading,
  successMessage,
  error,
}: ForgotPasswordFormProps) {
  const {
    register,
    handleSubmit,
    formState: { errors },
  } = useForm<ForgotPasswordRequest>({
    resolver: yupResolver(schema),
  });

  const submitHandler: SubmitHandler<ForgotPasswordRequest> = (data) => {
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
        {...register('email')}
        label="Email Address"
        type="email"
        fullWidth
        margin="normal"
        error={!!errors.email}
        helperText={errors.email?.message}
      />
      <Button
        type="submit"
        variant="contained"
        color="primary"
        fullWidth
        size="large"
        disabled={loading}
        sx={{ mt: 3 }}
      >
        {loading ? 'Sending...' : 'Send Reset Link'}
      </Button>
    </Box>
  );
}

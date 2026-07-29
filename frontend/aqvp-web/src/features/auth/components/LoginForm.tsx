import { useForm, type SubmitHandler } from 'react-hook-form';
import { yupResolver } from '@hookform/resolvers/yup';
import * as yup from 'yup';
import { Box, Button, Link, TextField, Typography } from '@mui/material';

import type { LoginRequest } from '@/types/auth';
import { ROUTES } from '@/constants/routes';

const schema = yup.object({
  usernameOrEmail: yup.string().required('Username or email is required'),
  password: yup
    .string()
    .required('Password is required')
    .min(6, 'Password must be at least 6 characters'),
});

interface LoginFormProps {
  onSubmit: (data: LoginRequest) => void;
  loading?: boolean;
  error?: string | null;
}

export function LoginForm({ onSubmit, loading, error }: LoginFormProps) {
  const {
    register,
    handleSubmit,
    formState: { errors },
  } = useForm<LoginRequest>({
    resolver: yupResolver(schema),
  });

  const submitHandler: SubmitHandler<LoginRequest> = (data) => {
    onSubmit(data);
  };

  return (
    <Box component="form" onSubmit={handleSubmit(submitHandler)} noValidate sx={{ width: '100%' }}>
      {error && (
        <Typography color="error" variant="body2" sx={{ mb: 2 }}>
          {error}
        </Typography>
      )}
      <TextField
        {...register('usernameOrEmail')}
        label="Username or Email"
        fullWidth
        margin="normal"
        error={!!errors.usernameOrEmail}
        helperText={errors.usernameOrEmail?.message}
      />
      <TextField
        {...register('password')}
        label="Password"
        type="password"
        fullWidth
        margin="normal"
        error={!!errors.password}
        helperText={errors.password?.message}
      />
      <Box display="flex" justifyContent="space-between" mt={1}>
        <Link href={ROUTES.REGISTER} variant="body2">
          Create account
        </Link>
        <Link href={ROUTES.FORGOT_PASSWORD} variant="body2">
          Forgot password?
        </Link>
      </Box>
      <Button
        type="submit"
        variant="contained"
        color="primary"
        fullWidth
        size="large"
        disabled={loading}
        sx={{ mt: 3 }}
      >
        {loading ? 'Signing in...' : 'Sign In'}
      </Button>
    </Box>
  );
}

import { useForm, type SubmitHandler } from 'react-hook-form';
import { yupResolver } from '@hookform/resolvers/yup';
import * as yup from 'yup';
import { Box, Button, TextField, Typography } from '@mui/material';

import type { RegisterRequest } from '@/types/auth';
import { PASSWORD_PATTERN, PASSWORD_HELP_TEXT } from '@/utils/passwordValidation';

const schema = yup.object({
  username: yup.string().required('Username is required'),
  email: yup.string().email('Must be a valid email').required('Email is required'),
  password: yup
    .string()
    .required('Password is required')
    .matches(PASSWORD_PATTERN, PASSWORD_HELP_TEXT),
  firstName: yup.string(),
  lastName: yup.string(),
});

interface RegisterFormProps {
  onSubmit: (data: RegisterRequest) => void;
  loading?: boolean;
  error?: string | null;
}

export function RegisterForm({ onSubmit, loading, error }: RegisterFormProps) {
  const {
    register,
    handleSubmit,
    formState: { errors },
  } = useForm<RegisterRequest>({
    resolver: yupResolver(schema),
  });

  const submitHandler: SubmitHandler<RegisterRequest> = (data) => {
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
        {...register('username')}
        label="Username"
        fullWidth
        margin="normal"
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
      <TextField
        {...register('firstName')}
        label="First Name"
        fullWidth
        margin="normal"
      />
      <TextField
        {...register('lastName')}
        label="Last Name"
        fullWidth
        margin="normal"
      />
      <TextField
        {...register('password')}
        label="Password"
        type="password"
        fullWidth
        margin="normal"
        error={!!errors.password}
        helperText={errors.password?.message ?? PASSWORD_HELP_TEXT}
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
        {loading ? 'Creating account...' : 'Create Administrator Account'}
      </Button>
    </Box>
  );
}

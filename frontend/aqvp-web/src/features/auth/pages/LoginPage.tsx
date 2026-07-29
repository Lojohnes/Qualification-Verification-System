import { useEffect } from 'react';
import { Container, Paper, Typography, Box } from '@mui/material';
import { useNavigate } from 'react-router-dom';

import { LoginForm } from '@/features/auth/components/LoginForm';
import { useAppDispatch, useAppSelector } from '@/hooks/redux';
import { login } from '@/features/auth/slices/authSlice';
import { useSnackbar } from '@/hooks/useSnackbar';
import { LoadingSpinner } from '@/components/ui/LoadingSpinner';
import { MESSAGES } from '@/constants/messages';
import { ROUTES } from '@/constants/routes';
import type { LoginRequest } from '@/types/auth';

export function LoginPage() {
  const dispatch = useAppDispatch();
  const navigate = useNavigate();
  const { showSnackbar } = useSnackbar();
  const { isAuthenticated, loading, error } = useAppSelector((state) => state.auth);

  useEffect(() => {
    if (isAuthenticated) {
      showSnackbar(MESSAGES.LOGIN_SUCCESS, 'success');
      navigate(ROUTES.DASHBOARD, { replace: true });
    }
  }, [isAuthenticated, navigate, showSnackbar]);

  const handleSubmit = (data: LoginRequest) => {
    dispatch(login(data));
  };

  return (
    <Container
      maxWidth={false}
      sx={{
        minHeight: '100vh',
        display: 'flex',
        alignItems: 'center',
        justifyContent: 'center',
        bgcolor: 'background.default',
      }}
    >
      <Paper elevation={4} sx={{ p: 4, width: '100%', maxWidth: 420 }}>
        <Box textAlign="center" mb={3}>
          <Typography variant="h4" fontWeight={600} color="primary">
            AQVP
          </Typography>
          <Typography variant="body1" color="text.secondary">
            Sign in to your account
          </Typography>
        </Box>
        {loading && !error ? (
          <LoadingSpinner />
        ) : (
          <LoginForm onSubmit={handleSubmit} loading={loading} error={error} />
        )}
        <Typography
          variant="caption"
          color="text.secondary"
          display="block"
          textAlign="center"
          mt={2}
        >
          Demo account: admin / Admin123!
        </Typography>
      </Paper>
    </Container>
  );
}

import { useEffect, useState } from 'react';
import { Box, Container, Paper, Typography, Button } from '@mui/material';
import { useNavigate } from 'react-router-dom';

import { RegisterForm } from '@/features/auth/components/RegisterForm';
import { LoadingSpinner } from '@/components/ui/LoadingSpinner';
import { useAppDispatch, useAppSelector } from '@/hooks/redux';
import { register } from '@/features/auth/slices/authSlice';
import { authService } from '@/features/auth/services/authService';
import { useSnackbar } from '@/hooks/useSnackbar';
import { ROUTES } from '@/constants/routes';
import type { RegisterRequest } from '@/types/auth';

export function RegisterPage() {
  const dispatch = useAppDispatch();
  const navigate = useNavigate();
  const { showSnackbar } = useSnackbar();
  const { isAuthenticated, loading, error } = useAppSelector((state) => state.auth);
  const [checkingAvailability, setCheckingAvailability] = useState(true);
  const [available, setAvailable] = useState(false);

  useEffect(() => {
    authService
      .getRegistrationStatus()
      .then((status) => setAvailable(status.available))
      .catch(() => setAvailable(false))
      .finally(() => setCheckingAvailability(false));
  }, []);

  useEffect(() => {
    if (isAuthenticated) {
      showSnackbar('Administrator account created. Welcome!', 'success');
      navigate(ROUTES.DASHBOARD, { replace: true });
    }
  }, [isAuthenticated, navigate, showSnackbar]);

  const handleSubmit = (data: RegisterRequest) => {
    dispatch(register(data));
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
            Set up your organization
          </Typography>
        </Box>
        {checkingAvailability ? (
          <LoadingSpinner />
        ) : available ? (
          <>
            <Typography variant="body2" color="text.secondary" sx={{ mb: 2 }}>
              You are creating the first account for this system. It will automatically be granted
              administrator rights so you can invite staff and assign them roles.
            </Typography>
            {loading ? (
              <LoadingSpinner />
            ) : (
              <RegisterForm onSubmit={handleSubmit} loading={loading} error={error} />
            )}
          </>
        ) : (
          <>
            <Typography variant="body1" color="text.secondary" paragraph textAlign="center">
              An administrator account already exists for this system. Please contact your
              administrator to request access.
            </Typography>
            <Button fullWidth variant="contained" onClick={() => navigate(ROUTES.LOGIN)}>
              Back to Login
            </Button>
          </>
        )}
      </Paper>
    </Container>
  );
}

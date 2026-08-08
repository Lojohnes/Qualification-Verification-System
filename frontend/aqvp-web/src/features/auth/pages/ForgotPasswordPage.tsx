import { useState } from 'react';
import { Container, Paper, Typography, Box, Link } from '@mui/material';

import { ForgotPasswordForm } from '@/features/auth/components/ForgotPasswordForm';
import { authService } from '@/features/auth/services/authService';
import { MESSAGES } from '@/constants/messages';
import { ROUTES } from '@/constants/routes';
import type { ForgotPasswordRequest } from '@/types/auth';

export function ForgotPasswordPage() {
  const [loading, setLoading] = useState(false);
  const [successMessage, setSuccessMessage] = useState<string | null>(null);
  const [error, setError] = useState<string | null>(null);

  const handleSubmit = async (data: ForgotPasswordRequest) => {
    setLoading(true);
    setError(null);
    setSuccessMessage(null);
    try {
      await authService.forgotPassword(data);
      setSuccessMessage(MESSAGES.FORGOT_PASSWORD_SUCCESS);
    } catch {
      setError(MESSAGES.GENERIC_ERROR);
    } finally {
      setLoading(false);
    }
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
            Forgot Password
          </Typography>
          <Typography variant="body1" color="text.secondary">
            Enter your email and we will send a reset link if the account exists.
          </Typography>
        </Box>
        <ForgotPasswordForm
          onSubmit={handleSubmit}
          loading={loading}
          successMessage={successMessage}
          error={error}
        />
        <Box textAlign="center" mt={2}>
          <Link href={ROUTES.LOGIN} variant="body2">
            Back to login
          </Link>
        </Box>
      </Paper>
    </Container>
  );
}

import { useState } from 'react';
import { Container, Paper, Typography, Box, Link } from '@mui/material';
import { useSearchParams } from 'react-router-dom';

import { ResetPasswordForm, type ResetPasswordFormValues } from '@/features/auth/components/ResetPasswordForm';
import { authService } from '@/features/auth/services/authService';
import { MESSAGES } from '@/constants/messages';
import { ROUTES } from '@/constants/routes';

export function ResetPasswordPage() {
  const [searchParams] = useSearchParams();
  const token = searchParams.get('token');
  const [loading, setLoading] = useState(false);
  const [successMessage, setSuccessMessage] = useState<string | null>(null);
  const [error, setError] = useState<string | null>(
    token ? null : MESSAGES.RESET_PASSWORD_INVALID_TOKEN
  );

  const handleSubmit = async (data: ResetPasswordFormValues) => {
    if (!token) {
      setError(MESSAGES.RESET_PASSWORD_INVALID_TOKEN);
      return;
    }
    setLoading(true);
    setError(null);
    setSuccessMessage(null);
    try {
      await authService.resetPassword({
        token,
        newPassword: data.newPassword,
        confirmPassword: data.confirmPassword,
      });
      setSuccessMessage(MESSAGES.RESET_PASSWORD_SUCCESS);
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
            Reset Password
          </Typography>
          <Typography variant="body1" color="text.secondary" sx={{ mt: 1 }}>
            Enter your new password below.
          </Typography>
        </Box>
        <ResetPasswordForm
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

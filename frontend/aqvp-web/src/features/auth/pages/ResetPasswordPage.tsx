import { Container, Paper, Typography, Box, Link } from '@mui/material';

import { ROUTES } from '@/constants/routes';

export function ResetPasswordPage() {
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
            This page is a placeholder. Reset-password token handling and form submission will be
            implemented when the backend endpoint is finalised.
          </Typography>
        </Box>
        <Box textAlign="center" mt={2}>
          <Link href={ROUTES.LOGIN} variant="body2">
            Back to login
          </Link>
        </Box>
      </Paper>
    </Container>
  );
}

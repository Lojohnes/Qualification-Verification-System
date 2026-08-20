import { Container, Paper, Typography, Button, Box } from '@mui/material';
import { useNavigate } from 'react-router-dom';

import { MESSAGES } from '@/constants/messages';
import { ROUTES } from '@/constants/routes';
import { useAuth } from '@/hooks/useAuth';

export function AccessDeniedPage() {
  const navigate = useNavigate();
  const { logout } = useAuth();

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
      <Paper elevation={4} sx={{ p: 4, width: '100%', maxWidth: 420, textAlign: 'center' }}>
        <Typography variant="h1" color="error" fontWeight={700}>
          403
        </Typography>
        <Typography variant="h5" fontWeight={600} sx={{ mt: 2 }}>
          Access Denied
        </Typography>
        <Typography variant="body1" color="text.secondary" sx={{ mt: 1, mb: 3 }}>
          {MESSAGES.ACCESS_DENIED}
        </Typography>
        <Box display="flex" flexDirection="column" gap={2}>
          <Button variant="contained" color="primary" onClick={() => navigate(ROUTES.DASHBOARD)}>
            Back to Dashboard
          </Button>
          <Button variant="outlined" color="primary" onClick={logout}>
            Log In Again
          </Button>
        </Box>
      </Paper>
    </Container>
  );
}

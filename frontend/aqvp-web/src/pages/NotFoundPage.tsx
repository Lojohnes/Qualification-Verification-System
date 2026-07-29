import { Container, Paper, Typography, Button } from '@mui/material';
import { useNavigate } from 'react-router-dom';

import { MESSAGES } from '@/constants/messages';
import { ROUTES } from '@/constants/routes';

export function NotFoundPage() {
  const navigate = useNavigate();

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
        <Typography variant="h1" color="primary" fontWeight={700}>
          404
        </Typography>
        <Typography variant="h5" fontWeight={600} sx={{ mt: 2 }}>
          Page Not Found
        </Typography>
        <Typography variant="body1" color="text.secondary" sx={{ mt: 1, mb: 3 }}>
          {MESSAGES.NOT_FOUND}
        </Typography>
        <Button variant="contained" color="primary" onClick={() => navigate(ROUTES.DASHBOARD)}>
          Back to Dashboard
        </Button>
      </Paper>
    </Container>
  );
}

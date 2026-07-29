import { Container, Paper, Typography, Button } from '@mui/material';
import { useNavigate } from 'react-router-dom';

import { ROUTES } from '@/constants/routes';

export function RegisterPage() {
  const navigate = useNavigate();

  return (
    <Container maxWidth="sm" sx={{ mt: 8 }}>
      <Paper elevation={3} sx={{ p: 4, textAlign: 'center' }}>
        <Typography variant="h4" fontWeight={600} gutterBottom>
          Register
        </Typography>
        <Typography variant="body1" color="text.secondary" paragraph>
          Public self-registration is not enabled in this release. Please contact your administrator
          to create an account.
        </Typography>
        <Button variant="contained" onClick={() => navigate(ROUTES.LOGIN)}>
          Back to Login
        </Button>
      </Paper>
    </Container>
  );
}

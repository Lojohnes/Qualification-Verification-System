import { Button, Paper, Typography, Stack } from '@mui/material';
import AddIcon from '@mui/icons-material/Add';
import VerifiedIcon from '@mui/icons-material/Verified';
import SchoolIcon from '@mui/icons-material/School';
import { useNavigate } from 'react-router-dom';

import { ROUTES } from '@/constants/routes';

export function QuickActions() {
  const navigate = useNavigate();

  return (
    <Paper elevation={2} sx={{ p: 3, height: '100%' }}>
      <Typography variant="h6" fontWeight={600} gutterBottom>
        Quick Actions
      </Typography>
      <Stack spacing={2} mt={2}>
        <Button
          variant="outlined"
          startIcon={<AddIcon />}
          fullWidth
          onClick={() => navigate(ROUTES.USERS)}
        >
          Add User
        </Button>
        <Button
          variant="outlined"
          startIcon={<SchoolIcon />}
          fullWidth
          onClick={() => navigate(ROUTES.QUALIFICATION)}
        >
          Issue Qualification
        </Button>
        <Button
          variant="outlined"
          startIcon={<VerifiedIcon />}
          fullWidth
          onClick={() => navigate(ROUTES.VERIFICATION)}
        >
          Verify Qualification
        </Button>
      </Stack>
    </Paper>
  );
}

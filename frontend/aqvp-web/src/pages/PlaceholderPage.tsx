import { Container, Paper, Typography, Box } from '@mui/material';
import { useTheme } from '@mui/material/styles';

interface PlaceholderPageProps {
  module: string;
  sprint: string;
}

export function PlaceholderPage({ module, sprint }: PlaceholderPageProps) {
  const theme = useTheme();

  return (
    <Container maxWidth="lg" sx={{ py: 4 }}>
      <Paper elevation={2} sx={{ p: 6, textAlign: 'center' }}>
        <Typography variant="h4" fontWeight={600} color="primary" gutterBottom>
          {module} Module
        </Typography>
        <Typography variant="body1" color="text.secondary" sx={{ mb: 3 }}>
          This module will be implemented during {sprint}.
        </Typography>
        <Box
          sx={{
            display: 'inline-block',
            px: 3,
            py: 1,
            borderRadius: 2,
            bgcolor: theme.palette.mode === 'dark' ? 'rgba(255,255,255,0.05)' : 'rgba(0,0,0,0.05)',
          }}
        >
          <Typography variant="body2" color="text.secondary">
            Coming soon
          </Typography>
        </Box>
      </Paper>
    </Container>
  );
}

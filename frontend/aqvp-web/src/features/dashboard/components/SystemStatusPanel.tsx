import { Box, Paper, Typography, Chip, Stack } from '@mui/material';

import type { DashboardServiceStatus } from '@/features/dashboard/services/dashboardService';

interface SystemStatusPanelProps {
  services: DashboardServiceStatus[];
}

export function SystemStatusPanel({ services }: SystemStatusPanelProps) {
  return (
    <Paper elevation={2} sx={{ p: 3, height: '100%' }}>
      <Typography variant="h6" fontWeight={600} gutterBottom>
        System Status
      </Typography>
      <Stack spacing={2} mt={2}>
        {services.map((service) => (
          <Box key={service.name}>
            <Box display="flex" justifyContent="space-between" alignItems="center">
              <Typography variant="body1">{service.name}</Typography>
              <Chip
                label={service.status}
                color={service.status === 'operational' ? 'success' : 'error'}
                size="small"
              />
            </Box>
          </Box>
        ))}
      </Stack>
    </Paper>
  );
}

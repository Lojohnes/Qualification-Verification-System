import { Box, Paper, Typography, Chip, LinearProgress, Stack } from '@mui/material';

interface ServiceStatus {
  name: string;
  status: 'operational' | 'degraded' | 'down';
  uptime?: number;
}

const services: ServiceStatus[] = [
  { name: 'Identity Service', status: 'operational', uptime: 99.9 },
  { name: 'Database', status: 'operational', uptime: 99.99 },
  { name: 'Document Service', status: 'operational', uptime: 99.5 },
  { name: 'Notification Service', status: 'degraded', uptime: 98.2 },
];

export function SystemStatusPanel() {
  return (
    <Paper elevation={2} sx={{ p: 3, height: '100%' }}>
      <Typography variant="h6" fontWeight={600} gutterBottom>
        System Status
      </Typography>
      <Stack spacing={2} mt={2}>
        {services.map((service) => (
          <Box key={service.name}>
            <Box display="flex" justifyContent="space-between" alignItems="center" mb={1}>
              <Typography variant="body1">{service.name}</Typography>
              <Chip
                label={service.status}
                color={
                  service.status === 'operational'
                    ? 'success'
                    : service.status === 'degraded'
                      ? 'warning'
                      : 'error'
                }
                size="small"
              />
            </Box>
            {service.uptime && (
              <>
                <LinearProgress variant="determinate" value={service.uptime} color="success" />
                <Typography variant="caption" color="text.secondary">
                  Uptime: {service.uptime}%
                </Typography>
              </>
            )}
          </Box>
        ))}
      </Stack>
    </Paper>
  );
}

import { Typography, Paper, Grid2 as Grid } from '@mui/material';

import { DashboardCards } from '@/features/dashboard/components/DashboardCards';
import { RecentActivityTable } from '@/features/dashboard/components/RecentActivityTable';
import { SystemStatusPanel } from '@/features/dashboard/components/SystemStatusPanel';
import { QuickActions } from '@/features/dashboard/components/QuickActions';

export function DashboardPage() {
  return (
    <>
      <Typography variant="h4" fontWeight={600} gutterBottom>
        Dashboard
      </Typography>
      <DashboardCards />

      <Grid container spacing={3} mt={2}>
        <Grid size={{ xs: 12, lg: 8 }}>
          <Paper elevation={2} sx={{ p: 3 }}>
            <Typography variant="h6" fontWeight={600} gutterBottom>
              Recent Activity
            </Typography>
            <RecentActivityTable />
          </Paper>
        </Grid>
        <Grid size={{ xs: 12, lg: 4 }}>
          <Grid container spacing={3} direction="column">
            <Grid>
              <SystemStatusPanel />
            </Grid>
            <Grid>
              <QuickActions />
            </Grid>
          </Grid>
        </Grid>
      </Grid>
    </>
  );
}

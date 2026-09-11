import { useEffect, useState } from 'react';
import { Typography, Paper, Grid2 as Grid } from '@mui/material';

import { DashboardCards } from '@/features/dashboard/components/DashboardCards';
import { RecentActivityTable } from '@/features/dashboard/components/RecentActivityTable';
import { SystemStatusPanel } from '@/features/dashboard/components/SystemStatusPanel';
import { QuickActions } from '@/features/dashboard/components/QuickActions';
import { dashboardService, type DashboardData } from '@/features/dashboard/services/dashboardService';
import { useSnackbar } from '@/hooks/useSnackbar';

const emptyData: DashboardData = {
  users: 0,
  qualifications: 0,
  verifications: 0,
  documents: 0,
  recentActivity: [],
  services: [],
};

export function DashboardPage() {
  const { showSnackbar } = useSnackbar();
  const [data, setData] = useState<DashboardData>(emptyData);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    dashboardService
      .getData()
      .then(setData)
      .catch(() => showSnackbar('Failed to load dashboard data.', 'error'))
      .finally(() => setLoading(false));
  }, [showSnackbar]);

  return (
    <>
      <Typography variant="h4" fontWeight={600} gutterBottom>
        Dashboard
      </Typography>
      <DashboardCards
        loading={loading}
        users={data.users}
        qualifications={data.qualifications}
        verifications={data.verifications}
        documents={data.documents}
      />

      <Grid container spacing={3} mt={2}>
        <Grid size={{ xs: 12, lg: 8 }}>
          <Paper elevation={2} sx={{ p: 3 }}>
            <Typography variant="h6" fontWeight={600} gutterBottom>
              Recent Activity
            </Typography>
            <RecentActivityTable events={data.recentActivity} />
          </Paper>
        </Grid>
        <Grid size={{ xs: 12, lg: 4 }}>
          <Grid container spacing={3} direction="column">
            <Grid>
              <SystemStatusPanel services={data.services} />
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

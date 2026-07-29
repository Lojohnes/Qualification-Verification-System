import { Grid2 as Grid } from '@mui/material';
import PeopleIcon from '@mui/icons-material/People';
import SchoolIcon from '@mui/icons-material/School';
import VerifiedIcon from '@mui/icons-material/Verified';
import DescriptionIcon from '@mui/icons-material/Description';

import { StatCard } from '@/components/ui/StatCard';

export function DashboardCards() {
  return (
    <Grid container spacing={3}>
      <Grid size={{ xs: 12, sm: 6, md: 3 }}>
        <StatCard title="Total Users" value="1,248" icon={<PeopleIcon />} color="primary" />
      </Grid>
      <Grid size={{ xs: 12, sm: 6, md: 3 }}>
        <StatCard title="Qualifications" value="8,932" icon={<SchoolIcon />} color="success" />
      </Grid>
      <Grid size={{ xs: 12, sm: 6, md: 3 }}>
        <StatCard title="Verifications" value="12,405" icon={<VerifiedIcon />} color="info" />
      </Grid>
      <Grid size={{ xs: 12, sm: 6, md: 3 }}>
        <StatCard title="Documents" value="24,110" icon={<DescriptionIcon />} color="warning" />
      </Grid>
    </Grid>
  );
}

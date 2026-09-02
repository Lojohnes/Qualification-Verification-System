import { Grid2 as Grid } from '@mui/material';
import PeopleIcon from '@mui/icons-material/People';
import SchoolIcon from '@mui/icons-material/School';
import VerifiedIcon from '@mui/icons-material/Verified';
import DescriptionIcon from '@mui/icons-material/Description';

import { StatCard } from '@/components/ui/StatCard';

interface DashboardCardsProps {
  loading: boolean;
  users: number;
  qualifications: number;
  verifications: number;
  documents: number;
}

export function DashboardCards({ loading, users, qualifications, verifications, documents }: DashboardCardsProps) {
  const value = (count: number) => (loading ? '—' : count.toLocaleString());

  return (
    <Grid container spacing={3}>
      <Grid size={{ xs: 12, sm: 6, md: 3 }}>
        <StatCard title="Total Users" value={value(users)} icon={<PeopleIcon />} color="primary" />
      </Grid>
      <Grid size={{ xs: 12, sm: 6, md: 3 }}>
        <StatCard title="Qualifications" value={value(qualifications)} icon={<SchoolIcon />} color="success" />
      </Grid>
      <Grid size={{ xs: 12, sm: 6, md: 3 }}>
        <StatCard title="Verifications" value={value(verifications)} icon={<VerifiedIcon />} color="info" />
      </Grid>
      <Grid size={{ xs: 12, sm: 6, md: 3 }}>
        <StatCard title="Documents" value={value(documents)} icon={<DescriptionIcon />} color="warning" />
      </Grid>
    </Grid>
  );
}

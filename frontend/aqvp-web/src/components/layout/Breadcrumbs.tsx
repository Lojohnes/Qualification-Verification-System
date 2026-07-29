import { Breadcrumbs as MuiBreadcrumbs, Link, Typography } from '@mui/material';
import { useLocation, Link as RouterLink } from 'react-router-dom';

import { ROUTES } from '@/constants/routes';

const routeLabels: Record<string, string> = {
  [ROUTES.DASHBOARD]: 'Dashboard',
  [ROUTES.USERS]: 'Users',
  [ROUTES.ROLES]: 'Roles',
  [ROUTES.PERMISSIONS]: 'Permissions',
  [ROUTES.INSTITUTION]: 'Institution',
  [ROUTES.QUALIFICATION]: 'Qualification',
  [ROUTES.VERIFICATION]: 'Verification',
  [ROUTES.DOCUMENTS]: 'Documents',
  [ROUTES.AUDIT]: 'Audit',
  [ROUTES.REPORTS]: 'Reports',
  [ROUTES.SETTINGS]: 'Settings',
};

export function Breadcrumbs() {
  const location = useLocation();
  const path = location.pathname;

  const segments = path.split('/').filter(Boolean);
  if (segments.length === 0) {
    return (
      <MuiBreadcrumbs aria-label="breadcrumb">
        <Typography color="text.primary">Dashboard</Typography>
      </MuiBreadcrumbs>
    );
  }

  const buildPath = (index: number) => `/${segments.slice(0, index + 1).join('/')}`;

  return (
    <MuiBreadcrumbs aria-label="breadcrumb">
      <Link component={RouterLink} to={ROUTES.DASHBOARD} underline="hover" color="inherit">
        Dashboard
      </Link>
      {segments.map((segment, index) => {
        const segmentPath = buildPath(index);
        const label =
          routeLabels[segmentPath] ?? segment.charAt(0).toUpperCase() + segment.slice(1);
        const isLast = index === segments.length - 1;

        if (isLast) {
          return (
            <Typography key={segmentPath} color="text.primary">
              {label}
            </Typography>
          );
        }

        return (
          <Link
            key={segmentPath}
            component={RouterLink}
            to={segmentPath}
            underline="hover"
            color="inherit"
          >
            {label}
          </Link>
        );
      })}
    </MuiBreadcrumbs>
  );
}

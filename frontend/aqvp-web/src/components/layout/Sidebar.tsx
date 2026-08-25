import {
  Drawer,
  List,
  ListItem,
  ListItemButton,
  ListItemIcon,
  ListItemText,
  Toolbar,
  Typography,
  Box,
  useTheme,
} from '@mui/material';
import DashboardIcon from '@mui/icons-material/Dashboard';
import ShieldIcon from '@mui/icons-material/Shield';
import BusinessIcon from '@mui/icons-material/Business';
import SchoolIcon from '@mui/icons-material/School';
import PeopleIcon from '@mui/icons-material/People';
import WorkspacePremiumIcon from '@mui/icons-material/WorkspacePremium';
import VerifiedIcon from '@mui/icons-material/Verified';
import DescriptionIcon from '@mui/icons-material/Description';
import HistoryIcon from '@mui/icons-material/History';
import BarChartIcon from '@mui/icons-material/BarChart';
import SettingsIcon from '@mui/icons-material/Settings';
import NotificationsIcon from '@mui/icons-material/Notifications';
import { useNavigate, useLocation } from 'react-router-dom';

import { ROUTES } from '@/constants/routes';
import { usePermission } from '@/hooks/usePermission';

const DRAWER_WIDTH = 260;

interface SidebarProps {
  open: boolean;
  onClose: () => void;
}

interface MenuItemConfig {
  label: string;
  path: string;
  icon: React.ReactNode;
  indent?: boolean;
  permission?: string;
}

const menuItems: MenuItemConfig[] = [
  { label: 'Dashboard', path: ROUTES.DASHBOARD, icon: <DashboardIcon /> },
  { label: 'Identity', path: '#', icon: <ShieldIcon /> },
  {
    label: 'Users',
    path: ROUTES.USERS,
    icon: <ShieldIcon />,
    indent: true,
    permission: 'user:read',
  },
  {
    label: 'Roles',
    path: ROUTES.ROLES,
    icon: <ShieldIcon />,
    indent: true,
    permission: 'role:read',
  },
  {
    label: 'Permissions',
    path: ROUTES.PERMISSIONS,
    icon: <ShieldIcon />,
    indent: true,
    permission: 'role:read',
  },
  { label: 'Institution', path: '#', icon: <BusinessIcon /> },
  { label: 'Institutions', path: ROUTES.INSTITUTIONS, icon: <BusinessIcon />, indent: true },
  { label: 'Faculties', path: ROUTES.FACULTIES, icon: <BusinessIcon />, indent: true },
  { label: 'Departments', path: ROUTES.DEPARTMENTS, icon: <BusinessIcon />, indent: true },
  { label: 'Programs', path: ROUTES.PROGRAMS, icon: <SchoolIcon />, indent: true },
  { label: 'Qualification', path: '#', icon: <SchoolIcon /> },
  {
    label: 'Students',
    path: ROUTES.STUDENTS,
    icon: <PeopleIcon />,
    indent: true,
    permission: 'student:read',
  },
  {
    label: 'Qualifications',
    path: ROUTES.QUALIFICATIONS,
    icon: <WorkspacePremiumIcon />,
    indent: true,
    permission: 'qualification:read',
  },
  { label: 'Verification', path: ROUTES.VERIFICATION, icon: <VerifiedIcon />, permission: 'verification:read' },
  { label: 'Documents', path: ROUTES.DOCUMENTS, icon: <DescriptionIcon />, permission: 'qualification:read' },
  { label: 'Reports', path: '#', icon: <BarChartIcon /> },
  { label: 'Audit', path: ROUTES.AUDIT, icon: <HistoryIcon />, indent: true },
  { label: 'Notifications', path: ROUTES.NOTIFICATIONS, icon: <NotificationsIcon />, indent: true },
  { label: 'Settings', path: ROUTES.SETTINGS, icon: <SettingsIcon /> },
];

export function Sidebar({ open, onClose }: SidebarProps) {
  const theme = useTheme();
  const navigate = useNavigate();
  const location = useLocation();
  const { hasPermission } = usePermission();

  const permissionFiltered = menuItems.filter(
    (item) => !item.permission || hasPermission(item.permission)
  );
  const visibleMenuItems = permissionFiltered.filter((item, index) => {
    if (item.path !== '#') return true;
    const next = permissionFiltered[index + 1];
    return !!next && next.path !== '#';
  });

  const handleClick = (path: string) => {
    if (path !== '#') {
      navigate(path);
    }
    onClose();
  };

  const drawerContent = (
    <Box>
      <Toolbar sx={{ display: 'flex', alignItems: 'center', justifyContent: 'center' }}>
        <Typography variant="h6" fontWeight={600} color="primary">
          AQVP
        </Typography>
      </Toolbar>
      <List>
        {visibleMenuItems.map((item) => {
          const isActive =
            location.pathname === item.path || location.pathname.startsWith(`${item.path}/`);
          const isHeader = item.path === '#';
          return (
            <ListItem key={item.label} disablePadding sx={{ pl: item.indent ? 3 : 0 }}>
              <ListItemButton
                selected={!isHeader && isActive}
                onClick={() => handleClick(item.path)}
                disabled={isHeader}
              >
                {!isHeader && <ListItemIcon>{item.icon}</ListItemIcon>}
                <ListItemText primary={item.label} />
              </ListItemButton>
            </ListItem>
          );
        })}
      </List>
    </Box>
  );

  return (
    <>
      <Drawer
        variant="temporary"
        open={open}
        onClose={onClose}
        ModalProps={{ keepMounted: true }}
        sx={{
          display: { xs: 'block', md: 'none' },
          '& .MuiDrawer-paper': { boxSizing: 'border-box', width: DRAWER_WIDTH },
        }}
      >
        {drawerContent}
      </Drawer>
      <Drawer
        variant="permanent"
        sx={{
          display: { xs: 'none', md: 'block' },
          '& .MuiDrawer-paper': { boxSizing: 'border-box', width: DRAWER_WIDTH },
        }}
        open
      >
        <Box sx={{ height: '100%', bgcolor: theme.palette.background.paper }}>{drawerContent}</Box>
      </Drawer>
    </>
  );
}

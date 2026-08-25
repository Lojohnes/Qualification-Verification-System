import {
  AppBar,
  Toolbar,
  Typography,
  IconButton,
  Box,
  Tooltip,
  Badge,
  Avatar,
  Menu,
  MenuItem,
  useTheme,
} from '@mui/material';
import MenuIcon from '@mui/icons-material/Menu';
import NotificationsIcon from '@mui/icons-material/Notifications';
import DarkModeIcon from '@mui/icons-material/DarkMode';
import LightModeIcon from '@mui/icons-material/LightMode';

import { useNavigate } from 'react-router-dom';

import { useThemeContext } from '@/hooks/useThemeContext';
import { useAuth } from '@/hooks/useAuth';
import { useState } from 'react';
import { ROUTES } from '@/constants/routes';

interface TopNavProps {
  onMenuToggle: () => void;
}

export function TopNav({ onMenuToggle }: TopNavProps) {
  const theme = useTheme();
  const { mode, toggleTheme } = useThemeContext();
  const { user, logout } = useAuth();
  const navigate = useNavigate();
  const [anchorEl, setAnchorEl] = useState<null | HTMLElement>(null);

  const handleMenuOpen = (event: React.MouseEvent<HTMLElement>) => {
    setAnchorEl(event.currentTarget);
  };

  const handleMenuClose = () => {
    setAnchorEl(null);
  };

  const handleSettings = () => {
    handleMenuClose();
    navigate(ROUTES.SETTINGS);
  };

  const handleLogout = () => {
    handleMenuClose();
    logout();
  };

  const displayName =
    user?.firstName && user?.lastName
      ? `${user.firstName} ${user.lastName}`
      : user?.username || 'User';

  return (
    <AppBar position="fixed" elevation={1} sx={{ zIndex: theme.zIndex.drawer + 1 }}>
      <Toolbar>
        <IconButton color="inherit" edge="start" onClick={onMenuToggle} sx={{ mr: 2 }}>
          <MenuIcon />
        </IconButton>
        <Typography variant="h6" noWrap component="div" sx={{ flexGrow: 1 }}>
          AQVP
        </Typography>

        <Box display="flex" alignItems="center" gap={1}>
          <Tooltip title="Notifications">
            <IconButton color="inherit" onClick={() => navigate(ROUTES.NOTIFICATIONS)}>
              <Badge color="error">
                <NotificationsIcon />
              </Badge>
            </IconButton>
          </Tooltip>

          <Tooltip title={mode === 'light' ? 'Switch to dark mode' : 'Switch to light mode'}>
            <IconButton color="inherit" onClick={toggleTheme}>
              {mode === 'light' ? <DarkModeIcon /> : <LightModeIcon />}
            </IconButton>
          </Tooltip>

          <IconButton color="inherit" onClick={handleMenuOpen}>
            <Avatar sx={{ width: 32, height: 32, bgcolor: 'secondary.main' }}>
              {displayName.charAt(0).toUpperCase()}
            </Avatar>
          </IconButton>
          <Menu
            anchorEl={anchorEl}
            open={Boolean(anchorEl)}
            onClose={handleMenuClose}
            anchorOrigin={{ vertical: 'bottom', horizontal: 'right' }}
            transformOrigin={{ vertical: 'top', horizontal: 'right' }}
          >
            <MenuItem disabled>{displayName}</MenuItem>
            <MenuItem onClick={handleSettings}>Settings</MenuItem>
            <MenuItem onClick={handleLogout}>Logout</MenuItem>
          </Menu>
        </Box>
      </Toolbar>
    </AppBar>
  );
}

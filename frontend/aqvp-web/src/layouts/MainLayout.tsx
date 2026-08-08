import { useState } from 'react';
import { Box, Container, Toolbar } from '@mui/material';

import { TopNav } from '@/components/layout/TopNav';
import { Sidebar } from '@/components/layout/Sidebar';
import { Footer } from '@/components/layout/Footer';
import { Breadcrumbs } from '@/components/layout/Breadcrumbs';
import { Outlet } from 'react-router-dom';

const DRAWER_WIDTH = 260;

export function MainLayout() {
  const [mobileOpen, setMobileOpen] = useState(false);

  const handleDrawerToggle = () => {
    setMobileOpen((prev) => !prev);
  };

  return (
    <Box display="flex" minHeight="100vh">
      <TopNav onMenuToggle={handleDrawerToggle} />
      <Sidebar open={mobileOpen} onClose={handleDrawerToggle} />
      <Box
        component="main"
        sx={{
          flexGrow: 1,
          display: 'flex',
          flexDirection: 'column',
          width: { md: `calc(100% - ${DRAWER_WIDTH}px)` },
          ml: { md: `${DRAWER_WIDTH}px` },
        }}
      >
        <Toolbar />
        <Box flexGrow={1} bgcolor="background.default">
          <Container maxWidth="xl" sx={{ py: 3 }}>
            <Breadcrumbs />
            <Box mt={2}>
              <Outlet />
            </Box>
          </Container>
        </Box>
        <Footer />
      </Box>
    </Box>
  );
}

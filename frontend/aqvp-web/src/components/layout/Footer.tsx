import { Box, Container, Typography } from '@mui/material';

export function Footer() {
  return (
    <Box component="footer" py={2} bgcolor="background.paper" borderTop={1} borderColor="divider">
      <Container maxWidth="xl">
        <Typography variant="body2" color="text.secondary" textAlign="center">
          © {new Date().getFullYear()} Academic Qualification Verification Platform. All rights
          reserved.
        </Typography>
      </Container>
    </Box>
  );
}

import { Box, CircularProgress, type CircularProgressProps } from '@mui/material';

interface LoadingSpinnerProps extends CircularProgressProps {
  fullScreen?: boolean;
}

export function LoadingSpinner({ fullScreen = false, ...props }: LoadingSpinnerProps) {
  const content = <CircularProgress color="primary" {...props} />;

  if (!fullScreen) {
    return (
      <Box display="flex" justifyContent="center" alignItems="center" p={4}>
        {content}
      </Box>
    );
  }

  return (
    <Box display="flex" justifyContent="center" alignItems="center" minHeight="100vh">
      {content}
    </Box>
  );
}

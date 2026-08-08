import {
  Dialog,
  DialogTitle,
  DialogContent,
  DialogContentText,
  DialogActions,
  Button,
} from '@mui/material';

import { useAppDispatch, useAppSelector } from '@/hooks/redux';
import { clearCredentials, setSessionExpired } from '@/features/auth/slices/authSlice';
import { useNavigate } from 'react-router-dom';
import { ROUTES } from '@/constants/routes';

export function SessionExpiredDialog() {
  const { sessionExpired } = useAppSelector((state) => state.auth);
  const dispatch = useAppDispatch();
  const navigate = useNavigate();

  const handleClose = () => {
    dispatch(setSessionExpired(false));
    dispatch(clearCredentials());
    navigate(ROUTES.LOGIN, { replace: true });
  };

  return (
    <Dialog open={sessionExpired} onClose={handleClose}>
      <DialogTitle>Session Expired</DialogTitle>
      <DialogContent>
        <DialogContentText>
          Your session has expired or your credentials are no longer valid. Please log in again to
          continue.
        </DialogContentText>
      </DialogContent>
      <DialogActions>
        <Button onClick={handleClose} variant="contained" color="primary">
          Go to Login
        </Button>
      </DialogActions>
    </Dialog>
  );
}

import { useCallback } from 'react';
import { useNavigate } from 'react-router-dom';

import { useAppDispatch, useAppSelector } from '@/hooks/redux';
import { clearCredentials, login } from '@/features/auth/slices/authSlice';
import { authService } from '@/features/auth/services/authService';
import { getItem, removeItem } from '@/utils/storage';
import { STORAGE_KEYS } from '@/constants/storage';
import { ROUTES } from '@/constants/routes';

export function useAuth() {
  const dispatch = useAppDispatch();
  const navigate = useNavigate();
  const auth = useAppSelector((state) => state.auth);

  const logout = useCallback(async () => {
    const refreshToken = auth.refreshToken ?? getItem<string>(STORAGE_KEYS.REFRESH_TOKEN);
    if (refreshToken) {
      try {
        await authService.logout(refreshToken);
      } catch {
        // continue logout even if backend call fails
      }
    }
    dispatch(clearCredentials());
    removeItem(STORAGE_KEYS.ACCESS_TOKEN);
    removeItem(STORAGE_KEYS.REFRESH_TOKEN);
    removeItem(STORAGE_KEYS.USER);
    navigate(ROUTES.LOGIN, { replace: true });
  }, [auth.refreshToken, dispatch, navigate]);

  return {
    ...auth,
    login: (credentials: Parameters<typeof login>[0]) => dispatch(login(credentials)),
    logout,
  };
}

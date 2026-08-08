import { createSlice, createAsyncThunk, type PayloadAction } from '@reduxjs/toolkit';

import type { LoginRequest, User } from '@/types/auth';
import { STORAGE_KEYS } from '@/constants/storage';
import { getItem, removeItem, setItem } from '@/utils/storage';
import { authService } from '@/features/auth/services/authService';
import { parseUserFromToken } from '@/utils/jwt';

interface AuthState {
  user: User | null;
  accessToken: string | null;
  refreshToken: string | null;
  isAuthenticated: boolean;
  loading: boolean;
  error: string | null;
  sessionExpired: boolean;
}

const loadInitialState = (): Partial<AuthState> => {
  const user = getItem<User>(STORAGE_KEYS.USER);
  const accessToken = getItem<string>(STORAGE_KEYS.ACCESS_TOKEN);
  const refreshToken = getItem<string>(STORAGE_KEYS.REFRESH_TOKEN);
  return {
    user,
    accessToken,
    refreshToken,
    isAuthenticated: !!accessToken && !!user,
  };
};

const initialState: AuthState = {
  user: null,
  accessToken: null,
  refreshToken: null,
  isAuthenticated: false,
  loading: false,
  error: null,
  sessionExpired: false,
  ...loadInitialState(),
};

export const login = createAsyncThunk(
  'auth/login',
  async (credentials: LoginRequest, { rejectWithValue }) => {
    try {
      const response = await authService.login(credentials);
      return response;
    } catch (error) {
      return rejectWithValue(error instanceof Error ? error.message : 'Login failed');
    }
  }
);

export const refreshAccessToken = createAsyncThunk(
  'auth/refresh',
  async (refreshToken: string, { rejectWithValue }) => {
    try {
      const response = await authService.refresh(refreshToken);
      return response;
    } catch (error) {
      return rejectWithValue(error instanceof Error ? error.message : 'Refresh failed');
    }
  }
);

const authSlice = createSlice({
  name: 'auth',
  initialState,
  reducers: {
    setCredentials: (
      state,
      action: PayloadAction<{ accessToken: string; refreshToken: string; user: User }>
    ) => {
      state.accessToken = action.payload.accessToken;
      state.refreshToken = action.payload.refreshToken;
      state.user = action.payload.user;
      state.isAuthenticated = true;
      state.sessionExpired = false;
      setItem(STORAGE_KEYS.ACCESS_TOKEN, action.payload.accessToken);
      setItem(STORAGE_KEYS.REFRESH_TOKEN, action.payload.refreshToken);
      setItem(STORAGE_KEYS.USER, action.payload.user);
    },
    clearCredentials: (state) => {
      state.accessToken = null;
      state.refreshToken = null;
      state.user = null;
      state.isAuthenticated = false;
      state.loading = false;
      state.error = null;
      removeItem(STORAGE_KEYS.ACCESS_TOKEN);
      removeItem(STORAGE_KEYS.REFRESH_TOKEN);
      removeItem(STORAGE_KEYS.USER);
    },
    setSessionExpired: (state, action: PayloadAction<boolean>) => {
      state.sessionExpired = action.payload;
    },
    clearError: (state) => {
      state.error = null;
    },
  },
  extraReducers: (builder) => {
    builder
      .addCase(login.pending, (state) => {
        state.loading = true;
        state.error = null;
      })
      .addCase(login.fulfilled, (state, action) => {
        state.loading = false;
        state.accessToken = action.payload.accessToken;
        state.refreshToken = action.payload.refreshToken;
        state.user = action.payload.user;
        state.isAuthenticated = true;
        state.sessionExpired = false;
        setItem(STORAGE_KEYS.ACCESS_TOKEN, action.payload.accessToken);
        setItem(STORAGE_KEYS.REFRESH_TOKEN, action.payload.refreshToken);
        if (action.payload.user) {
          setItem(STORAGE_KEYS.USER, action.payload.user);
        }
      })
      .addCase(login.rejected, (state, action) => {
        state.loading = false;
        state.error = (action.payload as string) ?? 'Login failed';
      })
      .addCase(refreshAccessToken.fulfilled, (state, action) => {
        state.accessToken = action.payload.accessToken;
        state.refreshToken = action.payload.refreshToken;
        state.user = parseUserFromToken(action.payload.accessToken);
        state.isAuthenticated = true;
        setItem(STORAGE_KEYS.ACCESS_TOKEN, action.payload.accessToken);
        setItem(STORAGE_KEYS.REFRESH_TOKEN, action.payload.refreshToken);
        if (state.user) {
          setItem(STORAGE_KEYS.USER, state.user);
        }
      })
      .addCase(refreshAccessToken.rejected, (state) => {
        state.accessToken = null;
        state.refreshToken = null;
        state.user = null;
        state.isAuthenticated = false;
        state.sessionExpired = true;
        removeItem(STORAGE_KEYS.ACCESS_TOKEN);
        removeItem(STORAGE_KEYS.REFRESH_TOKEN);
        removeItem(STORAGE_KEYS.USER);
      });
  },
});

export const { setCredentials, clearCredentials, setSessionExpired, clearError } =
  authSlice.actions;
export default authSlice.reducer;

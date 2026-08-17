import axios from 'axios';

import type {
  AuthResponse,
  ForgotPasswordRequest,
  LoginRequest,
  RegisterRequest,
  RegistrationStatusResponse,
  ResetPasswordRequest,
  User,
} from '@/types/auth';
import { API_BASE_URL, API_ENDPOINTS } from '@/constants/api';
import { API_TIMEOUT_MS } from '@/constants/api';
import { parseUserFromToken } from '@/utils/jwt';

const publicApi = axios.create({
  baseURL: API_BASE_URL,
  timeout: API_TIMEOUT_MS,
  headers: {
    'Content-Type': 'application/json',
  },
});

export const authService = {
  login: async (credentials: LoginRequest): Promise<AuthResponse & { user: User | null }> => {
    const response = await publicApi.post<AuthResponse>(API_ENDPOINTS.AUTH.LOGIN, credentials);
    const user = parseUserFromToken(response.data.accessToken);
    return { ...response.data, user };
  },

  register: async (data: RegisterRequest): Promise<AuthResponse & { user: User | null }> => {
    const response = await publicApi.post<AuthResponse>(API_ENDPOINTS.AUTH.REGISTER, data);
    const user = parseUserFromToken(response.data.accessToken);
    return { ...response.data, user };
  },

  getRegistrationStatus: async (): Promise<RegistrationStatusResponse> => {
    const response = await publicApi.get<RegistrationStatusResponse>(
      API_ENDPOINTS.AUTH.REGISTRATION_STATUS
    );
    return response.data;
  },

  logout: async (refreshToken: string): Promise<void> => {
    await publicApi.post(API_ENDPOINTS.AUTH.LOGOUT, { refreshToken });
  },

  refresh: async (refreshToken: string): Promise<AuthResponse> => {
    const response = await publicApi.post<AuthResponse>(API_ENDPOINTS.AUTH.REFRESH, {
      refreshToken,
    });
    return response.data;
  },

  forgotPassword: async (request: ForgotPasswordRequest): Promise<void> => {
    await publicApi.post(API_ENDPOINTS.AUTH.FORGOT_PASSWORD, request);
  },

  resetPassword: async (request: ResetPasswordRequest): Promise<void> => {
    await publicApi.post(API_ENDPOINTS.AUTH.RESET_PASSWORD, request);
  },
};

export const API_BASE_URL = import.meta.env.VITE_API_BASE_URL || 'http://localhost:8081';

export const API_ENDPOINTS = {
  AUTH: {
    LOGIN: '/api/v1/auth/login',
    LOGOUT: '/api/v1/auth/logout',
    REFRESH: '/api/v1/auth/refresh',
    FORGOT_PASSWORD: '/api/v1/auth/forgot-password',
    RESET_PASSWORD: '/api/v1/auth/reset-password',
    ME: '/api/v1/auth/me',
  },
  IDENTITY: {
    USERS: '/api/v1/users',
    ROLES: '/api/v1/roles',
    PERMISSIONS: '/api/v1/permissions',
  },
} as const;

export const API_TIMEOUT_MS = 30000;

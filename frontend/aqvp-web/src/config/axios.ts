import axios, { type AxiosError, type AxiosInstance, type InternalAxiosRequestConfig } from 'axios';
import { API_BASE_URL, API_TIMEOUT_MS, QUALIFICATION_API_BASE_URL } from '@/constants/api';
import { STORAGE_KEYS } from '@/constants/storage';
import {
  clearCredentials,
  refreshAccessToken,
  setCredentials,
  setSessionExpired,
} from '@/features/auth/slices/authSlice';
import { getItem, removeItem, setItem } from '@/utils/storage';
import type { AppStore } from '@/store/store';

let storeRef: AppStore | null = null;
let isRefreshing = false;
let refreshSubscribers: Array<(token: string) => void> = [];

export const setupAxiosInterceptors = (store: AppStore) => {
  storeRef = store;
};

const subscribeTokenRefresh = (callback: (token: string) => void) => {
  refreshSubscribers.push(callback);
};

const onTokenRefreshed = (token: string) => {
  refreshSubscribers.forEach((callback) => callback(token));
  refreshSubscribers = [];
};

const attachAuthInterceptors = (client: AxiosInstance): AxiosInstance => {
  client.interceptors.request.use((config: InternalAxiosRequestConfig) => {
    const state = storeRef?.getState();
    const token = state?.auth.accessToken ?? getItem<string>(STORAGE_KEYS.ACCESS_TOKEN);
    if (token && config.headers) {
      config.headers.Authorization = `Bearer ${token}`;
    }
    return config;
  });

  client.interceptors.response.use(
    (response) => response,
    async (error: AxiosError) => {
      const originalRequest = error.config as InternalAxiosRequestConfig & { _retry?: boolean };

      if (error.response?.status !== 401 || originalRequest._retry) {
        return Promise.reject(error);
      }

      const refreshToken =
        storeRef?.getState().auth.refreshToken ?? getItem<string>(STORAGE_KEYS.REFRESH_TOKEN);

      if (!refreshToken) {
        storeRef?.dispatch(clearCredentials());
        storeRef?.dispatch(setSessionExpired(true));
        return Promise.reject(error);
      }

      if (!isRefreshing) {
        isRefreshing = true;
        originalRequest._retry = true;

        try {
          if (!storeRef) {
            throw new Error('Store not initialized');
          }
          const resultAction = await storeRef.dispatch(refreshAccessToken(refreshToken));
          const result = resultAction.payload as
            { accessToken: string; refreshToken: string } | undefined;

          if (!result) {
            throw new Error('Refresh failed');
          }

          const { accessToken, refreshToken: newRefreshToken } = result;
          storeRef.dispatch(
            setCredentials({
              accessToken,
              refreshToken: newRefreshToken,
              user: storeRef.getState().auth.user!,
            })
          );
          setItem(STORAGE_KEYS.ACCESS_TOKEN, accessToken);
          setItem(STORAGE_KEYS.REFRESH_TOKEN, newRefreshToken);
          onTokenRefreshed(accessToken);
        } catch (refreshError) {
          if (storeRef) {
            storeRef.dispatch(clearCredentials());
            storeRef.dispatch(setSessionExpired(true));
          }
          removeItem(STORAGE_KEYS.ACCESS_TOKEN);
          removeItem(STORAGE_KEYS.REFRESH_TOKEN);
          removeItem(STORAGE_KEYS.USER);
          return Promise.reject(refreshError);
        } finally {
          isRefreshing = false;
        }
      }

      return new Promise((resolve) => {
        subscribeTokenRefresh((token) => {
          if (originalRequest.headers) {
            originalRequest.headers.Authorization = `Bearer ${token}`;
          }
          resolve(client(originalRequest));
        });
      });
    }
  );

  return client;
};

const createApiClient = (baseURL: string): AxiosInstance =>
  attachAuthInterceptors(
    axios.create({
      baseURL,
      timeout: API_TIMEOUT_MS,
      headers: {
        'Content-Type': 'application/json',
      },
    })
  );

/** Authenticated client for the Identity service (users, roles, permissions, auth). */
export const api = createApiClient(API_BASE_URL);

/** Authenticated client for the Qualification service (institutions, programs). */
export const qualificationApi = createApiClient(QUALIFICATION_API_BASE_URL);

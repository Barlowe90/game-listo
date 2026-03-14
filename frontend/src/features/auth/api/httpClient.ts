import axios, { AxiosError, InternalAxiosRequestConfig } from 'axios';
import type { AuthResponse } from './auth.types';
import { getRefreshToken, saveRefreshToken, clearRefreshToken } from './tokenStorage';
import {
  getAccessToken,
  setAccessToken as setBridgeAccessToken,
  clearAccessToken as clearBridgeAccessToken,
  notifyUnauthorized,
  notifyTokenRefreshed,
} from './authSessionBridge';

// con esta clase hago que todas las llamadas al back (login, me, logout, refresh) sean consistentes y no repetir la misma config en cada función authApi

let refreshPromise: Promise<AuthResponse> | null = null;

export const httpClient = axios.create({
  baseURL: process.env.NEXT_PUBLIC_API_URL,
  headers: {
    'Content-Type': 'application/json',
  },
});

const refreshClient = axios.create({
  baseURL: process.env.NEXT_PUBLIC_API_URL,
  headers: {
    'Content-Type': 'application/json',
  },
});

// con la siguiente funcion evito que tenga que añadir a cada peticion el bearer token
httpClient.interceptors.request.use((config: InternalAxiosRequestConfig) => {
  const token = getAccessToken();

  if (token) {
    config.headers = config.headers ?? {};
    config.headers.Authorization = `Bearer ${token}`;
  }

  return config;
});

httpClient.interceptors.response.use(
  (response) => response,
  async (error: AxiosError) => {
    const originalRequest = error.config as
      | (InternalAxiosRequestConfig & { _retry?: boolean })
      | undefined;

    const status = error.response?.status;
    const requestUrl = originalRequest?.url ?? '';

    const isAuthRoute =
      requestUrl.includes('/v1/usuarios/auth/login') ||
      requestUrl.includes('/v1/usuarios/auth/refresh') ||
      requestUrl.includes('/v1/usuarios/auth/logout');

    if (!originalRequest || status !== 401 || originalRequest._retry || isAuthRoute) {
      return Promise.reject(error);
    }

    const refreshToken = getRefreshToken();

    if (!refreshToken) {
      clearRefreshToken();
      clearBridgeAccessToken();
      await notifyUnauthorized();
      return Promise.reject(error);
    }

    originalRequest._retry = true;

    try {
      refreshPromise ??= refreshClient
        .post<AuthResponse>('/v1/usuarios/auth/refresh', { refreshToken })
        .then((response) => response.data)
        .finally(() => {
          refreshPromise = null;
        });

      const authResponse = await refreshPromise;

      saveRefreshToken(authResponse.tokens.refreshToken);
      setBridgeAccessToken(authResponse.tokens.accessToken);
      await notifyTokenRefreshed(authResponse);

      originalRequest.headers = originalRequest.headers ?? {};
      originalRequest.headers.Authorization = `Bearer ${authResponse.tokens.accessToken}`;

      return httpClient(originalRequest);
    } catch (refreshError) {
      clearRefreshToken();
      clearBridgeAccessToken();
      await notifyUnauthorized();
      return Promise.reject(refreshError);
    }
  },
);

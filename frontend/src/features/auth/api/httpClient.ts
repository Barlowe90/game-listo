import axios, { AxiosError, InternalAxiosRequestConfig } from 'axios';
import { getAccessTokenValue, getRefreshTokenValue } from './auth.types';
import { getRefreshToken, saveRefreshToken, clearRefreshToken } from './tokenStorage';
import {
  getAccessToken,
  setAccessToken as setBridgeAccessToken,
  clearAccessToken as clearBridgeAccessToken,
  notifyUnauthorized,
  notifyTokenRefreshed,
} from './authSessionBridge';
import { shouldClearSessionAfterRefreshError } from './refreshError';
import { executeRefreshRequest } from './refreshRequest';

// con esta clase hago que todas las llamadas al back (login, me, logout, refresh) sean consistentes y no repetir la misma config en cada funcion authApi

export const httpClient = axios.create({
  baseURL: process.env.NEXT_PUBLIC_API_URL,
  headers: {
    'Content-Type': 'application/json',
  },
});

// con la siguiente funcion evito que tenga que anadir a cada peticion el bearer token
httpClient.interceptors.request.use((config: InternalAxiosRequestConfig) => {
  const token = getAccessToken();
  const requestUrl = config.url ?? '';

  const isAuthRoute =
    requestUrl.includes('/v1/usuarios/auth/login') ||
    requestUrl.includes('/v1/usuarios/auth/register') ||
    requestUrl.includes('/v1/usuarios/auth/verify-email') ||
    requestUrl.includes('/v1/usuarios/auth/resend-verification') ||
    requestUrl.includes('/v1/usuarios/auth/forgot-password') ||
    requestUrl.includes('/v1/usuarios/auth/reset-password') ||
    requestUrl.includes('/v1/usuarios/auth/refresh') ||
    requestUrl.includes('/v1/usuarios/auth/logout');

  if (token && !isAuthRoute) {
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
      requestUrl.includes('/v1/usuarios/auth/register') ||
      requestUrl.includes('/v1/usuarios/auth/verify-email') ||
      requestUrl.includes('/v1/usuarios/auth/resend-verification') ||
      requestUrl.includes('/v1/usuarios/auth/forgot-password') ||
      requestUrl.includes('/v1/usuarios/auth/reset-password') ||
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
      const authResponse = await executeRefreshRequest(refreshToken);
      const nextRefreshToken = getRefreshTokenValue(authResponse);
      const currentRefreshToken = getRefreshToken();

      if (currentRefreshToken !== refreshToken && currentRefreshToken !== nextRefreshToken) {
        return Promise.reject(error);
      }

      saveRefreshToken(nextRefreshToken);
      setBridgeAccessToken(getAccessTokenValue(authResponse));
      await notifyTokenRefreshed(authResponse);

      originalRequest.headers = originalRequest.headers ?? {};
      originalRequest.headers.Authorization = `Bearer ${getAccessTokenValue(authResponse)}`;

      return httpClient(originalRequest);
    } catch (refreshError) {
      if (
        shouldClearSessionAfterRefreshError(refreshError) &&
        getRefreshToken() === refreshToken
      ) {
        clearRefreshToken();
        clearBridgeAccessToken();
        await notifyUnauthorized();
      }

      return Promise.reject(refreshError);
    }
  },
);

import axios from 'axios';
import type { AuthResponse } from './auth.types';
import { getApiBaseUrl } from '@/shared/config/api';

const refreshClient = axios.create({
  baseURL: getApiBaseUrl(),
  headers: {
    'Content-Type': 'application/json',
  },
});

const refreshRequests = new Map<string, Promise<AuthResponse>>();

export function executeRefreshRequest(refreshToken: string): Promise<AuthResponse> {
  const existingRequest = refreshRequests.get(refreshToken);

  if (existingRequest) {
    return existingRequest;
  }

  const request = refreshClient
    .post<AuthResponse>('/v1/usuarios/auth/refresh', { refreshToken })
    .then((response) => response.data)
    .finally(() => {
      if (refreshRequests.get(refreshToken) === request) {
        refreshRequests.delete(refreshToken);
      }
    });

  refreshRequests.set(refreshToken, request);
  return request;
}

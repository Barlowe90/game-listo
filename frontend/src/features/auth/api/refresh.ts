import { AuthResponse } from './auth.types';
import { httpClient } from './httpClient';

export async function refresh(refreshToken: string): Promise<AuthResponse> {
  const response = await httpClient.post<AuthResponse>('/v1/usuarios/auth/refresh', {
    refreshToken,
  });

  return response.data;
}

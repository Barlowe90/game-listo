import { AuthResponse } from './auth.types';
import { httpClient } from './httpClient';

export async function login(email: string, password: string): Promise<AuthResponse> {
  const response = await httpClient.post<AuthResponse>('/v1/usuarios/auth/login', {
    email,
    password,
  });

  return response.data;
}

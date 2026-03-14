import { httpClient } from './httpClient';

export interface RegisterRequest {
  username: string;
  email: string;
  password: string;
}

export async function register(data: RegisterRequest): Promise<void> {
  await httpClient.post('/v1/usuarios/auth/register', data);
}

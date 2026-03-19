import { httpClient } from './httpClient';

export interface ResetPasswordRequest {
  email: string;
  nuevaContrasena: string;
  token: string;
}

export async function resetPassword(data: ResetPasswordRequest): Promise<void> {
  await httpClient.post('/v1/usuarios/auth/reset-password', data);
}

import { httpClient } from './httpClient';

export interface ForgotPasswordRequest {
  email: string;
}

export async function forgotPassword(data: ForgotPasswordRequest): Promise<void> {
  await httpClient.post('/v1/usuarios/auth/forgot-password', data);
}

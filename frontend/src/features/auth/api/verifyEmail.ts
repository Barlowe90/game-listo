import { httpClient } from './httpClient';

export interface VerifyEmailRequest {
  token: string;
}

export async function verifyEmail(data: VerifyEmailRequest): Promise<void> {
  await httpClient.post('/v1/usuarios/auth/verify-email', data);
}

import { httpClient } from './httpClient';

export interface ResendVerificationRequest {
  email: string;
}

export async function resendVerification(data: ResendVerificationRequest): Promise<void> {
  await httpClient.post('/v1/usuarios/auth/resend-verification', data);
}

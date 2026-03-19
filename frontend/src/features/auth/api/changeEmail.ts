import { httpClient } from './httpClient';

export async function changeEmail(email: string): Promise<void> {
  await httpClient.put('/v1/usuarios/email', { email });
}

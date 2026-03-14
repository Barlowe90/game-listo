import { httpClient } from './httpClient';

export async function logout(refreshToken: string, accessToken?: string | null): Promise<void> {
  await httpClient.post(
    '/v1/usuarios/auth/logout',
    {
      refreshToken,
    },
    {
      headers: accessToken ? { Authorization: `Bearer ${accessToken}` } : undefined,
    },
  );
}

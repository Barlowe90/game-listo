import { UsuarioResponse } from './auth.types';
import { httpClient } from './httpClient';

export async function me(accessToken: string): Promise<UsuarioResponse> {
  const response = await httpClient.get<UsuarioResponse>('/v1/usuarios/auth/me', {
    headers: {
      Authorization: `Bearer ${accessToken}`,
    },
  });

  return response.data;
}

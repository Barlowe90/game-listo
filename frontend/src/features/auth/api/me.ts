import { UsuarioResponse } from './auth.types';
import { httpClient } from './httpClient';

export async function me(): Promise<UsuarioResponse> {
  const response = await httpClient.get<UsuarioResponse>('/v1/usuarios/auth/me');

  return response.data;
}

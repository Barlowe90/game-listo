import type { UsuarioResponse } from './auth.types';
import { httpClient } from './httpClient';

export async function unlinkDiscord(): Promise<UsuarioResponse> {
  const response = await httpClient.delete<UsuarioResponse>('/v1/usuarios/discord');

  return response.data;
}

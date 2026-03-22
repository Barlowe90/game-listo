import type { UsuarioResponse } from './auth.types';
import { httpClient } from './httpClient';

export async function linkDiscord(discordUserId: string): Promise<UsuarioResponse> {
  const response = await httpClient.put<UsuarioResponse>('/v1/usuarios/discord', {
    discordUserId,
  });

  return response.data;
}

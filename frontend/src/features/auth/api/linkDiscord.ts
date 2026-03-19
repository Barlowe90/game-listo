import type { UsuarioResponse } from './auth.types';
import { httpClient } from './httpClient';

export async function linkDiscord(
  discordUserId: string,
  discordUsername: string,
): Promise<UsuarioResponse> {
  const response = await httpClient.put<UsuarioResponse>('/v1/usuarios/discord', {
    discordUserId,
    discordUsername,
  });

  return response.data;
}

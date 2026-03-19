import { httpClient } from '@/features/auth/api/httpClient';
import type { UsuarioRef } from '@/features/social/model/social.types';

async function listFriends(): Promise<UsuarioRef[]> {
  const response = await httpClient.get<UsuarioRef[]>('/v1/social/users/friends');

  return response.data;
}

export const socialApi = {
  listFriends,
};

import { httpClient } from '@/features/auth/api/httpClient';
import type { ResumenSocialJuego, UsuarioRef } from '@/features/social/model/social.types';

async function listFriends(): Promise<UsuarioRef[]> {
  const response = await httpClient.get<UsuarioRef[]>('/v1/social/users/friends');

  return response.data;
}

async function getGameSummary(gameId: number): Promise<ResumenSocialJuego> {
  const response = await httpClient.get<ResumenSocialJuego>(`/v1/social/games/${gameId}/summary`);

  return response.data;
}

async function addFriend(friendId: string): Promise<void> {
  await httpClient.post(`/v1/social/users/friends/${friendId}`, null);
}

async function removeFriend(friendId: string): Promise<void> {
  await httpClient.delete(`/v1/social/users/friends/${friendId}`);
}

export const socialApi = {
  addFriend,
  getGameSummary,
  listFriends,
  removeFriend,
};

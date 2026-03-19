import { httpClient } from '@/features/auth/api/httpClient';
import type { UsuarioRef } from '@/features/social/model/social.types';

async function listFriends(): Promise<UsuarioRef[]> {
  const response = await httpClient.get<UsuarioRef[]>('/v1/social/users/friends');

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
  listFriends,
  removeFriend,
};

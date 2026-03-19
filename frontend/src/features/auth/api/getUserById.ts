import type { UsuarioResponse } from '@/features/auth/api/auth.types';
import { httpClient } from '@/features/auth/api/httpClient';

export async function getUserById(userId: string): Promise<UsuarioResponse> {
  const response = await httpClient.get<UsuarioResponse>(`/v1/usuarios/${userId}`);

  return response.data;
}

import { httpClient } from '@/features/auth/api/httpClient';
import type {
  CrearPublicacionPayload,
  Publicacion,
} from '@/features/publicaciones/model/publicaciones.types';

async function getPublicacionesPorJuego(gameId: number): Promise<Publicacion[]> {
  const response = await httpClient.get<Publicacion[]>(`/v1/publicaciones/game/${gameId}`);

  return response.data;
}

async function getPublicacionesPorUsuario(userId: string): Promise<Publicacion[]> {
  const response = await httpClient.get<Publicacion[]>(`/v1/publicaciones/user/${userId}`);

  return response.data;
}

async function createPublicacion(payload: CrearPublicacionPayload): Promise<Publicacion> {
  const response = await httpClient.post<Publicacion>('/v1/publicaciones', payload);

  return response.data;
}

export const publicacionesApi = {
  createPublicacion,
  getPublicacionesPorJuego,
  getPublicacionesPorUsuario,
};

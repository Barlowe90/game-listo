import { httpClient } from '@/features/auth/api/httpClient';
import type {
  CrearPublicacionPayload,
  EditarPublicacionPayload,
  GrupoJuego,
  PublicacionDetalle,
  Publicacion,
  SolicitudUnion,
} from '@/features/publicaciones/model/publicaciones.types';

async function getPublicacionesPorJuego(gameId: number): Promise<Publicacion[]> {
  const response = await httpClient.get<Publicacion[]>(`/v1/publicaciones/game/${gameId}`);

  return response.data;
}

async function getPublicacionesPorUsuario(userId: string): Promise<Publicacion[]> {
  const response = await httpClient.get<Publicacion[]>(`/v1/publicaciones/user/${userId}`);

  return response.data;
}

async function getPublicacion(publicacionId: string): Promise<PublicacionDetalle> {
  const response = await httpClient.get<PublicacionDetalle>(`/v1/publicaciones/${publicacionId}`);

  return response.data;
}

async function getGrupoJuego(grupoId: string): Promise<GrupoJuego> {
  const response = await httpClient.get<GrupoJuego>(`/v1/publicaciones/grupos/${grupoId}`);

  return response.data;
}

async function createPublicacion(payload: CrearPublicacionPayload): Promise<Publicacion> {
  const response = await httpClient.post<Publicacion>('/v1/publicaciones', payload);

  return response.data;
}

async function createSolicitudUnion(publicacionId: string): Promise<SolicitudUnion> {
  const response = await httpClient.post<SolicitudUnion>(
    `/v1/publicaciones/${publicacionId}/solicitud-union`,
  );

  return response.data;
}

async function getSolicitudesUnionEnviadas(): Promise<SolicitudUnion[]> {
  const response = await httpClient.get<SolicitudUnion[]>('/v1/publicaciones/solicitudes-union/enviadas');

  return response.data;
}

async function getSolicitudesUnionRecibidas(): Promise<SolicitudUnion[]> {
  const response = await httpClient.get<SolicitudUnion[]>('/v1/publicaciones/solicitudes-union/recibidas');

  return response.data;
}

async function updatePublicacion(
  publicacionId: string,
  payload: EditarPublicacionPayload,
): Promise<Publicacion> {
  const response = await httpClient.put<Publicacion>(`/v1/publicaciones/${publicacionId}`, payload);

  return response.data;
}

async function deletePublicacion(publicacionId: string): Promise<void> {
  await httpClient.delete(`/v1/publicaciones/${publicacionId}`);
}

export const publicacionesApi = {
  createPublicacion,
  createSolicitudUnion,
  deletePublicacion,
  getGrupoJuego,
  getPublicacion,
  getPublicacionesPorJuego,
  getPublicacionesPorUsuario,
  getSolicitudesUnionEnviadas,
  getSolicitudesUnionRecibidas,
  updatePublicacion,
};

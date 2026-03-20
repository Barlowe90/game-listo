import { getUserById } from '@/features/auth/api/getUserById';
import type { UsuarioResponse } from '@/features/auth/api/auth.types';
import { getGamesByIds } from '@/features/catalogo/api/catalogApi';
import { publicacionesApi } from '@/features/publicaciones/api/publicacionesApi';
import type {
  Publicacion,
  PublicacionDetalle,
  SolicitudUnion,
} from '@/features/publicaciones/model/publicaciones.types';

export function getPublicacionesCountLabel(count: number) {
  return `${count} ${count === 1 ? 'publicacion' : 'publicaciones'}`;
}

export function getSolicitudesCountLabel(count: number) {
  return `${count} ${count === 1 ? 'solicitud' : 'solicitudes'}`;
}

export function getGruposCountLabel(count: number) {
  return `${count} ${count === 1 ? 'grupo' : 'grupos'}`;
}

export function formatSolicitudEstado(estado: SolicitudUnion['estadoSolicitud']) {
  switch (estado) {
    case 'SOLICITADA':
      return 'Pendiente';
    case 'ACEPTADA':
      return 'Aceptada';
    case 'RECHAZADA':
      return 'Rechazada';
    default:
      return estado;
  }
}

export function formatShortId(value: string) {
  return value.slice(0, 8);
}

export async function loadGameTitles(publicaciones: Array<Pick<Publicacion, 'gameId'>>) {
  const gameIds = publicaciones
    .map((publicacion) => Number.parseInt(publicacion.gameId, 10))
    .filter(Number.isFinite);

  if (!gameIds.length) {
    return {};
  }

  try {
    const gamesMap = await getGamesByIds(gameIds);

    return Object.fromEntries(
      Array.from(gamesMap.entries()).map(([gameId, game]) => [String(gameId), game.name]),
    );
  } catch {
    return {};
  }
}

export async function loadPublicacionesDetalleMap(publicacionIds: string[]) {
  const uniquePublicacionIds = [...new Set(publicacionIds)];

  if (!uniquePublicacionIds.length) {
    return {};
  }

  const pairs = await Promise.all(
    uniquePublicacionIds.map(async (publicacionId) => {
      try {
        const publicacion = await publicacionesApi.getPublicacion(publicacionId);
        return [publicacionId, publicacion] as const;
      } catch {
        return [publicacionId, null] as const;
      }
    }),
  );

  return Object.fromEntries(pairs) as Record<string, PublicacionDetalle | null>;
}

export async function loadUsuariosMap(userIds: string[]) {
  const uniqueUserIds = [...new Set(userIds)];

  if (!uniqueUserIds.length) {
    return {};
  }

  const pairs = await Promise.all(
    uniqueUserIds.map(async (userId) => {
      try {
        const user = await getUserById(userId);
        return [userId, user] as const;
      } catch {
        return [userId, null] as const;
      }
    }),
  );

  return Object.fromEntries(pairs) as Record<string, UsuarioResponse | null>;
}

export function getJoinedPublicaciones(
  solicitudesEnviadas: SolicitudUnion[],
  publicacionesDetalleById: Record<string, PublicacionDetalle | null>,
  userId: string | null,
) {
  if (!userId) {
    return [];
  }

  const publicaciones = solicitudesEnviadas
    .filter((solicitud) => solicitud.estadoSolicitud === 'ACEPTADA')
    .map((solicitud) => publicacionesDetalleById[solicitud.publicacionId])
    .filter((publicacion): publicacion is PublicacionDetalle => Boolean(publicacion?.grupoId))
    .filter((publicacion) =>
      publicacion.participantes.some((participante) => participante.id === userId),
    );

  return Array.from(
    new Map(publicaciones.map((publicacion) => [publicacion.id, publicacion])).values(),
  );
}

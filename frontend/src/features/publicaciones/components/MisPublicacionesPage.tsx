'use client';

import axios from 'axios';
import { useEffect, useState } from 'react';
import type { UsuarioResponse } from '@/features/auth/api/auth.types';
import { useAuth } from '@/features/auth/hooks/useAuth';
import { publicacionesApi } from '@/features/publicaciones/api/publicacionesApi';
import { GrupoJuegoInfoDialog } from '@/features/publicaciones/components/GrupoJuegoInfoDialog';
import { MisPublicacionesGridSection } from '@/features/publicaciones/components/MisPublicacionesGridSection';
import { MisPublicacionesHeader } from '@/features/publicaciones/components/MisPublicacionesHeader';
import { MisPublicacionesJoinedGroupsSection } from '@/features/publicaciones/components/MisPublicacionesJoinedGroupsSection';
import { MisPublicacionesSolicitudesSection } from '@/features/publicaciones/components/MisPublicacionesSolicitudesSection';
import { PublicacionAbandonarGrupoDialog } from '@/features/publicaciones/components/PublicacionAbandonarGrupoDialog';
import { PublicacionDeleteDialog } from '@/features/publicaciones/components/PublicacionDeleteDialog';
import { PublicacionEditorDialog } from '@/features/publicaciones/components/PublicacionEditorDialog';
import {
  getJoinedPublicaciones,
  loadGameTitles,
  loadPublicacionesDetalleMap,
  loadUsuariosMap,
} from '@/features/publicaciones/components/misPublicaciones.utils';
import type {
  EditarPublicacionPayload,
  GrupoJuego,
  Publicacion,
  PublicacionDetalle,
  SolicitudUnion,
  SolicitudUnionEstadoResolucion,
} from '@/features/publicaciones/model/publicaciones.types';
import { PageSection } from '@/shared/components/layout/PageSection';
import { Toast } from '@/shared/components/ui/Toast';

interface ApiErrorResponse {
  error?: string;
  message?: string;
}

function getApiErrorMessage(error: unknown, fallback: string) {
  if (axios.isAxiosError<ApiErrorResponse>(error)) {
    return error.response?.data?.error ?? error.response?.data?.message ?? fallback;
  }

  return fallback;
}

async function loadGrupoJuego(publicacion: Publicacion): Promise<GrupoJuego | null> {
  if (!publicacion.grupoId) {
    return null;
  }

  try {
    return await publicacionesApi.getGrupoJuego(publicacion.grupoId);
  } catch {
    return null;
  }
}

async function loadGruposPorPublicacion(publicaciones: Publicacion[]) {
  const grupos = await Promise.all(
    publicaciones.map(async (publicacion) => [publicacion.id, await loadGrupoJuego(publicacion)]),
  );

  return Object.fromEntries(grupos) as Record<string, GrupoJuego | null>;
}

export function MisPublicacionesPage() {
  const { user } = useAuth();
  const userId = user?.id ?? null;
  const [publicaciones, setPublicaciones] = useState<Publicacion[]>([]);
  const [solicitudesEnviadas, setSolicitudesEnviadas] = useState<SolicitudUnion[]>([]);
  const [solicitudesRecibidas, setSolicitudesRecibidas] = useState<SolicitudUnion[]>([]);
  const [gameTitlesById, setGameTitlesById] = useState<Record<string, string>>({});
  const [publicacionesDetalleById, setPublicacionesDetalleById] = useState<
    Record<string, PublicacionDetalle | null>
  >({});
  const [gruposByPublicacionId, setGruposByPublicacionId] = useState<
    Record<string, GrupoJuego | null>
  >({});
  const [usuariosById, setUsuariosById] = useState<Record<string, UsuarioResponse | null>>({});
  const [isLoading, setIsLoading] = useState(true);
  const [loadError, setLoadError] = useState<string | null>(null);
  const [successMessage, setSuccessMessage] = useState<string | null>(null);
  const [editingPublicacion, setEditingPublicacion] = useState<Publicacion | null>(null);
  const [deletingPublicacion, setDeletingPublicacion] = useState<Publicacion | null>(null);
  const [leavingPublicacion, setLeavingPublicacion] = useState<Publicacion | null>(null);
  const [groupInfoTarget, setGroupInfoTarget] = useState<{
    grupoId: string;
    grupo: GrupoJuego | null;
    publicacionTitle: string;
  } | null>(null);
  const [isSubmittingPublicacion, setIsSubmittingPublicacion] = useState(false);
  const [isDeletingPublicacion, setIsDeletingPublicacion] = useState(false);
  const [isLeavingGroup, setIsLeavingGroup] = useState(false);
  const [resolvingSolicitudIds, setResolvingSolicitudIds] = useState<string[]>([]);

  useEffect(() => {
    let ignore = false;

    async function loadPageData() {
      if (!userId) {
        setPublicaciones([]);
        setSolicitudesEnviadas([]);
        setSolicitudesRecibidas([]);
        setGameTitlesById({});
        setPublicacionesDetalleById({});
        setGruposByPublicacionId({});
        setUsuariosById({});
        setIsLoading(false);
        return;
      }

      setIsLoading(true);
      setLoadError(null);

      try {
        const [nextPublicaciones, nextSolicitudesEnviadas, nextSolicitudesRecibidas] =
          await Promise.all([
            publicacionesApi.getPublicacionesPorUsuario(userId),
            publicacionesApi.getSolicitudesUnionEnviadas(),
            publicacionesApi.getSolicitudesUnionRecibidas(),
          ]);

        const publicacionIds = [
          ...nextSolicitudesEnviadas.map((solicitud) => solicitud.publicacionId),
          ...nextSolicitudesRecibidas.map((solicitud) => solicitud.publicacionId),
        ];
        const requestUserIds = nextSolicitudesRecibidas.map((solicitud) => solicitud.usuarioId);

        const [nextPublicacionesDetalleById, nextUsuariosById, nextGruposByPublicacionId] =
          await Promise.all([
            loadPublicacionesDetalleMap(publicacionIds),
            loadUsuariosMap(requestUserIds),
            loadGruposPorPublicacion(nextPublicaciones),
          ]);
        const nextSolicitudesPublicacionDetalle = Object.values(
          nextPublicacionesDetalleById,
        ).filter((publicacion): publicacion is PublicacionDetalle => Boolean(publicacion));
        const nextGameTitlesById = await loadGameTitles([
          ...nextPublicaciones,
          ...nextSolicitudesPublicacionDetalle,
        ]);

        if (ignore) {
          return;
        }

        setPublicaciones(nextPublicaciones);
        setSolicitudesEnviadas(nextSolicitudesEnviadas);
        setSolicitudesRecibidas(nextSolicitudesRecibidas);
        setGameTitlesById(nextGameTitlesById);
        setPublicacionesDetalleById(nextPublicacionesDetalleById);
        setGruposByPublicacionId(nextGruposByPublicacionId);
        setUsuariosById(nextUsuariosById);
      } catch (error) {
        if (ignore) {
          return;
        }

        setLoadError(
          getApiErrorMessage(error, 'No se pudieron cargar tus publicaciones y solicitudes.'),
        );
      } finally {
        if (!ignore) {
          setIsLoading(false);
        }
      }
    }

    void loadPageData();

    return () => {
      ignore = true;
    };
  }, [userId]);

  function handleEditDialogOpenChange(open: boolean) {
    if (isSubmittingPublicacion) {
      return;
    }

    if (!open) {
      setEditingPublicacion(null);
    }
  }

  function handleDeleteDialogOpenChange(open: boolean) {
    if (isDeletingPublicacion) {
      return;
    }

    if (!open) {
      setDeletingPublicacion(null);
    }
  }

  function handleLeaveDialogOpenChange(open: boolean) {
    if (isLeavingGroup) {
      return;
    }

    if (!open) {
      setLeavingPublicacion(null);
    }
  }

  function handleGroupInfoDialogOpenChange(open: boolean) {
    if (!open) {
      setGroupInfoTarget(null);
    }
  }

  async function handleUpdatePublicacion(payload: EditarPublicacionPayload) {
    if (!editingPublicacion) {
      return;
    }

    setIsSubmittingPublicacion(true);
    setLoadError(null);
    setSuccessMessage(null);

    try {
      const updatedPublicacion = await publicacionesApi.updatePublicacion(
        editingPublicacion.id,
        payload,
      );

      setPublicaciones((currentPublicaciones) =>
        currentPublicaciones.map((currentPublicacion) =>
          currentPublicacion.id === updatedPublicacion.id
            ? {
                ...currentPublicacion,
                ...updatedPublicacion,
                grupoId: updatedPublicacion.grupoId ?? currentPublicacion.grupoId,
              }
            : currentPublicacion,
        ),
      );
      setPublicacionesDetalleById((currentPublicacionesDetalleById) => {
        const currentDetail = currentPublicacionesDetalleById[updatedPublicacion.id];

        if (!currentDetail) {
          return currentPublicacionesDetalleById;
        }

        return {
          ...currentPublicacionesDetalleById,
          [updatedPublicacion.id]: {
            ...currentDetail,
            ...updatedPublicacion,
            grupoId: updatedPublicacion.grupoId ?? currentDetail.grupoId,
          },
        };
      });
      setSuccessMessage('Publicacion actualizada correctamente.');
    } finally {
      setIsSubmittingPublicacion(false);
    }
  }

  async function handleDeletePublicacion() {
    if (!deletingPublicacion) {
      return;
    }

    setIsDeletingPublicacion(true);
    setLoadError(null);
    setSuccessMessage(null);

    try {
      await publicacionesApi.deletePublicacion(deletingPublicacion.id);
      setPublicaciones((currentPublicaciones) =>
        currentPublicaciones.filter(
          (currentPublicacion) => currentPublicacion.id !== deletingPublicacion.id,
        ),
      );
      setSolicitudesRecibidas((currentSolicitudesRecibidas) =>
        currentSolicitudesRecibidas.filter(
          (solicitud) => solicitud.publicacionId !== deletingPublicacion.id,
        ),
      );
      setPublicacionesDetalleById((currentPublicacionesDetalleById) => {
        const nextPublicacionesDetalleById = { ...currentPublicacionesDetalleById };
        delete nextPublicacionesDetalleById[deletingPublicacion.id];
        return nextPublicacionesDetalleById;
      });
      setGruposByPublicacionId((currentGruposByPublicacionId) => {
        const nextGruposByPublicacionId = { ...currentGruposByPublicacionId };
        delete nextGruposByPublicacionId[deletingPublicacion.id];
        return nextGruposByPublicacionId;
      });
      setDeletingPublicacion(null);
      setSuccessMessage('Publicacion eliminada correctamente.');
    } catch (error) {
      setLoadError(getApiErrorMessage(error, 'No se pudo eliminar la publicacion.'));
    } finally {
      setIsDeletingPublicacion(false);
    }
  }

  function handleViewGroupInfo(publicacion: Publicacion) {
    if (!publicacion.grupoId) {
      return;
    }

    setGroupInfoTarget({
      grupoId: publicacion.grupoId,
      grupo: gruposByPublicacionId[publicacion.id] ?? null,
      publicacionTitle: publicacion.titulo,
    });
  }

  function handleLeaveGroup(publicacion: Publicacion) {
    setLeavingPublicacion(publicacion);
  }

  async function handleAbandonarGrupo() {
    if (!leavingPublicacion || !userId) {
      return;
    }

    setIsLeavingGroup(true);
    setLoadError(null);
    setSuccessMessage(null);

    try {
      await publicacionesApi.abandonarGrupo(leavingPublicacion.id);
      setPublicacionesDetalleById((currentPublicacionesDetalleById) => {
        const currentDetail = currentPublicacionesDetalleById[leavingPublicacion.id];

        if (!currentDetail) {
          return currentPublicacionesDetalleById;
        }

        const nextParticipantes = currentDetail.participantes.filter(
          (participante) => participante.id !== userId,
        );

        return {
          ...currentPublicacionesDetalleById,
          [leavingPublicacion.id]: {
            ...currentDetail,
            participantes: nextParticipantes,
            participantesCount: nextParticipantes.length,
            plazasDisponibles: Math.max(
              currentDetail.jugadoresMaximos - nextParticipantes.length,
              0,
            ),
          },
        };
      });
      setLeavingPublicacion(null);
      setSuccessMessage('Has abandonado el grupo correctamente.');
    } catch (error) {
      setLoadError(getApiErrorMessage(error, 'No se pudo abandonar el grupo.'));
    } finally {
      setIsLeavingGroup(false);
    }
  }

  async function handleResolveSolicitud(
    solicitud: SolicitudUnion,
    estadoSolicitud: SolicitudUnionEstadoResolucion,
  ) {
    setResolvingSolicitudIds((currentSolicitudIds) =>
      currentSolicitudIds.includes(solicitud.id)
        ? currentSolicitudIds
        : [...currentSolicitudIds, solicitud.id],
    );
    setLoadError(null);
    setSuccessMessage(null);

    try {
      const updatedSolicitud = await publicacionesApi.resolveSolicitudUnion(
        solicitud.id,
        estadoSolicitud,
      );

      setSolicitudesRecibidas((currentSolicitudesRecibidas) =>
        currentSolicitudesRecibidas.map((currentSolicitud) =>
          currentSolicitud.id === updatedSolicitud.id ? updatedSolicitud : currentSolicitud,
        ),
      );

      if (estadoSolicitud === 'ACEPTADA') {
        try {
          const updatedPublicacionDetalle = await publicacionesApi.getPublicacion(
            solicitud.publicacionId,
          );
          const updatedGrupo = await loadGrupoJuego(updatedPublicacionDetalle);

          setPublicacionesDetalleById((currentPublicacionesDetalleById) => ({
            ...currentPublicacionesDetalleById,
            [updatedPublicacionDetalle.id]: updatedPublicacionDetalle,
          }));
          setGruposByPublicacionId((currentGruposByPublicacionId) => ({
            ...currentGruposByPublicacionId,
            [solicitud.publicacionId]: updatedGrupo,
          }));
        } catch {
          // Si el refresco del detalle falla, mantenemos el cambio de estado de la solicitud.
        }
      }

      setSuccessMessage(
        estadoSolicitud === 'ACEPTADA'
          ? 'Solicitud aceptada correctamente.'
          : 'Solicitud rechazada correctamente.',
      );
    } catch (error) {
      setLoadError(
        getApiErrorMessage(
          error,
          estadoSolicitud === 'ACEPTADA'
            ? 'No se pudo aceptar la solicitud.'
            : 'No se pudo rechazar la solicitud.',
        ),
      );
    } finally {
      setResolvingSolicitudIds((currentSolicitudIds) =>
        currentSolicitudIds.filter((solicitudId) => solicitudId !== solicitud.id),
      );
    }
  }

  const joinedPublicaciones = getJoinedPublicaciones(
    solicitudesEnviadas,
    publicacionesDetalleById,
    publicaciones,
    gruposByPublicacionId,
    userId,
  );
  const disableCardActions = isSubmittingPublicacion || isDeletingPublicacion || isLeavingGroup;

  return (
    <PageSection size="wide">
      <div className="grid gap-8">
        <MisPublicacionesHeader publicacionesCount={publicaciones.length} />

        <MisPublicacionesSolicitudesSection
          isLoading={isLoading}
          onResolveSolicitud={handleResolveSolicitud}
          publicacionesDetalleById={publicacionesDetalleById}
          resolvingSolicitudIds={resolvingSolicitudIds}
          solicitudesEnviadas={solicitudesEnviadas}
          solicitudesRecibidas={solicitudesRecibidas}
          usuariosById={usuariosById}
        />

        {loadError ? <Toast variant="error" title={loadError} /> : null}
        {successMessage ? <Toast title={successMessage} /> : null}

        <MisPublicacionesJoinedGroupsSection
          currentUserId={userId}
          disableActions={disableCardActions}
          gameTitlesById={gameTitlesById}
          isLoading={isLoading}
          joinedPublicaciones={joinedPublicaciones}
          onLeaveGroup={handleLeaveGroup}
          onViewGroupInfo={handleViewGroupInfo}
        />

        <MisPublicacionesGridSection
          currentUserId={userId}
          disableActions={disableCardActions}
          gameTitlesById={gameTitlesById}
          gruposByPublicacionId={gruposByPublicacionId}
          isLoading={isLoading}
          onDelete={setDeletingPublicacion}
          onEdit={setEditingPublicacion}
          onViewGroupInfo={handleViewGroupInfo}
          publicaciones={publicaciones}
        />

        {editingPublicacion ? (
          <PublicacionEditorDialog
            open={editingPublicacion !== null}
            mode="edit"
            publicacion={editingPublicacion}
            isSubmitting={isSubmittingPublicacion}
            onOpenChange={handleEditDialogOpenChange}
            onSubmit={handleUpdatePublicacion}
          />
        ) : null}

        <PublicacionDeleteDialog
          open={deletingPublicacion !== null}
          publicacion={deletingPublicacion}
          isDeleting={isDeletingPublicacion}
          onOpenChange={handleDeleteDialogOpenChange}
          onConfirm={handleDeletePublicacion}
        />

        <PublicacionAbandonarGrupoDialog
          open={leavingPublicacion !== null}
          publicacion={leavingPublicacion}
          isLeaving={isLeavingGroup}
          onOpenChange={handleLeaveDialogOpenChange}
          onConfirm={handleAbandonarGrupo}
        />

        <GrupoJuegoInfoDialog
          open={groupInfoTarget !== null}
          grupoId={groupInfoTarget?.grupoId ?? null}
          grupo={groupInfoTarget?.grupo ?? null}
          publicacionTitle={groupInfoTarget?.publicacionTitle}
          onOpenChange={handleGroupInfoDialogOpenChange}
        />
      </div>
    </PageSection>
  );
}

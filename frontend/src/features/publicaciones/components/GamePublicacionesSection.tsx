'use client';

import axios from 'axios';
import { Plus } from 'lucide-react';
import Link from 'next/link';
import { useEffect, useState } from 'react';
import { useAuth } from '@/features/auth/hooks/useAuth';
import { publicacionesApi } from '@/features/publicaciones/api/publicacionesApi';
import { GrupoJuegoInfoDialog } from '@/features/publicaciones/components/GrupoJuegoInfoDialog';
import { PublicacionCard } from '@/features/publicaciones/components/PublicacionCard';
import { PublicacionDeleteDialog } from '@/features/publicaciones/components/PublicacionDeleteDialog';
import { PublicacionEditorDialog } from '@/features/publicaciones/components/PublicacionEditorDialog';
import type {
  CrearPublicacionPayload,
  EditarPublicacionPayload,
  GrupoJuego,
  Publicacion,
} from '@/features/publicaciones/model/publicaciones.types';
import { EmptyPublicationsState } from '@/shared/components/domain/EmptyPublicationsState';
import { Badge } from '@/shared/components/ui/Badge';
import { Button } from '@/shared/components/ui/Button';
import { Card } from '@/shared/components/ui/Card';
import { Toast } from '@/shared/components/ui/Toast';

interface ApiErrorResponse {
  error?: string;
  errors?: Record<string, string>;
  message?: string;
}

interface GamePublicacionesSectionProps {
  gameId: number;
  initialPublicaciones?: Publicacion[];
  initialGrupos?: GrupoJuego[];
}

function getApiErrorMessage(error: unknown, fallback: string) {
  if (axios.isAxiosError<ApiErrorResponse>(error)) {
    const responseData = error.response?.data;

    return responseData?.error ?? responseData?.message ?? fallback;
  }

  return fallback;
}

function getPublicacionesCountLabel(count: number) {
  return `${count} ${count === 1 ? 'publicacion' : 'publicaciones'}`;
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

export function GamePublicacionesSection({ gameId, initialPublicaciones, initialGrupos }: GamePublicacionesSectionProps) {
  const { status, user } = useAuth();
  const userId = user?.id ?? null;
  const [publicaciones, setPublicaciones] = useState<Publicacion[]>(initialPublicaciones ?? []);
  
  const initialGruposMap = initialGrupos ? Object.fromEntries(initialGrupos.map(g => [g.publicacionId, g])) : {};
  const [gruposByPublicacionId, setGruposByPublicacionId] = useState<
    Record<string, GrupoJuego | null>
  >(initialGruposMap);
  
  const [requestedPublicacionIds, setRequestedPublicacionIds] = useState<Record<string, true>>({});
  const [joiningPublicacionId, setJoiningPublicacionId] = useState<string | null>(null);
  const [isLoadingPublicaciones, setIsLoadingPublicaciones] = useState(false);
  const [loadError, setLoadError] = useState<string | null>(null);
  const [successMessage, setSuccessMessage] = useState<string | null>(null);
  const [isCreateDialogOpen, setIsCreateDialogOpen] = useState(false);
  const [editingPublicacion, setEditingPublicacion] = useState<Publicacion | null>(null);
  const [deletingPublicacion, setDeletingPublicacion] = useState<Publicacion | null>(null);
  const [groupInfoTarget, setGroupInfoTarget] = useState<{
    grupoId: string;
    grupo: GrupoJuego | null;
    publicacionTitle: string;
  } | null>(null);
  const [isSubmittingPublicacion, setIsSubmittingPublicacion] = useState(false);
  const [isDeletingPublicacion, setIsDeletingPublicacion] = useState(false);

  useEffect(() => {
    if (initialPublicaciones) {
      return;
    }
    
    let ignore = false;

    async function loadPublicaciones() {
      setIsLoadingPublicaciones(true);
      setLoadError(null);
      setRequestedPublicacionIds({});

      try {
        const nextPublicaciones = await publicacionesApi.getPublicacionesPorJuego(gameId);
        const nextGruposByPublicacionId = await loadGruposPorPublicacion(nextPublicaciones);

        if (ignore) {
          return;
        }

        setPublicaciones(nextPublicaciones);
        setGruposByPublicacionId(nextGruposByPublicacionId);
      } catch (error) {
        if (ignore) {
          return;
        }

        setLoadError(getApiErrorMessage(error, 'No se pudieron cargar las publicaciones.'));
      } finally {
        if (!ignore) {
          setIsLoadingPublicaciones(false);
        }
      }
    }

    void loadPublicaciones();

    return () => {
      ignore = true;
    };
  }, [gameId, initialPublicaciones]);

  function handleCreateDialogOpenChange(open: boolean) {
    if (isSubmittingPublicacion) {
      return;
    }

    if (!open) {
      setIsCreateDialogOpen(false);
      return;
    }

    setSuccessMessage(null);
    setIsCreateDialogOpen(true);
  }

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

  function handleGroupInfoDialogOpenChange(open: boolean) {
    if (!open) {
      setGroupInfoTarget(null);
    }
  }

  async function handleCreatePublicacion(payload: CrearPublicacionPayload) {
    setIsSubmittingPublicacion(true);
    setSuccessMessage(null);

    try {
      const createdPublicacion = await publicacionesApi.createPublicacion(payload);
      const createdGrupo = await loadGrupoJuego(createdPublicacion);

      setPublicaciones((currentPublicaciones) => [
        createdPublicacion,
        ...currentPublicaciones.filter(
          (currentPublicacion) => currentPublicacion.id !== createdPublicacion.id,
        ),
      ]);
      setGruposByPublicacionId((currentGruposByPublicacionId) => ({
        ...currentGruposByPublicacionId,
        [createdPublicacion.id]: createdGrupo,
      }));
      setSuccessMessage('Publicacion creada correctamente.');
    } finally {
      setIsSubmittingPublicacion(false);
    }
  }

  async function handleUpdatePublicacion(payload: EditarPublicacionPayload) {
    if (!editingPublicacion) {
      return;
    }

    setIsSubmittingPublicacion(true);
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

  async function handleRequestJoin(publicacion: Publicacion) {
    if (
      status !== 'authenticated' ||
      joiningPublicacionId === publicacion.id ||
      requestedPublicacionIds[publicacion.id]
    ) {
      return;
    }

    setJoiningPublicacionId(publicacion.id);
    setLoadError(null);
    setSuccessMessage(null);

    try {
      await publicacionesApi.createSolicitudUnion(publicacion.id);
      setRequestedPublicacionIds((currentRequestedPublicacionIds) => ({
        ...currentRequestedPublicacionIds,
        [publicacion.id]: true,
      }));
      setSuccessMessage('Solicitud de union enviada correctamente.');
    } catch (error) {
      setLoadError(getApiErrorMessage(error, 'No se pudo enviar la solicitud de union.'));
    } finally {
      setJoiningPublicacionId(null);
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

  function renderCreateAction() {
    if (status === 'anonymous') {
      return (
        <Button asChild>
          <Link href="/login">Crear nueva publicacion</Link>
        </Button>
      );
    }

    if (status === 'loading') {
      return (
        <Button type="button" disabled>
          <Plus className="size-4" aria-hidden="true" />
          Cargando sesion
        </Button>
      );
    }

    return (
      <Button
        type="button"
        onClick={() => handleCreateDialogOpenChange(true)}
        disabled={isSubmittingPublicacion || isDeletingPublicacion}
      >
        Crear nueva publicacion
      </Button>
    );
  }

  return (
    <div className="grid gap-6">
      <Card className="rounded-[calc(var(--radius-xl)+0.75rem)] border border-border bg-white/90 shadow-elevated backdrop-blur-sm">
        <div className="grid gap-4 p-6 lg:grid-cols-[minmax(0,1fr)_auto] lg:items-center">
          <div className="flex flex-wrap items-center gap-3">
            <Badge variant="primary">{getPublicacionesCountLabel(publicaciones.length)}</Badge>
            {renderCreateAction()}
          </div>
        </div>
      </Card>

      {loadError ? <Toast variant="error" title={loadError} /> : null}
      {successMessage ? <Toast title={successMessage} /> : null}

      {isLoadingPublicaciones && !publicaciones.length ? (
        <Card className="rounded-[calc(var(--radius-xl)+0.5rem)] border border-border bg-white/80 shadow-surface">
          <div className="grid gap-3 p-6">
            <h3 className="text-lg font-semibold tracking-tight text-foreground">
              Cargando publicaciones
            </h3>
            <p className="text-sm leading-relaxed text-secondary">
              Estamos recuperando los grupos de este juego.
            </p>
          </div>
        </Card>
      ) : publicaciones.length ? (
        <div className="grid gap-5 xl:grid-cols-2 2xl:grid-cols-3">
          {publicaciones.map((publicacion) => (
            <PublicacionCard
              key={publicacion.id}
              publicacion={publicacion}
              participantes={gruposByPublicacionId[publicacion.id]?.participantes ?? null}
              isAuthor={userId === publicacion.autorId}
              disableActions={
                isSubmittingPublicacion ||
                isDeletingPublicacion ||
                joiningPublicacionId === publicacion.id
              }
              isJoinRequested={Boolean(requestedPublicacionIds[publicacion.id])}
              joinActionHref={status === 'anonymous' ? '/login' : undefined}
              onRequestJoin={
                status === 'authenticated' && userId !== publicacion.autorId
                  ? () => void handleRequestJoin(publicacion)
                  : undefined
              }
              onEdit={userId === publicacion.autorId ? setEditingPublicacion : undefined}
              onDelete={userId === publicacion.autorId ? setDeletingPublicacion : undefined}
              onViewGroupInfo={publicacion.grupoId ? handleViewGroupInfo : undefined}
            />
          ))}
        </div>
      ) : (
        <EmptyPublicationsState title="Todavia no hay publicaciones conectadas a esta ficha" />
      )}

      <PublicacionEditorDialog
        open={isCreateDialogOpen}
        mode="create"
        gameId={gameId}
        isSubmitting={isSubmittingPublicacion}
        onOpenChange={handleCreateDialogOpenChange}
        onSubmit={handleCreatePublicacion}
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

      <GrupoJuegoInfoDialog
        open={groupInfoTarget !== null}
        grupoId={groupInfoTarget?.grupoId ?? null}
        grupo={groupInfoTarget?.grupo ?? null}
        publicacionTitle={groupInfoTarget?.publicacionTitle}
        onOpenChange={handleGroupInfoDialogOpenChange}
      />
    </div>
  );
}

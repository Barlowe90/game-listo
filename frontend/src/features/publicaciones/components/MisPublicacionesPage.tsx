'use client';

import axios from 'axios';
import Link from 'next/link';
import { useEffect, useState, type ReactNode } from 'react';
import { getUserById } from '@/features/auth/api/getUserById';
import type { UsuarioResponse } from '@/features/auth/api/auth.types';
import { useAuth } from '@/features/auth/hooks/useAuth';
import { getGamesByIds } from '@/features/catalogo/api/catalogApi';
import { publicacionesApi } from '@/features/publicaciones/api/publicacionesApi';
import { PublicacionCard } from '@/features/publicaciones/components/PublicacionCard';
import { PublicacionDeleteDialog } from '@/features/publicaciones/components/PublicacionDeleteDialog';
import { PublicacionEditorDialog } from '@/features/publicaciones/components/PublicacionEditorDialog';
import type {
  EditarPublicacionPayload,
  Publicacion,
  PublicacionDetalle,
  SolicitudUnion,
} from '@/features/publicaciones/model/publicaciones.types';
import { EmptyPublicationsState } from '@/shared/components/domain/EmptyPublicationsState';
import { PageSection } from '@/shared/components/layout/PageSection';
import { Avatar } from '@/shared/components/ui/Avatar';
import { Badge } from '@/shared/components/ui/Badge';
import { Button } from '@/shared/components/ui/Button';
import { Card } from '@/shared/components/ui/Card';
import { Toast } from '@/shared/components/ui/Toast';

interface ApiErrorResponse {
  error?: string;
  message?: string;
}

interface SolicitudUnionItemCardProps {
  title: string;
  count: number;
  description: string;
  emptyMessage: string;
  isLoading: boolean;
  children: ReactNode;
}

function getApiErrorMessage(error: unknown, fallback: string) {
  if (axios.isAxiosError<ApiErrorResponse>(error)) {
    return error.response?.data?.error ?? error.response?.data?.message ?? fallback;
  }

  return fallback;
}

function getPublicacionesCountLabel(count: number) {
  return `${count} ${count === 1 ? 'publicacion' : 'publicaciones'}`;
}

function getSolicitudesCountLabel(count: number) {
  return `${count} ${count === 1 ? 'solicitud' : 'solicitudes'}`;
}

function formatSolicitudEstado(estado: SolicitudUnion['estadoSolicitud']) {
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

function formatShortId(value: string) {
  return value.slice(0, 8);
}

async function loadGameTitles(publicaciones: Publicacion[]) {
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

async function loadPublicacionesDetalleMap(publicacionIds: string[]) {
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

async function loadUsuariosMap(userIds: string[]) {
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

function SolicitudUnionItemCard({
  title,
  count,
  description,
  emptyMessage,
  isLoading,
  children,
}: Readonly<SolicitudUnionItemCardProps>) {
  return (
    <Card className="rounded-[calc(var(--radius-xl)+0.5rem)] border border-border bg-white/90 shadow-elevated backdrop-blur-sm">
      <div className="grid gap-5 p-6">
        <div className="flex flex-wrap items-start justify-between gap-3">
          <div className="grid gap-1">
            <h2 className="text-lg font-semibold tracking-tight text-foreground">{title}</h2>
            <p className="text-sm leading-relaxed text-secondary">{description}</p>
          </div>

          <Badge variant="primary">{getSolicitudesCountLabel(count)}</Badge>
        </div>

        {isLoading ? (
          <p className="text-sm leading-relaxed text-secondary">Estamos cargando esta lista.</p>
        ) : count ? (
          <div className="grid gap-3">{children}</div>
        ) : (
          <p className="text-sm leading-relaxed text-secondary">{emptyMessage}</p>
        )}
      </div>
    </Card>
  );
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
  const [usuariosById, setUsuariosById] = useState<Record<string, UsuarioResponse | null>>({});
  const [isLoading, setIsLoading] = useState(true);
  const [loadError, setLoadError] = useState<string | null>(null);
  const [successMessage, setSuccessMessage] = useState<string | null>(null);
  const [editingPublicacion, setEditingPublicacion] = useState<Publicacion | null>(null);
  const [deletingPublicacion, setDeletingPublicacion] = useState<Publicacion | null>(null);
  const [isSubmittingPublicacion, setIsSubmittingPublicacion] = useState(false);
  const [isDeletingPublicacion, setIsDeletingPublicacion] = useState(false);

  useEffect(() => {
    let ignore = false;

    async function loadPageData() {
      if (!userId) {
        setPublicaciones([]);
        setSolicitudesEnviadas([]);
        setSolicitudesRecibidas([]);
        setGameTitlesById({});
        setPublicacionesDetalleById({});
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

        const [nextGameTitlesById, nextPublicacionesDetalleById, nextUsuariosById] =
          await Promise.all([
            loadGameTitles(nextPublicaciones),
            loadPublicacionesDetalleMap(publicacionIds),
            loadUsuariosMap(requestUserIds),
          ]);

        if (ignore) {
          return;
        }

        setPublicaciones(nextPublicaciones);
        setSolicitudesEnviadas(nextSolicitudesEnviadas);
        setSolicitudesRecibidas(nextSolicitudesRecibidas);
        setGameTitlesById(nextGameTitlesById);
        setPublicacionesDetalleById(nextPublicacionesDetalleById);
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
      setDeletingPublicacion(null);
      setSuccessMessage('Publicacion eliminada correctamente.');
    } catch (error) {
      setLoadError(getApiErrorMessage(error, 'No se pudo eliminar la publicacion.'));
    } finally {
      setIsDeletingPublicacion(false);
    }
  }

  return (
    <PageSection size="wide">
      <div className="grid gap-8">
        <div className="grid gap-4">
          <div className="flex flex-wrap items-start justify-between gap-4">
            <div className="grid gap-2">
              <h1 className="text-3xl font-bold tracking-tight text-foreground">
                Mis publicaciones
              </h1>
            </div>
          </div>

          <div className="flex flex-wrap items-center gap-3">
            <Badge variant="primary">{getPublicacionesCountLabel(publicaciones.length)}</Badge>
          </div>
        </div>

        <div className="grid gap-5 xl:grid-cols-2">
          <SolicitudUnionItemCard
            title="Solicitudes enviadas"
            count={solicitudesEnviadas.length}
            description="Aqui veras las peticiones que has enviado para unirte a otras publicaciones."
            emptyMessage="Todavia no has enviado ninguna solicitud de union."
            isLoading={isLoading}
          >
            {solicitudesEnviadas.map((solicitud) => {
              const publicacion = publicacionesDetalleById[solicitud.publicacionId];
              const publicacionTitle =
                publicacion?.titulo ?? `Publicacion ${formatShortId(solicitud.publicacionId)}`;
              const publicacionHref = publicacion ? `/videojuego/${publicacion.gameId}` : null;

              return (
                <div
                  key={solicitud.id}
                  className="flex flex-wrap items-center justify-between gap-3 rounded-[calc(var(--radius-xl)+0.2rem)] border border-border bg-surface/80 px-4 py-4"
                >
                  <div className="grid gap-1">
                    <span className="text-sm font-semibold text-foreground">
                      {publicacionTitle}
                    </span>
                    <span className="text-xs text-secondary">
                      Solicitud {formatShortId(solicitud.id)} -{' '}
                      {formatSolicitudEstado(solicitud.estadoSolicitud)}
                    </span>
                  </div>

                  <div className="flex flex-wrap items-center gap-2">
                    <Badge
                      variant={solicitud.estadoSolicitud === 'SOLICITADA' ? 'primary' : 'neutral'}
                    >
                      {formatSolicitudEstado(solicitud.estadoSolicitud)}
                    </Badge>
                    {publicacionHref ? (
                      <Button asChild variant="secondary" size="sm">
                        <Link href={publicacionHref}>Ver juego</Link>
                      </Button>
                    ) : null}
                  </div>
                </div>
              );
            })}
          </SolicitudUnionItemCard>

          <SolicitudUnionItemCard
            title="Solicitudes recibidas"
            count={solicitudesRecibidas.length}
            description="Aqui apareceran los usuarios que quieren unirse a tus publicaciones."
            emptyMessage="Todavia no has recibido solicitudes de union."
            isLoading={isLoading}
          >
            {solicitudesRecibidas.map((solicitud) => {
              const publicacion = publicacionesDetalleById[solicitud.publicacionId];
              const solicitante = usuariosById[solicitud.usuarioId];
              const publicacionTitle =
                publicacion?.titulo ?? `Publicacion ${formatShortId(solicitud.publicacionId)}`;
              const publicacionHref = publicacion ? `/videojuego/${publicacion.gameId}` : null;
              const solicitanteName =
                solicitante?.username ?? `Usuario ${formatShortId(solicitud.usuarioId)}`;

              return (
                <div
                  key={solicitud.id}
                  className="flex flex-wrap items-center justify-between gap-3 rounded-[calc(var(--radius-xl)+0.2rem)] border border-border bg-surface/80 px-4 py-4"
                >
                  <div className="flex min-w-0 items-center gap-3">
                    <Avatar
                      src={solicitante?.avatar}
                      name={solicitanteName}
                      size="sm"
                      className="size-10"
                    />
                    <div className="grid gap-1">
                      <span className="text-sm font-semibold text-foreground">
                        {solicitanteName}
                      </span>
                      <span className="text-xs text-secondary">
                        Quiere unirse a {publicacionTitle}
                      </span>
                    </div>
                  </div>

                  <div className="flex flex-wrap items-center gap-2">
                    <Badge
                      variant={solicitud.estadoSolicitud === 'SOLICITADA' ? 'primary' : 'neutral'}
                    >
                      {formatSolicitudEstado(solicitud.estadoSolicitud)}
                    </Badge>
                    {publicacionHref ? (
                      <Button asChild variant="secondary" size="sm">
                        <Link href={publicacionHref}>Ver juego</Link>
                      </Button>
                    ) : null}
                  </div>
                </div>
              );
            })}
          </SolicitudUnionItemCard>
        </div>

        {loadError ? <Toast variant="error" title={loadError} /> : null}
        {successMessage ? <Toast title={successMessage} /> : null}

        {isLoading ? (
          <Card className="rounded-[calc(var(--radius-xl)+0.5rem)] border border-border bg-white/80 shadow-surface">
            <div className="grid gap-3 p-6">
              <h2 className="text-lg font-semibold tracking-tight text-foreground">
                Cargando tu actividad
              </h2>
              <p className="text-sm leading-relaxed text-secondary">
                Estamos recuperando tus publicaciones y tus solicitudes de union.
              </p>
            </div>
          </Card>
        ) : publicaciones.length ? (
          <div className="grid gap-5 xl:grid-cols-2 2xl:grid-cols-3">
            {publicaciones.map((publicacion) => (
              <PublicacionCard
                key={publicacion.id}
                publicacion={publicacion}
                gameTitle={gameTitlesById[publicacion.gameId]}
                showGameLink
                isAuthor={userId === publicacion.autorId}
                disableActions={isSubmittingPublicacion || isDeletingPublicacion}
                onEdit={userId === publicacion.autorId ? setEditingPublicacion : undefined}
                onDelete={userId === publicacion.autorId ? setDeletingPublicacion : undefined}
              />
            ))}
          </div>
        ) : (
          <EmptyPublicationsState
            title="Todavia no has creado publicaciones"
            description="Crea una publicacion desde la ficha de un videojuego para que aparezca aqui como autor."
            action={
              <Button asChild>
                <Link href="/catalogo">Buscar un juego</Link>
              </Button>
            }
          />
        )}

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
      </div>
    </PageSection>
  );
}

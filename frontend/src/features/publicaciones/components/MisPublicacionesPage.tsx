'use client';

import axios from 'axios';
import Link from 'next/link';
import { useEffect, useState } from 'react';
import { useAuth } from '@/features/auth/hooks/useAuth';
import { getGamesByIds } from '@/features/catalogo/api/catalogApi';
import { publicacionesApi } from '@/features/publicaciones/api/publicacionesApi';
import { PublicacionCard } from '@/features/publicaciones/components/PublicacionCard';
import { PublicacionDeleteDialog } from '@/features/publicaciones/components/PublicacionDeleteDialog';
import { PublicacionEditorDialog } from '@/features/publicaciones/components/PublicacionEditorDialog';
import type {
  EditarPublicacionPayload,
  Publicacion,
} from '@/features/publicaciones/model/publicaciones.types';
import { EmptyPublicationsState } from '@/shared/components/domain/EmptyPublicationsState';
import { PageSection } from '@/shared/components/layout/PageSection';
import { Badge } from '@/shared/components/ui/Badge';
import { Button } from '@/shared/components/ui/Button';
import { Card } from '@/shared/components/ui/Card';
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

function getPublicacionesCountLabel(count: number) {
  return `${count} ${count === 1 ? 'publicacion' : 'publicaciones'}`;
}

export function MisPublicacionesPage() {
  const { user } = useAuth();
  const userId = user?.id ?? null;
  const [publicaciones, setPublicaciones] = useState<Publicacion[]>([]);
  const [gameTitlesById, setGameTitlesById] = useState<Record<string, string>>({});
  const [isLoading, setIsLoading] = useState(true);
  const [loadError, setLoadError] = useState<string | null>(null);
  const [successMessage, setSuccessMessage] = useState<string | null>(null);
  const [editingPublicacion, setEditingPublicacion] = useState<Publicacion | null>(null);
  const [deletingPublicacion, setDeletingPublicacion] = useState<Publicacion | null>(null);
  const [isSubmittingPublicacion, setIsSubmittingPublicacion] = useState(false);
  const [isDeletingPublicacion, setIsDeletingPublicacion] = useState(false);

  useEffect(() => {
    let ignore = false;

    async function loadPublicaciones() {
      if (!userId) {
        setIsLoading(false);
        return;
      }

      setIsLoading(true);
      setLoadError(null);

      try {
        const nextPublicaciones = await publicacionesApi.getPublicacionesPorUsuario(userId);

        if (ignore) {
          return;
        }

        setPublicaciones(nextPublicaciones);

        const gameIds = nextPublicaciones
          .map((publicacion) => Number.parseInt(publicacion.gameId, 10))
          .filter(Number.isFinite);

        if (!gameIds.length) {
          setGameTitlesById({});
          return;
        }

        try {
          const gamesMap = await getGamesByIds(gameIds);

          if (ignore) {
            return;
          }

          setGameTitlesById(
            Object.fromEntries(
              Array.from(gamesMap.entries()).map(([gameId, game]) => [String(gameId), game.name]),
            ),
          );
        } catch {
          if (!ignore) {
            setGameTitlesById({});
          }
        }
      } catch (error) {
        if (ignore) {
          return;
        }

        setLoadError(
          getApiErrorMessage(error, 'No se pudieron cargar tus publicaciones como autor.'),
        );
      } finally {
        if (!ignore) {
          setIsLoading(false);
        }
      }
    }

    void loadPublicaciones();

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

        {loadError ? <Toast variant="error" title={loadError} /> : null}
        {successMessage ? <Toast title={successMessage} /> : null}

        {isLoading ? (
          <Card className="rounded-[calc(var(--radius-xl)+0.5rem)] border border-border bg-white/80 shadow-surface">
            <div className="grid gap-3 p-6">
              <h2 className="text-lg font-semibold tracking-tight text-foreground">
                Cargando tus publicaciones
              </h2>
              <p className="text-sm leading-relaxed text-secondary">
                Estamos recuperando las publicaciones donde figuras como autor.
              </p>
            </div>
          </Card>
        ) : publicaciones.length ? (
          <div className="grid gap-5 xl:grid-cols-2">
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

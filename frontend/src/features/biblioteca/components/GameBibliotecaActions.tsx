'use client';

import { useEffect, useMemo, useState } from 'react';
import { bibliotecaApi } from '@/features/biblioteca/api/bibliotecaApi';
import { useAuth } from '@/features/auth/hooks/useAuth';
import type {
  BibliotecaEstado,
  BibliotecaLista,
} from '@/features/biblioteca/model/biblioteca.types';
import {
  formatBibliotecaEnumLabel,
  isBibliotecaEstado,
} from '@/features/biblioteca/model/biblioteca.utils';
import { Toast } from '@/shared/components/ui/Toast';
import {
  BibliotecaActionRow,
  BibliotecaAddToListDialog,
  BibliotecaRatingCard,
  formatRatingSelectValue,
  formatRatingValue,
  getApiErrorMessage,
  parseRatingSelectValue,
} from './gameBibliotecaActions.shared';

interface GameBibliotecaActionsProps {
  gameId: number;
}

export function GameBibliotecaActions({ gameId }: GameBibliotecaActionsProps) {
  const { status, user } = useAuth();
  const [listas, setListas] = useState<BibliotecaLista[]>([]);
  const [estadoActual, setEstadoActual] = useState<BibliotecaEstado | null>(null);
  const [ratingActual, setRatingActual] = useState<number | null>(null);
  const [ratingDraft, setRatingDraft] = useState('');
  const [error, setError] = useState<string | null>(null);
  const [successMessage, setSuccessMessage] = useState<string | null>(null);
  const [isLoadingLibraryContext, setIsLoadingLibraryContext] = useState(false);
  const [isSavingEstado, setIsSavingEstado] = useState(false);
  const [isSavingRating, setIsSavingRating] = useState(false);
  const [isListDialogOpen, setIsListDialogOpen] = useState(false);
  const [addingToListId, setAddingToListId] = useState<string | null>(null);

  useEffect(() => {
    if (status === 'anonymous') {
      setListas([]);
      setEstadoActual(null);
      setRatingActual(null);
      setRatingDraft('');
      setError(null);
      setSuccessMessage(null);
      setIsLoadingLibraryContext(false);
      return;
    }

    if (status !== 'authenticated' || !user) {
      setIsLoadingLibraryContext(true);
      return;
    }

    const userId = user.id;
    let ignore = false;

    async function loadLibraryContext() {
      setIsLoadingLibraryContext(true);
      setError(null);

      try {
        const [nextListas, gameStates] = await Promise.all([
          bibliotecaApi.getUserLists(),
          bibliotecaApi.getGameStates(gameId),
        ]);

        if (ignore) {
          return;
        }

        const estadoDelUsuario = gameStates.find(
          (gameState) => gameState.usuarioRefId === userId && isBibliotecaEstado(gameState.estado),
        );
        const nextRating =
          typeof estadoDelUsuario?.rating === 'number' && Number.isFinite(estadoDelUsuario.rating)
            ? estadoDelUsuario.rating
            : null;

        setListas(nextListas);
        setEstadoActual(estadoDelUsuario?.estado ?? null);
        setRatingActual(nextRating);
        setRatingDraft(formatRatingSelectValue(nextRating));
      } catch (nextError) {
        if (ignore) {
          return;
        }

        setListas([]);
        setEstadoActual(null);
        setRatingActual(null);
        setRatingDraft('');
        setError(
          getApiErrorMessage(
            nextError,
            'No pudimos cargar tu estado ni tus listas para este juego.',
          ),
        );
      } finally {
        if (!ignore) {
          setIsLoadingLibraryContext(false);
        }
      }
    }

    void loadLibraryContext();

    return () => {
      ignore = true;
    };
  }, [gameId, status, user]);

  const listasPersonalizadas = useMemo(
    () =>
      [...listas]
        .filter((lista) => lista.tipo === 'PERSONALIZADA')
        .sort((leftList, rightList) =>
          leftList.nombre.localeCompare(rightList.nombre, 'es', { sensitivity: 'base' }),
        ),
    [listas],
  );

  const bibliotecaHref = user ? `/usuario/${user.id}?seccion=biblioteca` : '/login';
  const hasEstadoSeleccionado = estadoActual !== null;
  const ratingInputId = `game-${gameId}-rating`;

  async function handleSelectEstado(estado: BibliotecaEstado) {
    if (status !== 'authenticated' || !user || isSavingEstado) {
      return;
    }

    const previousEstado = estadoActual;
    const previousRating = ratingActual;
    const previousRatingDraft = ratingDraft;

    if (estadoActual === estado) {
      setIsSavingEstado(true);
      setError(null);
      setSuccessMessage(null);
      setEstadoActual(null);
      setRatingActual(null);
      setRatingDraft('');

      try {
        await bibliotecaApi.deleteGameState(gameId);
        setSuccessMessage('Estado eliminado correctamente.');
      } catch (nextError) {
        setEstadoActual(previousEstado);
        setRatingActual(previousRating);
        setRatingDraft(previousRatingDraft);
        setError(getApiErrorMessage(nextError, 'No se pudo eliminar el estado del juego.'));
      } finally {
        setIsSavingEstado(false);
      }

      return;
    }

    const nextRating = previousRating ?? 0;

    setIsSavingEstado(true);
    setError(null);
    setSuccessMessage(null);
    setEstadoActual(estado);

    if (previousEstado === null) {
      setRatingActual(nextRating);
      setRatingDraft(formatRatingSelectValue(nextRating));
    }

    try {
      await bibliotecaApi.createGameState(gameId, estado);
      setSuccessMessage(`Estado actualizado a ${formatBibliotecaEnumLabel(estado)}.`);
    } catch (nextError) {
      setEstadoActual(previousEstado);
      setRatingActual(previousRating);
      setRatingDraft(previousRatingDraft);
      setError(getApiErrorMessage(nextError, 'No se pudo guardar el estado del juego.'));
    } finally {
      setIsSavingEstado(false);
    }
  }

  async function handleSelectRating(nextValue: string) {
    if (
      status !== 'authenticated' ||
      !user ||
      isSavingRating ||
      isSavingEstado ||
      isLoadingLibraryContext ||
      !hasEstadoSeleccionado
    ) {
      return;
    }

    const nextRating = parseRatingSelectValue(nextValue);

    if (nextRating === null || nextRating === ratingActual) {
      setRatingDraft(formatRatingSelectValue(ratingActual));
      return;
    }

    const previousRating = ratingActual;
    const previousRatingDraft = ratingDraft;

    setIsSavingRating(true);
    setError(null);
    setSuccessMessage(null);
    setRatingActual(nextRating);
    setRatingDraft(nextValue);

    try {
      await bibliotecaApi.rateGame(gameId, nextRating);
      setSuccessMessage(`Puntuacion guardada: ${formatRatingValue(nextRating)} / 10.`);
    } catch (nextError) {
      setRatingActual(previousRating);
      setRatingDraft(previousRatingDraft);
      setError(getApiErrorMessage(nextError, 'No se pudo guardar la puntuacion del juego.'));
    } finally {
      setIsSavingRating(false);
    }
  }

  async function handleAddToList(lista: BibliotecaLista) {
    if (addingToListId || status !== 'authenticated') {
      return;
    }

    setAddingToListId(lista.id);
    setError(null);
    setSuccessMessage(null);

    try {
      await bibliotecaApi.addGameToList(lista.id, gameId);

      setListas((currentLists) =>
        currentLists.map((currentList) => {
          if (currentList.id !== lista.id) {
            return currentList;
          }

          if (currentList.juegos.some((juego) => juego.gameId === gameId)) {
            return currentList;
          }

          return {
            ...currentList,
            juegos: [
              ...currentList.juegos,
              {
                gameId,
                nombre: null,
                cover: null,
                estado: estadoActual,
              },
            ],
          };
        }),
      );
      setSuccessMessage(`Juego anadido a la lista "${lista.nombre}".`);
      setIsListDialogOpen(false);
    } catch (nextError) {
      setError(getApiErrorMessage(nextError, 'No se pudo anadir el juego a la lista.'));
    } finally {
      setAddingToListId(null);
    }
  }

  if (status === 'loading') {
    return (
      <div className="grid gap-3">
        <BibliotecaRatingCard
          disabled
          hasEstadoSeleccionado={hasEstadoSeleccionado}
          helperText="Estamos cargando tu nota."
          isSavingRating={isSavingRating}
          onChange={(value) => {
            void handleSelectRating(value);
          }}
          ratingActual={ratingActual}
          ratingDraft={ratingDraft}
          ratingInputId={ratingInputId}
        />

        <BibliotecaActionRow disabled selectedEstado={estadoActual} />
      </div>
    );
  }

  if (status !== 'authenticated') {
    return (
      <div className="grid gap-3">
        <BibliotecaRatingCard
          hasEstadoSeleccionado={hasEstadoSeleccionado}
          href="/login"
          isSavingRating={isSavingRating}
          onChange={(value) => {
            void handleSelectRating(value);
          }}
          ratingActual={ratingActual}
          ratingDraft={ratingDraft}
          ratingInputId={ratingInputId}
        />

        <BibliotecaActionRow href="/login" selectedEstado={estadoActual} />

        {error ? <Toast variant="error" title={error} /> : null}
      </div>
    );
  }

  return (
    <div className="grid gap-3">
      <BibliotecaRatingCard
        disabled={isLoadingLibraryContext || isSavingEstado}
        hasEstadoSeleccionado={hasEstadoSeleccionado}
        helperText={
          isLoadingLibraryContext && !hasEstadoSeleccionado
            ? 'Estamos cargando tu nota.'
            : undefined
        }
        isSavingRating={isSavingRating}
        onChange={(value) => {
          void handleSelectRating(value);
        }}
        ratingActual={ratingActual}
        ratingDraft={ratingDraft}
        ratingInputId={ratingInputId}
      />

      <BibliotecaActionRow
        disabled={isLoadingLibraryContext || isSavingEstado}
        onAddToListClick={() => setIsListDialogOpen(true)}
        onSelectEstado={(estado) => {
          void handleSelectEstado(estado);
        }}
        selectedEstado={estadoActual}
      />

      {error ? <Toast variant="error" title={error} /> : null}
      {successMessage ? <Toast title={successMessage} /> : null}

      <BibliotecaAddToListDialog
        addingToListId={addingToListId}
        bibliotecaHref={bibliotecaHref}
        gameId={gameId}
        isLoadingLibraryContext={isLoadingLibraryContext}
        isOpen={isListDialogOpen}
        listasPersonalizadas={listasPersonalizadas}
        onAddToList={(lista) => {
          void handleAddToList(lista);
        }}
        onOpenChange={setIsListDialogOpen}
      />
    </div>
  );
}

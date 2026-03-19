'use client';

import axios from 'axios';
import Image from 'next/image';
import Link from 'next/link';
import { useEffect, useMemo, useState } from 'react';
import { bibliotecaApi } from '@/features/biblioteca/api/bibliotecaApi';
import { useAuth } from '@/features/auth/hooks/useAuth';
import type {
  BibliotecaEstado,
  BibliotecaLista,
} from '@/features/biblioteca/model/biblioteca.types';
import { BIBLIOTECA_ESTADOS } from '@/features/biblioteca/model/biblioteca.types';
import {
  formatBibliotecaEnumLabel,
  isBibliotecaEstado,
} from '@/features/biblioteca/model/biblioteca.utils';
import { cn } from '@/lib/cn';
import { Button } from '@/shared/components/ui/Button';
import {
  Dialog,
  DialogBody,
  DialogContent,
  DialogFooter,
  DialogHeader,
  DialogTitle,
} from '@/shared/components/ui/Dialog';
import { Toast } from '@/shared/components/ui/Toast';

interface ApiErrorResponse {
  error?: string;
  errors?: Record<string, string>;
  message?: string;
}

interface GameBibliotecaActionsProps {
  gameId: number;
}

function getApiErrorMessage(error: unknown, fallback: string) {
  if (axios.isAxiosError<ApiErrorResponse>(error)) {
    const responseData = error.response?.data;

    return responseData?.error ?? responseData?.message ?? fallback;
  }

  return fallback;
}

function getEstadoIconSrc(estado: BibliotecaEstado) {
  return `/${estado.toLowerCase()}.svg`;
}

function EstadoChipContent({ estado }: Readonly<{ estado: BibliotecaEstado }>) {
  return (
    <>
      <Image
        src={getEstadoIconSrc(estado)}
        alt=""
        aria-hidden="true"
        width={16}
        height={16}
        className="size-4"
      />
      <span>{formatBibliotecaEnumLabel(estado)}</span>
    </>
  );
}

function PlusChipContent() {
  return (
    <>
      <Image src="/plus.svg" alt="" aria-hidden="true" width={20} height={20} className="size-5" />
      <span>Anadir a lista</span>
    </>
  );
}

const actionChipClassName =
  'inline-flex min-h-[70px] min-w-[84px] flex-col items-center justify-center gap-1.5 rounded-[calc(var(--radius-xl)+0.25rem)] border px-3 py-2 text-center text-[13px] font-semibold transition-[background-color,border-color,color,box-shadow,opacity] duration-[var(--duration-fast)] ease-[var(--easing-standard)]';

const activeActionChipClassName =
  'border-transparent bg-primary text-primary-foreground shadow-surface [&_img]:brightness-0 [&_img]:invert';

const inactiveActionChipClassName =
  'border-border bg-primary-soft/70 text-foreground hover:border-border-strong hover:bg-surface';

const disabledActionChipClassName = 'pointer-events-none opacity-[var(--opacity-disabled)]';

const ratingCardClassName =
  'grid gap-3 rounded-[calc(var(--radius-xl)+0.4rem)] border border-border bg-white/85 p-4 shadow-surface';

const RATING_OPTIONS = Array.from({ length: 41 }, (_, index) => index * 0.25);

function formatRatingValue(rating: number) {
  return String(rating).replace('.', ',');
}

function formatRatingSummary(rating: number | null) {
  return rating === null ? 'Sin nota' : `${formatRatingValue(rating)} / 10`;
}

function formatRatingSelectValue(rating: number | null) {
  if (rating === null) {
    return '';
  }

  return String(rating);
}

function parseRatingSelectValue(value: string) {
  if (!value) {
    return null;
  }

  const parsedValue = Number(value);

  if (!Number.isFinite(parsedValue)) {
    return null;
  }

  return parsedValue;
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

  function renderEstadoChip(
    estado: BibliotecaEstado,
    options?: { href?: string; disabled?: boolean },
  ) {
    const isSelected = estadoActual === estado;
    const className = cn(
      actionChipClassName,
      isSelected ? activeActionChipClassName : inactiveActionChipClassName,
      options?.disabled ? disabledActionChipClassName : null,
    );

    if (options?.href) {
      return (
        <Link
          key={estado}
          href={options.href}
          className={className}
          aria-label={formatBibliotecaEnumLabel(estado)}
        >
          <EstadoChipContent estado={estado} />
        </Link>
      );
    }

    return (
      <button
        key={estado}
        type="button"
        className={className}
        onClick={() => {
          void handleSelectEstado(estado);
        }}
        aria-pressed={isSelected}
        disabled={options?.disabled}
      >
        <EstadoChipContent estado={estado} />
      </button>
    );
  }

  function renderAddToListChip(options?: { href?: string; disabled?: boolean }) {
    const className = cn(
      actionChipClassName,
      'min-w-[120px]',
      inactiveActionChipClassName,
      options?.disabled ? disabledActionChipClassName : null,
    );

    if (options?.href) {
      return (
        <Link key="add-to-list" href={options.href} className={className}>
          <PlusChipContent />
        </Link>
      );
    }

    return (
      <button
        key="add-to-list"
        type="button"
        className={className}
        onClick={() => setIsListDialogOpen(true)}
        disabled={options?.disabled}
      >
        <PlusChipContent />
      </button>
    );
  }

  function renderRatingCard(options?: {
    href?: string;
    disabled?: boolean;
    helperText?: string;
  }) {
    const helperMessage =
      options?.helperText ??
      (options?.href
        ? 'Inicia sesion para puntuar.'
        : !hasEstadoSeleccionado
          ? 'Selecciona primero un estado.'
          : null);

    return (
      <div className={ratingCardClassName}>
        <div className="flex flex-wrap items-center gap-3">
          <label
            htmlFor={ratingInputId}
            className="text-xs font-semibold tracking-[0.08em] text-primary uppercase"
          >
            Tu nota
          </label>

          <select
            id={ratingInputId}
            value={ratingDraft}
            onChange={(event) => {
              void handleSelectRating(event.target.value);
            }}
            disabled={
              Boolean(options?.href) ||
              Boolean(options?.disabled) ||
              !hasEstadoSeleccionado ||
              isSavingRating
            }
            className={cn(
              'min-h-[var(--target-min-size)] min-w-[11rem] rounded-md border border-border bg-white px-3 py-2 text-sm text-foreground shadow-surface transition-[border-color,background-color,color,box-shadow] duration-[var(--duration-fast)] ease-[var(--easing-standard)] focus-visible:border-primary',
              (options?.href || options?.disabled || !hasEstadoSeleccionado || isSavingRating) &&
                'cursor-not-allowed bg-surface text-muted-foreground',
            )}
            aria-describedby={helperMessage ? `${ratingInputId}-help` : undefined}
          >
            <option value="" disabled>
              Selecciona una nota
            </option>
            {RATING_OPTIONS.map((ratingOption) => (
              <option key={ratingOption} value={ratingOption}>
                {formatRatingValue(ratingOption)}
              </option>
            ))}
          </select>

          <span className="inline-flex items-center rounded-pill border border-border bg-background px-3 py-1 text-sm font-semibold text-foreground">
            {isSavingRating ? 'Guardando...' : formatRatingSummary(ratingActual)}
          </span>

          {options?.href ? (
            <Button asChild variant="secondary" className="sm:min-w-[10rem]">
              <Link href={options.href}>Iniciar sesion</Link>
            </Button>
          ) : null}
        </div>

        {helperMessage ? (
          <p id={`${ratingInputId}-help`} className="text-sm text-secondary">
            {helperMessage}
          </p>
        ) : null}
      </div>
    );
  }

  if (status === 'loading') {
    return (
      <div className="grid gap-3">
        {renderRatingCard({
          disabled: true,
          helperText: 'Estamos cargando tu nota.',
        })}

        <div className="flex flex-wrap gap-3">
          {BIBLIOTECA_ESTADOS.map((estado) => renderEstadoChip(estado, { disabled: true }))}
          {renderAddToListChip({ disabled: true })}
        </div>
      </div>
    );
  }

  if (status !== 'authenticated') {
    return (
      <div className="grid gap-3">
        {renderRatingCard({ href: '/login' })}

        <div className="flex flex-wrap gap-3">
          {BIBLIOTECA_ESTADOS.map((estado) => renderEstadoChip(estado, { href: '/login' }))}
          {renderAddToListChip({ href: '/login' })}
        </div>

        {error ? <Toast variant="error" title={error} /> : null}
      </div>
    );
  }

  return (
    <div className="grid gap-3">
      {renderRatingCard({
        disabled: isLoadingLibraryContext || isSavingEstado,
        helperText:
          isLoadingLibraryContext && !hasEstadoSeleccionado
            ? 'Estamos cargando tu nota.'
            : undefined,
      })}

      <div className="flex flex-wrap gap-3">
        {BIBLIOTECA_ESTADOS.map((estado) =>
          renderEstadoChip(estado, { disabled: isLoadingLibraryContext || isSavingEstado }),
        )}
        {renderAddToListChip({ disabled: isLoadingLibraryContext })}
      </div>

      {error ? <Toast variant="error" title={error} /> : null}
      {successMessage ? <Toast title={successMessage} /> : null}

      <Dialog open={isListDialogOpen} onOpenChange={setIsListDialogOpen}>
        <DialogContent className="max-w-xl">
          <DialogHeader>
            <DialogTitle>Anadir a lista</DialogTitle>
          </DialogHeader>

          <DialogBody>
            {isLoadingLibraryContext ? (
              <p className="text-sm leading-relaxed text-secondary">
                Estamos cargando tus listas personalizadas.
              </p>
            ) : listasPersonalizadas.length ? (
              <div className="grid gap-3">
                {listasPersonalizadas.map((lista) => {
                  const yaAnadido = lista.juegos.some((juego) => juego.gameId === gameId);

                  return (
                    <div
                      key={lista.id}
                      className="flex flex-wrap items-center justify-between gap-3 rounded-[calc(var(--radius-xl)+0.2rem)] border border-border bg-white/80 px-4 py-4"
                    >
                      <div className="grid gap-1">
                        <span className="text-sm font-semibold text-foreground">
                          {lista.nombre}
                        </span>
                        <span className="text-xs text-secondary">
                          {lista.juegos.length} {lista.juegos.length === 1 ? 'juego' : 'juegos'}
                        </span>
                      </div>

                      <Button
                        type="button"
                        variant={yaAnadido ? 'ghost' : 'secondary'}
                        disabled={yaAnadido}
                        loading={addingToListId === lista.id}
                        onClick={() => {
                          void handleAddToList(lista);
                        }}
                      >
                        {yaAnadido ? 'Ya anadido' : 'Anadir'}
                      </Button>
                    </div>
                  );
                })}
              </div>
            ) : (
              <div className="grid gap-3">
                <p className="text-sm leading-relaxed text-secondary">
                  Todavia no tienes listas personalizadas. Puedes crearlas desde tu biblioteca y
                  volver despues.
                </p>
                <Button asChild variant="secondary" className="w-fit">
                  <Link href={bibliotecaHref}>Ir a mi biblioteca</Link>
                </Button>
              </div>
            )}
          </DialogBody>

          <DialogFooter>
            <Button
              type="button"
              variant="secondary"
              onClick={() => setIsListDialogOpen(false)}
              disabled={Boolean(addingToListId)}
            >
              Cerrar
            </Button>
          </DialogFooter>
        </DialogContent>
      </Dialog>
    </div>
  );
}

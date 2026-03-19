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
  'border-transparent bg-primary text-primary-foreground shadow-surface';

const inactiveActionChipClassName =
  'border-border bg-primary-soft/70 text-foreground hover:border-border-strong hover:bg-surface';

const disabledActionChipClassName = 'pointer-events-none opacity-[var(--opacity-disabled)]';

export function GameBibliotecaActions({ gameId }: GameBibliotecaActionsProps) {
  const { status, user } = useAuth();
  const [listas, setListas] = useState<BibliotecaLista[]>([]);
  const [estadoActual, setEstadoActual] = useState<BibliotecaEstado | null>(null);
  const [error, setError] = useState<string | null>(null);
  const [successMessage, setSuccessMessage] = useState<string | null>(null);
  const [isLoadingLibraryContext, setIsLoadingLibraryContext] = useState(false);
  const [isSavingEstado, setIsSavingEstado] = useState(false);
  const [isListDialogOpen, setIsListDialogOpen] = useState(false);
  const [addingToListId, setAddingToListId] = useState<string | null>(null);

  useEffect(() => {
    if (status === 'anonymous') {
      setListas([]);
      setEstadoActual(null);
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

        setListas(nextListas);
        setEstadoActual(estadoDelUsuario?.estado ?? null);
      } catch (nextError) {
        if (ignore) {
          return;
        }

        setListas([]);
        setEstadoActual(null);
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

  async function handleSelectEstado(estado: BibliotecaEstado) {
    if (status !== 'authenticated' || !user || isSavingEstado || estadoActual === estado) {
      return;
    }

    const previousEstado = estadoActual;
    setIsSavingEstado(true);
    setError(null);
    setSuccessMessage(null);
    setEstadoActual(estado);

    try {
      await bibliotecaApi.createGameState(gameId, estado);
      setSuccessMessage(`Estado actualizado a ${formatBibliotecaEnumLabel(estado)}.`);
    } catch (nextError) {
      setEstadoActual(previousEstado);
      setError(getApiErrorMessage(nextError, 'No se pudo guardar el estado del juego.'));
    } finally {
      setIsSavingEstado(false);
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

  if (status === 'loading') {
    return (
      <div className="flex flex-wrap gap-3">
        {BIBLIOTECA_ESTADOS.map((estado) => renderEstadoChip(estado, { disabled: true }))}
        {renderAddToListChip({ disabled: true })}
      </div>
    );
  }

  if (status !== 'authenticated') {
    return (
      <div className="grid gap-3">
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

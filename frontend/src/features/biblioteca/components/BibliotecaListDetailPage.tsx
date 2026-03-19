'use client';

import axios from 'axios';
import Image from 'next/image';
import Link from 'next/link';
import { useEffect, useMemo, useState } from 'react';
import { useRouter } from 'next/navigation';
import { bibliotecaApi } from '@/features/biblioteca/api/bibliotecaApi';
import type {
  BibliotecaLista,
  BibliotecaListaJuego,
} from '@/features/biblioteca/model/biblioteca.types';
import {
  formatBibliotecaEnumLabel,
  getOfficialListNames,
} from '@/features/biblioteca/model/biblioteca.utils';
import { useAuth } from '@/features/auth/hooks/useAuth';
import { getGamesByIds } from '@/features/catalogo/api/catalogApi';
import { cn } from '@/lib/cn';
import { GameArtwork } from '@/shared/components/domain/GameArtwork';
import { PlatformChip } from '@/shared/components/domain/TagList';
import { InfoPanelCard } from '@/shared/components/domain/InfoPanelCard';
import { PageSection } from '@/shared/components/layout/PageSection';
import { Badge } from '@/shared/components/ui/Badge';
import { Button } from '@/shared/components/ui/Button';
import { Card } from '@/shared/components/ui/Card';
import {
  Dialog,
  DialogBody,
  DialogContent,
  DialogDescription,
  DialogFooter,
  DialogHeader,
  DialogTitle,
} from '@/shared/components/ui/Dialog';
import { Input } from '@/shared/components/ui/Input';
import { SectionHeader } from '@/shared/components/ui/SectionHeader';
import { Skeleton } from '@/shared/components/ui/Skeleton';
import { Toast } from '@/shared/components/ui/Toast';

interface BibliotecaListDetailPageProps {
  listaId: string;
}

interface ApiErrorResponse {
  error?: string;
  errors?: Record<string, string>;
  message?: string;
}

interface BibliotecaListaJuegoDetalle extends BibliotecaListaJuego {
  plataformas: string[];
}

const LIST_NAME_PATTERN = /^[a-zA-Z0-9 _-]{3,30}$/;

function getApiErrorMessage(error: unknown, fallback: string) {
  if (axios.isAxiosError<ApiErrorResponse>(error)) {
    const responseData = error.response?.data;

    return responseData?.error ?? responseData?.message ?? fallback;
  }

  return fallback;
}

function ListTypeBadge({ tipo }: Readonly<{ tipo: BibliotecaLista['tipo'] }>) {
  const isPersonalizada = tipo === 'PERSONALIZADA';

  return (
    <span
      className={cn(
        'inline-flex items-center rounded-pill px-3 py-1 text-[11px] font-semibold tracking-[0.08em] uppercase',
        isPersonalizada ? 'bg-primary-soft text-primary' : 'bg-surface text-muted-foreground',
      )}
    >
      {isPersonalizada ? 'Personalizada' : 'Oficial'}
    </span>
  );
}

function EstadoBadge({ estado }: Readonly<{ estado: string | null }>) {
  if (!estado) {
    return <Badge variant="neutral">Sin estado</Badge>;
  }

  return <Badge variant="primary">{formatBibliotecaEnumLabel(estado)}</Badge>;
}

function BibliotecaListDetailLoading() {
  return (
    <div className="grid gap-6">
      <SectionHeader
        title={<Skeleton variant="line" size="lg" className="w-48" />}
        action={<Skeleton variant="block" size="sm" className="h-11 w-32 rounded-md" />}
      />

      <Card padding="md" className="rounded-[calc(var(--radius-xl)+0.75rem)]">
        <div className="grid gap-3">
          <Skeleton variant="line" size="sm" className="w-28" />
          <Skeleton variant="line" size="md" className="w-52" />
        </div>
      </Card>

      <div className="grid gap-4">
        {[0, 1, 2].map((item) => (
          <Card key={item} padding="md" className="rounded-[calc(var(--radius-xl)+0.75rem)]">
            <div className="grid gap-4 md:grid-cols-[5rem_minmax(0,1.4fr)_minmax(0,1fr)_auto] md:items-center">
              <Skeleton variant="block" size="md" className="h-28 w-20 rounded-xl" />
              <Skeleton variant="line" size="md" className="w-40" />
              <div className="flex gap-2">
                <Skeleton variant="line" size="sm" className="w-16" />
                <Skeleton variant="line" size="sm" className="w-20" />
              </div>
              <Skeleton variant="line" size="sm" className="w-24" />
            </div>
          </Card>
        ))}
      </div>
    </div>
  );
}

function BibliotecaGameRow({ juego }: Readonly<{ juego: BibliotecaListaJuegoDetalle }>) {
  return (
    <Card
      padding="md"
      className="rounded-[calc(var(--radius-xl)+0.75rem)] border border-border bg-white/92 shadow-elevated"
    >
      <div className="grid gap-4 md:grid-cols-[5rem_minmax(0,1.4fr)_minmax(0,1fr)_auto] md:items-center">
        <Link href={`/videojuego/${juego.gameId}`} className="block w-20">
          <GameArtwork
            aspect="portrait"
            radius="md"
            coverUrl={juego.cover}
            title={juego.nombre?.trim() || `Juego #${juego.gameId}`}
            sizes="80px"
            className="w-20 shadow-surface"
          />
        </Link>

        <div className="grid gap-1">
          <Link
            href={`/videojuego/${juego.gameId}`}
            className="text-base font-semibold text-foreground transition-colors hover:text-primary"
          >
            {juego.nombre?.trim() || `Juego #${juego.gameId}`}
          </Link>
          <p className="text-sm text-secondary">ID #{juego.gameId}</p>
        </div>

        <div className="flex min-h-10 flex-wrap items-center gap-2">
          {juego.plataformas.length ? (
            juego.plataformas.slice(0, 3).map((plataforma) => (
              <PlatformChip key={`${juego.gameId}-${plataforma}`} className="px-2.5 py-1 text-xs">
                {plataforma}
              </PlatformChip>
            ))
          ) : (
            <span className="text-sm text-secondary">Sin plataforma registrada</span>
          )}
        </div>

        <div className="justify-self-start md:justify-self-end">
          <EstadoBadge estado={juego.estado} />
        </div>
      </div>
    </Card>
  );
}

export function BibliotecaListDetailPage({ listaId }: BibliotecaListDetailPageProps) {
  const router = useRouter();
  const { status, user } = useAuth();
  const [lista, setLista] = useState<BibliotecaLista | null>(null);
  const [officialListNames, setOfficialListNames] = useState<Set<string>>(new Set());
  const [platformsByGameId, setPlatformsByGameId] = useState<Record<number, string[]>>({});
  const [isLoading, setIsLoading] = useState(false);
  const [error, setError] = useState<string | null>(null);
  const [successMessage, setSuccessMessage] = useState<string | null>(null);
  const [reloadKey, setReloadKey] = useState(0);
  const [isEditingName, setIsEditingName] = useState(false);
  const [nombreDraft, setNombreDraft] = useState('');
  const [nombreError, setNombreError] = useState<string | null>(null);
  const [isSavingName, setIsSavingName] = useState(false);
  const [isDeleteDialogOpen, setIsDeleteDialogOpen] = useState(false);
  const [isDeletingList, setIsDeletingList] = useState(false);

  useEffect(() => {
    if (status !== 'authenticated') {
      setIsLoading(status === 'loading');
      return;
    }

    let ignore = false;

    async function loadListDetail() {
      setIsLoading(true);
      setError(null);
      setSuccessMessage(null);

      try {
        const [listaResult, listasResult] = await Promise.allSettled([
          bibliotecaApi.getListById(listaId),
          bibliotecaApi.getUserLists(),
        ]);

        if (listaResult.status === 'rejected') {
          throw listaResult.reason;
        }

        const nextLista = listaResult.value;

        if (ignore) {
          return;
        }

        setLista(nextLista);
        setNombreDraft(nextLista.nombre);
        setNombreError(null);
        setIsEditingName(false);

        if (listasResult.status === 'fulfilled') {
          setOfficialListNames(getOfficialListNames(listasResult.value));
        } else {
          setOfficialListNames(new Set());
        }

        const gameIds = nextLista.juegos.map((juego) => juego.gameId).filter(Number.isFinite);

        if (!gameIds.length) {
          setPlatformsByGameId({});
          return;
        }

        try {
          const gamesMap = await getGamesByIds(gameIds);

          if (ignore) {
            return;
          }

          const nextPlatformsByGameId = gameIds.reduce<Record<number, string[]>>(
            (result, gameId) => {
              result[gameId] = gamesMap.get(gameId)?.platforms ?? [];
              return result;
            },
            {},
          );

          setPlatformsByGameId(nextPlatformsByGameId);
        } catch {
          if (!ignore) {
            setPlatformsByGameId({});
            setError(
              'La lista se ha cargado, pero no pudimos recuperar las plataformas de algunos juegos.',
            );
          }
        }
      } catch (nextError) {
        if (ignore) {
          return;
        }

        setLista(null);
        setOfficialListNames(new Set());
        setPlatformsByGameId({});
        setError(getApiErrorMessage(nextError, 'No se pudo cargar la lista.'));
      } finally {
        if (!ignore) {
          setIsLoading(false);
        }
      }
    }

    void loadListDetail();

    return () => {
      ignore = true;
    };
  }, [listaId, reloadKey, status]);

  const juegosDetalle = useMemo<BibliotecaListaJuegoDetalle[]>(() => {
    if (!lista) {
      return [];
    }

    return lista.juegos.map((juego) => ({
      ...juego,
      plataformas: platformsByGameId[juego.gameId] ?? [],
    }));
  }, [lista, platformsByGameId]);

  const backHref = user ? `/usuario/${user.id}?seccion=biblioteca` : '/biblioteca';
  const canManageList = lista?.tipo === 'PERSONALIZADA';

  async function handleRenameSubmit() {
    if (!lista || !canManageList || isSavingName) {
      return;
    }

    const nextNombre = nombreDraft.trim();
    setNombreError(null);
    setError(null);
    setSuccessMessage(null);

    if (!nextNombre) {
      setNombreError('Introduce un nombre para la lista.');
      return;
    }

    if (!LIST_NAME_PATTERN.test(nextNombre)) {
      setNombreError(
        'Usa entre 3 y 30 caracteres con letras, numeros, espacios, guiones o guiones bajos.',
      );
      return;
    }

    if (officialListNames.has(nextNombre.toUpperCase())) {
      setNombreError('Ese nombre esta reservado para una lista oficial.');
      return;
    }

    if (nextNombre === lista.nombre) {
      setIsEditingName(false);
      return;
    }

    setIsSavingName(true);

    try {
      const updatedList = await bibliotecaApi.updateListName(lista.id, nextNombre);
      setLista((currentList) => (currentList ? { ...currentList, ...updatedList } : updatedList));
      setNombreDraft(updatedList.nombre);
      setIsEditingName(false);
      setSuccessMessage('Nombre de la lista actualizado correctamente.');
    } catch (renameError) {
      if (axios.isAxiosError<ApiErrorResponse>(renameError)) {
        const fieldErrors = renameError.response?.data?.errors;
        const fieldMessage = fieldErrors?.nombre ?? null;

        if (fieldMessage) {
          setNombreError(fieldMessage);
        } else {
          setError(getApiErrorMessage(renameError, 'No se pudo actualizar el nombre de la lista.'));
        }
      } else {
        setError('No se pudo actualizar el nombre de la lista.');
      }
    } finally {
      setIsSavingName(false);
    }
  }

  async function handleDeleteList() {
    if (!lista || !canManageList || isDeletingList) {
      return;
    }

    setIsDeletingList(true);
    setError(null);
    setSuccessMessage(null);

    try {
      await bibliotecaApi.deleteList(lista.id);
      router.push(backHref);
    } catch (deleteError) {
      setError(getApiErrorMessage(deleteError, 'No se pudo eliminar la lista.'));
      setIsDeleteDialogOpen(false);
    } finally {
      setIsDeletingList(false);
    }
  }

  return (
    <div className="relative overflow-hidden bg-[radial-gradient(circle_at_top_left,#f8f9ff_0%,#eef0ff_42%,#e7e7fb_100%)]">
      <div className="pointer-events-none absolute left-[-8rem] top-14 h-72 w-72 rounded-full bg-white/40 blur-3xl" />
      <div className="pointer-events-none absolute right-[-6rem] top-40 h-80 w-80 rounded-full bg-primary-soft blur-3xl" />

      <PageSection size="wide" className="relative z-10 py-10 lg:py-14">
        {isLoading && !lista ? (
          <BibliotecaListDetailLoading />
        ) : error && !lista ? (
          <div className="grid gap-6">
            <SectionHeader
              title="Detalle de lista"
              action={
                <Button onClick={() => setReloadKey((currentValue) => currentValue + 1)}>
                  Reintentar
                </Button>
              }
            />

            <Toast variant="error" title={error} />

            <InfoPanelCard
              title="No pudimos cargar la lista"
              description="Intentalo de nuevo en unos segundos o vuelve a tu perfil para abrir otra lista."
            >
              <div className="flex flex-wrap justify-end gap-3">
                <Button asChild variant="secondary">
                  <Link href={backHref}>Volver a biblioteca</Link>
                </Button>
              </div>
            </InfoPanelCard>
          </div>
        ) : (
          <div className="grid gap-6">
            <SectionHeader
              title={
                lista ? (
                  <span className="inline-flex flex-wrap items-center gap-3">
                    {isEditingName ? (
                      <Input
                        value={nombreDraft}
                        onChange={(event) => {
                          setNombreDraft(event.target.value);
                          setNombreError(null);
                          setError(null);
                          setSuccessMessage(null);
                        }}
                        onKeyDown={(event) => {
                          if (event.key === 'Enter') {
                            event.preventDefault();
                            void handleRenameSubmit();
                          }

                          if (event.key === 'Escape') {
                            event.preventDefault();
                            setNombreDraft(lista.nombre);
                            setNombreError(null);
                            setIsEditingName(false);
                          }
                        }}
                        autoFocus
                        disabled={isSavingName}
                        state={nombreError ? 'error' : 'default'}
                        className="w-[min(26rem,70vw)] bg-white"
                        aria-label="Editar nombre de la lista"
                      />
                    ) : (
                      <span>{lista.nombre}</span>
                    )}

                    {canManageList && !isEditingName ? (
                      <button
                        type="button"
                        onClick={() => {
                          setNombreDraft(lista.nombre);
                          setNombreError(null);
                          setError(null);
                          setSuccessMessage(null);
                          setIsEditingName(true);
                        }}
                        className="inline-flex size-10 items-center justify-center rounded-pill border border-border bg-white/80 transition-colors hover:border-border-strong hover:bg-white"
                        aria-label="Editar nombre de la lista"
                      >
                        <Image
                          src="/lapiz_editar.svg"
                          alt=""
                          width={18}
                          height={18}
                          className="size-[18px]"
                        />
                      </button>
                    ) : null}
                  </span>
                ) : (
                  'Detalle de lista'
                )
              }
              action={
                <div className="flex flex-wrap items-center gap-3">
                  {canManageList ? (
                    <Button
                      variant="destructive"
                      onClick={() => setIsDeleteDialogOpen(true)}
                      disabled={isDeletingList}
                    >
                      Eliminar lista
                    </Button>
                  ) : null}
                  <Button asChild variant="secondary">
                    <Link href={backHref}>Volver a biblioteca</Link>
                  </Button>
                </div>
              }
            />

            {error ? <Toast variant="error" title={error} /> : null}
            {successMessage ? <Toast title={successMessage} /> : null}
            {nombreError ? <Toast variant="error" title={nombreError} /> : null}

            {lista ? (
              <InfoPanelCard
                title={
                  <div className="flex flex-wrap items-center gap-3">
                    <span>Resumen de la lista</span>
                    <ListTypeBadge tipo={lista.tipo} />
                  </div>
                }
                description={`${lista.juegos.length} ${lista.juegos.length === 1 ? 'juego' : 'juegos'} en esta lista`}
              >
                {isEditingName ? (
                  <p className="text-sm leading-relaxed text-secondary">
                    Pulsa Enter para guardar el nuevo nombre o Escape para cancelar.
                  </p>
                ) : null}
              </InfoPanelCard>
            ) : null}

            {
              <div className="grid gap-4">
                <div className="hidden rounded-[calc(var(--radius-xl)+0.4rem)] border border-border bg-white/70 px-6 py-3 md:grid md:grid-cols-[5rem_minmax(0,1.4fr)_minmax(0,1fr)_auto] md:items-center">
                  <span className="text-xs font-semibold tracking-[0.08em] text-primary uppercase">
                    Cover
                  </span>
                  <span className="text-xs font-semibold tracking-[0.08em] text-primary uppercase">
                    Titulo
                  </span>
                  <span className="text-xs font-semibold tracking-[0.08em] text-primary uppercase">
                    Plataforma
                  </span>
                  <span className="text-xs font-semibold tracking-[0.08em] text-primary uppercase">
                    Estado
                  </span>
                </div>

                {juegosDetalle.map((juego) => (
                  <BibliotecaGameRow key={`${listaId}-${juego.gameId}`} juego={juego} />
                ))}
              </div>
            }
          </div>
        )}
      </PageSection>

      <Dialog open={isDeleteDialogOpen} onOpenChange={setIsDeleteDialogOpen}>
        <DialogContent className="max-w-lg">
          <DialogHeader>
            <DialogTitle>Eliminar lista</DialogTitle>
            <DialogDescription>
              Esta accion eliminara la lista personalizada y no se puede deshacer.
            </DialogDescription>
          </DialogHeader>

          <DialogBody>
            <p className="text-sm leading-relaxed text-secondary">
              Vas a eliminar{' '}
              <strong className="font-semibold text-foreground">{lista?.nombre}</strong>.
            </p>
          </DialogBody>

          <DialogFooter>
            <Button
              type="button"
              variant="secondary"
              onClick={() => setIsDeleteDialogOpen(false)}
              disabled={isDeletingList}
            >
              Cancelar
            </Button>
            <Button
              type="button"
              variant="destructive"
              onClick={() => void handleDeleteList()}
              loading={isDeletingList}
            >
              Eliminar lista
            </Button>
          </DialogFooter>
        </DialogContent>
      </Dialog>
    </div>
  );
}

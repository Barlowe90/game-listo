'use client';

import Link from 'next/link';
import { useEffect, useMemo, useState } from 'react';
import { useRouter } from 'next/navigation';
import { bibliotecaApi } from '@/features/biblioteca/api/bibliotecaApi';
import type { BibliotecaLista } from '@/features/biblioteca/model/biblioteca.types';
import { getOfficialListNames } from '@/features/biblioteca/model/biblioteca.utils';
import { useAuth } from '@/features/auth/hooks/useAuth';
import { getGamesByIds } from '@/features/catalogo/api/catalogApi';
import { InfoPanelCard } from '@/shared/components/domain/InfoPanelCard';
import { PageSection } from '@/shared/components/layout/PageSection';
import { Button } from '@/shared/components/ui/Button';
import { Toast } from '@/shared/components/ui/Toast';
import { BibliotecaDeleteListDialog } from './BibliotecaDeleteListDialog';
import { BibliotecaListDetailHeader } from './BibliotecaListDetailHeader';
import { BibliotecaListGamesSection } from './BibliotecaListGamesSection';
import {
  BibliotecaListDetailLoading,
  LIST_NAME_PATTERN,
  ListTypeBadge,
  getApiErrorMessage,
  type BibliotecaListaJuegoDetalle,
} from './bibliotecaListDetail.shared';

interface BibliotecaListDetailPageProps {
  listaId: string;
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

  function resetFeedback() {
    setNombreError(null);
    setError(null);
    setSuccessMessage(null);
  }

  function handleBeginEditing() {
    if (!lista) {
      return;
    }

    setNombreDraft(lista.nombre);
    resetFeedback();
    setIsEditingName(true);
  }

  function handleCancelEditing() {
    setNombreDraft(lista?.nombre ?? '');
    setNombreError(null);
    setIsEditingName(false);
  }

  function handleNombreDraftChange(value: string) {
    setNombreDraft(value);
    resetFeedback();
  }

  async function handleRenameSubmit() {
    if (!lista || !canManageList || isSavingName) {
      return;
    }

    const nextNombre = nombreDraft.trim();
    resetFeedback();

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
      if (
        typeof renameError === 'object' &&
        renameError !== null &&
        'isAxiosError' in renameError &&
        (renameError as { isAxiosError?: boolean }).isAxiosError
      ) {
        const fieldErrors = (
          renameError as { response?: { data?: { errors?: Record<string, string> } } }
        ).response?.data?.errors;
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
            <InfoPanelCard
              title="No pudimos cargar la lista"
              description="Intentalo de nuevo en unos segundos o vuelve a tu perfil para abrir otra lista."
            >
              <div className="flex flex-wrap justify-end gap-3">
                <Button onClick={() => setReloadKey((currentValue) => currentValue + 1)}>
                  Reintentar
                </Button>
                <Button asChild variant="secondary">
                  <Link href={backHref}>Volver a biblioteca</Link>
                </Button>
              </div>
            </InfoPanelCard>
          </div>
        ) : (
          <div className="grid gap-6">
            <BibliotecaListDetailHeader
              backHref={backHref}
              canManageList={canManageList}
              isDeletingList={isDeletingList}
              isEditingName={isEditingName}
              isSavingName={isSavingName}
              lista={lista}
              nombreDraft={nombreDraft}
              nombreError={nombreError}
              onBeginEditing={handleBeginEditing}
              onCancelEditing={handleCancelEditing}
              onDeleteClick={() => setIsDeleteDialogOpen(true)}
              onDraftChange={handleNombreDraftChange}
              onSave={() => {
                void handleRenameSubmit();
              }}
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

            <BibliotecaListGamesSection juegosDetalle={juegosDetalle} listaId={listaId} />
          </div>
        )}
      </PageSection>

      <BibliotecaDeleteListDialog
        isDeletingList={isDeletingList}
        listaNombre={lista?.nombre}
        onConfirm={() => {
          void handleDeleteList();
        }}
        onOpenChange={setIsDeleteDialogOpen}
        open={isDeleteDialogOpen}
      />
    </div>
  );
}

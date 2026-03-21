'use client';

import Link from 'next/link';
import { useEffect, useState, type FormEvent } from 'react';
import { bibliotecaApi } from '@/features/biblioteca/api/bibliotecaApi';
import { useAuth } from '@/features/auth/hooks/useAuth';
import type { BibliotecaLista } from '@/features/biblioteca/model/biblioteca.types';
import { getOfficialListNames } from '@/features/biblioteca/model/biblioteca.utils';
import { InfoPanelCard } from '@/shared/components/domain/InfoPanelCard';
import { Grid } from '@/shared/components/layout/Grid';
import { Button } from '@/shared/components/ui/Button';
import { Card } from '@/shared/components/ui/Card';
import {
  Dialog,
  DialogBody,
  DialogContent,
  DialogFooter,
  DialogHeader,
  DialogTitle,
} from '@/shared/components/ui/Dialog';
import { EmptyState } from '@/shared/components/ui/EmptyState';
import { FormField } from '@/shared/components/ui/FormField';
import { Input } from '@/shared/components/ui/Input';
import { SectionHeader } from '@/shared/components/ui/SectionHeader';
import { Skeleton } from '@/shared/components/ui/Skeleton';
import { Toast } from '@/shared/components/ui/Toast';
import {
  BIBLIOTECA_LIST_TEXT,
  getApiErrorMessage,
  getApiFieldErrorMessage,
  getGameCountLabel,
  ListTypeBadge,
  validateBibliotecaListName,
} from './biblioteca.shared';

const STEAM_ID_64_PATTERN = /^\d{17}$/;

function sortLists(lists: BibliotecaLista[]) {
  return [...lists].sort((leftList, rightList) =>
    leftList.nombre.localeCompare(rightList.nombre, 'es', { sensitivity: 'base' }),
  );
}

function validateSteamId64(steamId64Draft: string) {
  const normalizedSteamId64 = steamId64Draft.trim();

  if (!normalizedSteamId64) {
    return {
      errorMessage: 'Introduce tu SteamID64.',
      normalizedSteamId64,
    };
  }

  if (!STEAM_ID_64_PATTERN.test(normalizedSteamId64)) {
    return {
      errorMessage: 'El SteamID64 debe tener 17 digitos.',
      normalizedSteamId64,
    };
  }

  return {
    errorMessage: null,
    normalizedSteamId64,
  };
}

function BibliotecaListCard({ lista }: Readonly<{ lista: BibliotecaLista }>) {
  return (
    <Link href={`/biblioteca/listas/${lista.id}`} className="block h-full">
      <InfoPanelCard
        title={
          <div className="flex flex-wrap items-start justify-between gap-3">
            <span>{lista.nombre}</span>
            <ListTypeBadge tipo={lista.tipo} />
          </div>
        }
        description={getGameCountLabel(lista.juegos.length)}
        className="h-full transition-[transform,box-shadow,border-color] duration-[var(--duration-fast)] ease-[var(--easing-standard)] hover:-translate-y-px hover:border-border-strong hover:shadow-[0_18px_45px_rgba(15,23,42,0.12)]"
      ></InfoPanelCard>
    </Link>
  );
}

function BibliotecaStats({ listas }: Readonly<{ listas: BibliotecaLista[] }>) {
  const listasPersonalizadas = listas.filter((lista) => lista.tipo === 'PERSONALIZADA').length;
  const juegosTotales = listas.reduce((total, lista) => total + lista.juegos.length, 0);

  return (
    <Grid variant="stats">
      <Card variant="informative" padding="md" className="grid gap-2">
        <span className="text-sm font-medium text-muted-foreground">Listas totales</span>
        <strong className="text-2xl font-bold tracking-tight text-foreground">
          {listas.length}
        </strong>
      </Card>

      <Card variant="informative" padding="md" className="grid gap-2">
        <span className="text-sm font-medium text-muted-foreground">Listas personalizadas</span>
        <strong className="text-2xl font-bold tracking-tight text-foreground">
          {listasPersonalizadas}
        </strong>
      </Card>

      <Card variant="informative" padding="md" className="grid gap-2">
        <span className="text-sm font-medium text-muted-foreground">Juegos guardados</span>
        <strong className="text-2xl font-bold tracking-tight text-foreground">
          {juegosTotales}
        </strong>
      </Card>
    </Grid>
  );
}

function BibliotecaLoadingState() {
  return (
    <div className="grid gap-6">
      <SectionHeader
        title="Biblioteca"
        action={<Skeleton variant="block" size="sm" className="h-11 w-44 rounded-md" />}
      />

      <Grid variant="stats">
        {[0, 1, 2].map((item) => (
          <Card key={item} variant="informative" padding="md" className="grid gap-3">
            <Skeleton variant="line" size="sm" className="w-28" />
            <Skeleton variant="line" size="lg" className="w-16" />
            <Skeleton variant="line" size="sm" className="w-full" />
            <Skeleton variant="line" size="sm" className="w-4/5" />
          </Card>
        ))}
      </Grid>

      <div className="grid gap-4 lg:grid-cols-2">
        {[0, 1].map((item) => (
          <Card
            key={item}
            padding="md"
            className="grid gap-4 rounded-[calc(var(--radius-xl)+0.75rem)]"
          >
            <div className="grid gap-2">
              <Skeleton variant="line" size="md" className="w-40" />
              <Skeleton variant="line" size="sm" className="w-24" />
            </div>

            <Skeleton variant="block" size="sm" className="h-14 rounded-xl" />
            <Skeleton variant="block" size="sm" className="h-14 rounded-xl" />
            <Skeleton variant="block" size="sm" className="h-14 rounded-xl" />
          </Card>
        ))}
      </div>
    </div>
  );
}

export function ProfileBibliotecaSection() {
  const { status } = useAuth();
  const [listas, setListas] = useState<BibliotecaLista[]>([]);
  const [listasError, setListasError] = useState<string | null>(null);
  const [listasSuccess, setListasSuccess] = useState<string | null>(null);
  const [isLoadingListas, setIsLoadingListas] = useState(false);
  const [reloadKey, setReloadKey] = useState(0);
  const [isCreateDialogOpen, setIsCreateDialogOpen] = useState(false);
  const [isImportSteamDialogOpen, setIsImportSteamDialogOpen] = useState(false);
  const [nombreListaDraft, setNombreListaDraft] = useState('');
  const [nombreListaError, setNombreListaError] = useState<string | null>(null);
  const [isCreatingList, setIsCreatingList] = useState(false);
  const [steamId64Draft, setSteamId64Draft] = useState('');
  const [steamId64Error, setSteamId64Error] = useState<string | null>(null);
  const [isImportingSteam, setIsImportingSteam] = useState(false);

  useEffect(() => {
    if (status === 'anonymous') {
      setListas([]);
      setListasError(null);
      setListasSuccess(null);
      setIsLoadingListas(false);
      return;
    }

    if (status !== 'authenticated') {
      setIsLoadingListas(true);
      return;
    }

    let ignore = false;

    async function loadListas() {
      setIsLoadingListas(true);
      setListasError(null);

      try {
        const nextListas = await bibliotecaApi.getUserLists();

        if (ignore) {
          return;
        }

        setListas(sortLists(nextListas));
      } catch (error) {
        if (ignore) {
          return;
        }

        setListasError(getApiErrorMessage(error, 'No se pudieron cargar tus listas.'));
      } finally {
        if (!ignore) {
          setIsLoadingListas(false);
        }
      }
    }

    void loadListas();

    return () => {
      ignore = true;
    };
  }, [reloadKey, status]);

  function handleCreateDialogOpenChange(open: boolean) {
    setIsCreateDialogOpen(open);

    if (!open) {
      setNombreListaDraft('');
      setNombreListaError(null);
    }
  }

  function handleImportSteamDialogOpenChange(open: boolean) {
    setIsImportSteamDialogOpen(open);

    if (!open && !isImportingSteam) {
      setSteamId64Error(null);
    }
  }

  async function handleCreateListSubmit(event: FormEvent<HTMLFormElement>) {
    event.preventDefault();
    setNombreListaError(null);
    setListasError(null);
    setListasSuccess(null);

    const { errorMessage, normalizedName: nextNombre } = validateBibliotecaListName(
      nombreListaDraft,
      getOfficialListNames(listas),
    );

    if (errorMessage) {
      setNombreListaError(errorMessage);
      return;
    }

    setIsCreatingList(true);

    try {
      const createdList = await bibliotecaApi.createList({ nombre: nextNombre });

      setListas((currentListas) =>
        sortLists([...currentListas.filter((lista) => lista.id !== createdList.id), createdList]),
      );
      setListasSuccess('Lista creada correctamente.');
      handleCreateDialogOpenChange(false);
    } catch (error) {
      const fieldMessage =
        getApiFieldErrorMessage(error, 'nombre') ?? getApiFieldErrorMessage(error, 'tipo');

      if (fieldMessage) {
        setNombreListaError(fieldMessage);
      } else {
        setListasError(getApiErrorMessage(error, 'No se pudo crear la lista.'));
      }
    } finally {
      setIsCreatingList(false);
    }
  }

  async function handleImportSteamSubmit(event: FormEvent<HTMLFormElement>) {
    event.preventDefault();
    setSteamId64Error(null);
    setListasError(null);
    setListasSuccess(null);

    const { errorMessage, normalizedSteamId64 } = validateSteamId64(steamId64Draft);

    if (errorMessage) {
      setSteamId64Error(errorMessage);
      return;
    }

    setIsImportingSteam(true);

    try {
      const result = await bibliotecaApi.importSteamLibrary(normalizedSteamId64);

      setSteamId64Draft(normalizedSteamId64);
      setListasSuccess(
        `Importacion completada: ${result.steamOwnedCount} juegos detectados, ` +
          `${result.resolvedCount} encontrados en el catalogo, ${result.addedCount} nuevos, ` +
          `${result.alreadyPresentCount} ya estaban en tu lista Steam y ` +
          `${result.unresolvedCount} no se pudieron resolver.`,
      );
      handleImportSteamDialogOpenChange(false);
      setReloadKey((currentValue) => currentValue + 1);
    } catch (error) {
      const fieldMessage = getApiFieldErrorMessage(error, 'steamId64');

      if (fieldMessage) {
        setSteamId64Error(fieldMessage);
      } else {
        setListasError(
          getApiErrorMessage(error, 'No se pudo importar tu biblioteca de Steam.'),
        );
      }
    } finally {
      setIsImportingSteam(false);
    }
  }

  if (status === 'loading' || (isLoadingListas && !listas.length)) {
    return <BibliotecaLoadingState />;
  }

  if (status !== 'authenticated') {
    return (
      <div className="grid gap-6">
        <SectionHeader title="Biblioteca" />
        <EmptyState
          title="Inicia sesion para ver tu biblioteca"
          description="Tus listas y estados viven en tu perfil autenticado."
          action={
            <Button asChild>
              <Link href="/login">Iniciar sesion</Link>
            </Button>
          }
        />
      </div>
    );
  }

  if (listasError && !listas.length) {
    return (
      <div className="grid gap-6">
        <SectionHeader
          title="Biblioteca"
          action={
            <Button onClick={() => setReloadKey((currentValue) => currentValue + 1)}>
              Reintentar
            </Button>
          }
        />

        <Toast variant="error" title={listasError} />

        <InfoPanelCard
          title="No pudimos cargar tus listas"
          description="Vuelve a intentarlo en unos segundos. Si el problema sigue, revisaremos la comunicacion con biblioteca."
        >
          <div className="flex flex-wrap justify-end gap-3">
            <Button
              variant="secondary"
              onClick={() => setReloadKey((currentValue) => currentValue + 1)}
            >
              Volver a cargar
            </Button>
          </div>
        </InfoPanelCard>
      </div>
    );
  }

  return (
    <div className="grid gap-6">
      <SectionHeader
        title="Biblioteca"
        action={
          <div className="flex flex-wrap justify-end gap-3">
            <Button
              variant="secondary"
              onClick={() => handleImportSteamDialogOpenChange(true)}
            >
              Importar Steam
            </Button>
            <Button onClick={() => handleCreateDialogOpenChange(true)}>Crear nueva lista</Button>
          </div>
        }
      />

      {listasError ? <Toast variant="error" title={listasError} /> : null}
      {listasSuccess ? <Toast title={listasSuccess} /> : null}

      <BibliotecaStats listas={listas} />

      <div className="grid gap-4">
        <div className="grid gap-1">
          <h3 className="text-xl font-semibold tracking-tight text-foreground">Tus listas</h3>
        </div>

        <div className="grid gap-4 lg:grid-cols-2">
          {listas.map((lista) => (
            <BibliotecaListCard key={lista.id} lista={lista} />
          ))}
        </div>
      </div>

      <Dialog open={isCreateDialogOpen} onOpenChange={handleCreateDialogOpenChange}>
        <DialogContent className="max-w-lg">
          <DialogHeader>
            <DialogTitle>Crear nueva lista</DialogTitle>
          </DialogHeader>

          <form className="grid gap-4" onSubmit={handleCreateListSubmit}>
            <DialogBody>
              <FormField
                label="Nombre de la lista"
                htmlFor="create-list-name"
                required
                errorMessage={nombreListaError}
                helpText="Entre 3 y 30 caracteres. Usa letras, numeros, espacios, guiones o guiones bajos."
              >
                <Input
                  id="create-list-name"
                  value={nombreListaDraft}
                  onChange={(event) => {
                    setNombreListaDraft(event.target.value);
                    setNombreListaError(null);
                    setListasError(null);
                    setListasSuccess(null);
                  }}
                  placeholder="Mi backlog cooperativo"
                  autoComplete="off"
                  disabled={isCreatingList}
                  state={nombreListaError ? 'error' : 'default'}
                />
              </FormField>
            </DialogBody>

            <DialogFooter>
              <Button
                type="button"
                variant="secondary"
                onClick={() => handleCreateDialogOpenChange(false)}
                disabled={isCreatingList}
              >
                {BIBLIOTECA_LIST_TEXT.cancel}
              </Button>
              <Button type="submit" loading={isCreatingList}>
                Crear lista
              </Button>
            </DialogFooter>
          </form>
        </DialogContent>
      </Dialog>

      <Dialog open={isImportSteamDialogOpen} onOpenChange={handleImportSteamDialogOpenChange}>
        <DialogContent className="max-w-lg">
          <DialogHeader>
            <DialogTitle>Importar desde Steam</DialogTitle>
          </DialogHeader>

          <form className="grid gap-4" onSubmit={handleImportSteamSubmit}>
            <DialogBody>
              <FormField
                label="Id de Steam"
                htmlFor="steam-id-64"
                required
                errorMessage={steamId64Error}
                helpText="Introduce tu SteamID64 para importar los juegos de tu biblioteca que existan en nuestro catalogo."
              >
                <Input
                  id="steam-id-64"
                  value={steamId64Draft}
                  onChange={(event) => {
                    setSteamId64Draft(event.target.value);
                    setSteamId64Error(null);
                    setListasError(null);
                    setListasSuccess(null);
                  }}
                  placeholder="76561198000000000"
                  autoComplete="off"
                  inputMode="numeric"
                  disabled={isImportingSteam}
                  state={steamId64Error ? 'error' : 'default'}
                />
              </FormField>
            </DialogBody>

            <DialogFooter>
              <Button
                type="button"
                variant="secondary"
                onClick={() => handleImportSteamDialogOpenChange(false)}
                disabled={isImportingSteam}
              >
                {BIBLIOTECA_LIST_TEXT.cancel}
              </Button>
              <Button type="submit" loading={isImportingSteam} disabled={isImportingSteam}>
                Importar
              </Button>
            </DialogFooter>
          </form>
        </DialogContent>
      </Dialog>
    </div>
  );
}

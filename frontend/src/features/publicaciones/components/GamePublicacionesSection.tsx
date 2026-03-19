'use client';

import axios from 'axios';
import Link from 'next/link';
import { useEffect, useState, type FormEvent, type SVGProps } from 'react';
import { useAuth } from '@/features/auth/hooks/useAuth';
import { publicacionesApi } from '@/features/publicaciones/api/publicacionesApi';
import {
  PUBLICACION_DIAS,
  PUBLICACION_ESTILO_JUEGO_OPTIONS,
  PUBLICACION_EXPERIENCIA_OPTIONS,
  PUBLICACION_FRANJAS,
  PUBLICACION_IDIOMA_OPTIONS,
  type Publicacion,
  type PublicacionDiaSemana,
  type PublicacionDisponibilidad,
  type PublicacionEstiloJuego,
  type PublicacionExperiencia,
  type PublicacionFranjaHoraria,
  type PublicacionIdioma,
} from '@/features/publicaciones/model/publicaciones.types';
import { cn } from '@/lib/cn';
import {
  AvailabilityMatrix,
  type AvailabilityDay,
  type AvailabilityMatrixValue,
  type AvailabilityPeriod,
} from '@/shared/components/domain/AvailabilityMatrix';
import { EmptyPublicationsState } from '@/shared/components/domain/EmptyPublicationsState';
import { Badge } from '@/shared/components/ui/Badge';
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
import { FormField } from '@/shared/components/ui/FormField';
import { Input } from '@/shared/components/ui/Input';
import { Toast } from '@/shared/components/ui/Toast';

interface ApiErrorResponse {
  error?: string;
  errors?: Record<string, string>;
  message?: string;
}

interface GamePublicacionesSectionProps {
  gameId: number;
}

interface PublicacionFormState {
  titulo: string;
  idioma: PublicacionIdioma;
  experiencia: PublicacionExperiencia;
  estiloJuego: PublicacionEstiloJuego;
  jugadoresMaximos: string;
  disponibilidad: PublicacionDisponibilidad;
}

type PublicacionFormErrors = Partial<Record<'general' | 'jugadoresMaximos' | 'titulo', string>>;

const idiomaLabelMap = new Map(
  PUBLICACION_IDIOMA_OPTIONS.map((option) => [option.value, option.label]),
);

const experienciaLabelMap = new Map(
  PUBLICACION_EXPERIENCIA_OPTIONS.map((option) => [option.value, option.label]),
);

const estiloLabelMap = new Map(
  PUBLICACION_ESTILO_JUEGO_OPTIONS.map((option) => [option.value, option.label]),
);

const backendToMatrixDayMap: Record<PublicacionDiaSemana, AvailabilityDay> = {
  LUNES: 'lunes',
  MARTES: 'martes',
  MIERCOLES: 'miercoles',
  JUEVES: 'jueves',
  VIERNES: 'viernes',
  SABADO: 'sabado',
  DOMINGO: 'domingo',
};

const backendToMatrixPeriodMap: Record<PublicacionFranjaHoraria, AvailabilityPeriod> = {
  DIA: 'manana',
  TARDE: 'tarde',
  NOCHE: 'noche',
};

const selectClassName =
  'min-h-[var(--target-min-size)] w-full rounded-md border border-border bg-card px-4 py-2 text-sm text-foreground shadow-surface transition-[border-color,background-color,color,box-shadow] duration-[var(--duration-fast)] ease-[var(--easing-standard)] hover:border-border-strong focus-visible:border-primary disabled:cursor-not-allowed disabled:bg-surface disabled:text-muted-foreground';

function PlusIcon(props: SVGProps<SVGSVGElement>) {
  return (
    <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="1.8" {...props}>
      <path strokeLinecap="round" strokeLinejoin="round" d="M12 5v14M5 12h14" />
    </svg>
  );
}

function createInitialFormState(): PublicacionFormState {
  return {
    titulo: '',
    idioma: 'ESP',
    experiencia: 'NOVATO',
    estiloJuego: 'DISFRUTAR_DEL_JUEGO',
    jugadoresMaximos: '4',
    disponibilidad: {},
  };
}

function getApiErrorMessage(error: unknown, fallback: string, field?: string) {
  if (axios.isAxiosError<ApiErrorResponse>(error)) {
    const responseData = error.response?.data;

    if (field && responseData?.errors?.[field]) {
      return responseData.errors[field];
    }

    return responseData?.error ?? responseData?.message ?? fallback;
  }

  return fallback;
}

function getPublicacionesCountLabel(count: number) {
  return `${count} ${count === 1 ? 'publicacion' : 'publicaciones'}`;
}

function mapDisponibilidadToMatrix(
  disponibilidad: PublicacionDisponibilidad | null,
): AvailabilityMatrixValue {
  if (!disponibilidad) {
    return {};
  }

  return PUBLICACION_DIAS.reduce<AvailabilityMatrixValue>((matrix, day) => {
    const franjas = disponibilidad[day.value] ?? [];

    if (!franjas.length) {
      return matrix;
    }

    matrix[backendToMatrixDayMap[day.value]] = franjas.map(
      (franja) => backendToMatrixPeriodMap[franja],
    );

    return matrix;
  }, {});
}

function PublicacionResumenCard({ publicacion }: Readonly<{ publicacion: Publicacion }>) {
  return (
    <Card className="rounded-[calc(var(--radius-xl)+0.5rem)] border border-border bg-white/90 shadow-elevated backdrop-blur-sm">
      <div className="grid gap-5 p-6">
        <div className="grid gap-3">
          <div className="flex flex-wrap items-start justify-between gap-3">
            <div className="grid gap-2">
              <h3 className="text-xl font-semibold tracking-tight text-foreground">
                {publicacion.titulo}
              </h3>
              <p className="text-sm leading-relaxed text-secondary">
                Grupo creado y listo para recibir solicitudes de union.
              </p>
            </div>

            <span className="inline-flex items-center rounded-pill border border-border bg-background px-3 py-1 text-sm font-semibold text-foreground">
              Hasta {publicacion.jugadoresMaximos} jugadores
            </span>
          </div>

          <div className="flex flex-wrap gap-2">
            <Badge variant="primary">
              {idiomaLabelMap.get(publicacion.idioma) ?? publicacion.idioma}
            </Badge>
            <Badge>
              {experienciaLabelMap.get(publicacion.experiencia) ?? publicacion.experiencia}
            </Badge>
            <Badge>{estiloLabelMap.get(publicacion.estiloJuego) ?? publicacion.estiloJuego}</Badge>
          </div>
        </div>

        <AvailabilityMatrix availability={mapDisponibilidadToMatrix(publicacion.disponibilidad)} />
      </div>
    </Card>
  );
}

function DisponibilidadEditor({
  disponibilidad,
  onToggle,
}: Readonly<{
  disponibilidad: PublicacionDisponibilidad;
  onToggle: (dia: PublicacionDiaSemana, franja: PublicacionFranjaHoraria) => void;
}>) {
  return (
    <div className="overflow-x-auto rounded-[calc(var(--radius-xl)+0.25rem)] border border-border bg-surface p-4">
      <table className="w-full border-separate border-spacing-2 text-left">
        <caption className="sr-only">Editor de disponibilidad semanal</caption>
        <thead>
          <tr>
            <th className="w-20 text-xs font-semibold tracking-[0.08em] text-secondary uppercase">
              Dia
            </th>
            {PUBLICACION_FRANJAS.map((franja) => (
              <th
                key={franja.value}
                className="text-center text-xs font-semibold tracking-[0.08em] text-secondary uppercase"
                scope="col"
              >
                <span className="sm:hidden">{franja.shortLabel}</span>
                <span className="hidden sm:inline">{franja.label}</span>
              </th>
            ))}
          </tr>
        </thead>
        <tbody>
          {PUBLICACION_DIAS.map((dia) => (
            <tr key={dia.value}>
              <th
                scope="row"
                className="text-xs font-semibold tracking-[0.08em] text-foreground uppercase"
              >
                <span className="sm:hidden">{dia.shortLabel}</span>
                <span className="hidden sm:inline">{dia.label}</span>
              </th>
              {PUBLICACION_FRANJAS.map((franja) => {
                const isActive = disponibilidad[dia.value]?.includes(franja.value) ?? false;

                return (
                  <td key={`${dia.value}-${franja.value}`} className="text-center">
                    <button
                      type="button"
                      className={cn(
                        'inline-flex h-10 w-full min-w-14 items-center justify-center rounded-xl border text-xs font-semibold transition-[background-color,border-color,color,box-shadow] duration-[var(--duration-fast)] ease-[var(--easing-standard)]',
                        isActive
                          ? 'border-transparent bg-primary text-primary-foreground shadow-surface'
                          : 'border-border bg-background text-secondary hover:border-border-strong hover:bg-white',
                      )}
                      onClick={() => onToggle(dia.value, franja.value)}
                      aria-pressed={isActive}
                    >
                      {isActive ? 'Si' : 'No'}
                    </button>
                  </td>
                );
              })}
            </tr>
          ))}
        </tbody>
      </table>
    </div>
  );
}

export function GamePublicacionesSection({ gameId }: GamePublicacionesSectionProps) {
  const { status } = useAuth();
  const [publicaciones, setPublicaciones] = useState<Publicacion[]>([]);
  const [reloadKey, setReloadKey] = useState(0);
  const [isLoadingPublicaciones, setIsLoadingPublicaciones] = useState(false);
  const [loadError, setLoadError] = useState<string | null>(null);
  const [successMessage, setSuccessMessage] = useState<string | null>(null);
  const [isCreateDialogOpen, setIsCreateDialogOpen] = useState(false);
  const [isCreatingPublicacion, setIsCreatingPublicacion] = useState(false);
  const [formState, setFormState] = useState<PublicacionFormState>(createInitialFormState);
  const [formErrors, setFormErrors] = useState<PublicacionFormErrors>({});

  useEffect(() => {
    let ignore = false;

    async function loadPublicaciones() {
      setIsLoadingPublicaciones(true);
      setLoadError(null);

      try {
        const nextPublicaciones = await publicacionesApi.getPublicacionesPorJuego(gameId);

        if (ignore) {
          return;
        }

        setPublicaciones(nextPublicaciones);
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
  }, [gameId, reloadKey]);

  function resetCreateForm() {
    setFormState(createInitialFormState());
    setFormErrors({});
  }

  function closeCreateDialog() {
    setIsCreateDialogOpen(false);
    resetCreateForm();
  }

  function handleCreateDialogOpenChange(open: boolean) {
    if (isCreatingPublicacion) {
      return;
    }

    if (!open) {
      closeCreateDialog();
      return;
    }

    setSuccessMessage(null);
    setFormErrors({});
    setIsCreateDialogOpen(true);
  }

  function handleToggleDisponibilidad(dia: PublicacionDiaSemana, franja: PublicacionFranjaHoraria) {
    setFormState((currentFormState) => {
      const currentFranjas = currentFormState.disponibilidad[dia] ?? [];
      const nextFranjas = currentFranjas.includes(franja)
        ? currentFranjas.filter((currentFranja) => currentFranja !== franja)
        : [...currentFranjas, franja];
      const nextDisponibilidad = { ...currentFormState.disponibilidad };

      if (nextFranjas.length) {
        nextDisponibilidad[dia] = nextFranjas;
      } else {
        delete nextDisponibilidad[dia];
      }

      return {
        ...currentFormState,
        disponibilidad: nextDisponibilidad,
      };
    });
  }

  async function handleCreatePublicacionSubmit(event: FormEvent<HTMLFormElement>) {
    event.preventDefault();
    setFormErrors({});
    setSuccessMessage(null);

    const titulo = formState.titulo.trim();

    if (!titulo) {
      setFormErrors({ titulo: 'Introduce un titulo para la publicacion.' });
      return;
    }

    const jugadoresMaximos = Number.parseInt(formState.jugadoresMaximos, 10);

    if (!Number.isInteger(jugadoresMaximos) || jugadoresMaximos < 2 || jugadoresMaximos > 99) {
      setFormErrors({
        jugadoresMaximos: 'Introduce un numero entero entre 2 y 99.',
      });
      return;
    }

    setIsCreatingPublicacion(true);

    try {
      const createdPublicacion = await publicacionesApi.createPublicacion({
        gameId,
        titulo,
        idioma: formState.idioma,
        experiencia: formState.experiencia,
        estiloJuego: formState.estiloJuego,
        jugadoresMaximos,
        disponibilidad: formState.disponibilidad,
      });

      setPublicaciones((currentPublicaciones) => [
        createdPublicacion,
        ...currentPublicaciones.filter(
          (currentPublicacion) => currentPublicacion.id !== createdPublicacion.id,
        ),
      ]);
      setSuccessMessage('Publicacion creada correctamente.');
      closeCreateDialog();
    } catch (error) {
      const tituloError = getApiErrorMessage(error, '', 'titulo') || undefined;
      const jugadoresMaximosError = getApiErrorMessage(error, '', 'jugadoresMaximos') || undefined;
      const generalError = axios.isAxiosError<ApiErrorResponse>(error)
        ? (error.response?.data?.error ?? error.response?.data?.message)
        : undefined;

      setFormErrors({
        titulo: tituloError,
        jugadoresMaximos: jugadoresMaximosError,
        general:
          generalError ??
          (tituloError || jugadoresMaximosError ? undefined : 'No se pudo crear la publicacion.'),
      });
    } finally {
      setIsCreatingPublicacion(false);
    }
  }

  function renderCreateAction() {
    if (status === 'anonymous') {
      return (
        <Button asChild>
          <Link href="/login">
            <PlusIcon className="size-4" aria-hidden="true" />
            Crear nueva publicacion
          </Link>
        </Button>
      );
    }

    if (status === 'loading') {
      return (
        <Button type="button" disabled>
          <PlusIcon className="size-4" aria-hidden="true" />
          Cargando sesion
        </Button>
      );
    }

    return (
      <Button
        type="button"
        onClick={() => handleCreateDialogOpenChange(true)}
        disabled={isCreatingPublicacion}
      >
        <PlusIcon className="size-4" aria-hidden="true" />
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
        <div className="grid gap-5 xl:grid-cols-2">
          {publicaciones.map((publicacion) => (
            <PublicacionResumenCard key={publicacion.id} publicacion={publicacion} />
          ))}
        </div>
      ) : (
        <EmptyPublicationsState title="Todavia no hay publicaciones conectadas a esta ficha" />
      )}

      <Dialog open={isCreateDialogOpen} onOpenChange={handleCreateDialogOpenChange}>
        <DialogContent className="max-w-4xl p-0">
          <DialogHeader className="gap-2 border-b border-border px-6 pt-6 pb-0">
            <DialogTitle>Crear nueva publicacion</DialogTitle>
            <p className="text-sm leading-relaxed text-secondary">
              Rellena los campos que necesita CrearPublicacionRequest para publicar tu grupo.
            </p>
          </DialogHeader>

          <form className="grid gap-0" onSubmit={handleCreatePublicacionSubmit}>
            <DialogBody className="grid gap-6 px-6">
              {formErrors.general ? <Toast variant="error" title={formErrors.general} /> : null}

              <div className="grid gap-4 md:grid-cols-2">
                <FormField
                  label="Titulo de la publicacion"
                  htmlFor="publicacion-titulo"
                  required
                  errorMessage={formErrors.titulo}
                >
                  <Input
                    id="publicacion-titulo"
                    value={formState.titulo}
                    onChange={(event) => {
                      setFormState((currentFormState) => ({
                        ...currentFormState,
                        titulo: event.target.value,
                      }));
                      setFormErrors((currentErrors) => ({
                        ...currentErrors,
                        titulo: undefined,
                        general: undefined,
                      }));
                    }}
                    placeholder="Busco grupo para avanzar la campana"
                    autoComplete="off"
                    disabled={isCreatingPublicacion}
                    state={formErrors.titulo ? 'error' : 'default'}
                  />
                </FormField>

                <FormField label="Idioma" htmlFor="publicacion-idioma" required>
                  <select
                    id="publicacion-idioma"
                    value={formState.idioma}
                    onChange={(event) => {
                      setFormState((currentFormState) => ({
                        ...currentFormState,
                        idioma: event.target.value as PublicacionIdioma,
                      }));
                      setFormErrors((currentErrors) => ({
                        ...currentErrors,
                        general: undefined,
                      }));
                    }}
                    className={selectClassName}
                    disabled={isCreatingPublicacion}
                  >
                    {PUBLICACION_IDIOMA_OPTIONS.map((option) => (
                      <option key={option.value} value={option.value}>
                        {option.label}
                      </option>
                    ))}
                  </select>
                </FormField>

                <FormField label="Experiencia" htmlFor="publicacion-experiencia" required>
                  <select
                    id="publicacion-experiencia"
                    value={formState.experiencia}
                    onChange={(event) => {
                      setFormState((currentFormState) => ({
                        ...currentFormState,
                        experiencia: event.target.value as PublicacionExperiencia,
                      }));
                      setFormErrors((currentErrors) => ({
                        ...currentErrors,
                        general: undefined,
                      }));
                    }}
                    className={selectClassName}
                    disabled={isCreatingPublicacion}
                  >
                    {PUBLICACION_EXPERIENCIA_OPTIONS.map((option) => (
                      <option key={option.value} value={option.value}>
                        {option.label}
                      </option>
                    ))}
                  </select>
                </FormField>

                <FormField label="Estilo de juego" htmlFor="publicacion-estilo" required>
                  <select
                    id="publicacion-estilo"
                    value={formState.estiloJuego}
                    onChange={(event) => {
                      setFormState((currentFormState) => ({
                        ...currentFormState,
                        estiloJuego: event.target.value as PublicacionEstiloJuego,
                      }));
                      setFormErrors((currentErrors) => ({
                        ...currentErrors,
                        general: undefined,
                      }));
                    }}
                    className={selectClassName}
                    disabled={isCreatingPublicacion}
                  >
                    {PUBLICACION_ESTILO_JUEGO_OPTIONS.map((option) => (
                      <option key={option.value} value={option.value}>
                        {option.label}
                      </option>
                    ))}
                  </select>
                </FormField>

                <FormField
                  label="Jugadores maximos"
                  htmlFor="publicacion-jugadores-maximos"
                  required
                  helpText="Incluye al autor dentro del total del grupo."
                  errorMessage={formErrors.jugadoresMaximos}
                >
                  <Input
                    id="publicacion-jugadores-maximos"
                    type="number"
                    inputMode="numeric"
                    min={2}
                    max={99}
                    value={formState.jugadoresMaximos}
                    onChange={(event) => {
                      setFormState((currentFormState) => ({
                        ...currentFormState,
                        jugadoresMaximos: event.target.value,
                      }));
                      setFormErrors((currentErrors) => ({
                        ...currentErrors,
                        jugadoresMaximos: undefined,
                        general: undefined,
                      }));
                    }}
                    disabled={isCreatingPublicacion}
                    state={formErrors.jugadoresMaximos ? 'error' : 'default'}
                  />
                </FormField>
              </div>

              <div className="grid gap-3">
                <div className="grid gap-1">
                  <h3 className="text-sm font-semibold tracking-[0.08em] text-primary uppercase">
                    Disponibilidad semanal
                  </h3>
                  <p className="text-sm leading-relaxed text-secondary">
                    Marca las franjas en las que sueles jugar. Si prefieres decidirlo mas tarde,
                    puedes dejar la tabla vacia.
                  </p>
                </div>

                <DisponibilidadEditor
                  disponibilidad={formState.disponibilidad}
                  onToggle={handleToggleDisponibilidad}
                />
              </div>
            </DialogBody>

            <DialogFooter className="border-t border-border px-6 py-4">
              <Button
                type="button"
                variant="secondary"
                onClick={() => handleCreateDialogOpenChange(false)}
                disabled={isCreatingPublicacion}
              >
                Cancelar
              </Button>
              <Button type="submit" loading={isCreatingPublicacion}>
                Crear publicacion
              </Button>
            </DialogFooter>
          </form>
        </DialogContent>
      </Dialog>
    </div>
  );
}

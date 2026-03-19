'use client';

import axios from 'axios';
import { useEffect, useState, type FormEvent } from 'react';
import type {
  CrearPublicacionPayload,
  EditarPublicacionPayload,
  Publicacion,
  PublicacionDiaSemana,
  PublicacionDisponibilidad,
  PublicacionEstiloJuego,
  PublicacionExperiencia,
  PublicacionFranjaHoraria,
  PublicacionIdioma,
} from '@/features/publicaciones/model/publicaciones.types';
import {
  PUBLICACION_DIAS,
  PUBLICACION_ESTILO_JUEGO_OPTIONS,
  PUBLICACION_EXPERIENCIA_OPTIONS,
  PUBLICACION_FRANJAS,
  PUBLICACION_IDIOMA_OPTIONS,
} from '@/features/publicaciones/model/publicaciones.types';
import { cn } from '@/lib/cn';
import { Button } from '@/shared/components/ui/Button';
import {
  Dialog,
  DialogBody,
  DialogContent,
  DialogDescription,
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

interface PublicacionFormState {
  titulo: string;
  idioma: PublicacionIdioma;
  experiencia: PublicacionExperiencia;
  estiloJuego: PublicacionEstiloJuego;
  jugadoresMaximos: string;
  disponibilidad: PublicacionDisponibilidad;
}

type PublicacionFormErrors = Partial<Record<'general' | 'jugadoresMaximos' | 'titulo', string>>;

type PublicacionEditorCreateProps = {
  open: boolean;
  mode: 'create';
  gameId: number;
  isSubmitting: boolean;
  onOpenChange: (open: boolean) => void;
  onSubmit: (payload: CrearPublicacionPayload) => Promise<void>;
};

type PublicacionEditorEditProps = {
  open: boolean;
  mode: 'edit';
  publicacion: Publicacion;
  isSubmitting: boolean;
  onOpenChange: (open: boolean) => void;
  onSubmit: (payload: EditarPublicacionPayload) => Promise<void>;
};

type PublicacionEditorDialogProps = PublicacionEditorCreateProps | PublicacionEditorEditProps;

const selectClassName =
  'min-h-[var(--target-min-size)] w-full rounded-md border border-border bg-card px-4 py-2 text-sm text-foreground shadow-surface transition-[border-color,background-color,color,box-shadow] duration-[var(--duration-fast)] ease-[var(--easing-standard)] hover:border-border-strong focus-visible:border-primary disabled:cursor-not-allowed disabled:bg-surface disabled:text-muted-foreground';

function createInitialFormState(publicacion?: Publicacion | null): PublicacionFormState {
  return {
    titulo: publicacion?.titulo ?? '',
    idioma: publicacion?.idioma ?? 'ESP',
    experiencia: publicacion?.experiencia ?? 'NOVATO',
    estiloJuego: publicacion?.estiloJuego ?? 'DISFRUTAR_DEL_JUEGO',
    jugadoresMaximos: String(publicacion?.jugadoresMaximos ?? 4),
    disponibilidad: publicacion?.disponibilidad ?? {},
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

export function PublicacionEditorDialog(props: Readonly<PublicacionEditorDialogProps>) {
  const editingPublicacion = props.mode === 'edit' ? props.publicacion : null;
  const [formState, setFormState] = useState<PublicacionFormState>(() =>
    createInitialFormState(editingPublicacion),
  );
  const [formErrors, setFormErrors] = useState<PublicacionFormErrors>({});

  useEffect(() => {
    if (!props.open) {
      return;
    }

    setFormState(createInitialFormState(editingPublicacion));
    setFormErrors({});
  }, [editingPublicacion, props.open]);

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

  async function handleSubmit(event: FormEvent<HTMLFormElement>) {
    event.preventDefault();
    setFormErrors({});

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

    try {
      if (props.mode === 'create') {
        await props.onSubmit({
          gameId: props.gameId,
          titulo,
          idioma: formState.idioma,
          experiencia: formState.experiencia,
          estiloJuego: formState.estiloJuego,
          jugadoresMaximos,
          disponibilidad: formState.disponibilidad,
        });
      } else {
        await props.onSubmit({
          titulo,
          idioma: formState.idioma,
          experiencia: formState.experiencia,
          estiloJuego: formState.estiloJuego,
          jugadoresMaximos,
          disponibilidad: formState.disponibilidad,
        });
      }

      props.onOpenChange(false);
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
          (tituloError || jugadoresMaximosError
            ? undefined
            : props.mode === 'create'
              ? 'No se pudo crear la publicacion.'
              : 'No se pudo actualizar la publicacion.'),
      });
    }
  }

  function handleOpenChange(open: boolean) {
    if (props.isSubmitting) {
      return;
    }

    props.onOpenChange(open);
  }

  return (
    <Dialog open={props.open} onOpenChange={handleOpenChange}>
      <DialogContent className="max-w-4xl p-0">
        <DialogHeader className="gap-2 border-b border-border px-6 pt-6 pb-0">
          <DialogTitle>
            {props.mode === 'create' ? 'Crear nueva publicacion' : 'Editar publicacion'}
          </DialogTitle>
          <DialogDescription>
            {props.mode === 'create'
              ? 'Rellena los datos de tu grupo para publicar la busqueda.'
              : 'Actualiza los datos de tu publicacion y guarda los cambios.'}
          </DialogDescription>
        </DialogHeader>

        <form className="grid gap-0" onSubmit={handleSubmit}>
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
                  disabled={props.isSubmitting}
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
                  disabled={props.isSubmitting}
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
                  disabled={props.isSubmitting}
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
                  disabled={props.isSubmitting}
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
                  disabled={props.isSubmitting}
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
              onClick={() => handleOpenChange(false)}
              disabled={props.isSubmitting}
            >
              Cancelar
            </Button>
            <Button type="submit" loading={props.isSubmitting}>
              {props.mode === 'create' ? 'Crear publicacion' : 'Guardar cambios'}
            </Button>
          </DialogFooter>
        </form>
      </DialogContent>
    </Dialog>
  );
}

'use client';

import axios from 'axios';
import { useState, type FormEvent } from 'react';
import type {
  CrearPublicacionPayload,
  EditarPublicacionPayload,
  Publicacion,
  PublicacionDiaSemana,
  PublicacionEstiloJuego,
  PublicacionExperiencia,
  PublicacionFranjaHoraria,
  PublicacionIdioma,
} from '@/features/publicaciones/model/publicaciones.types';
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
import { Toast } from '@/shared/components/ui/Toast';
import {
  PublicacionAvailabilitySection,
  PublicacionBasicFields,
  createInitialFormState,
  getApiErrorMessage,
  toggleDisponibilidad,
  type PublicacionFormErrors,
  type PublicacionFormState,
} from './publicacionEditor.shared';

interface ApiErrorResponse {
  error?: string;
  message?: string;
}

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
type PublicacionEditorDialogFormProps = {
  dialogProps: PublicacionEditorDialogProps;
  editingPublicacion: Publicacion | null;
  onOpenChange: (open: boolean) => void;
};

function getGeneralErrorMessage(
  error: unknown,
  hasFieldErrors: boolean,
  mode: PublicacionEditorDialogProps['mode'],
) {
  const generalError = axios.isAxiosError<ApiErrorResponse>(error)
    ? (error.response?.data?.error ?? error.response?.data?.message)
    : undefined;

  if (generalError) {
    return generalError;
  }

  if (hasFieldErrors) {
    return undefined;
  }

  return mode === 'create'
    ? 'No se pudo crear la publicacion.'
    : 'No se pudo actualizar la publicacion.';
}

function PublicacionEditorDialogForm({
  dialogProps,
  editingPublicacion,
  onOpenChange,
}: Readonly<PublicacionEditorDialogFormProps>) {
  const mode = dialogProps.mode;
  const [formState, setFormState] = useState<PublicacionFormState>(() =>
    createInitialFormState(editingPublicacion),
  );
  const [formErrors, setFormErrors] = useState<PublicacionFormErrors>({});

  function clearGeneralError() {
    setFormErrors((currentErrors) => ({
      ...currentErrors,
      general: undefined,
    }));
  }

  function clearFieldError(field: keyof PublicacionFormErrors) {
    setFormErrors((currentErrors) => ({
      ...currentErrors,
      [field]: undefined,
      general: undefined,
    }));
  }

  function updateFormState(
    recipe: (currentFormState: PublicacionFormState) => PublicacionFormState,
  ) {
    setFormState((currentFormState) => recipe(currentFormState));
  }

  function handleTituloChange(value: string) {
    updateFormState((currentFormState) => ({
      ...currentFormState,
      titulo: value,
    }));
    clearFieldError('titulo');
  }

  function handleIdiomaChange(value: PublicacionIdioma) {
    updateFormState((currentFormState) => ({
      ...currentFormState,
      idioma: value,
    }));
    clearGeneralError();
  }

  function handleExperienciaChange(value: PublicacionExperiencia) {
    updateFormState((currentFormState) => ({
      ...currentFormState,
      experiencia: value,
    }));
    clearGeneralError();
  }

  function handleEstiloJuegoChange(value: PublicacionEstiloJuego) {
    updateFormState((currentFormState) => ({
      ...currentFormState,
      estiloJuego: value,
    }));
    clearGeneralError();
  }

  function handleJugadoresMaximosChange(value: string) {
    updateFormState((currentFormState) => ({
      ...currentFormState,
      jugadoresMaximos: value,
    }));
    clearFieldError('jugadoresMaximos');
  }

  function handleToggleDisponibilidad(dia: PublicacionDiaSemana, franja: PublicacionFranjaHoraria) {
    updateFormState((currentFormState) => ({
      ...currentFormState,
      disponibilidad: toggleDisponibilidad(currentFormState.disponibilidad, dia, franja),
    }));
    clearGeneralError();
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
      if (mode === 'create') {
        await dialogProps.onSubmit({
          gameId: dialogProps.gameId,
          titulo,
          idioma: formState.idioma,
          experiencia: formState.experiencia,
          estiloJuego: formState.estiloJuego,
          jugadoresMaximos,
          disponibilidad: formState.disponibilidad,
        });
      } else {
        await dialogProps.onSubmit({
          titulo,
          idioma: formState.idioma,
          experiencia: formState.experiencia,
          estiloJuego: formState.estiloJuego,
          jugadoresMaximos,
          disponibilidad: formState.disponibilidad,
        });
      }

      onOpenChange(false);
    } catch (error) {
      const tituloError = getApiErrorMessage(error, '', 'titulo') || undefined;
      const jugadoresMaximosError = getApiErrorMessage(error, '', 'jugadoresMaximos') || undefined;

      setFormErrors({
        titulo: tituloError,
        jugadoresMaximos: jugadoresMaximosError,
        general: getGeneralErrorMessage(error, Boolean(tituloError || jugadoresMaximosError), mode),
      });
    }
  }

  return (
    <form className="grid gap-0" onSubmit={handleSubmit}>
      <DialogBody className="grid gap-6 px-6">
        {formErrors.general ? <Toast variant="error" title={formErrors.general} /> : null}

        <PublicacionBasicFields
          formErrors={formErrors}
          formState={formState}
          isSubmitting={dialogProps.isSubmitting}
          onExperienciaChange={handleExperienciaChange}
          onEstiloJuegoChange={handleEstiloJuegoChange}
          onIdiomaChange={handleIdiomaChange}
          onJugadoresMaximosChange={handleJugadoresMaximosChange}
          onTituloChange={handleTituloChange}
        />

        <PublicacionAvailabilitySection
          disponibilidad={formState.disponibilidad}
          onToggle={handleToggleDisponibilidad}
        />
      </DialogBody>

      <DialogFooter className="border-t border-border px-6 py-4">
        <Button
          type="button"
          variant="secondary"
          onClick={() => onOpenChange(false)}
          disabled={dialogProps.isSubmitting}
        >
          Cancelar
        </Button>
        <Button type="submit" loading={dialogProps.isSubmitting}>
          {mode === 'create' ? 'Crear publicacion' : 'Guardar cambios'}
        </Button>
      </DialogFooter>
    </form>
  );
}

export function PublicacionEditorDialog(props: Readonly<PublicacionEditorDialogProps>) {
  const editingPublicacion = props.mode === 'edit' ? props.publicacion : null;

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

        {props.open ? (
          <PublicacionEditorDialogForm
            key={
              props.mode === 'edit'
                ? `${props.publicacion.id}-${props.open}`
                : `create-${props.open}`
            }
            dialogProps={props}
            editingPublicacion={editingPublicacion}
            onOpenChange={handleOpenChange}
          />
        ) : null}
      </DialogContent>
    </Dialog>
  );
}

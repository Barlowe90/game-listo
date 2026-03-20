import type { Publicacion } from '@/features/publicaciones/model/publicaciones.types';
import type {
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
import { FormField } from '@/shared/components/ui/FormField';
import { Input } from '@/shared/components/ui/Input';

interface ApiErrorResponse {
  error?: string;
  errors?: Record<string, string>;
  message?: string;
}

export interface PublicacionFormState {
  titulo: string;
  idioma: PublicacionIdioma;
  experiencia: PublicacionExperiencia;
  estiloJuego: PublicacionEstiloJuego;
  jugadoresMaximos: string;
  disponibilidad: PublicacionDisponibilidad;
}

export type PublicacionFormErrors = Partial<
  Record<'general' | 'jugadoresMaximos' | 'titulo', string>
>;

const selectClassName =
  'min-h-[var(--target-min-size)] w-full rounded-md border border-border bg-card px-4 py-2 text-sm text-foreground shadow-surface transition-[border-color,background-color,color,box-shadow] duration-[var(--duration-fast)] ease-[var(--easing-standard)] hover:border-border-strong focus-visible:border-primary disabled:cursor-not-allowed disabled:bg-surface disabled:text-muted-foreground';

export function createInitialFormState(publicacion?: Publicacion | null): PublicacionFormState {
  return {
    titulo: publicacion?.titulo ?? '',
    idioma: publicacion?.idioma ?? 'ESP',
    experiencia: publicacion?.experiencia ?? 'NOVATO',
    estiloJuego: publicacion?.estiloJuego ?? 'DISFRUTAR_DEL_JUEGO',
    jugadoresMaximos: String(publicacion?.jugadoresMaximos ?? 4),
    disponibilidad: publicacion?.disponibilidad ?? {},
  };
}

export function getApiErrorMessage(error: unknown, fallback: string, field?: string) {
  if (
    typeof error === 'object' &&
    error !== null &&
    'isAxiosError' in error &&
    (error as { isAxiosError?: boolean }).isAxiosError
  ) {
    const responseData = (error as { response?: { data?: ApiErrorResponse } }).response?.data;

    if (field && responseData?.errors?.[field]) {
      return responseData.errors[field];
    }

    return responseData?.error ?? responseData?.message ?? fallback;
  }

  return fallback;
}

export function toggleDisponibilidad(
  disponibilidad: PublicacionDisponibilidad,
  dia: PublicacionDiaSemana,
  franja: PublicacionFranjaHoraria,
) {
  const currentFranjas = disponibilidad[dia] ?? [];
  const nextFranjas = currentFranjas.includes(franja)
    ? currentFranjas.filter((currentFranja) => currentFranja !== franja)
    : [...currentFranjas, franja];
  const nextDisponibilidad = { ...disponibilidad };

  if (nextFranjas.length) {
    nextDisponibilidad[dia] = nextFranjas;
  } else {
    delete nextDisponibilidad[dia];
  }

  return nextDisponibilidad;
}

export function PublicacionBasicFields({
  formErrors,
  formState,
  isSubmitting,
  onExperienciaChange,
  onEstiloJuegoChange,
  onIdiomaChange,
  onJugadoresMaximosChange,
  onTituloChange,
}: Readonly<{
  formErrors: PublicacionFormErrors;
  formState: PublicacionFormState;
  isSubmitting: boolean;
  onExperienciaChange: (value: PublicacionExperiencia) => void;
  onEstiloJuegoChange: (value: PublicacionEstiloJuego) => void;
  onIdiomaChange: (value: PublicacionIdioma) => void;
  onJugadoresMaximosChange: (value: string) => void;
  onTituloChange: (value: string) => void;
}>) {
  return (
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
          onChange={(event) => onTituloChange(event.target.value)}
          placeholder="Busco grupo para avanzar la campana"
          autoComplete="off"
          disabled={isSubmitting}
          state={formErrors.titulo ? 'error' : 'default'}
        />
      </FormField>

      <FormField label="Idioma" htmlFor="publicacion-idioma" required>
        <select
          id="publicacion-idioma"
          value={formState.idioma}
          onChange={(event) => onIdiomaChange(event.target.value as PublicacionIdioma)}
          className={selectClassName}
          disabled={isSubmitting}
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
          onChange={(event) => onExperienciaChange(event.target.value as PublicacionExperiencia)}
          className={selectClassName}
          disabled={isSubmitting}
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
          onChange={(event) => onEstiloJuegoChange(event.target.value as PublicacionEstiloJuego)}
          className={selectClassName}
          disabled={isSubmitting}
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
          onChange={(event) => onJugadoresMaximosChange(event.target.value)}
          disabled={isSubmitting}
          state={formErrors.jugadoresMaximos ? 'error' : 'default'}
        />
      </FormField>
    </div>
  );
}

export function DisponibilidadEditor({
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
                const dayLabel = dia.label.toLowerCase();
                const periodLabel = franja.label.toLowerCase();

                return (
                  <td key={`${dia.value}-${franja.value}`} className="text-center">
                    <button
                      type="button"
                      className={cn(
                        'inline-flex h-10 w-full min-w-14 items-center justify-center rounded-xl border transition-[background-color,border-color,color,box-shadow] duration-[var(--duration-fast)] ease-[var(--easing-standard)] focus-visible:outline-none focus-visible:ring-2 focus-visible:ring-primary/30 focus-visible:ring-offset-2 focus-visible:ring-offset-surface',
                        isActive
                          ? 'border-transparent bg-primary shadow-surface'
                          : 'border-border bg-background hover:border-border-strong hover:bg-white',
                      )}
                      onClick={() => onToggle(dia.value, franja.value)}
                      aria-pressed={isActive}
                      aria-label={`${dayLabel} por la ${periodLabel}: ${
                        isActive ? 'disponible' : 'no disponible'
                      }`}
                    >
                      <span className="sr-only">
                        {isActive ? 'Disponible' : 'No disponible'} el {dayLabel} por la{' '}
                        {periodLabel}
                      </span>
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

export function PublicacionAvailabilitySection({
  disponibilidad,
  onToggle,
}: Readonly<{
  disponibilidad: PublicacionDisponibilidad;
  onToggle: (dia: PublicacionDiaSemana, franja: PublicacionFranjaHoraria) => void;
}>) {
  return (
    <div className="grid gap-3">
      <div className="grid gap-1">
        <h3 className="text-sm font-semibold tracking-[0.08em] text-primary uppercase">
          Disponibilidad semanal
        </h3>
        <p className="text-sm leading-relaxed text-secondary">
          Marca las franjas en las que sueles jugar. Si prefieres decidirlo mas tarde, puedes dejar
          la tabla vacia.
        </p>
      </div>

      <DisponibilidadEditor disponibilidad={disponibilidad} onToggle={onToggle} />
    </div>
  );
}

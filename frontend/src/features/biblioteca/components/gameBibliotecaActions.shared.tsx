import Image from 'next/image';
import Link from 'next/link';
import type { ReactNode } from 'react';
import type {
  BibliotecaEstado,
  BibliotecaLista,
} from '@/features/biblioteca/model/biblioteca.types';
import { BIBLIOTECA_ESTADOS } from '@/features/biblioteca/model/biblioteca.types';
import { formatBibliotecaEnumLabel } from '@/features/biblioteca/model/biblioteca.utils';
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

interface ApiErrorResponse {
  error?: string;
  message?: string;
}

interface BibliotecaRatingCardProps {
  disabled?: boolean;
  hasEstadoSeleccionado: boolean;
  helperText?: string;
  href?: string;
  isSavingRating: boolean;
  onChange: (value: string) => void;
  ratingActual: number | null;
  ratingDraft: string;
  ratingInputId: string;
}

interface BibliotecaActionRowProps {
  disabled?: boolean;
  href?: string;
  onAddToListClick?: () => void;
  onSelectEstado?: (estado: BibliotecaEstado) => void;
  selectedEstado: BibliotecaEstado | null;
}

interface BibliotecaAddToListDialogProps {
  addingToListId: string | null;
  bibliotecaHref: string;
  gameId: number;
  isLoadingLibraryContext: boolean;
  isOpen: boolean;
  listasPersonalizadas: BibliotecaLista[];
  onAddToList: (lista: BibliotecaLista) => void;
  onOpenChange: (open: boolean) => void;
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

export const RATING_OPTIONS = Array.from({ length: 41 }, (_, index) => index * 0.25);

export function getApiErrorMessage(error: unknown, fallback: string) {
  if (
    typeof error === 'object' &&
    error !== null &&
    'isAxiosError' in error &&
    (error as { isAxiosError?: boolean }).isAxiosError
  ) {
    const responseData = (error as { response?: { data?: ApiErrorResponse } }).response?.data;
    return responseData?.error ?? responseData?.message ?? fallback;
  }

  return fallback;
}

function getEstadoIconSrc(estado: BibliotecaEstado) {
  return `/${estado.toLowerCase()}.svg`;
}

export function formatRatingValue(rating: number) {
  return String(rating).replace('.', ',');
}

export function formatRatingSummary(rating: number | null) {
  return rating === null ? 'Sin nota' : `${formatRatingValue(rating)} / 10`;
}

export function formatRatingSelectValue(rating: number | null) {
  if (rating === null) {
    return '';
  }

  return String(rating);
}

export function parseRatingSelectValue(value: string) {
  if (!value) {
    return null;
  }

  const parsedValue = Number(value);

  if (!Number.isFinite(parsedValue)) {
    return null;
  }

  return parsedValue;
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

function BibliotecaActionChip({
  ariaLabel,
  children,
  disabled = false,
  href,
  isSelected = false,
  minWidthClassName,
  onClick,
}: Readonly<{
  ariaLabel?: string;
  children: ReactNode;
  disabled?: boolean;
  href?: string;
  isSelected?: boolean;
  minWidthClassName?: string;
  onClick?: () => void;
}>) {
  const className = cn(
    actionChipClassName,
    minWidthClassName,
    isSelected ? activeActionChipClassName : inactiveActionChipClassName,
    disabled ? disabledActionChipClassName : null,
  );

  if (href) {
    return (
      <Link href={href} className={className} aria-label={ariaLabel}>
        {children}
      </Link>
    );
  }

  return (
    <button
      type="button"
      className={className}
      onClick={onClick}
      aria-label={ariaLabel}
      aria-pressed={isSelected}
      disabled={disabled}
    >
      {children}
    </button>
  );
}

export function BibliotecaRatingCard({
  disabled = false,
  hasEstadoSeleccionado,
  helperText,
  href,
  isSavingRating,
  onChange,
  ratingActual,
  ratingDraft,
  ratingInputId,
}: Readonly<BibliotecaRatingCardProps>) {
  const helperMessage =
    helperText ??
    (href
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
          onChange={(event) => onChange(event.target.value)}
          disabled={Boolean(href) || disabled || !hasEstadoSeleccionado || isSavingRating}
          className={cn(
            'min-h-[var(--target-min-size)] min-w-[11rem] rounded-md border border-border bg-white px-3 py-2 text-sm text-foreground shadow-surface transition-[border-color,background-color,color,box-shadow] duration-[var(--duration-fast)] ease-[var(--easing-standard)] focus-visible:border-primary',
            (href || disabled || !hasEstadoSeleccionado || isSavingRating) &&
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

        {href ? (
          <Button asChild variant="secondary" className="sm:min-w-[10rem]">
            <Link href={href}>Iniciar sesion</Link>
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

export function BibliotecaActionRow({
  disabled = false,
  href,
  onAddToListClick,
  onSelectEstado,
  selectedEstado,
}: Readonly<BibliotecaActionRowProps>) {
  return (
    <div className="flex flex-wrap gap-3">
      {BIBLIOTECA_ESTADOS.map((estado) => (
        <BibliotecaActionChip
          key={estado}
          ariaLabel={formatBibliotecaEnumLabel(estado)}
          disabled={disabled}
          href={href}
          isSelected={selectedEstado === estado}
          onClick={onSelectEstado ? () => onSelectEstado(estado) : undefined}
        >
          <EstadoChipContent estado={estado} />
        </BibliotecaActionChip>
      ))}

      <BibliotecaActionChip
        ariaLabel="Anadir a lista"
        disabled={disabled}
        href={href}
        minWidthClassName="min-w-[120px]"
        onClick={onAddToListClick}
      >
        <PlusChipContent />
      </BibliotecaActionChip>
    </div>
  );
}

export function BibliotecaAddToListDialog({
  addingToListId,
  bibliotecaHref,
  gameId,
  isLoadingLibraryContext,
  isOpen,
  listasPersonalizadas,
  onAddToList,
  onOpenChange,
}: Readonly<BibliotecaAddToListDialogProps>) {
  return (
    <Dialog open={isOpen} onOpenChange={onOpenChange}>
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
                      <span className="text-sm font-semibold text-foreground">{lista.nombre}</span>
                      <span className="text-xs text-secondary">
                        {lista.juegos.length} {lista.juegos.length === 1 ? 'juego' : 'juegos'}
                      </span>
                    </div>

                    <Button
                      type="button"
                      variant={yaAnadido ? 'ghost' : 'secondary'}
                      disabled={yaAnadido}
                      loading={addingToListId === lista.id}
                      onClick={() => onAddToList(lista)}
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
            onClick={() => onOpenChange(false)}
            disabled={Boolean(addingToListId)}
          >
            Cerrar
          </Button>
        </DialogFooter>
      </DialogContent>
    </Dialog>
  );
}

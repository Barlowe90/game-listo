'use client';

import Image from 'next/image';
import Link from 'next/link';
import type {
  Publicacion,
  PublicacionDiaSemana,
  PublicacionDisponibilidad,
  PublicacionFranjaHoraria,
} from '@/features/publicaciones/model/publicaciones.types';
import {
  PUBLICACION_DIAS,
  PUBLICACION_ESTILO_JUEGO_OPTIONS,
  PUBLICACION_EXPERIENCIA_OPTIONS,
  PUBLICACION_IDIOMA_OPTIONS,
} from '@/features/publicaciones/model/publicaciones.types';
import { cn } from '@/lib/cn';
import {
  AvailabilityMatrix,
  type AvailabilityDay,
  type AvailabilityMatrixValue,
  type AvailabilityPeriod,
} from '@/shared/components/domain/AvailabilityMatrix';
import { Badge } from '@/shared/components/ui/Badge';
import { Button } from '@/shared/components/ui/Button';
import { Card } from '@/shared/components/ui/Card';

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

interface PublicacionCardProps {
  publicacion: Publicacion;
  gameTitle?: string;
  showGameLink?: boolean;
  isAuthor?: boolean;
  disableActions?: boolean;
  onEdit?: (publicacion: Publicacion) => void;
  onDelete?: (publicacion: Publicacion) => void;
}

interface PublicacionActionButtonProps {
  iconSrc: string;
  label: string;
  disabled?: boolean;
  onClick?: () => void;
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

function PublicacionActionButton({
  iconSrc,
  label,
  disabled = false,
  onClick,
}: Readonly<PublicacionActionButtonProps>) {
  return (
    <button
      type="button"
      onClick={onClick}
      disabled={disabled}
      aria-label={label}
      className={cn(
        'inline-flex size-10 items-center justify-center rounded-pill border bg-white/90 shadow-surface transition-colors',
        'border-border hover:border-border-strong hover:bg-white',
        'disabled:cursor-not-allowed disabled:opacity-60',
      )}
    >
      <Image src={iconSrc} alt="" width={18} height={18} className="size-[18px]" />
    </button>
  );
}

export function PublicacionCard({
  publicacion,
  gameTitle,
  showGameLink = false,
  isAuthor = false,
  disableActions = false,
  onEdit,
  onDelete,
}: Readonly<PublicacionCardProps>) {
  const hasActions = isAuthor && (onEdit || onDelete);
  const resolvedGameTitle = gameTitle ?? `Juego #${publicacion.gameId}`;
  const gameHref = `/videojuego/${publicacion.gameId}`;

  return (
    <Card className="w-full justify-self-start rounded-[calc(var(--radius-xl)+0.5rem)] border border-border bg-white/90 shadow-elevated backdrop-blur-sm sm:max-w-full sm:w-fit">
      <div className="grid gap-5 p-6">
        <div className="grid gap-3">
          <div className="flex flex-wrap items-start justify-between gap-3">
            <div className="grid gap-2">
              {showGameLink ? (
                <Link
                  href={gameHref}
                  className="text-sm font-semibold tracking-[0.08em] text-primary uppercase hover:underline"
                >
                  {resolvedGameTitle}
                </Link>
              ) : null}

              <h2 className="text-xl font-semibold tracking-tight text-foreground">
                {publicacion.titulo}
              </h2>
            </div>

            {hasActions ? (
              <div className="flex shrink-0 items-center gap-2">
                {onEdit ? (
                  <PublicacionActionButton
                    iconSrc="/lapiz_editar.svg"
                    label="Editar publicacion"
                    disabled={disableActions}
                    onClick={() => onEdit(publicacion)}
                  />
                ) : null}
                {onDelete ? (
                  <Button
                    type="button"
                    variant="destructive"
                    size="sm"
                    onClick={() => onDelete(publicacion)}
                    disabled={disableActions}
                  >
                    Eliminar
                  </Button>
                ) : null}
              </div>
            ) : null}
          </div>

          <div className="flex flex-wrap gap-2">
            <Badge variant="primary">
              {idiomaLabelMap.get(publicacion.idioma) ?? publicacion.idioma}
            </Badge>
            <Badge>
              {experienciaLabelMap.get(publicacion.experiencia) ?? publicacion.experiencia}
            </Badge>
            <Badge>{estiloLabelMap.get(publicacion.estiloJuego) ?? publicacion.estiloJuego}</Badge>
            <Badge>Hasta {publicacion.jugadoresMaximos} jugadores</Badge>
          </div>
        </div>

        <AvailabilityMatrix
          availability={mapDisponibilidadToMatrix(publicacion.disponibilidad)}
          compact
          stretch
          abbreviatedLabels={false}
        />
      </div>
    </Card>
  );
}

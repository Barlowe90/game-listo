'use client';

import { Pencil, Plus, type LucideIcon } from 'lucide-react';
import Link from 'next/link';
import type {
  Publicacion,
  PublicacionDiaSemana,
  PublicacionDisponibilidad,
  PublicacionFranjaHoraria,
  UsuarioRef,
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
import { Avatar } from '@/shared/components/ui/Avatar';
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
  participantes?: UsuarioRef[] | null;
  gameTitle?: string;
  showGameLink?: boolean;
  isAuthor?: boolean;
  disableActions?: boolean;
  isJoinRequested?: boolean;
  joinActionHref?: string;
  onEdit?: (publicacion: Publicacion) => void;
  onDelete?: (publicacion: Publicacion) => void;
  onViewGroupInfo?: (publicacion: Publicacion) => void;
  onLeaveGroup?: (publicacion: Publicacion) => void;
  onRequestJoin?: () => void;
}

interface PublicacionActionButtonProps {
  icon: LucideIcon;
  label: string;
  disabled?: boolean;
  onClick?: () => void;
}

interface JoinSlotProps {
  disabled?: boolean;
  href?: string;
  label: string;
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
  icon: Icon,
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
      <Icon className="size-[18px]" aria-hidden="true" />
    </button>
  );
}

function JoinSlot({ disabled = false, href, label, onClick }: Readonly<JoinSlotProps>) {
  const className = cn(
    'inline-flex size-10 items-center justify-center rounded-pill border-2 bg-white text-primary shadow-surface transition-[background-color,border-color,color,transform] duration-[var(--duration-fast)] ease-[var(--easing-standard)]',
    disabled
      ? 'border-primary/35 text-primary/40'
      : 'border-primary/75 hover:-translate-y-0.5 hover:bg-primary-soft/40',
  );

  if (href && !disabled) {
    return (
      <Link href={href} aria-label={label} className={className}>
        <Plus className="size-4" aria-hidden="true" />
      </Link>
    );
  }

  if (onClick && !disabled) {
    return (
      <button type="button" aria-label={label} className={className} onClick={onClick}>
        <Plus className="size-4" aria-hidden="true" />
      </button>
    );
  }

  return (
    <span aria-label={label} className={className} role="img">
      <Plus className="size-4" aria-hidden="true" />
    </span>
  );
}

function sortParticipantes(publicacion: Publicacion, participantes: UsuarioRef[]) {
  return [...participantes].sort((left, right) => {
    if (left.id === publicacion.autorId) {
      return -1;
    }

    if (right.id === publicacion.autorId) {
      return 1;
    }

    return left.username.localeCompare(right.username, 'es', { sensitivity: 'base' });
  });
}

export function PublicacionCard({
  publicacion,
  participantes,
  gameTitle,
  showGameLink = false,
  isAuthor = false,
  disableActions = false,
  isJoinRequested = false,
  joinActionHref,
  onEdit,
  onDelete,
  onViewGroupInfo,
  onLeaveGroup,
  onRequestJoin,
}: Readonly<PublicacionCardProps>) {
  const canViewGroupInfo = Boolean(publicacion.grupoId && onViewGroupInfo);
  const canLeaveGroup = Boolean(onLeaveGroup);
  const hasActions = Boolean(onEdit || onDelete || canViewGroupInfo || canLeaveGroup);
  const showParticipantSlots = participantes !== undefined;
  const resolvedGameTitle = gameTitle ?? `Juego #${publicacion.gameId}`;
  const gameHref = `/videojuego/${publicacion.gameId}`;
  const totalSlots = Math.max(publicacion.jugadoresMaximos, 1);
  const sortedParticipantes = sortParticipantes(publicacion, participantes ?? []);
  const visibleParticipantes = (
    sortedParticipantes.length
      ? sortedParticipantes
      : [{ id: publicacion.autorId, username: 'Autor', avatar: null }]
  ).slice(0, totalSlots);
  const emptySlotsCount = Math.max(totalSlots - visibleParticipantes.length, 0);
  const joinDisabled =
    disableActions ||
    isAuthor ||
    isJoinRequested ||
    visibleParticipantes.length >= totalSlots ||
    (!joinActionHref && !onRequestJoin);

  return (
    <Card className="h-full w-full rounded-[calc(var(--radius-xl)+0.5rem)] border border-border bg-white/90 shadow-elevated backdrop-blur-sm">
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
              <div className="flex shrink-0 flex-wrap items-center justify-end gap-2">
                {canViewGroupInfo ? (
                  <Button
                    type="button"
                    variant="secondary"
                    size="sm"
                    onClick={() => onViewGroupInfo?.(publicacion)}
                    disabled={disableActions}
                  >
                    Ver info del grupo
                  </Button>
                ) : null}
                {onEdit ? (
                  <PublicacionActionButton
                    icon={Pencil}
                    label="Editar publicacion"
                    disabled={disableActions}
                    onClick={() => onEdit(publicacion)}
                  />
                ) : null}
                {onLeaveGroup ? (
                  <Button
                    type="button"
                    variant="destructive"
                    size="sm"
                    onClick={() => onLeaveGroup(publicacion)}
                    disabled={disableActions}
                  >
                    Abandonar grupo
                  </Button>
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

        {showParticipantSlots ? (
          <div className="flex flex-wrap items-center gap-2" aria-label="Plazas del grupo">
            {visibleParticipantes.map((participante) => (
              <Link
                key={participante.id}
                href={`/usuario/${participante.id}`}
                aria-label={`Ver perfil de ${participante.username}`}
                className="rounded-pill transition-transform duration-[var(--duration-fast)] ease-[var(--easing-standard)] hover:-translate-y-0.5 focus-visible:outline-none focus-visible:ring-2 focus-visible:ring-primary/35 focus-visible:ring-offset-2 focus-visible:ring-offset-white"
              >
                <Avatar
                  src={participante.avatar}
                  name={participante.username}
                  size="sm"
                  className="size-10 border-primary/15 bg-white text-primary shadow-surface"
                />
              </Link>
            ))}

            {Array.from({ length: emptySlotsCount }, (_, slotIndex) => (
              <JoinSlot
                key={`join-slot-${publicacion.id}-${slotIndex}`}
                disabled={joinDisabled}
                href={joinActionHref}
                label={
                  isJoinRequested
                    ? 'Solicitud de union ya enviada'
                    : `Solicitar union a ${publicacion.titulo}`
                }
                onClick={onRequestJoin}
              />
            ))}
          </div>
        ) : null}
      </div>
    </Card>
  );
}

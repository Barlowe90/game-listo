'use client';

import axios from 'axios';
import Link from 'next/link';
import { useEffect, useState } from 'react';
import { publicacionesApi } from '@/features/publicaciones/api/publicacionesApi';
import type { GrupoJuego } from '@/features/publicaciones/model/publicaciones.types';
import { Avatar } from '@/shared/components/ui/Avatar';
import { Badge } from '@/shared/components/ui/Badge';
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

interface ApiErrorResponse {
  error?: string;
  message?: string;
}

interface GrupoJuegoInfoDialogProps {
  open: boolean;
  grupoId: string | null;
  grupo?: GrupoJuego | null;
  publicacionTitle?: string | null;
  onOpenChange: (open: boolean) => void;
}

function getApiErrorMessage(error: unknown, fallback: string) {
  if (axios.isAxiosError<ApiErrorResponse>(error)) {
    return error.response?.data?.error ?? error.response?.data?.message ?? fallback;
  }

  return fallback;
}

function formatFechaCreacion(value: string) {
  const date = new Date(value);

  if (Number.isNaN(date.getTime())) {
    return value;
  }

  return new Intl.DateTimeFormat('es-ES', {
    dateStyle: 'medium',
    timeStyle: 'short',
  }).format(date);
}

function getParticipantesCountLabel(count: number) {
  return `${count} ${count === 1 ? 'participante' : 'participantes'}`;
}

export function GrupoJuegoInfoDialog({
  open,
  grupoId,
  grupo,
  publicacionTitle,
  onOpenChange,
}: Readonly<GrupoJuegoInfoDialogProps>) {
  const [grupoData, setGrupoData] = useState<GrupoJuego | null>(grupo ?? null);
  const [isLoading, setIsLoading] = useState(false);
  const [loadError, setLoadError] = useState<string | null>(null);

  useEffect(() => {
    setGrupoData(grupo ?? null);
    setLoadError(null);
  }, [grupo, grupoId]);

  useEffect(() => {
    const currentGrupoId = grupoId ?? '';

    if (!open || !currentGrupoId || (grupo && grupo.id === currentGrupoId)) {
      return;
    }

    let ignore = false;

    async function loadGrupoJuego() {
      setIsLoading(true);
      setLoadError(null);

      try {
        const nextGrupo = await publicacionesApi.getGrupoJuego(currentGrupoId);

        if (ignore) {
          return;
        }

        setGrupoData(nextGrupo);
      } catch (error) {
        if (ignore) {
          return;
        }

        setLoadError(getApiErrorMessage(error, 'No se pudo cargar la informacion del grupo.'));
      } finally {
        if (!ignore) {
          setIsLoading(false);
        }
      }
    }

    void loadGrupoJuego();

    return () => {
      ignore = true;
    };
  }, [open, grupo, grupoId]);

  return (
    <Dialog open={open} onOpenChange={onOpenChange}>
      <DialogContent className="max-w-2xl">
        <DialogHeader>
          <DialogTitle>Informacion del grupo</DialogTitle>
          <DialogDescription>
            {publicacionTitle
              ? `Detalles del grupo vinculado a ${publicacionTitle}.`
              : 'Detalles completos del grupo vinculado a esta publicacion.'}
          </DialogDescription>
        </DialogHeader>

        <DialogBody>
          {!grupoId ? (
            <p className="text-sm leading-relaxed text-secondary">
              Esta publicacion todavia no tiene un grupo asociado.
            </p>
          ) : isLoading ? (
            <p className="text-sm leading-relaxed text-secondary">
              Estamos cargando la informacion del grupo.
            </p>
          ) : loadError ? (
            <p className="rounded-xl border border-error/20 bg-error-soft px-4 py-3 text-sm text-error">
              {loadError}
            </p>
          ) : grupoData ? (
            <div className="grid gap-5">

              <div className="grid gap-2 rounded-[calc(var(--radius-xl)-0.1rem)] border border-border bg-surface/70 p-4">
                <span className="text-xs font-semibold tracking-[0.08em] text-secondary uppercase">
                  Fecha de creacion
                </span>
                <p className="text-sm text-foreground">
                  {formatFechaCreacion(grupoData.fechaCreacion)}
                </p>
              </div>

              <div className="grid gap-4 rounded-[calc(var(--radius-xl)-0.1rem)] border border-border bg-surface/70 p-4">
                <div className="flex flex-wrap items-center justify-between gap-3">
                  <div className="grid gap-1">
                    <h3 className="text-base font-semibold tracking-tight text-foreground">
                      Participantes
                    </h3>
                    <p className="text-sm leading-relaxed text-secondary">
                      Lista actual de usuarios dentro del grupo.
                    </p>
                  </div>

                  <Badge variant="primary">
                    {getParticipantesCountLabel(grupoData.participantes.length)}
                  </Badge>
                </div>

                {grupoData.participantes.length ? (
                  <div className="grid gap-3">
                    {grupoData.participantes.map((participante) => (
                      <div
                        key={participante.id}
                        className="flex items-center gap-3 rounded-[calc(var(--radius-xl)-0.2rem)] border border-border bg-white/80 px-4 py-3"
                      >
                        <Link
                          href={`/usuario/${participante.id}`}
                          aria-label={`Ver perfil de ${participante.username}`}
                          className="rounded-pill transition-transform duration-[var(--duration-fast)] ease-[var(--easing-standard)] hover:-translate-y-0.5 focus-visible:outline-none focus-visible:ring-2 focus-visible:ring-primary/35 focus-visible:ring-offset-2 focus-visible:ring-offset-white"
                        >
                          <Avatar
                            src={participante.avatar}
                            name={participante.username}
                            size="sm"
                            className="size-10"
                          />
                        </Link>
                        <div className="grid gap-1 min-w-0">
                          <span className="truncate text-sm font-semibold text-foreground">
                            {participante.username}
                          </span>
                        </div>
                      </div>
                    ))}
                  </div>
                ) : (
                  <p className="text-sm leading-relaxed text-secondary">
                    Este grupo todavia no tiene participantes registrados.
                  </p>
                )}
              </div>
            </div>
          ) : (
            <p className="text-sm leading-relaxed text-secondary">
              No hay informacion disponible para este grupo.
            </p>
          )}
        </DialogBody>

        <DialogFooter>
          <Button type="button" variant="secondary" onClick={() => onOpenChange(false)}>
            Cerrar
          </Button>
        </DialogFooter>
      </DialogContent>
    </Dialog>
  );
}

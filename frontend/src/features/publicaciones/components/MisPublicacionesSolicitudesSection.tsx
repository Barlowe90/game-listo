import Link from 'next/link';
import { Check, X, type LucideIcon } from 'lucide-react';
import type { ReactNode } from 'react';
import type { UsuarioResponse } from '@/features/auth/api/auth.types';
import type {
  PublicacionDetalle,
  SolicitudUnion,
  SolicitudUnionEstadoResolucion,
} from '@/features/publicaciones/model/publicaciones.types';
import { cn } from '@/lib/cn';
import { Avatar } from '@/shared/components/ui/Avatar';
import { Badge } from '@/shared/components/ui/Badge';
import { Button } from '@/shared/components/ui/Button';
import { Card } from '@/shared/components/ui/Card';
import {
  formatShortId,
  formatSolicitudEstado,
  getSolicitudesCountLabel,
} from './misPublicaciones.utils';

interface MisPublicacionesSolicitudesSectionProps {
  isLoading: boolean;
  onResolveSolicitud: (
    solicitud: SolicitudUnion,
    estadoSolicitud: SolicitudUnionEstadoResolucion,
  ) => void;
  publicacionesDetalleById: Record<string, PublicacionDetalle | null>;
  resolvingSolicitudIds: string[];
  solicitudesEnviadas: SolicitudUnion[];
  solicitudesRecibidas: SolicitudUnion[];
  usuariosById: Record<string, UsuarioResponse | null>;
}

interface SolicitudUnionItemCardProps {
  title: string;
  count: number;
  description: string;
  emptyMessage: string;
  isLoading: boolean;
  children: ReactNode;
}

interface SolicitudActionIconButtonProps {
  icon: LucideIcon;
  label: string;
  tone: 'accept' | 'reject';
  disabled?: boolean;
  onClick: () => void;
}

function SolicitudActionIconButton({
  icon: Icon,
  label,
  tone,
  disabled = false,
  onClick,
}: Readonly<SolicitudActionIconButtonProps>) {
  return (
    <button
      type="button"
      aria-label={label}
      title={label}
      disabled={disabled}
      onClick={onClick}
      className={cn(
        'inline-flex size-10 items-center justify-center rounded-pill border shadow-surface transition-colors',
        'disabled:cursor-not-allowed disabled:opacity-60',
        tone === 'accept'
          ? 'border-emerald-200 bg-emerald-50 text-emerald-600 hover:border-emerald-300 hover:bg-emerald-100'
          : 'border-rose-200 bg-rose-50 text-rose-600 hover:border-rose-300 hover:bg-rose-100',
      )}
    >
      <Icon aria-hidden="true" className="size-[18px]" />
    </button>
  );
}

function SolicitudUnionItemCard({
  title,
  count,
  description,
  emptyMessage,
  isLoading,
  children,
}: Readonly<SolicitudUnionItemCardProps>) {
  return (
    <Card className="rounded-[calc(var(--radius-xl)+0.5rem)] border border-border bg-white/90 shadow-elevated backdrop-blur-sm">
      <div className="grid gap-5 p-6">
        <div className="flex flex-wrap items-start justify-between gap-3">
          <div className="grid gap-1">
            <h2 className="text-lg font-semibold tracking-tight text-foreground">{title}</h2>
            <p className="text-sm leading-relaxed text-secondary">{description}</p>
          </div>

          <Badge variant="primary">{getSolicitudesCountLabel(count)}</Badge>
        </div>

        {isLoading ? (
          <p className="text-sm leading-relaxed text-secondary">Estamos cargando esta lista.</p>
        ) : count ? (
          <div className="grid gap-3">{children}</div>
        ) : (
          <p className="text-sm leading-relaxed text-secondary">{emptyMessage}</p>
        )}
      </div>
    </Card>
  );
}

function SolicitudEnviadaRow({
  publicacion,
  solicitud,
}: Readonly<{
  publicacion: PublicacionDetalle | null;
  solicitud: SolicitudUnion;
}>) {
  const publicacionTitle =
    publicacion?.titulo ?? `Publicacion ${formatShortId(solicitud.publicacionId)}`;
  const publicacionHref = publicacion ? `/videojuego/${publicacion.gameId}` : null;

  return (
    <div className="flex flex-wrap items-center justify-between gap-3 rounded-[calc(var(--radius-xl)+0.2rem)] border border-border bg-surface/80 px-4 py-4">
      <div className="grid gap-1">
        <span className="text-sm font-semibold text-foreground">{publicacionTitle}</span>
      </div>

      <div className="flex flex-wrap items-center gap-2">
        <Badge variant={solicitud.estadoSolicitud === 'SOLICITADA' ? 'primary' : 'neutral'}>
          {formatSolicitudEstado(solicitud.estadoSolicitud)}
        </Badge>
        {publicacionHref ? (
          <Button asChild variant="secondary" size="sm">
            <Link href={publicacionHref}>Ver juego</Link>
          </Button>
        ) : null}
      </div>
    </div>
  );
}

function SolicitudRecibidaRow({
  isResolving,
  onResolveSolicitud,
  publicacion,
  solicitante,
  solicitud,
}: Readonly<{
  isResolving: boolean;
  onResolveSolicitud: (
    solicitud: SolicitudUnion,
    estadoSolicitud: SolicitudUnionEstadoResolucion,
  ) => void;
  publicacion: PublicacionDetalle | null;
  solicitante: UsuarioResponse | null;
  solicitud: SolicitudUnion;
}>) {
  const publicacionTitle =
    publicacion?.titulo ?? `Publicacion ${formatShortId(solicitud.publicacionId)}`;
  const publicacionHref = publicacion ? `/videojuego/${publicacion.gameId}` : null;
  const solicitanteName = solicitante?.username ?? `Usuario ${formatShortId(solicitud.usuarioId)}`;
  const canResolve = solicitud.estadoSolicitud === 'SOLICITADA';

  return (
    <div className="flex flex-wrap items-center justify-between gap-3 rounded-[calc(var(--radius-xl)+0.2rem)] border border-border bg-surface/80 px-4 py-4">
      <div className="flex min-w-0 items-center gap-3">
        <Avatar src={solicitante?.avatar} name={solicitanteName} size="sm" className="size-10" />
        <div className="grid gap-1">
          <span className="text-sm font-semibold text-foreground">{solicitanteName}</span>
          <span className="text-xs text-secondary">Quiere unirse a {publicacionTitle}</span>
        </div>
      </div>

      <div className="flex flex-wrap items-center gap-2">
        <Badge variant={solicitud.estadoSolicitud === 'SOLICITADA' ? 'primary' : 'neutral'}>
          {formatSolicitudEstado(solicitud.estadoSolicitud)}
        </Badge>
        {canResolve ? (
          <>
            <SolicitudActionIconButton
              icon={Check}
              label={`Aceptar la solicitud de ${solicitanteName}`}
              tone="accept"
              disabled={isResolving}
              onClick={() => onResolveSolicitud(solicitud, 'ACEPTADA')}
            />
            <SolicitudActionIconButton
              icon={X}
              label={`Rechazar la solicitud de ${solicitanteName}`}
              tone="reject"
              disabled={isResolving}
              onClick={() => onResolveSolicitud(solicitud, 'RECHAZADA')}
            />
          </>
        ) : null}
        {publicacionHref ? (
          <Button asChild variant="secondary" size="sm">
            <Link href={publicacionHref}>Ver juego</Link>
          </Button>
        ) : null}
      </div>
    </div>
  );
}

export function MisPublicacionesSolicitudesSection({
  isLoading,
  onResolveSolicitud,
  publicacionesDetalleById,
  resolvingSolicitudIds,
  solicitudesEnviadas,
  solicitudesRecibidas,
  usuariosById,
}: Readonly<MisPublicacionesSolicitudesSectionProps>) {
  return (
    <div className="grid gap-5 xl:grid-cols-2">
      <SolicitudUnionItemCard
        title="Solicitudes enviadas"
        count={solicitudesEnviadas.length}
        description="Aqui veras las peticiones que has enviado para unirte a otras publicaciones."
        emptyMessage="Todavia no has enviado ninguna solicitud de union."
        isLoading={isLoading}
      >
        {solicitudesEnviadas.map((solicitud) => (
          <SolicitudEnviadaRow
            key={solicitud.id}
            publicacion={publicacionesDetalleById[solicitud.publicacionId]}
            solicitud={solicitud}
          />
        ))}
      </SolicitudUnionItemCard>

      <SolicitudUnionItemCard
        title="Solicitudes recibidas"
        count={solicitudesRecibidas.length}
        description="Aqui apareceran los usuarios que quieren unirse a tus publicaciones."
        emptyMessage="Todavia no has recibido solicitudes de union."
        isLoading={isLoading}
      >
        {solicitudesRecibidas.map((solicitud) => (
          <SolicitudRecibidaRow
            key={solicitud.id}
            isResolving={resolvingSolicitudIds.includes(solicitud.id)}
            onResolveSolicitud={onResolveSolicitud}
            publicacion={publicacionesDetalleById[solicitud.publicacionId]}
            solicitante={usuariosById[solicitud.usuarioId]}
            solicitud={solicitud}
          />
        ))}
      </SolicitudUnionItemCard>
    </div>
  );
}

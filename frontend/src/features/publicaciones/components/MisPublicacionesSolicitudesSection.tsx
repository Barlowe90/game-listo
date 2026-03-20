import Link from 'next/link';
import type { ReactNode } from 'react';
import type { UsuarioResponse } from '@/features/auth/api/auth.types';
import type {
  PublicacionDetalle,
  SolicitudUnion,
} from '@/features/publicaciones/model/publicaciones.types';
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
  publicacionesDetalleById: Record<string, PublicacionDetalle | null>;
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
        <span className="text-xs text-secondary">
          Solicitud {formatShortId(solicitud.id)} -{' '}
          {formatSolicitudEstado(solicitud.estadoSolicitud)}
        </span>
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
  publicacion,
  solicitante,
  solicitud,
}: Readonly<{
  publicacion: PublicacionDetalle | null;
  solicitante: UsuarioResponse | null;
  solicitud: SolicitudUnion;
}>) {
  const publicacionTitle =
    publicacion?.titulo ?? `Publicacion ${formatShortId(solicitud.publicacionId)}`;
  const publicacionHref = publicacion ? `/videojuego/${publicacion.gameId}` : null;
  const solicitanteName = solicitante?.username ?? `Usuario ${formatShortId(solicitud.usuarioId)}`;

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
  publicacionesDetalleById,
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
            publicacion={publicacionesDetalleById[solicitud.publicacionId]}
            solicitante={usuariosById[solicitud.usuarioId]}
            solicitud={solicitud}
          />
        ))}
      </SolicitudUnionItemCard>
    </div>
  );
}

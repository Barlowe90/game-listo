import type {
  Publicacion,
  PublicacionDetalle,
} from '@/features/publicaciones/model/publicaciones.types';
import { PublicacionCard } from '@/features/publicaciones/components/PublicacionCard';
import { Badge } from '@/shared/components/ui/Badge';
import { Card } from '@/shared/components/ui/Card';
import { getGruposCountLabel } from './misPublicaciones.utils';

interface MisPublicacionesJoinedGroupsSectionProps {
  currentUserId: string | null;
  disableActions: boolean;
  gameTitlesById: Record<string, string>;
  isLoading: boolean;
  joinedPublicaciones: PublicacionDetalle[];
  onLeaveGroup: (publicacion: Publicacion) => void;
  onViewGroupInfo: (publicacion: Publicacion) => void;
}

export function MisPublicacionesJoinedGroupsSection({
  currentUserId,
  disableActions,
  gameTitlesById,
  isLoading,
  joinedPublicaciones,
  onLeaveGroup,
  onViewGroupInfo,
}: Readonly<MisPublicacionesJoinedGroupsSectionProps>) {
  return (
    <Card className="rounded-[calc(var(--radius-xl)+0.5rem)] border border-border bg-white/90 shadow-elevated backdrop-blur-sm">
      <div className="grid gap-5 p-6">
        <div className="flex flex-wrap items-start justify-between gap-3">
          <div className="grid gap-1">
            <h2 className="text-lg font-semibold tracking-tight text-foreground">
              Grupos en los que participas
            </h2>
            <p className="text-sm leading-relaxed text-secondary">
              Aqui veras los grupos en los que ya participas, tanto si eres autor como si te has unido como jugador.
            </p>
          </div>

          <Badge variant="primary">{getGruposCountLabel(joinedPublicaciones.length)}</Badge>
        </div>

        {isLoading ? (
          <p className="text-sm leading-relaxed text-secondary">
            Estamos cargando los grupos en los que participas.
          </p>
        ) : joinedPublicaciones.length ? (
          <div className="grid gap-5 xl:grid-cols-2 2xl:grid-cols-3">
            {joinedPublicaciones.map((publicacion) => {
              const isAuthor = currentUserId === publicacion.autorId;

              return (
                <PublicacionCard
                  key={`joined-${publicacion.id}`}
                  publicacion={publicacion}
                  participantes={publicacion.participantes}
                  gameTitle={gameTitlesById[publicacion.gameId]}
                  showGameLink
                  isAuthor={isAuthor}
                  disableActions={disableActions}
                  onLeaveGroup={isAuthor ? undefined : onLeaveGroup}
                  onViewGroupInfo={publicacion.grupoId ? onViewGroupInfo : undefined}
                />
              );
            })}
          </div>
        ) : (
          <p className="text-sm leading-relaxed text-secondary">
            Todavia no participas en ningun grupo de juego.
          </p>
        )}
      </div>
    </Card>
  );
}

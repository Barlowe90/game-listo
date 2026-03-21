import Link from 'next/link';
import type {
  GrupoJuego,
  Publicacion,
} from '@/features/publicaciones/model/publicaciones.types';
import { EmptyPublicationsState } from '@/shared/components/domain/EmptyPublicationsState';
import { Button } from '@/shared/components/ui/Button';
import { Card } from '@/shared/components/ui/Card';
import { PublicacionCard } from './PublicacionCard';

interface MisPublicacionesGridSectionProps {
  currentUserId: string | null;
  disableActions: boolean;
  gameTitlesById: Record<string, string>;
  gruposByPublicacionId: Record<string, GrupoJuego | null>;
  isLoading: boolean;
  onDelete: (publicacion: Publicacion) => void;
  onEdit: (publicacion: Publicacion) => void;
  onViewGroupInfo: (publicacion: Publicacion) => void;
  publicaciones: Publicacion[];
}

export function MisPublicacionesGridSection({
  currentUserId,
  disableActions,
  gameTitlesById,
  gruposByPublicacionId,
  isLoading,
  onDelete,
  onEdit,
  onViewGroupInfo,
  publicaciones,
}: Readonly<MisPublicacionesGridSectionProps>) {
  if (isLoading) {
    return (
      <Card className="rounded-[calc(var(--radius-xl)+0.5rem)] border border-border bg-white/80 shadow-surface">
        <div className="grid gap-3 p-6">
          <h2 className="text-lg font-semibold tracking-tight text-foreground">
            Cargando tu actividad
          </h2>
          <p className="text-sm leading-relaxed text-secondary">
            Estamos recuperando tus publicaciones y tus solicitudes de union.
          </p>
        </div>
      </Card>
    );
  }

  if (!publicaciones.length) {
    return (
      <EmptyPublicationsState
        title="Todavia no has creado publicaciones"
        description="Crea una publicacion desde la ficha de un videojuego para que aparezca aqui como autor."
        action={
          <Button asChild>
            <Link href="/catalogo">Buscar un juego</Link>
          </Button>
        }
      />
    );
  }

  return (
    <div className="grid gap-5 xl:grid-cols-2 2xl:grid-cols-3">
      {publicaciones.map((publicacion) => {
        const isAuthor = currentUserId === publicacion.autorId;

        return (
          <PublicacionCard
            key={publicacion.id}
            publicacion={publicacion}
            participantes={gruposByPublicacionId[publicacion.id]?.participantes ?? null}
            gameTitle={gameTitlesById[publicacion.gameId]}
            showGameLink
            isAuthor={isAuthor}
            disableActions={disableActions}
            onEdit={isAuthor ? onEdit : undefined}
            onDelete={isAuthor ? onDelete : undefined}
            onViewGroupInfo={publicacion.grupoId ? onViewGroupInfo : undefined}
          />
        );
      })}
    </div>
  );
}

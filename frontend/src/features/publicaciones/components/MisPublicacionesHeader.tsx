import { Badge } from '@/shared/components/ui/Badge';
import { getPublicacionesCountLabel } from './misPublicaciones.utils';

interface MisPublicacionesHeaderProps {
  publicacionesCount: number;
}

export function MisPublicacionesHeader({
  publicacionesCount,
}: Readonly<MisPublicacionesHeaderProps>) {
  return (
    <div className="grid gap-4">
      <div className="flex flex-wrap items-start justify-between gap-4">
        <div className="grid gap-2">
          <h1 className="text-3xl font-bold tracking-tight text-foreground">Mis publicaciones</h1>
        </div>
      </div>

      <div className="flex flex-wrap items-center gap-3">
        <Badge variant="primary">{getPublicacionesCountLabel(publicacionesCount)}</Badge>
      </div>
    </div>
  );
}

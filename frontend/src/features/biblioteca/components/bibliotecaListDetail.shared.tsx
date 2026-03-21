import Link from 'next/link';
import { GameArtwork } from '@/shared/components/domain/GameArtwork';
import { PlatformChip } from '@/shared/components/domain/TagList';
import { Badge } from '@/shared/components/ui/Badge';
import { Card } from '@/shared/components/ui/Card';
import { SectionHeader } from '@/shared/components/ui/SectionHeader';
import { Skeleton } from '@/shared/components/ui/Skeleton';
import {
  BIBLIOTECA_ESTADOS,
  type BibliotecaEstado,
  type BibliotecaListaJuego,
} from '@/features/biblioteca/model/biblioteca.types';
import {
  formatBibliotecaEnumLabel,
  isBibliotecaEstado,
} from '@/features/biblioteca/model/biblioteca.utils';
import { cn } from '@/lib/cn';

export interface BibliotecaListaJuegoDetalle extends BibliotecaListaJuego {
  plataformas: string[];
}

export function EstadoBadge({ estado }: Readonly<{ estado: string | null }>) {
  if (!estado) {
    return <Badge variant="neutral">Sin estado</Badge>;
  }

  return <Badge variant="primary">{formatBibliotecaEnumLabel(estado)}</Badge>;
}

export function BibliotecaListDetailLoading() {
  return (
    <div className="grid gap-6">
      <SectionHeader
        title={<Skeleton variant="line" size="lg" className="w-48" />}
        action={<Skeleton variant="block" size="sm" className="h-11 w-32 rounded-md" />}
      />

      <Card padding="md" className="rounded-[calc(var(--radius-xl)+0.75rem)]">
        <div className="grid gap-3">
          <Skeleton variant="line" size="sm" className="w-28" />
          <Skeleton variant="line" size="md" className="w-52" />
        </div>
      </Card>

      <div className="grid gap-4">
        {[0, 1, 2].map((item) => (
          <Card key={item} padding="md" className="rounded-[calc(var(--radius-xl)+0.75rem)]">
            <div className="grid gap-4 md:grid-cols-[5rem_minmax(0,1.4fr)_minmax(0,1fr)_auto] md:items-center">
              <Skeleton variant="block" size="md" className="h-28 w-20 rounded-xl" />
              <Skeleton variant="line" size="md" className="w-40" />
              <div className="flex gap-2">
                <Skeleton variant="line" size="sm" className="w-16" />
                <Skeleton variant="line" size="sm" className="w-20" />
              </div>
              <Skeleton variant="line" size="sm" className="w-24" />
            </div>
          </Card>
        ))}
      </div>
    </div>
  );
}

function EstadoActionSelect({
  gameId,
  isUpdatingEstado,
  onUpdateEstado,
  selectedEstado,
}: Readonly<{
  gameId: number;
  isUpdatingEstado: boolean;
  onUpdateEstado?: (gameId: number, estado: BibliotecaEstado | null) => void;
  selectedEstado: BibliotecaEstado | null;
}>) {
  const inputId = `game-${gameId}-estado-select`;

  return (
    <div className="grid gap-2">
      <label htmlFor={inputId} className="sr-only">
        Seleccionar estado
      </label>
      <select
        id={inputId}
        value={selectedEstado ?? ''}
        onChange={(event) => {
          const nextValue = event.target.value;
          onUpdateEstado?.(gameId, nextValue && isBibliotecaEstado(nextValue) ? nextValue : null);
        }}
        disabled={!onUpdateEstado || isUpdatingEstado}
        className={cn(
          'min-h-[var(--target-min-size)] min-w-[12rem] rounded-md border border-border bg-white px-3 py-2 text-sm text-foreground shadow-surface transition-[border-color,background-color,color,box-shadow] duration-[var(--duration-fast)] ease-[var(--easing-standard)] focus-visible:border-primary',
          (!onUpdateEstado || isUpdatingEstado) &&
            'cursor-not-allowed bg-surface text-muted-foreground',
        )}
      >
        <option value="">Sin estado</option>
        {BIBLIOTECA_ESTADOS.map((estado) => (
          <option key={estado} value={estado}>
            {formatBibliotecaEnumLabel(estado)}
          </option>
        ))}
      </select>

      {isUpdatingEstado ? <span className="text-xs text-secondary">Guardando...</span> : null}
    </div>
  );
}

export function BibliotecaGameRow({
  juego,
  showActionsColumn = false,
  isUpdatingEstado = false,
  onUpdateEstado,
}: Readonly<{
  juego: BibliotecaListaJuegoDetalle;
  showActionsColumn?: boolean;
  isUpdatingEstado?: boolean;
  onUpdateEstado?: (gameId: number, estado: BibliotecaEstado | null) => void;
}>) {
  return (
    <Card
      padding="md"
      className="rounded-[calc(var(--radius-xl)+0.75rem)] border border-border bg-white/92 shadow-elevated"
    >
      <div
        className={`grid gap-4 md:items-center ${
          showActionsColumn
            ? 'md:grid-cols-[5rem_minmax(0,1.4fr)_minmax(0,1fr)_auto_minmax(12rem,auto)]'
            : 'md:grid-cols-[5rem_minmax(0,1.4fr)_minmax(0,1fr)_auto]'
        }`}
      >
        <Link href={`/videojuego/${juego.gameId}`} className="block w-20">
          <GameArtwork
            aspect="portrait"
            radius="md"
            coverUrl={juego.cover}
            title={juego.nombre?.trim() || `Juego #${juego.gameId}`}
            sizes="80px"
            className="w-20 shadow-surface"
          />
        </Link>

        <div className="grid gap-1">
          <Link
            href={`/videojuego/${juego.gameId}`}
            className="text-base font-semibold text-foreground transition-colors hover:text-primary"
          >
            {juego.nombre?.trim() || `Juego #${juego.gameId}`}
          </Link>
          <p className="text-sm text-secondary">ID #{juego.gameId}</p>
        </div>

        <div className="flex min-h-10 flex-wrap items-center gap-2">
          {juego.plataformas.length ? (
            juego.plataformas.slice(0, 3).map((plataforma) => (
              <PlatformChip key={`${juego.gameId}-${plataforma}`} className="px-2.5 py-1 text-xs">
                {plataforma}
              </PlatformChip>
            ))
          ) : (
            <span className="text-sm text-secondary">Sin plataforma registrada</span>
          )}
        </div>

        <div className="justify-self-start md:justify-self-end">
          <EstadoBadge estado={juego.estado} />
        </div>

        {showActionsColumn ? (
          <div className="justify-self-start md:justify-self-end">
            <EstadoActionSelect
              gameId={juego.gameId}
              isUpdatingEstado={isUpdatingEstado}
              onUpdateEstado={onUpdateEstado}
              selectedEstado={juego.estado}
            />
          </div>
        ) : null}
      </div>
    </Card>
  );
}

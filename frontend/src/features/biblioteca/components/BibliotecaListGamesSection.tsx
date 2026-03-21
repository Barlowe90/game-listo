import type { BibliotecaEstado } from '@/features/biblioteca/model/biblioteca.types';
import { BibliotecaGameRow, type BibliotecaListaJuegoDetalle } from './bibliotecaListDetail.shared';

interface BibliotecaListGamesSectionProps {
  juegosDetalle: BibliotecaListaJuegoDetalle[];
  listaId: string;
  showActionsColumn?: boolean;
  updatingEstadoGameId?: number | null;
  onUpdateEstado?: (gameId: number, estado: BibliotecaEstado | null) => void;
}

export function BibliotecaListGamesSection({
  juegosDetalle,
  listaId,
  showActionsColumn = false,
  updatingEstadoGameId = null,
  onUpdateEstado,
}: Readonly<BibliotecaListGamesSectionProps>) {
  return (
    <div className="grid gap-4">
      <div
        className={`hidden rounded-[calc(var(--radius-xl)+0.4rem)] border border-border bg-white/70 px-6 py-3 md:grid md:items-center ${
          showActionsColumn
            ? 'md:grid-cols-[5rem_minmax(0,1.4fr)_minmax(0,1fr)_auto_minmax(12rem,auto)]'
            : 'md:grid-cols-[5rem_minmax(0,1.4fr)_minmax(0,1fr)_auto]'
        }`}
      >
        <span className="text-xs font-semibold tracking-[0.08em] text-primary uppercase">
          Cover
        </span>
        <span className="text-xs font-semibold tracking-[0.08em] text-primary uppercase">
          Titulo
        </span>
        <span className="text-xs font-semibold tracking-[0.08em] text-primary uppercase">
          Plataforma
        </span>
        <span className="text-xs font-semibold tracking-[0.08em] text-primary uppercase">
          Estado
        </span>
        {showActionsColumn ? (
          <span className="text-xs font-semibold tracking-[0.08em] text-primary uppercase">
            Acciones
          </span>
        ) : null}
      </div>

      {juegosDetalle.map((juego) => (
        <BibliotecaGameRow
          key={`${listaId}-${juego.gameId}`}
          juego={juego}
          showActionsColumn={showActionsColumn}
          isUpdatingEstado={updatingEstadoGameId === juego.gameId}
          onUpdateEstado={onUpdateEstado}
        />
      ))}
    </div>
  );
}

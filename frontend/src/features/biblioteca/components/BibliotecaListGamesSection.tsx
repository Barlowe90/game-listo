import { BibliotecaGameRow, type BibliotecaListaJuegoDetalle } from './bibliotecaListDetail.shared';

interface BibliotecaListGamesSectionProps {
  juegosDetalle: BibliotecaListaJuegoDetalle[];
  listaId: string;
}

export function BibliotecaListGamesSection({
  juegosDetalle,
  listaId,
}: Readonly<BibliotecaListGamesSectionProps>) {
  return (
    <div className="grid gap-4">
      <div className="hidden rounded-[calc(var(--radius-xl)+0.4rem)] border border-border bg-white/70 px-6 py-3 md:grid md:grid-cols-[5rem_minmax(0,1.4fr)_minmax(0,1fr)_auto] md:items-center">
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
      </div>

      {juegosDetalle.map((juego) => (
        <BibliotecaGameRow key={`${listaId}-${juego.gameId}`} juego={juego} />
      ))}
    </div>
  );
}

export const BIBLIOTECA_ESTADOS = [
  'DESEADO',
  'PENDIENTE',
  'JUGANDO',
  'COMPLETADO',
  'ABANDONADO',
] as const;

export type BibliotecaEstado = (typeof BIBLIOTECA_ESTADOS)[number];
export type BibliotecaListaTipo = 'OFICIAL' | 'PERSONALIZADA';

export interface BibliotecaListaJuego {
  gameId: number;
  nombre: string | null;
  cover: string | null;
  estado: BibliotecaEstado | null;
}

export interface BibliotecaLista {
  id: string;
  usuarioRefId: string;
  nombre: string;
  tipo: BibliotecaListaTipo;
  juegos: BibliotecaListaJuego[];
}

export interface CrearBibliotecaListaPayload {
  nombre: string;
  tipo?: 'PERSONALIZADA';
}

export interface BibliotecaGameEstado {
  id: string;
  usuarioRefId: string;
  gameId: number;
  estado: BibliotecaEstado;
  rating: number | null;
}

export interface ImportarBibliotecaSteamResult {
  listaId: string;
  listaNombre: string;
  steamOwnedCount: number;
  resolvedCount: number;
  addedCount: number;
  alreadyPresentCount: number;
  unresolvedCount: number;
}

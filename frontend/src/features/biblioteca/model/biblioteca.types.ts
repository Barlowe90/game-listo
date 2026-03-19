export type BibliotecaListaTipo = 'OFICIAL' | 'PERSONALIZADA';

export interface BibliotecaListaJuego {
  gameId: number;
  nombre: string | null;
  cover: string | null;
  estado: string | null;
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

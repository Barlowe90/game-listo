export interface UsuarioRef {
  id: string;
  username: string;
  avatar: string | null;
}

export interface ResumenSocialJuego {
  amigosDeseadoCount: number;
  amigosJugandoCount: number;
  amigosDeseadoPreview: UsuarioRef[];
  amigosJugandoPreview: UsuarioRef[];
}

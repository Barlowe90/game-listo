// contrato con la API
// este archivo describe el json que devuelve el backend, utilizo lo más limpio que es coger todos los datos del JSON

export interface TokenResponse {
  accessToken: string;
  refreshToken: string;
}

export interface UsuarioResponse {
  id: string;
  username: string;
  email: string;
  avatar?: string | null;
  role: string;
  language: string;
  status: string;
  discordUserId?: string | null;
  discordUsername?: string | null;
}

export interface AuthResponse {
  tokens: TokenResponse;
  user: UsuarioResponse;
}

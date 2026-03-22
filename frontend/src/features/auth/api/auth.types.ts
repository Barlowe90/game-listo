// Contrato real de la API de autenticacion del backend.

export interface TokenResponse {
  token: string;
  expiresAt: string;
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
}

export interface AuthResponse {
  accessToken: TokenResponse;
  refreshToken: TokenResponse;
  usuario: UsuarioResponse;
}

export function getAccessTokenValue(authResponse: AuthResponse): string {
  return authResponse.accessToken.token;
}

export function getRefreshTokenValue(authResponse: AuthResponse): string {
  return authResponse.refreshToken.token;
}

export function getAuthUser(authResponse: AuthResponse): UsuarioResponse {
  return authResponse.usuario;
}

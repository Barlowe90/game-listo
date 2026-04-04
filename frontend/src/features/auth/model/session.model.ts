// archivo define cómo vive la sesión dentro del frontend
// sirve para el state de react, si recargamos la página se pierde la info

export type SessionStatus = 'loading' | 'authenticated' | 'anonymous';

export interface User {
  id: string;
  username: string;
  email: string;
  avatar?: string | null;
  role: string;
  status: string;
  discordUserId?: string | null;
}

export interface Session {
  status: SessionStatus;
  user: User | null;
  accessToken: string | null;
}

export interface Tokens {
  accessToken: string;
  refreshToken: string;
}

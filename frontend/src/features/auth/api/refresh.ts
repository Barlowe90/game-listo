import { AuthResponse } from './auth.types';
import { executeRefreshRequest } from './refreshRequest';

export async function refresh(refreshToken: string): Promise<AuthResponse> {
  return executeRefreshRequest(refreshToken);
}

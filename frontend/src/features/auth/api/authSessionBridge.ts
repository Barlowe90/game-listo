import { AuthResponse } from './auth.types';

// este archivo no depende de react
// solo guarda en memoria el accesstoken actual para usar en httpclient

let currentAccessToken: string | null = null;
let onUnauthorized: (() => void | Promise<void>) | null = null;
let onTokenRefreshed: ((authResponse: AuthResponse) => void | Promise<void>) | null = null;

export function setAccessToken(token: string | null) {
  currentAccessToken = token;
}

export function getAccessToken() {
  return currentAccessToken;
}

export function clearAccessToken() {
  currentAccessToken = null;
}

export function setUnauthorizedHandler(handler: (() => void | Promise<void>) | null) {
  onUnauthorized = handler;
}

export async function notifyUnauthorized() {
  if (onUnauthorized) {
    await onUnauthorized();
  }
}

export function setTokenRefreshHandler(
  handler: ((authResponse: AuthResponse) => void | Promise<void>) | null,
) {
  onTokenRefreshed = handler;
}

export async function notifyTokenRefreshed(authResponse: AuthResponse) {
  if (onTokenRefreshed) {
    await onTokenRefreshed(authResponse);
  }
}

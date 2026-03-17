'use client'; // daba problemas al arrancar debido a que lo pide app router de next.js

import React, { createContext, useCallback, useEffect, useState, ReactNode } from 'react';
import { User, Session, SessionStatus } from '../model/session.model';
import { authApi } from '../api/authApi';
import { saveRefreshToken, getRefreshToken, clearRefreshToken } from '../api/tokenStorage';
import {
  setAccessToken as setBridgeAccessToken,
  clearAccessToken as clearBridgeAccessToken,
  setUnauthorizedHandler,
  setTokenRefreshHandler,
} from '../api/authSessionBridge';
import {
  getAccessTokenValue,
  getAuthUser,
  getRefreshTokenValue,
  type AuthResponse,
} from '../api/auth.types';
import { useRouter } from 'next/navigation';

// Define el contrato del contexto de autenticación del frontend.
// Aquí se describe qué información y qué funciones relacionadas con la sesión
// estarán disponibles en toda la aplicación.
export interface AuthContextType {
  user: User | null;
  accessToken: string | null;
  isAuthenticated: boolean;
  isLoading: boolean;
  status: SessionStatus;
  login: (email: string, password: string) => Promise<void>;
  logout: () => Promise<void>;
  refreshSession: () => Promise<void>;
  setSession: (session: Session) => void;
  clearSession: () => void;
}

// Contexto global de autenticación accesible desde cualquier componente
export const AuthContext = createContext<AuthContextType | undefined>(undefined);

interface AuthProviderProps {
  children: ReactNode;
}

export const AuthProvider = ({ children }: AuthProviderProps) => {
  // Estados donde se almacena la sesión en memoria dentro del frontend
  const [status, setStatus] = useState<SessionStatus>('loading');
  const [user, setUser] = useState<User | null>(null);
  // accessToken usado para autenticar las llamadas al backend
  const [accessToken, setAccessToken] = useState<string | null>(null);
  const [isLoading, setIsLoading] = useState<boolean>(true);
  const router = useRouter();

  const isAuthenticated = !!user && !!accessToken;

  // Actualiza el estado de sesión del frontend con los datos recibidos
  const setSession = useCallback((session: Session) => {
    setStatus(session.status);
    setUser(session.user);
    setAccessToken(session.accessToken);
    setBridgeAccessToken(session.accessToken);
  }, []);

  // Limpia la sesión del frontend (logout, refresh fallido o token inválido)
  const clearSession = useCallback(() => {
    setUser(null);
    setAccessToken(null);
    setStatus('anonymous');
    clearRefreshToken();
    clearBridgeAccessToken();
  }, []);

  // Flujo de login:
  // 1. El usuario introduce credenciales
  // 2. Se llama al endpoint de login mediante authApi
  // 3. Se guarda el refreshToken en localStorage
  // 4. Se guarda en memoria el accessToken y el usuario
  // 5. Se marca la sesión como autenticada
  const login = useCallback(
    async (email: string, password: string) => {
      setIsLoading(true);
      try {
        const authResponse = await authApi.login(email, password);
        saveRefreshToken(getRefreshTokenValue(authResponse));
        setSession({
          user: getAuthUser(authResponse),
          accessToken: getAccessTokenValue(authResponse),
          status: 'authenticated',
        });
      } finally {
        setIsLoading(false);
      }
    },
    [setSession],
  );

  // Flujo de logout:
  // 0. Obtener el refres token de localStorage
  // 1. Se llama al endpoint /logout para invalidar la sesión en el backend
  // 2. El frontend limpia los datos de sesión (tokens y usuario)
  // 3. Redirigo a home
  const logout = useCallback(async () => {
    const refreshToken = getRefreshToken();

    try {
      if (refreshToken) {
        await authApi.logout(refreshToken, accessToken);
      }
    } finally {
      clearSession();
      router.push('/');
    }
  }, [accessToken, clearSession, router]);

  // Intenta reconstruir la sesión usando el refreshToken almacenado.
  // Si el refresh tiene éxito se obtiene un nuevo accessToken y usuario.
  // Si falla, la sesión se limpia.
  const refreshSession = useCallback(async () => {
    const refreshToken = getRefreshToken();
    if (!refreshToken) {
      clearSession();
      setIsLoading(false);
      return;
    }

    setIsLoading(true);
    try {
      const authResponse = await authApi.refresh(refreshToken);
      saveRefreshToken(getRefreshTokenValue(authResponse));
      setSession({
        user: getAuthUser(authResponse),
        accessToken: getAccessTokenValue(authResponse),
        status: 'authenticated',
      });
    } catch {
      clearSession();
    } finally {
      setIsLoading(false);
    }
  }, [clearSession, setSession]);

  // Ejecuta el bootstrap de sesión al arrancar la aplicación.
  // Intenta recuperar la sesión usando el refreshToken.
  useEffect(() => {
    setUnauthorizedHandler(async () => {
      clearSession();
    });

    setTokenRefreshHandler(async (authResponse: AuthResponse) => {
      saveRefreshToken(getRefreshTokenValue(authResponse));
      setSession({
        status: 'authenticated',
        accessToken: getAccessTokenValue(authResponse),
        user: getAuthUser(authResponse),
      });
    });

    void refreshSession();

    return () => {
      setUnauthorizedHandler(null);
      setTokenRefreshHandler(null);
    };
  }, [clearSession, refreshSession, setSession]);

  // Expone el estado de autenticación y sus funciones al resto de la aplicación
  return (
    <AuthContext.Provider
      value={{
        status,
        user, // saber quien esta logeado
        accessToken, // llamada al back
        isAuthenticated, // saber si hay sesion
        isLoading,
        login,
        logout,
        refreshSession,
        setSession,
        clearSession,
      }}
    >
      {children}
    </AuthContext.Provider>
  );
};

'use client'; // daba problemas al arrancar debido a que lo pide app router de next.js

import React, {
  createContext,
  useCallback,
  useEffect,
  useRef,
  useState,
  ReactNode,
} from 'react';
import { useRouter } from 'next/navigation';
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

export const AuthContext = createContext<AuthContextType | undefined>(undefined);

interface AuthProviderProps {
  children: ReactNode;
}

export const AuthProvider = ({ children }: AuthProviderProps) => {
  const [status, setStatus] = useState<SessionStatus>('loading');
  const [user, setUser] = useState<User | null>(null);
  const [accessToken, setAccessToken] = useState<string | null>(null);
  const [isLoading, setIsLoading] = useState<boolean>(true);
  const isMountedRef = useRef(false);
  const router = useRouter();

  const isAuthenticated = !!user && !!accessToken;

  const setSession = useCallback((session: Session) => {
    setStatus(session.status);
    setUser(session.user);
    setAccessToken(session.accessToken);
    setBridgeAccessToken(session.accessToken);
  }, []);

  const clearSession = useCallback(() => {
    setUser(null);
    setAccessToken(null);
    setStatus('anonymous');
    clearRefreshToken();
    clearBridgeAccessToken();
  }, []);

  const login = useCallback(
    async (email: string, password: string) => {
      setIsLoading(true);

      try {
        const authResponse = await authApi.login(email, password);

        if (!isMountedRef.current) {
          return;
        }

        saveRefreshToken(getRefreshTokenValue(authResponse));
        setSession({
          user: getAuthUser(authResponse),
          accessToken: getAccessTokenValue(authResponse),
          status: 'authenticated',
        });
      } finally {
        if (isMountedRef.current) {
          setIsLoading(false);
        }
      }
    },
    [setSession],
  );

  const logout = useCallback(async () => {
    const refreshToken = getRefreshToken();

    try {
      if (refreshToken) {
        await authApi.logout(refreshToken, accessToken);
      }
    } finally {
      if (isMountedRef.current) {
        clearSession();
        router.push('/');
      }
    }
  }, [accessToken, clearSession, router]);

  const refreshSession = useCallback(async () => {
    const refreshToken = getRefreshToken();

    if (!refreshToken) {
      if (isMountedRef.current) {
        clearSession();
        setIsLoading(false);
      }

      return;
    }

    setIsLoading(true);

    try {
      const authResponse = await authApi.refresh(refreshToken);
      const nextRefreshToken = getRefreshTokenValue(authResponse);
      const currentRefreshToken = getRefreshToken();

      if (
        !isMountedRef.current ||
        (currentRefreshToken !== refreshToken && currentRefreshToken !== nextRefreshToken)
      ) {
        return;
      }

      saveRefreshToken(nextRefreshToken);
      setSession({
        user: getAuthUser(authResponse),
        accessToken: getAccessTokenValue(authResponse),
        status: 'authenticated',
      });
    } catch {
      if (isMountedRef.current && getRefreshToken() === refreshToken) {
        clearSession();
      }
    } finally {
      if (isMountedRef.current) {
        setIsLoading(false);
      }
    }
  }, [clearSession, setSession]);

  useEffect(() => {
    isMountedRef.current = true;

    setUnauthorizedHandler(async () => {
      if (!isMountedRef.current) {
        return;
      }

      clearSession();
      setIsLoading(false);
    });

    setTokenRefreshHandler(async (authResponse: AuthResponse) => {
      if (!isMountedRef.current) {
        return;
      }

      saveRefreshToken(getRefreshTokenValue(authResponse));
      setSession({
        status: 'authenticated',
        accessToken: getAccessTokenValue(authResponse),
        user: getAuthUser(authResponse),
      });
    });

    void refreshSession();

    return () => {
      isMountedRef.current = false;
      setUnauthorizedHandler(null);
      setTokenRefreshHandler(null);
    };
  }, [clearSession, refreshSession, setSession]);

  return (
    <AuthContext.Provider
      value={{
        status,
        user,
        accessToken,
        isAuthenticated,
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

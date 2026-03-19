'use client';

import { useContext } from 'react';
import { AuthContext, AuthContextType } from '@/features/auth/providers/AuthProvider';

export function useAuth(): AuthContextType {
  const context = useContext(AuthContext);
  if (!context) {
    throw new Error('useAuth se debe usar dentro del AuthProvider');
  }
  return context;
}

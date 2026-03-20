'use client';

import { PropsWithChildren } from 'react';

import { AuthProvider } from '@/features/auth/providers/AuthProvider';

export function AppProviders({ children }: PropsWithChildren) {
  return <AuthProvider>{children}</AuthProvider>;
}

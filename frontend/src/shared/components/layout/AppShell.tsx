'use client';

import type { ReactNode } from 'react';
import { usePathname } from 'next/navigation';
import { cn } from '@/lib/cn';
import { Footer } from '@/shared/components/layout/Footer';
import { Header } from '@/shared/components/layout/Header';

export interface AppShellProps {
  children: ReactNode;
}

export function AppShell({ children }: AppShellProps) {
  const pathname = usePathname();
  const isHomePage = pathname === '/';
  const isLoginPage = pathname === '/login';
  const hasIntegratedBackground = isHomePage || isLoginPage;

  return (
    <div
      className={cn(
        'flex min-h-screen flex-col',
        hasIntegratedBackground
          ? 'bg-[linear-gradient(180deg,#3B3FB7_0%,#070D29_100%)] text-primary-foreground'
          : 'bg-background text-foreground',
      )}
    >
      {isLoginPage ? null : <Header integrated={isHomePage} />}
      <main className={cn('flex-1', isLoginPage && 'flex')}>{children}</main>
      {isLoginPage ? null : <Footer integrated={isHomePage} />}
    </div>
  );
}

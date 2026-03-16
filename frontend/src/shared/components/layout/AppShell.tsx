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

  return (
    <div
      className={cn(
        'flex min-h-screen flex-col',
        isHomePage
          ? 'bg-[linear-gradient(180deg,#3B3FB7_0%,#070D29_100%)] text-primary-foreground'
          : 'bg-background text-foreground',
      )}
    >
      <Header integrated={isHomePage} />
      <main className="flex-1">{children}</main>
      <Footer integrated={isHomePage} />
    </div>
  );
}

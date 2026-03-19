'use client';

import { useAuth } from '@/features/auth/hooks/useAuth';
import { PageContainer } from '@/shared/components/ui/PageContainer';
import { Skeleton } from '@/shared/components/ui/Skeleton';
import { Surface } from '@/shared/components/ui/Surface';
import { useEffect } from 'react';
import { useRouter } from 'next/navigation';

interface ProtectedRouteProps {
  children: React.ReactNode;
}

export function ProtectedRoute({ children }: ProtectedRouteProps) {
  const { isAuthenticated, isLoading } = useAuth();
  const router = useRouter();

  useEffect(() => {
    if (!isLoading && !isAuthenticated) {
      router.replace('/login');
    }
  }, [isLoading, isAuthenticated, router]);

  if (isLoading) {
    return (
      <PageContainer className="py-10 lg:py-12">
        <Surface
          tone="surface"
          shadow="surface"
          padding="lg"
          className="grid gap-4"
          role="status"
          aria-live="polite"
          aria-busy="true"
        >
          <span className="sr-only">Cargando sesion...</span>
          <Skeleton variant="line" size="lg" className="mx-auto w-32" />
          <Skeleton variant="line" size="md" className="mx-auto w-full max-w-md" />
          <Skeleton variant="block" size="sm" className="w-full" />
        </Surface>
      </PageContainer>
    );
  }

  if (!isAuthenticated) {
    return null;
  }

  return <>{children}</>;
}

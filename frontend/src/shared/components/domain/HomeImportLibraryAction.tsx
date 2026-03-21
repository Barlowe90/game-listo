'use client';

import Link from 'next/link';
import { useAuth } from '@/features/auth/hooks/useAuth';
import { Button } from '@/shared/components/ui/Button';

export function HomeImportLibraryAction() {
  const { isAuthenticated, isLoading, user } = useAuth();

  const href =
    isAuthenticated && user
      ? `/usuario/${user.id}?seccion=biblioteca`
      : isLoading
        ? '/biblioteca'
        : '/login';

  return (
    <Button asChild variant="secondary">
      <Link href={href}>Importar biblioteca</Link>
    </Button>
  );
}

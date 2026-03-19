'use client';

import { useEffect } from 'react';
import { useRouter } from 'next/navigation';
import { useAuth } from '@/features/auth/hooks/useAuth';

export default function BibliotecaPage() {
  const router = useRouter();
  const { user } = useAuth();

  useEffect(() => {
    if (user) {
      router.replace(`/usuario/${user.id}?seccion=biblioteca`);
    }
  }, [router, user]);

  return null;
}

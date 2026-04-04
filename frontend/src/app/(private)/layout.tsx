import { AppShell } from '@/shared/components/layout/AppShell';
import { ProtectedRoute } from '@/features/auth/components/ProtectedRoute';
import { useId } from 'react';

export default function PrivateLayout({ children }: { children: React.ReactNode }) {
  const searchInputId = useId();

  return (
    <AppShell searchInputId={searchInputId}>
      <ProtectedRoute>{children}</ProtectedRoute>
    </AppShell>
  );
}

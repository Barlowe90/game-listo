import { AppShell } from '@/shared/components/layout/AppShell';
import { ProtectedRoute } from '@/features/auth/components/ProtectedRoute';

export default function PrivateLayout({ children }: { children: React.ReactNode }) {
  return (
    <AppShell>
      <ProtectedRoute>{children}</ProtectedRoute>
    </AppShell>
  );
}

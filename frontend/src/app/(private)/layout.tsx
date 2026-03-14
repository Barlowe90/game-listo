import { ProtectedRoute } from '@/features/auth/components/ProtectedRoute';

export default function PrivateLayout({ children }: { children: React.ReactNode }) {
  return <ProtectedRoute>{children}</ProtectedRoute>;
}

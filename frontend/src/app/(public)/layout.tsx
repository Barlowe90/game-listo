import { AppShell } from '@/shared/components/layout/AppShell';

export default function PublicLayout({ children }: { children: React.ReactNode }) {
  return <AppShell>{children}</AppShell>;
}


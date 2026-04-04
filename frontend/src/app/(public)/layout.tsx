import { AppShell } from '@/shared/components/layout/AppShell';
import { useId } from 'react';

export default function PublicLayout({ children }: { children: React.ReactNode }) {
  const searchInputId = useId();

  return <AppShell searchInputId={searchInputId}>{children}</AppShell>;
}


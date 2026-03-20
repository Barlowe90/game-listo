'use client';

import type { ReactNode } from 'react';
import { cn } from '@/lib/cn';
import { AppShell } from '@/shared/components/layout/AppShell';
import { PageSection } from '@/shared/components/layout/PageSection';
import { EmptyState } from '@/shared/components/ui/EmptyState';

interface AppRouteStateProps {
  action?: ReactNode;
  className?: string;
  description?: ReactNode;
  title: ReactNode;
}

export function AppRouteState({
  action,
  className,
  description,
  title,
}: AppRouteStateProps) {
  return (
    <AppShell>
      <PageSection size="wide" className="py-10 lg:py-14">
        <EmptyState
          title={title}
          description={description}
          action={action}
          className={cn(
            'min-h-[32rem] rounded-[calc(var(--radius-xl)+0.75rem)] border border-border bg-white/90 shadow-elevated backdrop-blur-sm',
            className,
          )}
        />
      </PageSection>
    </AppShell>
  );
}

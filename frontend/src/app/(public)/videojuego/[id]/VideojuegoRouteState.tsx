import type { ReactNode } from 'react';
import { cn } from '@/lib/cn';
import { PageSection } from '@/shared/components/layout/PageSection';
import { EmptyState } from '@/shared/components/ui/EmptyState';

interface VideojuegoRouteStateProps {
  action?: ReactNode;
  children?: ReactNode;
  className?: string;
  description?: ReactNode;
  title?: ReactNode;
}

export function VideojuegoRouteState({
  action,
  children,
  className,
  description,
  title,
}: VideojuegoRouteStateProps) {
  return (
    <div className="relative overflow-hidden bg-[radial-gradient(circle_at_top_left,#f8f9ff_0%,#eef0ff_42%,#e7e7fb_100%)]">
      <div className="pointer-events-none absolute left-[-8rem] top-14 h-72 w-72 rounded-full bg-white/40 blur-3xl" />
      <div className="pointer-events-none absolute right-[-6rem] top-40 h-80 w-80 rounded-full bg-primary-soft blur-3xl" />

      <PageSection size="wide" className="relative z-10 py-10 lg:py-14">
        {children ?? (
          <EmptyState
            title={title ?? 'Estado no disponible'}
            description={description}
            action={action}
            className={cn(
              'min-h-[28rem] rounded-[calc(var(--radius-xl)+0.75rem)] border border-border bg-white/90 shadow-elevated backdrop-blur-sm',
              className,
            )}
          />
        )}
      </PageSection>
    </div>
  );
}

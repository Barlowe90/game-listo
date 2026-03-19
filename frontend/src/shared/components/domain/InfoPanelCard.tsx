import type { HTMLAttributes, ReactNode } from 'react';
import { cn } from '@/lib/cn';
import { Card } from '@/shared/components/ui/Card';

export interface InfoPanelCardProps extends Omit<HTMLAttributes<HTMLDivElement>, 'title'> {
  description?: ReactNode;
  title?: ReactNode;
}

export function InfoPanelCard({
  children,
  className,
  description,
  title,
  ...props
}: InfoPanelCardProps) {
  const hasHeader = title || description;

  return (
    <Card
      className={cn(
        'rounded-[calc(var(--radius-xl)+0.75rem)] border border-border bg-white/90 shadow-elevated backdrop-blur-sm',
        className,
      )}
      {...props}
    >
      <div className="grid gap-4 p-6">
        {hasHeader ? (
          <div className="grid gap-2">
            {title ? <h2 className="text-lg font-semibold tracking-tight text-foreground">{title}</h2> : null}
            {description ? (
              <p className="text-sm leading-relaxed text-secondary">{description}</p>
            ) : null}
          </div>
        ) : null}
        {children}
      </div>
    </Card>
  );
}

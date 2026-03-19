import type { HTMLAttributes, ReactNode } from 'react';
import { cn } from '@/lib/cn';
import { Card } from '@/shared/components/ui/Card';

export interface EmptyStateProps extends Omit<HTMLAttributes<HTMLDivElement>, 'title'> {
  title: ReactNode;
  description?: ReactNode;
  action?: ReactNode;
  icon?: ReactNode;
}

export function EmptyState({
  title,
  description,
  action,
  icon,
  className,
  ...props
}: EmptyStateProps) {
  return (
    <Card
      variant="informative"
      className={cn('grid min-h-72 place-items-center', className)}
      {...props}
    >
      <div className="grid max-w-md justify-items-center gap-4 p-8 text-center">
        <span className="inline-flex size-14 items-center justify-center rounded-pill bg-primary-soft text-primary">
          {icon ?? (
            <svg aria-hidden="true" viewBox="0 0 24 24" className="size-7 fill-current">
              <path d="M5 4a3 3 0 0 0-3 3v10a3 3 0 0 0 3 3h14a3 3 0 0 0 3-3V9.83a3 3 0 0 0-.88-2.12l-2.83-2.83A3 3 0 0 0 16.17 4Zm0 2h11.17a1 1 0 0 1 .7.29l2.84 2.83a1 1 0 0 1 .29.71V17a1 1 0 0 1-1 1H5a1 1 0 0 1-1-1V7a1 1 0 0 1 1-1Zm3 3a1 1 0 0 0 0 2h8a1 1 0 1 0 0-2Zm0 4a1 1 0 0 0 0 2h5a1 1 0 1 0 0-2Z" />
            </svg>
          )}
        </span>

        <div className="grid gap-2">
          <h3 className="text-xl font-semibold tracking-tight text-foreground">{title}</h3>
          <p className="text-sm leading-relaxed text-secondary">{description}</p>
        </div>

        {action ? <div className="flex flex-wrap justify-center gap-3">{action}</div> : null}
      </div>
    </Card>
  );
}

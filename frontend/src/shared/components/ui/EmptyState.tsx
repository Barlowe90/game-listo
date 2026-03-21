import type { HTMLAttributes, ReactNode } from 'react';
import { Inbox } from 'lucide-react';
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
          {icon ?? <Inbox aria-hidden="true" className="size-7" />}
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

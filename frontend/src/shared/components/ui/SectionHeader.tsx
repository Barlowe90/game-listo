import type { HTMLAttributes, ReactNode } from 'react';
import { cn } from '@/lib/cn';

export interface SectionHeaderProps extends Omit<HTMLAttributes<HTMLDivElement>, 'title'> {
  title: ReactNode;
  action?: ReactNode;
}

export function SectionHeader({ title, action, className, ...props }: SectionHeaderProps) {
  return (
    <div
      className={cn('flex flex-col gap-4 sm:flex-row sm:items-end sm:justify-between', className)}
      {...props}
    >
      <div className="grid gap-2">
        <h2 className="text-3xl font-bold tracking-tight text-foreground">{title}</h2>
      </div>

      {action ? <div className="flex shrink-0 flex-wrap items-center gap-3">{action}</div> : null}
    </div>
  );
}

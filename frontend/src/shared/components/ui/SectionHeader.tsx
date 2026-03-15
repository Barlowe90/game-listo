import type { HTMLAttributes, ReactNode } from 'react';
import { cn } from '@/lib/cn';

export interface SectionHeaderProps extends Omit<HTMLAttributes<HTMLDivElement>, 'title'> {
  eyebrow?: ReactNode;
  title: ReactNode;
  subtitle?: ReactNode;
  action?: ReactNode;
}

export function SectionHeader({
  eyebrow,
  title,
  subtitle,
  action,
  className,
  ...props
}: SectionHeaderProps) {
  return (
    <div
      className={cn(
        'flex flex-col gap-4 sm:flex-row sm:items-end sm:justify-between',
        className,
      )}
      {...props}
    >
      <div className="grid gap-2">
        {eyebrow ? (
          <span className="text-sm font-semibold tracking-[0.08em] text-primary uppercase">
            {eyebrow}
          </span>
        ) : null}
        <h2 className="text-3xl font-bold tracking-tight text-foreground">{title}</h2>
        {subtitle ? (
          <p className="max-w-3xl text-sm leading-relaxed text-secondary">{subtitle}</p>
        ) : null}
      </div>

      {action ? <div className="flex shrink-0 flex-wrap items-center gap-3">{action}</div> : null}
    </div>
  );
}

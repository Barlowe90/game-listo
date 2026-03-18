import type { HTMLAttributes, ReactNode } from 'react';
import { cn } from '@/lib/cn';
import { FilterChip } from '@/shared/components/domain/FilterChip';
import { Card } from '@/shared/components/ui/Card';

interface FilterOption {
  active?: boolean;
  href?: string;
  label: ReactNode;
}

export interface FilterGroup {
  label: ReactNode;
  options: FilterOption[];
}

export interface FilterBarProps extends Omit<HTMLAttributes<HTMLDivElement>, 'title'> {
  groups: FilterGroup[];
  subtitle?: ReactNode;
  title?: ReactNode;
}

export function FilterBar({
  className,
  groups,
  subtitle,
  title,
  ...props
}: FilterBarProps) {
  return (
    <Card
      className={cn(
        'rounded-[calc(var(--radius-xl)+0.5rem)] border border-border bg-white/85 shadow-surface backdrop-blur-sm',
        className,
      )}
      {...props}
    >
      <div className="grid gap-5 p-5">
        {title || subtitle ? (
          <div className="grid gap-2">
            {title ? <h2 className="text-lg font-semibold text-foreground">{title}</h2> : null}
            {subtitle ? (
              <p className="text-sm leading-relaxed text-secondary">{subtitle}</p>
            ) : null}
          </div>
        ) : null}

        <div className="grid gap-4 xl:grid-cols-2">
          {groups.map((group, groupIndex) => (
            <div key={groupIndex} className="grid gap-3">
              <span className="text-xs font-semibold tracking-[0.08em] text-primary uppercase">
                {group.label}
              </span>
              <div className="flex flex-wrap gap-2">
                {group.options.map((option, optionIndex) => (
                  <FilterChip
                    key={`${groupIndex}-${optionIndex}`}
                    active={option.active}
                    href={option.href}
                  >
                    {option.label}
                  </FilterChip>
                ))}
              </div>
            </div>
          ))}
        </div>
      </div>
    </Card>
  );
}

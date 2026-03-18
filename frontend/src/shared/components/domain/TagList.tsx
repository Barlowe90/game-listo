import type { HTMLAttributes, ReactNode } from 'react';
import Link from 'next/link';
import { cva, type VariantProps } from 'class-variance-authority';
import { cn } from '@/lib/cn';
import { Badge } from '@/shared/components/ui/Badge';

const domainChipVariants = cva('px-3 py-1.5 text-sm transition-colors', {
  variants: {
    tone: {
      genre: 'border-transparent bg-primary-soft text-primary',
      platform: 'border-border bg-surface text-foreground',
      tag: 'border-border bg-background text-secondary',
    },
    interactive: {
      true: 'hover:border-border-strong hover:bg-card hover:text-foreground',
      false: '',
    },
  },
  defaultVariants: {
    tone: 'tag',
    interactive: false,
  },
});

interface DomainChipProps
  extends Omit<HTMLAttributes<HTMLSpanElement>, 'children'>,
    VariantProps<typeof domainChipVariants> {
  children: ReactNode;
  href?: string;
}

function DomainChip({
  children,
  className,
  href,
  interactive,
  tone,
  ...props
}: DomainChipProps) {
  const chipClassName = cn(domainChipVariants({ tone, interactive: interactive || Boolean(href) }));

  if (href) {
    return (
      <Link href={href} className={cn(chipClassName, className)}>
        {children}
      </Link>
    );
  }

  return (
    <Badge className={cn(chipClassName, className)} {...props}>
      {children}
    </Badge>
  );
}

export type GenreChipProps = Omit<DomainChipProps, 'tone'>;

export function GenreChip(props: GenreChipProps) {
  return <DomainChip tone="genre" {...props} />;
}

export type PlatformChipProps = Omit<DomainChipProps, 'tone'>;

export function PlatformChip(props: PlatformChipProps) {
  return <DomainChip tone="platform" {...props} />;
}

export interface TagListProps extends HTMLAttributes<HTMLDivElement> {
  emptyLabel?: ReactNode;
  getHref?: (item: string) => string | undefined;
  items: string[];
  limit?: number;
  tone?: VariantProps<typeof domainChipVariants>['tone'];
}

export function TagList({
  className,
  emptyLabel,
  getHref,
  items,
  limit,
  tone = 'tag',
  ...props
}: TagListProps) {
  if (!items.length) {
    return emptyLabel ? (
      <p className="text-sm leading-relaxed text-secondary">{emptyLabel}</p>
    ) : null;
  }

  const visibleItems = typeof limit === 'number' ? items.slice(0, limit) : items;

  return (
    <div className={cn('flex flex-wrap gap-2', className)} {...props}>
      {visibleItems.map((item) => {
        if (tone === 'genre') {
          return (
            <GenreChip key={item} href={getHref?.(item)}>
              {item}
            </GenreChip>
          );
        }

        if (tone === 'platform') {
          return (
            <PlatformChip key={item} href={getHref?.(item)}>
              {item}
            </PlatformChip>
          );
        }

        return (
          <DomainChip key={item} href={getHref?.(item)} tone="tag">
            {item}
          </DomainChip>
        );
      })}
    </div>
  );
}

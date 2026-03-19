import type { ButtonHTMLAttributes, ReactNode } from 'react';
import Link from 'next/link';
import { cva } from 'class-variance-authority';
import { cn } from '@/lib/cn';

const filterChipVariants = cva(
  'inline-flex min-h-[var(--target-min-size)] items-center justify-center gap-2 rounded-pill border px-4 py-2 text-sm font-medium transition-colors focus-visible:outline-none',
  {
    variants: {
      active: {
        true: 'border-transparent bg-primary text-primary-foreground shadow-surface',
        false:
          'border-border bg-surface text-secondary hover:border-border-strong hover:bg-card hover:text-foreground',
      },
    },
    defaultVariants: {
      active: false,
    },
  },
);

export interface FilterChipProps extends Omit<ButtonHTMLAttributes<HTMLButtonElement>, 'children'> {
  active?: boolean;
  children: ReactNode;
  href?: string;
}

export function FilterChip({
  active = false,
  children,
  className,
  href,
  type = 'button',
  ...props
}: FilterChipProps) {
  const filterClassName = cn(filterChipVariants({ active }), className);

  if (href) {
    return (
      <Link href={href} className={filterClassName}>
        {children}
      </Link>
    );
  }

  return (
    <button type={type} className={filterClassName} aria-pressed={active} {...props}>
      {children}
    </button>
  );
}

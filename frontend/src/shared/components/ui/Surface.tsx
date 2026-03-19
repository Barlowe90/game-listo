import type { HTMLAttributes } from 'react';
import { cva, type VariantProps } from 'class-variance-authority';
import { cn } from '@/lib/cn';

const surfaceVariants = cva(
  'rounded-xl border transition-colors duration-[var(--duration-normal)] ease-[var(--easing-standard)]',
  {
    variants: {
      tone: {
        surface: 'border-border bg-surface',
        card: 'border-border bg-card',
        ghost: 'border-transparent bg-transparent',
      },
      shadow: {
        none: 'shadow-none',
        surface: 'shadow-surface',
        elevated: 'shadow-elevated',
        overlay: 'shadow-overlay',
      },
      padding: {
        none: 'p-0',
        sm: 'p-4',
        md: 'p-6',
        lg: 'p-8',
      },
    },
    defaultVariants: {
      tone: 'surface',
      shadow: 'none',
      padding: 'md',
    },
  },
);

export interface SurfaceProps
  extends HTMLAttributes<HTMLDivElement>, VariantProps<typeof surfaceVariants> {}

export function Surface({ className, tone, shadow, padding, ...props }: SurfaceProps) {
  return <div className={cn(surfaceVariants({ tone, shadow, padding }), className)} {...props} />;
}

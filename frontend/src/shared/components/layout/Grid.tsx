import type { HTMLAttributes } from 'react';
import { cva, type VariantProps } from 'class-variance-authority';
import { cn } from '@/lib/cn';

const gridVariants = cva('grid', {
  variants: {
    variant: {
      stack: 'gap-6',
      cards: 'gap-4 md:grid-cols-2 xl:grid-cols-3',
      feature: 'gap-6 lg:grid-cols-[minmax(0,1.35fr)_minmax(20rem,0.95fr)]',
      contentAside: 'gap-6 lg:grid-cols-[minmax(0,1.5fr)_minmax(18rem,0.85fr)]',
      stats: 'gap-4 md:grid-cols-3',
      twoColumn: 'gap-4 lg:grid-cols-2',
    },
  },
  defaultVariants: {
    variant: 'stack',
  },
});

export interface GridProps
  extends HTMLAttributes<HTMLDivElement>,
    VariantProps<typeof gridVariants> {}

export function Grid({ className, variant, ...props }: GridProps) {
  return <div className={cn(gridVariants({ variant }), className)} {...props} />;
}


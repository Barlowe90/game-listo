import type { HTMLAttributes, ReactNode } from 'react';
import { cva, type VariantProps } from 'class-variance-authority';
import { cn } from '@/lib/cn';

const containerVariants = cva(
  'mx-auto w-full px-[var(--container-gutter-mobile)] md:px-[var(--container-gutter-tablet)] lg:px-[var(--container-gutter-desktop)]',
  {
    variants: {
      size: {
        default: 'max-w-[var(--container-width-default)]',
        narrow: 'max-w-[var(--container-width-narrow)]',
        wide: 'max-w-[var(--container-width-wide)]',
        full: 'max-w-none',
      },
    },
    defaultVariants: {
      size: 'default',
    },
  },
);

type ContainerElement = 'div' | 'section' | 'main' | 'header' | 'footer' | 'nav';

export interface ContainerProps
  extends VariantProps<typeof containerVariants>,
    Omit<HTMLAttributes<HTMLElement>, 'children' | 'className'> {
  as?: ContainerElement;
  children: ReactNode;
  className?: string;
}

export function Container({ as, children, className, size, ...props }: ContainerProps) {
  const Component = as ?? 'div';

  return (
    <Component className={cn(containerVariants({ size }), className)} {...props}>
      {children}
    </Component>
  );
}


import * as React from 'react';
import type { HTMLAttributes } from 'react';
import { Slot } from '@radix-ui/react-slot';
import { cva, type VariantProps } from 'class-variance-authority';
import { cn } from '@/lib/cn';

const cardVariants = cva(
  'overflow-hidden rounded-xl text-foreground transition-[transform,background-color,box-shadow] duration-[var(--duration-normal)] ease-[var(--easing-standard)]',
  {
    variants: {
      variant: {
        base: 'bg-card shadow-surface',
        clickable:
          'bg-card shadow-surface hover:-translate-y-px hover:shadow-elevated focus-visible:outline-none focus-visible:shadow-elevated',
        informative: 'bg-transparent shadow-none',
        home: 'bg-primary',
      },
      padding: {
        none: 'p-0',
        sm: 'p-4',
        md: 'p-6',
        lg: 'p-8',
      },
    },
    defaultVariants: {
      variant: 'base',
      padding: 'none',
    },
  },
);

export interface CardProps
  extends HTMLAttributes<HTMLDivElement>, VariantProps<typeof cardVariants> {
  asChild?: boolean;
}

export function Card({ asChild = false, className, variant, padding, ...props }: CardProps) {
  const Component = asChild ? Slot : 'div';

  return <Component className={cn(cardVariants({ variant, padding }), className)} {...props} />;
}

export function CardHeader({ className, ...props }: HTMLAttributes<HTMLDivElement>) {
  return <div className={cn('grid gap-2 p-6 pb-0', className)} {...props} />;
}

export function CardBody({ className, ...props }: HTMLAttributes<HTMLDivElement>) {
  return <div className={cn('grid gap-4 p-6', className)} {...props} />;
}

export function CardFooter({ className, ...props }: HTMLAttributes<HTMLDivElement>) {
  return <div className={cn('flex flex-wrap items-center gap-3 p-6 pt-0', className)} {...props} />;
}

export function CardTitle({ className, ...props }: React.HTMLAttributes<HTMLHeadingElement>) {
  return (
    <h3
      className={cn('text-lg font-semibold tracking-tight text-foreground', className)}
      {...props}
    />
  );
}

export function CardDescription({
  className,
  ...props
}: React.HTMLAttributes<HTMLParagraphElement>) {
  return <p className={cn('text-sm leading-relaxed text-secondary', className)} {...props} />;
}

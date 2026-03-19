import type { HTMLAttributes } from 'react';
import { cva, type VariantProps } from 'class-variance-authority';
import { cn } from '@/lib/cn';

const skeletonVariants = cva(
  'animate-pulse bg-primary-soft [animation-duration:calc(var(--duration-slow)*5)] [animation-timing-function:var(--easing-standard)]',
  {
    variants: {
      variant: {
        line: 'w-full rounded-sm',
        block: 'w-full rounded-lg',
        avatar: 'rounded-pill',
      },
      size: {
        sm: '',
        md: '',
        lg: '',
      },
    },
    compoundVariants: [
      {
        variant: 'line',
        size: 'sm',
        className: 'h-3',
      },
      {
        variant: 'line',
        size: 'md',
        className: 'h-4',
      },
      {
        variant: 'line',
        size: 'lg',
        className: 'h-6',
      },
      {
        variant: 'block',
        size: 'sm',
        className: 'h-16',
      },
      {
        variant: 'block',
        size: 'md',
        className: 'h-24',
      },
      {
        variant: 'block',
        size: 'lg',
        className: 'h-32',
      },
      {
        variant: 'avatar',
        size: 'sm',
        className: 'size-10',
      },
      {
        variant: 'avatar',
        size: 'md',
        className: 'size-12',
      },
      {
        variant: 'avatar',
        size: 'lg',
        className: 'size-16',
      },
    ],
    defaultVariants: {
      variant: 'line',
      size: 'md',
    },
  },
);

export interface SkeletonProps
  extends HTMLAttributes<HTMLDivElement>,
    VariantProps<typeof skeletonVariants> {}

export function Skeleton({ className, size, variant, ...props }: SkeletonProps) {
  return (
    <div
      aria-hidden="true"
      className={cn(skeletonVariants({ size, variant }), className)}
      {...props}
    />
  );
}

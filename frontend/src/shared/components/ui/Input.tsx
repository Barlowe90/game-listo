import * as React from 'react';
import { cva, type VariantProps } from 'class-variance-authority';
import { cn } from '@/lib/cn';

export const inputVariants = cva(
  'flex min-h-[var(--target-min-size)] w-full rounded-md border border-border bg-card text-foreground shadow-surface transition-[border-color,background-color,color,box-shadow] duration-[var(--duration-fast)] ease-[var(--easing-standard)] placeholder:text-muted-foreground hover:border-border-strong focus-visible:border-primary disabled:cursor-not-allowed disabled:bg-surface disabled:text-muted-foreground',
  {
    variants: {
      size: {
        sm: 'px-3 py-2 text-sm',
        md: 'px-4 py-2 text-sm',
        lg: 'min-h-12 px-4 py-3 text-base',
      },
      state: {
        default: '',
        error: 'border-error focus-visible:border-error',
      },
    },
    defaultVariants: {
      size: 'md',
      state: 'default',
    },
  },
);

export interface InputProps
  extends Omit<React.InputHTMLAttributes<HTMLInputElement>, 'size'>,
    VariantProps<typeof inputVariants> {}

export const Input = React.forwardRef<HTMLInputElement, InputProps>(
  ({ className, size, state, type = 'text', ...props }, ref) => {
    const isInvalid =
      state === 'error' || props['aria-invalid'] === true || props['aria-invalid'] === 'true';

    return (
      <input
        ref={ref}
        type={type}
        className={cn(inputVariants({ size, state }), className)}
        aria-invalid={isInvalid}
        {...props}
      />
    );
  },
);

Input.displayName = 'Input';

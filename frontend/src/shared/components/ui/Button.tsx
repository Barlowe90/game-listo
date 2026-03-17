'use client';

import * as React from 'react';
import { Slot } from '@radix-ui/react-slot';
import { cva, type VariantProps } from 'class-variance-authority';
import { cn } from '@/lib/cn';

export const buttonVariants = cva(
  'inline-flex min-h-[var(--target-min-size)] items-center justify-center gap-2 rounded-md border px-4 text-sm font-semibold whitespace-nowrap transition-[background-color,border-color,color,box-shadow,opacity,transform] duration-[var(--duration-fast)] ease-[var(--easing-standard)] focus-visible:outline-none disabled:pointer-events-none',
  {
    variants: {
      variant: {
        primary:
          'border-transparent bg-primary text-primary-foreground shadow-surface hover:bg-primary-hover active:bg-primary-active',
        secondary:
          'border-border bg-surface text-foreground hover:border-border-strong hover:bg-card',
        ghost:
          'border-transparent bg-transparent text-secondary! hover:bg-primary-soft hover:text-foreground',
      },
      size: {
        sm: 'px-3 text-sm',
        md: 'px-4 text-sm',
        lg: 'min-h-12 px-6 text-base',
      },
      loading: {
        true: 'data-[loading=true]:opacity-[var(--opacity-loading)]',
        false: '',
      },
    },
    defaultVariants: {
      variant: 'primary',
      size: 'md',
      loading: false,
    },
  },
);

export interface ButtonProps
  extends React.ButtonHTMLAttributes<HTMLButtonElement>, VariantProps<typeof buttonVariants> {
  asChild?: boolean;
  loading?: boolean;
}

export const Button = React.forwardRef<HTMLButtonElement, ButtonProps>(
  (
    { className, variant, size, loading = false, asChild = false, disabled, children, ...props },
    ref,
  ) => {
    const Component = asChild ? Slot : 'button';
    const isDisabled = disabled || loading;

    if (asChild) {
      return (
        <Component
          className={cn(buttonVariants({ variant, size, loading }), className)}
          data-loading={loading ? 'true' : undefined}
          aria-disabled={isDisabled ? true : undefined}
          aria-busy={loading ? true : undefined}
          ref={ref}
          {...props}
        >
          {children}
        </Component>
      );
    }

    return (
      <Component
        className={cn(buttonVariants({ variant, size, loading }), className)}
        data-loading={loading ? 'true' : undefined}
        aria-busy={loading ? true : undefined}
        disabled={isDisabled}
        ref={ref}
        {...props}
      >
        {loading ? (
          <span
            aria-hidden="true"
            className="size-4 animate-spin rounded-pill border-2 border-current 
            border-t-transparent [animation-duration:var(--duration-slow)] 
            [animation-timing-function:var(--easing-standard)]"
          />
        ) : null}
        <span>{children}</span>
      </Component>
    );
  },
);

Button.displayName = 'Button';

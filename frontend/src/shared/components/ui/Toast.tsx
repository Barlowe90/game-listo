import type { HTMLAttributes, ReactNode } from 'react';
import { cva, type VariantProps } from 'class-variance-authority';
import { cn } from '@/lib/cn';

const toastVariants = cva(
  'flex items-start gap-3 rounded-lg border px-4 py-3 shadow-surface transition-[background-color,border-color,color,box-shadow] duration-[var(--duration-fast)] ease-[var(--easing-standard)]',
  {
    variants: {
      variant: {
        success: 'border-success bg-success-soft text-success',
        error: 'border-error bg-error-soft text-error',
      },
    },
    defaultVariants: {
      variant: 'success',
    },
  },
);

export interface ToastProps
  extends Omit<HTMLAttributes<HTMLDivElement>, 'title'>,
    VariantProps<typeof toastVariants> {
  title?: ReactNode;
  description?: ReactNode;
}

export function Toast({ className, title, description, variant, children, ...props }: ToastProps) {
  const isError = variant === 'error';

  return (
    <div
      role={isError ? 'alert' : 'status'}
      aria-live={isError ? 'assertive' : 'polite'}
      className={cn(toastVariants({ variant }), className)}
      {...props}
    >
      <span aria-hidden="true" className="mt-1 size-2 rounded-pill bg-current" />
      <div className="grid gap-1">
        {title ? <p className="text-sm font-semibold">{title}</p> : null}
        {description ? <p className="text-sm leading-relaxed">{description}</p> : children}
      </div>
    </div>
  );
}

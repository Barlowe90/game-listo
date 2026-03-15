import * as React from 'react';
import type { HTMLAttributes, ReactNode } from 'react';
import { cn } from '@/lib/cn';

export interface FieldProps extends HTMLAttributes<HTMLDivElement> {
  label: ReactNode;
  htmlFor?: string;
  description?: ReactNode;
  error?: ReactNode;
  required?: boolean;
  children: ReactNode;
}

export function Field({
  label,
  htmlFor,
  description,
  error,
  required = false,
  children,
  className,
  ...props
}: FieldProps) {
  const generatedId = React.useId();
  const controlId = htmlFor ?? `field-${generatedId}`;
  const descriptionId = description ? `${controlId}-description` : undefined;
  const errorId = error ? `${controlId}-error` : undefined;

  const control = React.isValidElement(children)
    ? React.cloneElement(children as React.ReactElement<Record<string, unknown>>, {
        id: (children.props as Record<string, unknown>).id ?? controlId,
        'aria-describedby': [
          (children.props as Record<string, unknown>)['aria-describedby'],
          descriptionId,
          errorId,
        ]
          .filter(Boolean)
          .join(' ') || undefined,
        'aria-invalid':
          (children.props as Record<string, unknown>)['aria-invalid'] ?? (error ? true : undefined),
      })
    : children;

  return (
    <div className={cn('grid gap-2', className)} {...props}>
      <label htmlFor={controlId} className="text-sm font-medium text-foreground">
        {label}
        {required ? <span className="ml-1 text-primary">*</span> : null}
      </label>
      {control}
      {error ? (
        <p id={errorId} className="text-sm font-medium text-error" role="alert">
          {error}
        </p>
      ) : description ? (
        <p id={descriptionId} className="text-sm text-muted-foreground">
          {description}
        </p>
      ) : null}
    </div>
  );
}

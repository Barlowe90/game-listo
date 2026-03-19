import type { ReactNode } from 'react';
import { Field, type FieldProps } from '@/shared/components/ui/Field';

export interface FormFieldProps extends Omit<FieldProps, 'description' | 'error'> {
  helpText?: ReactNode;
  errorMessage?: ReactNode;
}

export function FormField({ helpText, errorMessage, ...props }: FormFieldProps) {
  return <Field description={helpText} error={errorMessage} {...props} />;
}

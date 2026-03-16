'use client';

import { startTransition, useId } from 'react';
import { useRouter } from 'next/navigation';
import { cn } from '@/lib/cn';
import { Input } from '@/shared/components/ui/Input';

export interface SearchBarProps {
  className?: string;
  inputClassName?: string;
  label?: string;
  placeholder?: string;
  targetPath?: string;
  queryParam?: string;
  size?: 'sm' | 'md';
  onSearch?: () => void;
  defaultValue?: string;
}

export function SearchBar({
  className,
  inputClassName,
  label = 'Buscar videojuegos',
  placeholder = 'Buscar videojuegos',
  targetPath = '/catalogo',
  queryParam = 'q',
  size = 'md',
  onSearch,
  defaultValue = '',
}: SearchBarProps) {
  const inputId = useId();
  const router = useRouter();

  const handleSubmit: React.FormEventHandler<HTMLFormElement> = (event) => {
    event.preventDefault();

    const formData = new FormData(event.currentTarget);
    const rawValue = formData.get(queryParam);
    const trimmedQuery = typeof rawValue === 'string' ? rawValue.trim() : '';
    const params = new URLSearchParams();

    if (trimmedQuery) {
      params.set(queryParam, trimmedQuery);
    }

    const serializedParams = params.toString();
    const href = serializedParams ? `${targetPath}?${serializedParams}` : targetPath;

    startTransition(() => {
      router.push(href);
    });

    onSearch?.();
  };

  return (
    <form
      role="search"
      aria-label={label}
      onSubmit={handleSubmit}
      className={cn('flex w-full items-center gap-2', className)}
    >
      <label htmlFor={inputId} className="sr-only">
        {label}
      </label>
      <Input
        id={inputId}
        name={queryParam}
        type="search"
        size={size}
        defaultValue={defaultValue}
        placeholder={placeholder}
        className={cn('w-full', inputClassName)}
      />
    </form>
  );
}

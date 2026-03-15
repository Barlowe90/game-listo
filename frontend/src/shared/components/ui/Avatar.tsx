import type { HTMLAttributes } from 'react';
import { cva, type VariantProps } from 'class-variance-authority';
import { cn } from '@/lib/cn';

const avatarVariants = cva(
  'inline-flex shrink-0 items-center justify-center overflow-hidden rounded-pill border border-border bg-surface text-primary shadow-surface',
  {
    variants: {
      size: {
        sm: 'size-10 text-sm',
        md: 'size-12 text-base',
        lg: 'size-16 text-lg',
      },
    },
    defaultVariants: {
      size: 'md',
    },
  },
);

function getInitials(name?: string | null) {
  if (!name) {
    return '';
  }

  return name
    .trim()
    .split(/\s+/)
    .map((part) => part[0] ?? '')
    .join('')
    .slice(0, 2)
    .toUpperCase();
}

export interface AvatarProps
  extends Omit<HTMLAttributes<HTMLSpanElement>, 'children'>,
    VariantProps<typeof avatarVariants> {
  src?: string | null;
  name?: string | null;
  alt?: string;
  fallback?: string;
}

export function Avatar({ className, size, src, name, alt, fallback, ...props }: AvatarProps) {
  const label = alt ?? name ?? 'Avatar';
  const initials = fallback ?? getInitials(name);

  if (src) {
    return (
      <span className={cn(avatarVariants({ size }), className)} {...props}>
        {/* eslint-disable-next-line @next/next/no-img-element */}
        <img src={src} alt={label} className="size-full object-cover" />
      </span>
    );
  }

  return (
    <span
      className={cn(avatarVariants({ size }), className)}
      role="img"
      aria-label={label}
      {...props}
    >
      {initials ? (
        <span aria-hidden="true" className="font-semibold">
          {initials}
        </span>
      ) : (
        <svg
          aria-hidden="true"
          viewBox="0 0 24 24"
          className="h-1/2 w-1/2 fill-current"
          focusable="false"
        >
          <path d="M12 12a4 4 0 1 0-4-4 4 4 0 0 0 4 4Zm0 2c-3.33 0-6 1.79-6 4v1h12v-1c0-2.21-2.67-4-6-4Z" />
        </svg>
      )}
    </span>
  );
}

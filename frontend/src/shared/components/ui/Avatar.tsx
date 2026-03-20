import type { HTMLAttributes } from 'react';
import { cva, type VariantProps } from 'class-variance-authority';
import { UserRound } from 'lucide-react';
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
  extends Omit<HTMLAttributes<HTMLSpanElement>, 'children'>, VariantProps<typeof avatarVariants> {
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
        <UserRound aria-hidden="true" className="h-1/2 w-1/2" focusable="false" />
      )}
    </span>
  );
}

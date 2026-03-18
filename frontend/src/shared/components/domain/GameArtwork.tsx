import type { HTMLAttributes } from 'react';
import Image from 'next/image';
import { cva, type VariantProps } from 'class-variance-authority';
import { cn } from '@/lib/cn';

const gameArtworkVariants = cva(
  'relative overflow-hidden border border-border bg-[linear-gradient(140deg,var(--color-primary-soft)_0%,var(--color-surface)_100%)]',
  {
    variants: {
      aspect: {
        portrait: 'aspect-[3/4]',
        landscape: 'aspect-[16/9]',
      },
      radius: {
        md: 'rounded-xl',
        lg: 'rounded-[calc(var(--radius-xl)+0.5rem)]',
        xl: 'rounded-[calc(var(--radius-xl)+0.85rem)]',
        none: 'rounded-none border-0',
      },
    },
    defaultVariants: {
      aspect: 'portrait',
      radius: 'lg',
    },
  },
);

export interface GameArtworkProps
  extends Omit<HTMLAttributes<HTMLDivElement>, 'children'>,
    VariantProps<typeof gameArtworkVariants> {
  coverUrl: string | null;
  title: string;
  priority?: boolean;
  sizes?: string;
  imageClassName?: string;
}

export function GameArtwork({
  aspect,
  className,
  coverUrl,
  imageClassName,
  priority = false,
  radius,
  sizes = '(max-width: 768px) 100vw, 33vw',
  title,
  ...props
}: GameArtworkProps) {
  return (
    <div className={cn(gameArtworkVariants({ aspect, radius }), className)} {...props}>
      {coverUrl ? (
        <Image
          src={coverUrl}
          alt={`Portada de ${title}`}
          fill
          priority={priority}
          sizes={sizes}
          className={cn('object-cover', imageClassName)}
        />
      ) : (
        <div className="grid h-full place-items-center p-6 text-center">
          <div className="grid gap-2">
            <span className="text-xs font-semibold tracking-[0.18em] text-primary uppercase">
              GameListo
            </span>
            <strong className="text-xl font-semibold tracking-tight text-foreground">
              {title}
            </strong>
          </div>
        </div>
      )}
    </div>
  );
}

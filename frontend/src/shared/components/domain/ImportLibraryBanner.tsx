import type { HTMLAttributes, ReactNode } from 'react';
import { cn } from '@/lib/cn';
import { PlatformChip } from '@/shared/components/domain/TagList';
import { Card } from '@/shared/components/ui/Card';

export interface ImportLibraryBannerProps extends Omit<HTMLAttributes<HTMLDivElement>, 'title'> {
  action?: ReactNode;
  description: ReactNode;
  platforms?: string[];
  title: ReactNode;
}

export function ImportLibraryBanner({
  action,
  className,
  description,
  platforms = [],
  title,
  ...props
}: ImportLibraryBannerProps) {
  return (
    <Card
      variant="home"
      padding="lg"
      className={cn(
        'relative overflow-hidden rounded-[calc(var(--radius-xl)+1rem)]',
        className,
      )}
      {...props}
    >
      <div className="pointer-events-none absolute right-[-6rem] top-[-5rem] h-56 w-56 rounded-full bg-white/10 blur-3xl" />
      <div className="grid gap-6">
        <div className="grid max-w-3xl gap-4">
          <h2 className="text-3xl font-bold tracking-tight text-white lg:text-4xl">{title}</h2>
          <p className="max-w-2xl text-base leading-relaxed text-white">{description}</p>
        </div>

        {platforms.length ? (
          <div className="flex flex-wrap gap-2">
            {platforms.map((platform) => (
              <PlatformChip
                key={platform}
                className="border-white/15 bg-white/10 text-white hover:bg-white/20"
              >
                {platform}
              </PlatformChip>
            ))}
          </div>
        ) : null}

        {action ? <div className="flex flex-wrap gap-3">{action}</div> : null}
      </div>
    </Card>
  );
}

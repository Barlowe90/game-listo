import type { HTMLAttributes, ReactNode } from 'react';
import { cn } from '@/lib/cn';
import { GameArtwork } from '@/shared/components/domain/GameArtwork';
import { PlatformChip } from '@/shared/components/domain/TagList';
import { Badge } from '@/shared/components/ui/Badge';
import { Card } from '@/shared/components/ui/Card';

interface GameHeroBadge {
  label: ReactNode;
  variant?: 'neutral' | 'primary';
}

interface GameHeroDetail {
  label: ReactNode;
  value: ReactNode;
}

export interface GameHeroProps extends Omit<HTMLAttributes<HTMLDivElement>, 'title'> {
  actionBar?: ReactNode;
  badges?: GameHeroBadge[];
  collaborators?: string[];
  coverUrl: string | null;
  details?: GameHeroDetail[];
  platforms?: string[];
  studio: string;
  title: ReactNode;
}

export function GameHero({
  actionBar,
  badges = [],
  className,
  collaborators = [],
  coverUrl,
  details = [],
  platforms = [],
  studio,
  title,
  ...props
}: GameHeroProps) {
  return (
    <Card
      className={cn(
        'rounded-[calc(var(--radius-xl)+1rem)] border border-border bg-white/90 shadow-elevated backdrop-blur-sm',
        className,
      )}
      {...props}
    >
      <div className="grid gap-6 p-6 sm:p-8 lg:grid-cols-[220px_minmax(0,1fr)]">
        <GameArtwork
          coverUrl={coverUrl}
          title={typeof title === 'string' ? title : 'Videojuego'}
          priority
          sizes="(max-width: 768px) 80vw, (max-width: 1280px) 280px, 320px"
          radius="xl"
        />

        <div className="grid gap-6">
          {badges.length ? (
            <div className="flex flex-wrap items-center gap-2">
              {badges.map((badge, index) => (
                <Badge
                  key={`${badge.variant ?? 'neutral'}-${index}`}
                  variant={badge.variant}
                  className="px-3 py-1.5 text-sm"
                >
                  {badge.label}
                </Badge>
              ))}
            </div>
          ) : null}

          <div className="grid gap-2">
            <h1 className="text-4xl font-semibold tracking-tight text-foreground lg:text-5xl">
              {title}
            </h1>
            <p className="text-xl text-secondary">{studio}</p>
            {collaborators.length ? (
              <p className="text-sm leading-relaxed text-secondary">
                Con colaboracion de {collaborators.join(', ')}.
              </p>
            ) : null}
          </div>

          {actionBar}

          {details.length ? (
            <div className="grid gap-3 sm:grid-cols-2 xl:grid-cols-3">
              {details.map((detail, index) => (
                <div
                  key={index}
                  className="grid gap-1 rounded-[calc(var(--radius-xl)+0.1rem)] bg-background p-4"
                >
                  <span className="text-xs font-semibold tracking-[0.08em] text-primary uppercase">
                    {detail.label}
                  </span>
                  <span className="text-sm font-medium text-foreground">{detail.value}</span>
                </div>
              ))}
            </div>
          ) : null}

          <div className="grid gap-3">
            <span className="text-sm font-semibold tracking-[0.08em] text-primary uppercase">
              Plataformas
            </span>
            {platforms.length ? (
              <div className="flex flex-wrap gap-2">
                {platforms.map((platform) => (
                  <PlatformChip key={platform}>{platform}</PlatformChip>
                ))}
              </div>
            ) : (
              <p className="text-sm leading-relaxed text-secondary">
                Todavia no hay plataformas registradas para este juego.
              </p>
            )}
          </div>
        </div>
      </div>
    </Card>
  );
}

import type { HTMLAttributes, ReactNode } from 'react';
import Link from 'next/link';
import { cn } from '@/lib/cn';
import {
  AvailabilityMatrix,
  type AvailabilityMatrixValue,
} from '@/shared/components/domain/AvailabilityMatrix';
import { AvatarGroup, type AvatarGroupMember } from '@/shared/components/domain/AvatarGroup';
import { Badge } from '@/shared/components/ui/Badge';
import { Card } from '@/shared/components/ui/Card';

interface PublicationBadge {
  label: ReactNode;
  variant?: 'neutral' | 'primary';
}

interface PublicationGameLink {
  href?: string;
  title: ReactNode;
}

export interface PublicationCardProps extends Omit<HTMLAttributes<HTMLDivElement>, 'title'> {
  availability: AvailabilityMatrixValue;
  badges?: PublicationBadge[];
  cta?: ReactNode;
  description?: ReactNode;
  game?: PublicationGameLink;
  participants?: AvatarGroupMember[];
  secondaryAction?: ReactNode;
  title: ReactNode;
}

export function PublicationCard({
  availability,
  badges = [],
  className,
  cta,
  description,
  game,
  participants = [],
  secondaryAction,
  title,
  ...props
}: PublicationCardProps) {
  return (
    <Card
      className={cn(
        'rounded-[calc(var(--radius-xl)+0.5rem)] border border-border bg-white/90 shadow-elevated backdrop-blur-sm',
        className,
      )}
      {...props}
    >
      <div className="grid gap-6 p-6">
        <div className="grid gap-3">
          {game ? (
            game.href ? (
              <Link
                href={game.href}
                className="text-sm font-semibold tracking-[0.08em] text-primary uppercase hover:underline"
              >
                {game.title}
              </Link>
            ) : (
              <span className="text-sm font-semibold tracking-[0.08em] text-primary uppercase">
                {game.title}
              </span>
            )
          ) : null}

          <div className="flex flex-wrap items-start justify-between gap-4">
            <div className="grid gap-2">
              <h3 className="text-xl font-semibold tracking-tight text-foreground">{title}</h3>
              {description ? (
                <p className="text-sm leading-relaxed text-secondary">{description}</p>
              ) : null}
            </div>
            {cta ? <div className="flex shrink-0 items-center gap-3">{cta}</div> : null}
          </div>

          {badges.length ? (
            <div className="flex flex-wrap gap-2">
              {badges.map((badge, index) => (
                <Badge
                  key={`${index}-${badge.variant ?? 'neutral'}`}
                  variant={badge.variant}
                  className="px-3 py-1.5 text-sm"
                >
                  {badge.label}
                </Badge>
              ))}
            </div>
          ) : null}
        </div>

        <AvailabilityMatrix availability={availability} compact />

        <div className="flex flex-wrap items-center justify-between gap-3 border-t border-border pt-4">
          {participants.length ? (
            <AvatarGroup members={participants} />
          ) : (
            <span className="text-sm leading-relaxed text-secondary">
              Todavia no hay jugadores unidos.
            </span>
          )}
          {secondaryAction ? <div className="flex flex-wrap gap-3">{secondaryAction}</div> : null}
        </div>
      </div>
    </Card>
  );
}

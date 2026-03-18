import type { HTMLAttributes } from 'react';
import Link from 'next/link';
import type { Game } from '@/features/catalogo/model/catalog.types';
import { cn } from '@/lib/cn';
import { GameArtwork } from '@/shared/components/domain/GameArtwork';
import {
  getGamePrimaryBadge,
  getGamePrimaryStudio,
  getGameShortDescription,
} from '@/shared/components/domain/game-domain.utils';
import { PlatformChip, TagList } from '@/shared/components/domain/TagList';
import {
  Card,
  CardBody,
  CardFooter,
  CardHeader,
  CardTitle,
} from '@/shared/components/ui/Card';
import { Badge } from '@/shared/components/ui/Badge';

export interface GameCardProps extends Omit<HTMLAttributes<HTMLDivElement>, 'title'> {
  ctaLabel?: string;
  description?: string;
  game: Game;
  href?: string;
}

function GameCardContent({
  ctaLabel,
  description,
  game,
}: {
  ctaLabel: string;
  description?: string;
  game: Game;
}) {
  const gameDescription = description ?? getGameShortDescription(game);
  const modes = game.gameModes.slice(0, 3);

  return (
    <>
      <div className="relative">
        <GameArtwork
          aspect="landscape"
          radius="none"
          coverUrl={game.coverUrl}
          title={game.name}
          className="aspect-[16/9]"
          imageClassName="transition-transform duration-[var(--duration-normal)] ease-[var(--easing-standard)] group-hover:scale-[1.03]"
        />
        <div className="pointer-events-none absolute inset-0 bg-[linear-gradient(180deg,transparent_45%,rgba(15,23,42,0.26)_100%)]" />
      </div>

      <CardHeader className="gap-3">
        <div className="flex flex-wrap items-center gap-2">
          <Badge variant="primary" className="px-3 py-1.5 text-sm">
            {getGamePrimaryBadge(game)}
          </Badge>
          {game.platforms.slice(0, 2).map((platform) => (
            <PlatformChip key={platform}>{platform}</PlatformChip>
          ))}
        </div>

        <div className="grid gap-1">
          <CardTitle className="text-xl">{game.name}</CardTitle>
          <p className="text-sm leading-relaxed text-secondary">{getGamePrimaryStudio(game)}</p>
        </div>
      </CardHeader>

      <CardBody className="gap-4 pt-4">
        <p className="text-sm leading-relaxed text-secondary">{gameDescription}</p>
        <TagList items={modes} tone="tag" />
      </CardBody>

      <CardFooter>
        <span className="text-sm font-semibold text-primary">{ctaLabel}</span>
      </CardFooter>
    </>
  );
}

export function GameCard({
  className,
  ctaLabel = 'Ver ficha completa',
  description,
  game,
  href,
  ...props
}: GameCardProps) {
  const cardClassName = cn(
    'group rounded-[calc(var(--radius-xl)+0.75rem)] border border-border bg-white/90 shadow-elevated backdrop-blur-sm',
    className,
  );

  if (href) {
    return (
      <Card asChild variant="clickable" className={cardClassName} {...props}>
        <Link href={href} className="grid h-full">
          <GameCardContent ctaLabel={ctaLabel} description={description} game={game} />
        </Link>
      </Card>
    );
  }

  return (
    <Card variant="clickable" className={cn(cardClassName, 'grid h-full')} {...props}>
      <GameCardContent ctaLabel={ctaLabel} description={description} game={game} />
    </Card>
  );
}

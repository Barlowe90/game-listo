import type { HTMLAttributes } from 'react';
import Link from 'next/link';
import type { Game } from '@/features/catalogo/model/catalog.types';
import { cn } from '@/lib/cn';
import { GameArtwork } from '@/shared/components/domain/GameArtwork';
import { PlatformChip, TagList } from '@/shared/components/domain/TagList';
import { Card, CardBody, CardHeader, CardTitle } from '@/shared/components/ui/Card';

export interface GameCardProps extends Omit<HTMLAttributes<HTMLDivElement>, 'title'> {
  game: Game;
  href?: string;
}

function GameCardContent({ game }: { game: Game }) {
  const modes = game.gameModes.slice(0, 3);
  const platforms = game.platforms.slice(0, 3);

  return (
    <>
      <div className="relative">
        <GameArtwork
          aspect="portrait"
          radius="none"
          coverUrl={game.coverUrl}
          title={game.name}
          sizes="(max-width: 768px) 50vw, (max-width: 1280px) 25vw, 20vw"
          className="aspect-[4/5] bg-[linear-gradient(160deg,#edf2ff_0%,#f8fafc_100%)]"
          imageClassName="transition-transform duration-[var(--duration-normal)] ease-[var(--easing-standard)] group-hover:scale-[1.03]"
        />
        <div className="pointer-events-none absolute inset-x-0 bottom-0 h-20 bg-[linear-gradient(180deg,transparent_0%,rgba(15,23,42,0.16)_100%)]" />
      </div>

      <CardHeader className="gap-3 p-4 pb-0">
        <div className="flex flex-wrap gap-2">
          {platforms.map((platform) => (
            <PlatformChip key={platform} className="px-2.5 py-1 text-xs">
              {platform}
            </PlatformChip>
          ))}
        </div>

        <CardTitle className="line-clamp-2 text-base leading-snug">{game.name}</CardTitle>
      </CardHeader>

      <CardBody className="gap-3 p-4 pt-3">
        <TagList
          items={modes}
          tone="tag"
          emptyLabel="Sin modos registrados"
          className="gap-2"
        />
      </CardBody>
    </>
  );
}

export function GameCard({ className, game, href, ...props }: GameCardProps) {
  const cardClassName = cn(
    'group rounded-[1.35rem] border border-border bg-white/92 shadow-[0_18px_40px_rgba(15,23,42,0.08)] backdrop-blur-sm',
    className,
  );

  if (href) {
    return (
      <Card asChild variant="clickable" className={cardClassName} {...props}>
        <Link href={href} className="grid h-full">
          <GameCardContent game={game} />
        </Link>
      </Card>
    );
  }

  return (
    <Card variant="clickable" className={cn(cardClassName, 'grid h-full')} {...props}>
      <GameCardContent game={game} />
    </Card>
  );
}

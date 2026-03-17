import type { ReactNode, SVGProps } from 'react';
import Image from 'next/image';
import Link from 'next/link';
import { notFound } from 'next/navigation';
import {
  getCatalogGames,
  getGameById,
  getGameDetailMedia,
  getGamesByIds,
} from '@/features/catalogo/api/catalogApi';
import type { Game } from '@/features/catalogo/model/catalog.types';
import { cn } from '@/lib/cn';
import { PageSection } from '@/shared/components/layout/PageSection';
import { Button } from '@/shared/components/ui/Button';
import { Card } from '@/shared/components/ui/Card';
import { EmptyState } from '@/shared/components/ui/EmptyState';
import { Tabs, TabsContent, TabsList, TabsTrigger } from '@/shared/components/ui/Tabs';

const MAX_RELATED_LINKS = 6;
const MAX_KEYWORDS = 18;

const LIBRARY_ACTIONS = [
  { label: 'Deseado', icon: HeartIcon },
  { label: 'Pendiente', icon: ClockIcon },
  { label: 'Jugando', icon: GamepadIcon },
  { label: 'Completado', icon: CheckIcon },
  { label: 'Abandonado', icon: SlashCircleIcon },
] as const;

interface RelatedEntry {
  id: number;
  href: string;
  label: string;
}

function SurfaceCard({ children, className }: { children: ReactNode; className?: string }) {
  return (
    <Card
      className={cn(
        'rounded-[2rem] border border-[#e2e6fb] bg-white/90 shadow-[0_28px_72px_rgba(73,80,137,0.12)] backdrop-blur-sm',
        className,
      )}
    >
      {children}
    </Card>
  );
}

function DetailPanel({
  children,
  className,
  title,
}: {
  children: ReactNode;
  className?: string;
  title: string;
}) {
  return (
    <SurfaceCard className={className}>
      <div className="grid gap-4 p-6">
        <h2 className="text-lg font-semibold tracking-tight text-foreground">{title}</h2>
        {children}
      </div>
    </SurfaceCard>
  );
}

function EmptyCopy({ children }: { children: ReactNode }) {
  return <p className="text-sm leading-relaxed text-secondary">{children}</p>;
}

function QuickActionPill({
  icon: Icon,
  label,
}: {
  icon: (props: SVGProps<SVGSVGElement>) => ReactNode;
  label: string;
}) {
  return (
    <div className="inline-flex min-h-[84px] min-w-[92px] flex-col justify-center gap-2 rounded-[1.25rem] border border-[#d7dcfa] bg-[#eef1ff] px-4 py-3 text-left text-sm font-semibold text-foreground">
      <Icon className="size-5 text-primary" aria-hidden="true" />
      <span>{label}</span>
    </div>
  );
}

function MetaStat({ label, value }: { label: string; value: string }) {
  return (
    <div className="grid gap-1 rounded-[1.25rem] bg-[#f6f7ff] p-4">
      <span className="text-xs font-semibold tracking-[0.08em] text-primary uppercase">
        {label}
      </span>
      <span className="text-sm font-medium text-foreground">{value}</span>
    </div>
  );
}

function PillList({ emptyLabel, items }: { emptyLabel: string; items: string[] }) {
  if (!items.length) {
    return <EmptyCopy>{emptyLabel}</EmptyCopy>;
  }

  return (
    <div className="flex flex-wrap gap-2">
      {items.map((item) => (
        <span
          key={item}
          className="inline-flex rounded-pill border border-[#dce1ff] bg-[#f6f7ff] px-3 py-1.5 text-sm text-foreground"
        >
          {item}
        </span>
      ))}
    </div>
  );
}

function TextList({ emptyLabel, items }: { emptyLabel: string; items: string[] }) {
  if (!items.length) {
    return <EmptyCopy>{emptyLabel}</EmptyCopy>;
  }

  return (
    <ul className="grid gap-2 text-sm leading-relaxed text-secondary">
      {items.map((item) => (
        <li key={item}>{item}</li>
      ))}
    </ul>
  );
}

function ExternalLinksList({ links }: { links: string[] }) {
  if (!links.length) {
    return <EmptyCopy>No hay enlaces externos registrados para este titulo.</EmptyCopy>;
  }

  return (
    <ul className="grid gap-2 text-sm leading-relaxed text-secondary">
      {links.slice(0, MAX_RELATED_LINKS).map((link) => (
        <li key={link}>
          <a
            href={link}
            target="_blank"
            rel="noreferrer"
            className="break-all text-primary underline-offset-4 hover:underline"
          >
            {link}
          </a>
        </li>
      ))}
      {links.length > MAX_RELATED_LINKS ? (
        <li className="text-secondary">+{links.length - MAX_RELATED_LINKS} enlaces mas</li>
      ) : null}
    </ul>
  );
}

function RelatedGameList({
  emptyLabel,
  gameIds,
  relatedGames,
}: {
  emptyLabel: string;
  gameIds: number[];
  relatedGames: Map<number, Game>;
}) {
  const { entries, remainingCount } = buildRelatedEntries(gameIds, relatedGames);

  if (!entries.length) {
    return <EmptyCopy>{emptyLabel}</EmptyCopy>;
  }

  return (
    <div className="grid gap-3">
      <div className="flex flex-wrap gap-2">
        {entries.map((entry) => (
          <Link
            key={entry.id}
            href={entry.href}
            className="inline-flex rounded-pill border border-[#d8dcfa] bg-[#eef1ff] px-3 py-1.5 text-sm font-medium text-primary transition-colors hover:border-[#bec6f5] hover:bg-white"
          >
            {entry.label}
          </Link>
        ))}
      </div>

      {remainingCount ? (
        <p className="text-sm text-secondary">+{remainingCount} referencias mas</p>
      ) : null}
    </div>
  );
}

function ParentGameLink({
  gameId,
  relatedGames,
}: {
  gameId: number | null;
  relatedGames: Map<number, Game>;
}) {
  if (!gameId) {
    return <EmptyCopy>Este juego no depende de un parent game registrado.</EmptyCopy>;
  }

  const entry = buildSingleRelatedEntry(gameId, relatedGames);

  return (
    <Link
      href={entry.href}
      className="inline-flex w-fit rounded-pill border border-[#d8dcfa] bg-[#eef1ff] px-3 py-1.5 text-sm font-medium text-primary transition-colors hover:border-[#bec6f5] hover:bg-white"
    >
      {entry.label}
    </Link>
  );
}

function HeroCover({ coverUrl, name }: { coverUrl: string | null; name: string }) {
  return (
    <div className="relative aspect-[3/4] overflow-hidden rounded-[1.65rem] border border-white bg-[linear-gradient(140deg,#dfe3ff_0%,#f7f8ff_100%)] shadow-inner">
      {coverUrl ? (
        <Image
          src={coverUrl}
          alt={`Portada de ${name}`}
          fill
          priority
          sizes="(max-width: 768px) 80vw, (max-width: 1280px) 280px, 320px"
          className="object-cover"
        />
      ) : (
        <div className="grid h-full place-items-center p-6 text-center">
          <div className="grid gap-2">
            <span className="text-xs font-semibold tracking-[0.18em] text-primary uppercase">
              GameListo
            </span>
            <strong className="text-2xl font-semibold tracking-tight text-foreground">
              {name}
            </strong>
          </div>
        </div>
      )}
    </div>
  );
}

function VideoEmbedCard({ index, videoUrl }: { index: number; videoUrl: string }) {
  const embedUrl = getYouTubeEmbedUrl(videoUrl);

  return (
    <SurfaceCard>
      <div className="grid gap-4 p-4">
        <div className="overflow-hidden rounded-[1.5rem] border border-[#e2e6fb]">
          <div className="aspect-video">
            <iframe
              title={`Video ${index + 1}`}
              src={embedUrl}
              className="h-full w-full"
              allow="accelerometer; autoplay; clipboard-write; encrypted-media; gyroscope; picture-in-picture"
              allowFullScreen
            />
          </div>
        </div>
        <a
          href={videoUrl}
          target="_blank"
          rel="noreferrer"
          className="text-sm font-medium text-primary underline-offset-4 hover:underline"
        >
          Abrir en YouTube
        </a>
      </div>
    </SurfaceCard>
  );
}

function ScreenshotCard({ index, screenshotUrl }: { index: number; screenshotUrl: string }) {
  return (
    <SurfaceCard>
      <div className="relative aspect-video overflow-hidden rounded-[1.9rem]">
        <Image
          src={screenshotUrl}
          alt={`Screenshot ${index + 1}`}
          fill
          sizes="(max-width: 768px) 100vw, (max-width: 1280px) 50vw, 33vw"
          className="object-cover"
        />
      </div>
    </SurfaceCard>
  );
}

function ArrowLeftIcon(props: SVGProps<SVGSVGElement>) {
  return (
    <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="1.8" {...props}>
      <path strokeLinecap="round" strokeLinejoin="round" d="M15 18l-6-6 6-6" />
    </svg>
  );
}

function HeartIcon(props: SVGProps<SVGSVGElement>) {
  return (
    <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="1.8" {...props}>
      <path
        strokeLinecap="round"
        strokeLinejoin="round"
        d="M12 20.5l-1.3-1.18C5.4 14.53 2 11.46 2 7.7A4.7 4.7 0 016.72 3 5.1 5.1 0 0112 6.09 5.1 5.1 0 0117.28 3 4.7 4.7 0 0122 7.7c0 3.76-3.4 6.83-8.7 11.62L12 20.5z"
      />
    </svg>
  );
}

function ClockIcon(props: SVGProps<SVGSVGElement>) {
  return (
    <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="1.8" {...props}>
      <circle cx="12" cy="12" r="9" />
      <path strokeLinecap="round" strokeLinejoin="round" d="M12 7v5l3 2" />
    </svg>
  );
}

function GamepadIcon(props: SVGProps<SVGSVGElement>) {
  return (
    <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="1.8" {...props}>
      <path
        strokeLinecap="round"
        strokeLinejoin="round"
        d="M7 9h10a4 4 0 013.9 4.9l-.56 2.47A2 2 0 0118.4 18H16a2 2 0 01-1.79-1.11l-.42-.84a2 2 0 00-1.79-1.11 2 2 0 00-1.79 1.11l-.42.84A2 2 0 018 18H5.6a2 2 0 01-1.95-1.54l-.55-2.47A4 4 0 017 9z"
      />
      <path strokeLinecap="round" strokeLinejoin="round" d="M8 12v3M6.5 13.5h3" />
      <circle cx="16.5" cy="12.5" r=".75" fill="currentColor" stroke="none" />
      <circle cx="18.5" cy="14.5" r=".75" fill="currentColor" stroke="none" />
    </svg>
  );
}

function CheckIcon(props: SVGProps<SVGSVGElement>) {
  return (
    <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="1.8" {...props}>
      <path strokeLinecap="round" strokeLinejoin="round" d="M5 12.5l4.2 4.2L19 7" />
    </svg>
  );
}

function SlashCircleIcon(props: SVGProps<SVGSVGElement>) {
  return (
    <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="1.8" {...props}>
      <circle cx="12" cy="12" r="9" />
      <path strokeLinecap="round" strokeLinejoin="round" d="M8 16L16 8" />
    </svg>
  );
}

function PlusIcon(props: SVGProps<SVGSVGElement>) {
  return (
    <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="1.8" {...props}>
      <path strokeLinecap="round" strokeLinejoin="round" d="M12 5v14M5 12h14" />
    </svg>
  );
}

async function resolveGameId(rawId: string) {
  if (rawId === 'demo') {
    const games = await getCatalogGames();
    return games[0]?.id ?? null;
  }

  const parsedId = Number(rawId);

  if (!Number.isInteger(parsedId) || parsedId <= 0) {
    return null;
  }

  return parsedId;
}

function uniqueStrings(values: Array<string | null | undefined>) {
  const seen = new Set<string>();
  const items: string[] = [];

  for (const value of values) {
    const trimmedValue = value?.trim();

    if (!trimmedValue) {
      continue;
    }

    const normalizedValue = trimmedValue.toLowerCase();

    if (seen.has(normalizedValue)) {
      continue;
    }

    seen.add(normalizedValue);
    items.push(trimmedValue);
  }

  return items;
}

function formatLabel(value: string | null | undefined) {
  if (!value) {
    return null;
  }

  const normalizedValue = value.replace(/_/g, ' ').trim();

  if (!normalizedValue) {
    return null;
  }

  return normalizedValue.charAt(0).toUpperCase() + normalizedValue.slice(1);
}

function getPrimaryStudio(game: Game) {
  return game.involvedCompanies[0] ?? 'Estudio no especificado';
}

function buildHeroTags(game: Game) {
  return uniqueStrings([
    ...game.genres,
    ...game.playerPerspectives,
    ...game.gameModes,
    ...game.themes,
  ]).slice(0, 12);
}

function buildAdditionalTags(game: Game) {
  return uniqueStrings(game.keywords).slice(0, MAX_KEYWORDS);
}

function buildRelatedEntries(gameIds: number[], relatedGames: Map<number, Game>) {
  const uniqueIds = [...new Set(gameIds)];
  const entries = uniqueIds.slice(0, MAX_RELATED_LINKS).map((gameId) => ({
    id: gameId,
    href: `/videojuego/${gameId}`,
    label: relatedGames.get(gameId)?.name ?? `Juego #${gameId}`,
  }));

  return {
    entries,
    remainingCount: Math.max(uniqueIds.length - MAX_RELATED_LINKS, 0),
  };
}

function buildSingleRelatedEntry(gameId: number, relatedGames: Map<number, Game>): RelatedEntry {
  return {
    id: gameId,
    href: `/videojuego/${gameId}`,
    label: relatedGames.get(gameId)?.name ?? `Juego #${gameId}`,
  };
}

function getYouTubeEmbedUrl(videoUrl: string) {
  try {
    const url = new URL(videoUrl);
    const searchVideoId = url.searchParams.get('v');

    if (searchVideoId) {
      return `https://www.youtube-nocookie.com/embed/${searchVideoId}`;
    }

    if (url.hostname.includes('youtu.be')) {
      const shortVideoId = url.pathname.replace(/\//g, '');

      if (shortVideoId) {
        return `https://www.youtube-nocookie.com/embed/${shortVideoId}`;
      }
    }

    return videoUrl;
  } catch {
    return videoUrl;
  }
}

export default async function VideojuegoPage({ params }: { params: Promise<{ id: string }> }) {
  const { id } = await params;
  const gameId = await resolveGameId(id);

  if (!gameId) {
    notFound();
  }

  const [game, media] = await Promise.all([getGameById(gameId), getGameDetailMedia(gameId)]);

  if (!game) {
    notFound();
  }

  const relatedIdsToResolve = [
    ...(game.parentGameId ? [game.parentGameId] : []),
    ...game.dlcIds.slice(0, MAX_RELATED_LINKS),
    ...game.expandedGames.slice(0, MAX_RELATED_LINKS),
    ...game.expansionIds.slice(0, MAX_RELATED_LINKS),
    ...game.remakeIds.slice(0, MAX_RELATED_LINKS),
    ...game.remasterIds.slice(0, MAX_RELATED_LINKS),
    ...game.similarGames.slice(0, MAX_RELATED_LINKS),
  ];

  const relatedGames = await getGamesByIds(relatedIdsToResolve);
  const heroTags = buildHeroTags(game);
  const additionalTags = buildAdditionalTags(game);
  const alternativeNames = uniqueStrings(game.alternativeNames);
  const platforms = uniqueStrings(game.platforms);
  const themes = uniqueStrings(game.themes);
  const genres = uniqueStrings(game.genres);
  const gameModes = uniqueStrings(game.gameModes);
  const perspectives = uniqueStrings(game.playerPerspectives);
  const franchises = uniqueStrings(game.franchises);
  const extraStudios = uniqueStrings(game.involvedCompanies).slice(1);
  const gameTypeLabel = formatLabel(game.gameType) ?? 'No definido';
  const gameStatusLabel = formatLabel(game.gameStatus) ?? 'No definido';

  return (
    <div className="relative overflow-hidden bg-[radial-gradient(circle_at_top_left,#f8f9ff_0%,#eef0ff_42%,#e7e7fb_100%)]">
      <div className="pointer-events-none absolute left-[-8rem] top-14 h-72 w-72 rounded-full bg-white/40 blur-3xl" />
      <div className="pointer-events-none absolute right-[-6rem] top-40 h-80 w-80 rounded-full bg-[#dfe3ff] blur-3xl" />

      <PageSection size="wide" className="relative z-10 py-10 lg:py-14">
        <Tabs defaultValue="sobre" className="grid gap-8">
          <div className="grid gap-6 xl:grid-cols-[minmax(0,1.02fr)_minmax(0,0.98fr)]">
            <SurfaceCard>
              <div className="grid gap-6 p-6 sm:p-8">
                <div className="grid gap-6 lg:grid-cols-[220px_minmax(0,1fr)]">
                  <HeroCover coverUrl={game.coverUrl} name={game.name} />

                  <div className="grid gap-6">
                    <div className="grid gap-3">
                      <div className="flex flex-wrap items-center gap-2">
                        <span className="inline-flex rounded-pill bg-[#eef1ff] px-3 py-1 text-sm font-medium text-primary">
                          {gameTypeLabel}
                        </span>
                        <span className="inline-flex rounded-pill bg-[#f6f7ff] px-3 py-1 text-sm font-medium text-foreground">
                          {gameStatusLabel}
                        </span>
                      </div>

                      <div className="grid gap-2">
                        <h1 className="text-4xl font-semibold tracking-tight text-foreground lg:text-5xl">
                          {game.name}
                        </h1>
                        <p className="text-xl text-secondary">{getPrimaryStudio(game)}</p>
                        {extraStudios.length ? (
                          <p className="text-sm leading-relaxed text-secondary">
                            Con colaboracion de {extraStudios.join(', ')}.
                          </p>
                        ) : null}
                      </div>
                    </div>

                    <div className="flex flex-wrap gap-3">
                      {LIBRARY_ACTIONS.map((action) => (
                        <QuickActionPill
                          key={action.label}
                          label={action.label}
                          icon={action.icon}
                        />
                      ))}

                      <Button
                        asChild
                        variant="secondary"
                        className="rounded-[1.25rem] border-[#d7dcfa] bg-[#eef1ff] px-5 text-foreground shadow-none hover:border-[#bec6f5] hover:bg-white"
                      >
                        <Link href="/login">
                          <PlusIcon className="size-4" aria-hidden="true" />
                          Anadir a lista
                        </Link>
                      </Button>
                    </div>

                    <div className="grid gap-3">
                      <span className="text-sm font-semibold tracking-[0.08em] text-primary uppercase">
                        Plataformas
                      </span>
                      <PillList
                        items={platforms}
                        emptyLabel="Todavia no hay plataformas registradas para este juego."
                      />
                    </div>
                  </div>
                </div>

                <TabsList className="border-[#d7dcfa] bg-[#eef1ff]/90">
                  <TabsTrigger value="sobre">Sobre</TabsTrigger>
                  <TabsTrigger value="publicaciones">Publicaciones</TabsTrigger>
                  <TabsTrigger value="videos">Videos</TabsTrigger>
                  <TabsTrigger value="screenshots">Screenshots</TabsTrigger>
                </TabsList>
              </div>
            </SurfaceCard>

            <SurfaceCard>
              <div className="grid gap-6 p-6 sm:p-8">
                <div className="grid gap-3">
                  <span className="text-sm font-semibold tracking-[0.08em] text-primary uppercase">
                    Genero y estilo
                  </span>
                  <div className="flex flex-wrap gap-3">
                    {heroTags.length ? (
                      heroTags.map((tag) => (
                        <span
                          key={tag}
                          className="inline-flex rounded-[1rem] bg-[#dfe3ff] px-4 py-2 text-sm font-medium text-foreground"
                        >
                          {tag}
                        </span>
                      ))
                    ) : (
                      <EmptyCopy>
                        No hay descriptores principales disponibles para esta ficha.
                      </EmptyCopy>
                    )}
                  </div>
                </div>

                <div className="grid gap-3">
                  <span className="text-sm font-semibold tracking-[0.08em] text-primary uppercase">
                    Tags adicionales
                  </span>
                  {additionalTags.length ? (
                    <div className="flex flex-wrap gap-x-3 gap-y-2">
                      {additionalTags.map((tag) => (
                        <Link
                          key={tag}
                          href={`/catalogo?q=${encodeURIComponent(tag)}`}
                          className="text-sm text-primary underline-offset-4 hover:underline"
                        >
                          {tag}
                        </Link>
                      ))}
                    </div>
                  ) : (
                    <EmptyCopy>
                      Este juego aun no tiene keywords adicionales en el backend.
                    </EmptyCopy>
                  )}
                </div>

                <div className="grid gap-3 border-t border-[#e7eafc] pt-4 sm:grid-cols-3">
                  <MetaStat label="Tipo" value={gameTypeLabel} />
                  <MetaStat label="Estado" value={gameStatusLabel} />
                  <MetaStat label="Franquicia" value={franchises[0] ?? 'Titulo independiente'} />
                </div>
              </div>
            </SurfaceCard>
          </div>

          <TabsContent value="sobre" className="grid gap-5 md:grid-cols-2 xl:grid-cols-3">
            <DetailPanel title="Descripcion general" className="md:col-span-2">
              <p className="text-[15px] leading-7 text-secondary">
                {game.summary?.trim() ??
                  'Todavia no hay una descripcion general disponible para este juego.'}
              </p>
            </DetailPanel>

            <DetailPanel title="Titulos alternativos">
              <TextList
                items={alternativeNames}
                emptyLabel="No se han registrado nombres alternativos para este titulo."
              />
            </DetailPanel>

            <DetailPanel title="Estilo y modos">
              <div className="grid gap-4">
                <div className="grid gap-2">
                  <span className="text-sm font-semibold text-foreground">Generos</span>
                  <PillList items={genres} emptyLabel="No hay generos asociados a este juego." />
                </div>
                <div className="grid gap-2">
                  <span className="text-sm font-semibold text-foreground">Perspectivas</span>
                  <PillList
                    items={perspectives}
                    emptyLabel="No hay perspectivas de juego registradas."
                  />
                </div>
                <div className="grid gap-2">
                  <span className="text-sm font-semibold text-foreground">Modos</span>
                  <PillList items={gameModes} emptyLabel="No hay modos de juego asociados." />
                </div>
              </div>
            </DetailPanel>

            <DetailPanel title="DLC">
              <RelatedGameList
                gameIds={game.dlcIds}
                relatedGames={relatedGames}
                emptyLabel="No hay DLC enlazados a este juego."
              />
            </DetailPanel>

            <DetailPanel title="Expansiones">
              <RelatedGameList
                gameIds={game.expansionIds}
                relatedGames={relatedGames}
                emptyLabel="No hay expansiones registradas."
              />
            </DetailPanel>

            <DetailPanel title="Expanded games">
              <RelatedGameList
                gameIds={game.expandedGames}
                relatedGames={relatedGames}
                emptyLabel="No hay expanded games relacionados."
              />
            </DetailPanel>

            <DetailPanel title="Remakes">
              <RelatedGameList
                gameIds={game.remakeIds}
                relatedGames={relatedGames}
                emptyLabel="No hay remakes enlazados a este titulo."
              />
            </DetailPanel>

            <DetailPanel title="Remasters">
              <RelatedGameList
                gameIds={game.remasterIds}
                relatedGames={relatedGames}
                emptyLabel="No hay remasters enlazados a este titulo."
              />
            </DetailPanel>

            <DetailPanel title="Juegos similares" className="md:col-span-2">
              <RelatedGameList
                gameIds={game.similarGames}
                relatedGames={relatedGames}
                emptyLabel="No hay juegos similares asociados todavia."
              />
            </DetailPanel>

            <DetailPanel title="Franquicias">
              <PillList
                items={franchises}
                emptyLabel="No se han asociado franquicias a este juego."
              />
            </DetailPanel>

            <DetailPanel title="Tematicas">
              <PillList items={themes} emptyLabel="No hay tematicas registradas." />
            </DetailPanel>

            <DetailPanel title="External games">
              <ExternalLinksList links={game.externalGames} />
            </DetailPanel>

            <DetailPanel title="Parent game">
              <ParentGameLink gameId={game.parentGameId} relatedGames={relatedGames} />
            </DetailPanel>
          </TabsContent>

          <TabsContent value="publicaciones">
            <EmptyState
              title="Todavia no hay publicaciones conectadas a esta ficha"
              description="La estructura ya esta preparada para que la comunidad pueda anadir posts, reseñas o busqueda de grupo sin rehacer el layout."
              action={
                <Button asChild variant="secondary">
                  <Link href="/login">Iniciar sesion para participar</Link>
                </Button>
              }
            />
          </TabsContent>

          <TabsContent value="videos">
            {media?.videos?.length ? (
              <div className="grid gap-5 md:grid-cols-2 xl:grid-cols-3">
                {media.videos.map((videoUrl, index) => (
                  <VideoEmbedCard key={`${videoUrl}-${index}`} index={index} videoUrl={videoUrl} />
                ))}
              </div>
            ) : (
              <EmptyState
                title="No hay videos disponibles por ahora"
                description="El backend del detalle aun no devuelve videos para este juego o la ficha todavia no tiene material asociado."
              />
            )}
          </TabsContent>

          <TabsContent value="screenshots">
            {media?.screenshots?.length ? (
              <div className="grid gap-5 md:grid-cols-2 xl:grid-cols-3">
                {media.screenshots.map((screenshotUrl, index) => (
                  <ScreenshotCard
                    key={`${screenshotUrl}-${index}`}
                    index={index}
                    screenshotUrl={screenshotUrl}
                  />
                ))}
              </div>
            ) : (
              <EmptyState
                title="No hay screenshots disponibles"
                description="Cuando el detalle multimedia tenga capturas asociadas apareceran aqui sin cambiar la navegacion local de la ficha."
              />
            )}
          </TabsContent>
        </Tabs>
      </PageSection>
    </div>
  );
}

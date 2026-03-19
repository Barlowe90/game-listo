import type { ReactNode } from 'react';
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
import { EmptyPublicationsState } from '@/shared/components/domain/EmptyPublicationsState';
import { GameActionBar, type GameActionItem } from '@/shared/components/domain/GameActionBar';
import { GameHero } from '@/shared/components/domain/GameHero';
import { InfoPanelCard } from '@/shared/components/domain/InfoPanelCard';
import {
  formatGameMetaLabel,
  getGameAdditionalTags,
  getGameCollaborators,
  getGameHeroTags,
  getGamePrimaryStudio,
  uniqueStrings,
} from '@/shared/components/domain/game-domain.utils';
import { TagList } from '@/shared/components/domain/TagList';
import { cn } from '@/lib/cn';
import { PageSection } from '@/shared/components/layout/PageSection';
import { Button } from '@/shared/components/ui/Button';
import { Card } from '@/shared/components/ui/Card';
import { EmptyState } from '@/shared/components/ui/EmptyState';
import { Tabs, TabsContent, TabsList, TabsTrigger } from '@/shared/components/ui/Tabs';

const MAX_RELATED_LINKS = 6;

const LIBRARY_ACTIONS: GameActionItem[] = [
  { key: 'quiero', href: '/login' },
  { key: 'tengo', href: '/login' },
  { key: 'jugando', href: '/login' },
  { key: 'jugado', href: '/login' },
] as const;

interface RelatedEntry {
  href: string;
  id: number;
  label: string;
}

function SurfaceCard({ children, className }: { children: ReactNode; className?: string }) {
  return (
    <Card
      className={cn(
        'rounded-[calc(var(--radius-xl)+0.75rem)] border border-border bg-white/90 shadow-elevated backdrop-blur-sm',
        className,
      )}
    >
      {children}
    </Card>
  );
}

function EmptyCopy({ children }: { children: ReactNode }) {
  return <p className="text-sm leading-relaxed text-secondary">{children}</p>;
}

function MetaStat({ label, value }: { label: string; value: string }) {
  return (
    <div className="grid gap-1 rounded-[calc(var(--radius-xl)+0.1rem)] bg-background p-4">
      <span className="text-xs font-semibold tracking-[0.08em] text-primary uppercase">
        {label}
      </span>
      <span className="text-sm font-medium text-foreground">{value}</span>
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
      <TagList
        items={entries.map((entry) => entry.label)}
        getHref={(label) => entries.find((entry) => entry.label === label)?.href}
        tone="tag"
      />
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

  return <TagList items={[entry.label]} getHref={() => entry.href} tone="tag" className="w-fit" />;
}

function VideoEmbedCard({ index, videoUrl }: { index: number; videoUrl: string }) {
  const embedUrl = getYouTubeEmbedUrl(videoUrl);

  return (
    <SurfaceCard>
      <div className="grid gap-4 p-4">
        <div className="overflow-hidden rounded-[calc(var(--radius-xl)+0.25rem)] border border-border">
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
      <div className="relative aspect-video overflow-hidden rounded-[calc(var(--radius-xl)+0.5rem)]">
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
  const heroTags = getGameHeroTags(game);
  const additionalTags = getGameAdditionalTags(game);
  const alternativeNames = uniqueStrings(game.alternativeNames);
  const platforms = uniqueStrings(game.platforms);
  const themes = uniqueStrings(game.themes);
  const genres = uniqueStrings(game.genres);
  const gameModes = uniqueStrings(game.gameModes);
  const perspectives = uniqueStrings(game.playerPerspectives);
  const franchises = uniqueStrings(game.franchises);
  const extraStudios = getGameCollaborators(game);
  const gameTypeLabel = formatGameMetaLabel(game.gameType) ?? 'No definido';
  const gameStatusLabel = formatGameMetaLabel(game.gameStatus) ?? 'No definido';

  return (
    <div className="relative overflow-hidden bg-[radial-gradient(circle_at_top_left,#f8f9ff_0%,#eef0ff_42%,#e7e7fb_100%)]">
      <div className="pointer-events-none absolute left-[-8rem] top-14 h-72 w-72 rounded-full bg-white/40 blur-3xl" />
      <div className="pointer-events-none absolute right-[-6rem] top-40 h-80 w-80 rounded-full bg-primary-soft blur-3xl" />

      <PageSection size="wide" className="relative z-10 py-10 lg:py-14">
        <Tabs defaultValue="sobre" className="grid gap-8">
          <div className="grid gap-4">
            <div className="grid gap-6 xl:grid-cols-[minmax(0,1.02fr)_minmax(0,0.98fr)]">
              <GameHero
                coverUrl={game.coverUrl}
                title={game.name}
                studio={getGamePrimaryStudio(game)}
                collaborators={extraStudios}
                badges={[
                  { label: gameTypeLabel, variant: 'primary' },
                  { label: gameStatusLabel, variant: 'neutral' },
                ]}
                platforms={platforms}
                actionBar={
                  <GameActionBar
                    actions={LIBRARY_ACTIONS}
                    listAction={{ href: '/login', label: 'Anadir a lista' }}
                  />
                }
                className="xl:col-start-1 xl:row-start-1"
              />

              <InfoPanelCard
                title="Genero y estilo"
                className="h-fit xl:col-start-2 xl:row-span-2 xl:row-start-1"
              >
                <div className="grid gap-6">
                  <div className="grid gap-3">
                    <span className="text-sm font-semibold tracking-[0.08em] text-primary uppercase">
                      Descriptores principales
                    </span>
                    <TagList
                      items={heroTags}
                      tone="genre"
                      emptyLabel="No hay descriptores principales disponibles para esta ficha."
                    />
                  </div>

                  <div className="grid gap-3">
                    <span className="text-sm font-semibold tracking-[0.08em] text-primary uppercase">
                      Tags adicionales
                    </span>
                    <TagList
                      items={additionalTags}
                      tone="tag"
                      getHref={(tag) => `/catalogo?q=${encodeURIComponent(tag)}`}
                      emptyLabel="Este juego no tiene tags."
                    />
                  </div>

                  <div className="grid gap-3 border-t border-border pt-4 sm:grid-cols-3">
                    <MetaStat label="Tipo" value={gameTypeLabel} />
                    <MetaStat label="Estado" value={gameStatusLabel} />
                    <MetaStat label="Franquicia" value={franchises[0] ?? 'Titulo independiente'} />
                  </div>
                </div>
              </InfoPanelCard>

              <TabsList className="border-border bg-primary-soft/80 xl:col-start-1 xl:row-start-2">
                <TabsTrigger value="sobre">Sobre</TabsTrigger>
                <TabsTrigger value="publicaciones">Publicaciones</TabsTrigger>
                <TabsTrigger value="videos">Videos</TabsTrigger>
                <TabsTrigger value="screenshots">Screenshots</TabsTrigger>
              </TabsList>
            </div>
          </div>

          <TabsContent value="sobre" className="grid gap-5 md:grid-cols-2 xl:grid-cols-3">
            <InfoPanelCard title="Descripcion general" className="md:col-span-2">
              <p className="text-[15px] leading-7 text-secondary">
                {game.summary?.trim() ?? 'Este juego no tiene descripcion general.'}
              </p>
            </InfoPanelCard>

            <InfoPanelCard title="Titulos alternativos">
              <TextList
                items={alternativeNames}
                emptyLabel="Este juego no tiene nombres alternativos."
              />
            </InfoPanelCard>

            <InfoPanelCard title="Estilo y modos">
              <div className="grid gap-4">
                <div className="grid gap-2">
                  <span className="text-sm font-semibold text-foreground">Generos</span>
                  <TagList
                    items={genres}
                    tone="genre"
                    emptyLabel="No hay generos asociados a este juego."
                  />
                </div>
                <div className="grid gap-2">
                  <span className="text-sm font-semibold text-foreground">Perspectivas</span>
                  <TagList
                    items={perspectives}
                    tone="tag"
                    emptyLabel="No hay perspectivas de juego registradas."
                  />
                </div>
                <div className="grid gap-2">
                  <span className="text-sm font-semibold text-foreground">Modos</span>
                  <TagList
                    items={gameModes}
                    tone="tag"
                    emptyLabel="No hay modos de juego asociados."
                  />
                </div>
              </div>
            </InfoPanelCard>

            <InfoPanelCard title="DLC">
              <RelatedGameList
                gameIds={game.dlcIds}
                relatedGames={relatedGames}
                emptyLabel="No hay DLC enlazados a este juego."
              />
            </InfoPanelCard>

            <InfoPanelCard title="Expansiones">
              <RelatedGameList
                gameIds={game.expansionIds}
                relatedGames={relatedGames}
                emptyLabel="No hay expansiones registradas."
              />
            </InfoPanelCard>

            <InfoPanelCard title="Juegos expandidos">
              <RelatedGameList
                gameIds={game.expandedGames}
                relatedGames={relatedGames}
                emptyLabel="No hay expanded games relacionados."
              />
            </InfoPanelCard>

            <InfoPanelCard title="Remakes">
              <RelatedGameList
                gameIds={game.remakeIds}
                relatedGames={relatedGames}
                emptyLabel="No hay remakes enlazados a este titulo."
              />
            </InfoPanelCard>

            <InfoPanelCard title="Remasters">
              <RelatedGameList
                gameIds={game.remasterIds}
                relatedGames={relatedGames}
                emptyLabel="No hay remasters enlazados a este titulo."
              />
            </InfoPanelCard>

            <InfoPanelCard title="Juegos similares" className="md:col-span-2">
              <RelatedGameList
                gameIds={game.similarGames}
                relatedGames={relatedGames}
                emptyLabel="No hay juegos similares asociados todavia."
              />
            </InfoPanelCard>

            <InfoPanelCard title="Franquicias">
              <TagList
                items={franchises}
                tone="tag"
                emptyLabel="No se han asociado franquicias a este juego."
              />
            </InfoPanelCard>

            <InfoPanelCard title="Tematicas">
              <TagList items={themes} tone="tag" emptyLabel="No hay tematicas registradas." />
            </InfoPanelCard>

            <InfoPanelCard title="Enlaces externos">
              <ExternalLinksList links={game.externalGames} />
            </InfoPanelCard>

            <InfoPanelCard title="Juego padre">
              <ParentGameLink gameId={game.parentGameId} relatedGames={relatedGames} />
            </InfoPanelCard>
          </TabsContent>

          <TabsContent value="publicaciones">
            <EmptyPublicationsState
              title="Todavia no hay publicaciones conectadas a esta ficha"
              description="La estructura social ya existe y puede crecer con grupos, reseñas y busqueda de compania sin cambiar la base visual."
              action={
                <>
                  <Button asChild>
                    <Link href="/publicaciones">Explorar publicaciones</Link>
                  </Button>
                  <Button asChild variant="secondary">
                    <Link href="/login">Iniciar sesion para participar</Link>
                  </Button>
                </>
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

import Image from 'next/image';
import Link from 'next/link';
import { getCatalogGames } from '@/features/catalogo/api/catalogApi';
import type { Game } from '@/features/catalogo/model/catalog.types';
import { Grid } from '@/shared/components/layout/Grid';
import { PageSection } from '@/shared/components/layout/PageSection';
import { SearchBar } from '@/shared/components/layout/SearchBar';
import { Badge } from '@/shared/components/ui/Badge';
import { Button } from '@/shared/components/ui/Button';
import {
  Card,
  CardBody,
  CardDescription,
  CardFooter,
  CardHeader,
  CardTitle,
} from '@/shared/components/ui/Card';
import { EmptyState } from '@/shared/components/ui/EmptyState';
import { SectionHeader } from '@/shared/components/ui/SectionHeader';

function getSearchValue(value: string | string[] | undefined) {
  if (Array.isArray(value)) {
    return value[0] ?? '';
  }

  return value ?? '';
}

function normalizeText(value: string | null | undefined) {
  return value?.trim().toLowerCase() ?? '';
}

function getSearchIndex(game: Game) {
  return [
    game.name,
    game.summary,
    game.gameType,
    game.gameStatus,
    ...game.genres,
    ...game.platforms,
    ...game.involvedCompanies,
    ...game.playerPerspectives,
    ...game.themes,
  ]
    .map(normalizeText)
    .join(' ');
}

function getPrimaryStudio(game: Game) {
  return game.involvedCompanies[0] ?? 'Estudio no especificado';
}

function getShortDescription(game: Game) {
  return (
    game.summary?.trim() ??
    'Esta ficha ya esta conectada al backend del catalogo y lista para crecer con biblioteca, media y contenido social.'
  );
}

function getPrimaryBadge(game: Game) {
  return game.genres[0] ?? game.gameType ?? 'Videojuego';
}

function renderCover(game: Game) {
  if (game.coverUrl) {
    return (
      <Image
        src={game.coverUrl}
        alt={`Portada de ${game.name}`}
        fill
        sizes="(max-width: 768px) 100vw, (max-width: 1280px) 50vw, 33vw"
        className="object-cover transition-transform duration-[var(--duration-normal)] ease-[var(--easing-standard)] group-hover:scale-[1.03]"
      />
    );
  }

  return (
    <div className="grid h-full place-items-center bg-[linear-gradient(140deg,#dfe2ff_0%,#f7f8ff_100%)] p-6 text-center">
      <div className="grid gap-2">
        <span className="text-xs font-semibold tracking-[0.18em] text-primary uppercase">
          GameListo
        </span>
        <strong className="text-xl font-semibold tracking-tight text-foreground">
          {game.name}
        </strong>
      </div>
    </div>
  );
}

export default async function CatalogoPage({
  searchParams,
}: {
  searchParams: Promise<{ q?: string | string[] }>;
}) {
  const resolvedSearchParams = await searchParams;
  const query = getSearchValue(resolvedSearchParams.q).trim();
  const normalizedQuery = normalizeText(query);
  const games = await getCatalogGames();
  const filteredGames = normalizedQuery
    ? games.filter((game) => getSearchIndex(game).includes(normalizedQuery))
    : games;
  const featuredGame = filteredGames[0] ?? games[0] ?? null;

  return (
    <PageSection size="wide">
      <div className="grid gap-8">
        <SectionHeader
          eyebrow="Catalogo"
          title="Explora fichas reales del catalogo"
          subtitle="El listado ya consume juegos reales del backend y mantiene el mismo sistema visual para busqueda, cards clicables y detalle por ID."
          action={
            featuredGame ? (
              <Button asChild>
                <Link href={`/videojuego/${featuredGame.id}`}>Abrir ficha destacada</Link>
              </Button>
            ) : null
          }
        />

        <Card
          variant="informative"
          padding="md"
          className="rounded-[1.75rem] border border-[#e2e8f0] bg-white/80 shadow-[0_24px_60px_rgba(59,63,183,0.08)] backdrop-blur-sm"
        >
          <div className="grid gap-4">
            <div className="flex flex-wrap items-center gap-3">
              <Badge variant="primary">Backend conectado</Badge>
              <Badge>Busqueda por nombre y metadatos</Badge>
              <Badge>Detalle enlazado por ID</Badge>
              {query ? <Badge variant="primary">Busqueda: {query}</Badge> : null}
            </div>

            <SearchBar className="max-w-2xl" defaultValue={query} />
          </div>
        </Card>

        {filteredGames.length ? (
          <Grid variant="cards" className="gap-5">
            {filteredGames.map((game) => (
              <Card
                key={game.id}
                asChild
                variant="clickable"
                className="group rounded-[1.75rem] border border-[#e2e8f0] bg-white/90 shadow-[0_24px_60px_rgba(59,63,183,0.08)] backdrop-blur-sm"
              >
                <Link href={`/videojuego/${game.id}`} className="grid h-full">
                  <div className="relative aspect-[16/9] overflow-hidden">
                    {renderCover(game)}
                    <div className="absolute inset-0 bg-[linear-gradient(180deg,transparent_45%,rgba(10,12,24,0.28)_100%)]" />
                  </div>

                  <CardHeader className="gap-3">
                    <div className="flex flex-wrap items-center gap-2">
                      <Badge variant="primary">{getPrimaryBadge(game)}</Badge>
                      {game.platforms.slice(0, 2).map((platform) => (
                        <Badge key={platform}>{platform}</Badge>
                      ))}
                    </div>

                    <div className="grid gap-1">
                      <CardTitle className="text-xl">{game.name}</CardTitle>
                      <CardDescription>{getPrimaryStudio(game)}</CardDescription>
                    </div>
                  </CardHeader>

                  <CardBody className="gap-4 pt-4">
                    <p className="text-sm leading-relaxed text-secondary">{getShortDescription(game)}</p>

                    <div className="flex flex-wrap gap-2">
                      {game.gameModes.slice(0, 3).map((mode) => (
                        <span
                          key={mode}
                          className="inline-flex rounded-pill bg-primary-soft px-3 py-1 text-xs font-medium text-primary"
                        >
                          {mode}
                        </span>
                      ))}
                    </div>
                  </CardBody>

                  <CardFooter>
                    <span className="text-sm font-semibold text-primary">Ver ficha completa</span>
                  </CardFooter>
                </Link>
              </Card>
            ))}
          </Grid>
        ) : (
          <EmptyState
            title="No hemos encontrado videojuegos con ese criterio"
            description="Prueba otra busqueda o vuelve al catalogo completo para seguir explorando las fichas reales."
            action={
              <>
                <Button asChild>
                  <Link href="/catalogo">Limpiar busqueda</Link>
                </Button>
                <Button asChild variant="secondary">
                  <Link href="/">Volver al inicio</Link>
                </Button>
              </>
            }
          />
        )}
      </div>
    </PageSection>
  );
}

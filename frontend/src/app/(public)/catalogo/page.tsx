import Link from 'next/link';
import { getCatalogGames } from '@/features/catalogo/api/catalogApi';
import { Grid } from '@/shared/components/layout/Grid';
import { PageSection } from '@/shared/components/layout/PageSection';
import { GameCard } from '@/shared/components/domain/GameCard';
import { buildGameSearchIndex, normalizeGameText } from '@/shared/components/domain/game-domain.utils';
import { SearchBar } from '@/shared/components/layout/SearchBar';
import { Badge } from '@/shared/components/ui/Badge';
import { Button } from '@/shared/components/ui/Button';
import { Card } from '@/shared/components/ui/Card';
import { EmptyState } from '@/shared/components/ui/EmptyState';
import { SectionHeader } from '@/shared/components/ui/SectionHeader';

function getSearchValue(value: string | string[] | undefined) {
  if (Array.isArray(value)) {
    return value[0] ?? '';
  }

  return value ?? '';
}

export default async function CatalogoPage({
  searchParams,
}: {
  searchParams: Promise<{ q?: string | string[] }>;
}) {
  const resolvedSearchParams = await searchParams;
  const query = getSearchValue(resolvedSearchParams.q).trim();
  const normalizedQuery = normalizeGameText(query);
  const games = await getCatalogGames();
  const filteredGames = normalizedQuery
    ? games.filter((game) => buildGameSearchIndex(game).includes(normalizedQuery))
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
              <GameCard
                key={game.id}
                game={game}
                href={`/videojuego/${game.id}`}
                className="group rounded-[1.75rem] border border-[#e2e8f0] bg-white/90 shadow-[0_24px_60px_rgba(59,63,183,0.08)] backdrop-blur-sm"
              />
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

import Link from 'next/link';
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

const games = [
  {
    slug: 'demo',
    title: 'GameListo Demo',
    genre: 'Action RPG',
    platforms: 'PC, PlayStation 5',
    description:
      'Ficha de referencia para validar tabs, dialog, badges y bloques de detalle sobre el mismo sistema visual.',
  },
  {
    slug: 'sea-of-stars',
    title: 'Sea of Stars',
    genre: 'JRPG',
    platforms: 'PC, Switch, PlayStation',
    description:
      'Ideal para explorar cards de catalogo con metadatos compactos y acciones de descubrimiento.',
  },
  {
    slug: 'hades-ii',
    title: 'Hades II',
    genre: 'Roguelike',
    platforms: 'PC',
    description:
      'Ejemplo de contenido listo para futuras integraciones de filtros, colecciones y actividad social.',
  },
] as const;

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
  const normalizedQuery = query.toLowerCase();
  const filteredGames = normalizedQuery
    ? games.filter((game) =>
        [game.title, game.genre, game.platforms, game.description].some((value) =>
          value.toLowerCase().includes(normalizedQuery),
        ),
      )
    : games;

  return (
    <PageSection>
      <div className="grid gap-8">
        <SectionHeader
          eyebrow="Catalogo"
          title="Descubre videojuegos sin salir del sistema visual"
          subtitle="El listado ya usa cards clicables, jerarquia compartida, buscador compartido y metadatos compactos para preparar la fase de datos reales."
          action={
            <Button asChild>
              <Link href="/videojuego/demo">Abrir ficha demo</Link>
            </Button>
          }
        />

        <Card variant="informative" padding="md">
          <div className="grid gap-4">
            <div className="flex flex-wrap items-center gap-3">
              <Badge variant="primary">Cards clicables</Badge>
              <Badge>Jerarquia semantica</Badge>
              <Badge>Preparado para filtros</Badge>
              {query ? <Badge variant="primary">Busqueda: {query}</Badge> : null}
            </div>

            <SearchBar className="max-w-2xl" defaultValue={query} />
          </div>
        </Card>

        {filteredGames.length ? (
          <Grid variant="cards">
            {filteredGames.map((game) => (
              <Card key={game.slug} asChild variant="clickable">
                <Link href={`/videojuego/${game.slug}`} className="block h-full">
                  <CardHeader>
                    <div className="flex flex-wrap items-center gap-2">
                      <Badge variant="primary">{game.genre}</Badge>
                      <Badge>{game.platforms}</Badge>
                    </div>
                    <CardTitle>{game.title}</CardTitle>
                    <CardDescription>{game.description}</CardDescription>
                  </CardHeader>
                  <CardBody className="pt-4">
                    <div className="grid gap-2 text-sm text-secondary">
                      <span>Slug: {game.slug}</span>
                      <span>Listo para enlazar ficha, comunidad y biblioteca.</span>
                    </div>
                  </CardBody>
                  <CardFooter>
                    <span className="text-sm font-semibold text-primary">Ver detalle</span>
                  </CardFooter>
                </Link>
              </Card>
            ))}
          </Grid>
        ) : (
          <EmptyState
            title="No hemos encontrado videojuegos con ese criterio"
            description="Prueba otra busqueda o vuelve al catalogo completo para seguir explorando las cards del MVP."
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

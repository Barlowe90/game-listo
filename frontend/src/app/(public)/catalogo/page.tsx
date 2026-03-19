import Link from 'next/link';
import {
  buildCatalogHref,
  buildPlatformFilters,
  buildPlatformTokenSet,
  buildVisiblePages,
  getPageIndex,
  getSelectedPlatforms,
} from '@/features/catalogo/catalog-page.utils';
import { getCatalogGamesPage, getCatalogPlatforms } from '@/features/catalogo/api/catalogApi';
import { CatalogPlatformFilters } from '@/features/catalogo/components/CatalogPlatformFilters';
import { GameCard } from '@/shared/components/domain/GameCard';
import { Grid } from '@/shared/components/layout/Grid';
import { PageSection } from '@/shared/components/layout/PageSection';
import { Button } from '@/shared/components/ui/Button';
import { Card } from '@/shared/components/ui/Card';
import { EmptyState } from '@/shared/components/ui/EmptyState';
import { SectionHeader } from '@/shared/components/ui/SectionHeader';

const CATALOG_PAGE_SIZE = 12;
const MAX_VISIBLE_PAGE_LINKS = 5;

type SearchParams = {
  page?: string | string[];
  platform?: string | string[];
};

export default async function CatalogoPage({
  searchParams,
}: {
  searchParams: Promise<SearchParams>;
}) {
  const resolvedSearchParams = await searchParams;
  const requestedPageIndex = getPageIndex(resolvedSearchParams.page);
  const selectedPlatforms = getSelectedPlatforms(resolvedSearchParams.platform);
  const platforms = await getCatalogPlatforms();

  const platformFilters = buildPlatformFilters(platforms);
  const platformQueryTokens = Array.from(
    buildPlatformTokenSet(selectedPlatforms, platformFilters),
  );
  const catalogPage = await getCatalogGamesPage({
    page: requestedPageIndex,
    size: CATALOG_PAGE_SIZE,
    platforms: platformQueryTokens,
  });

  const currentPageIndex = catalogPage.page;
  const totalCount = catalogPage.totalCount;
  const totalPages = catalogPage.totalPages;
  const visibleGames = catalogPage.items;

  const visiblePageLinks = buildVisiblePages(currentPageIndex, totalPages, MAX_VISIBLE_PAGE_LINKS);
  const firstVisibleResult = totalCount ? currentPageIndex * CATALOG_PAGE_SIZE + 1 : 0;
  const lastVisibleResult = totalCount ? firstVisibleResult + visibleGames.length - 1 : 0;

  return (
    <PageSection size="wide">
      <div className="grid gap-8">
        <SectionHeader title="Explora el catalogo de videojuegos" />

        <Card
          variant="informative"
          padding="md"
          className="rounded-[1.75rem] border border-[#e2e8f0] bg-white/80 shadow-[0_24px_60px_rgba(59,63,183,0.08)] backdrop-blur-sm"
        >
          <CatalogPlatformFilters
            platformFilters={platformFilters}
            selectedPlatforms={selectedPlatforms}
          />
        </Card>

        {visibleGames.length ? (
          <>
            <Grid variant="cards" className="gap-4 md:grid-cols-3 xl:grid-cols-4">
              {visibleGames.map((game) => (
                <GameCard
                  key={game.id}
                  game={game}
                  href={`/videojuego/${game.id}`}
                  className="h-full"
                />
              ))}
            </Grid>

            {totalPages > 1 ? (
              <div className="flex flex-wrap items-center justify-between gap-4 rounded-[1.5rem] border border-border bg-white/70 p-4 shadow-surface">
                <div className="flex flex-wrap items-center gap-2">
                  {currentPageIndex <= 0 ? (
                    <Button variant="secondary" size="sm" disabled>
                      Anterior
                    </Button>
                  ) : (
                    <Button asChild variant="secondary" size="sm">
                      <Link
                        href={buildCatalogHref({
                          page: currentPageIndex,
                          selectedPlatforms,
                        })}
                      >
                        Anterior
                      </Link>
                    </Button>
                  )}

                  {visiblePageLinks.map((pageIndex) => (
                    <Button
                      key={pageIndex}
                      asChild
                      variant={pageIndex === currentPageIndex ? 'primary' : 'secondary'}
                      size="sm"
                      className={
                        pageIndex === currentPageIndex ? 'text-white! hover:text-white' : undefined
                      }
                    >
                      <Link
                        href={buildCatalogHref({
                          page: pageIndex + 1,
                          selectedPlatforms,
                        })}
                        aria-current={pageIndex === currentPageIndex ? 'page' : undefined}
                      >
                        {pageIndex + 1}
                      </Link>
                    </Button>
                  ))}

                  {currentPageIndex >= totalPages - 1 ? (
                    <Button variant="secondary" size="sm" disabled>
                      Siguiente
                    </Button>
                  ) : (
                    <Button asChild variant="secondary" size="sm">
                      <Link
                        href={buildCatalogHref({
                          page: currentPageIndex + 2,
                          selectedPlatforms,
                        })}
                      >
                        Siguiente
                      </Link>
                    </Button>
                  )}
                </div>

                <p className="text-sm text-secondary">
                  Mostrando {firstVisibleResult}-{lastVisibleResult} de {totalCount} resultados.
                </p>
              </div>
            ) : null}
          </>
        ) : (
          <EmptyState
            title="No hemos encontrado videojuegos para esas plataformas"
            description="Prueba a desactivar alguna plataforma o vuelve al listado completo."
            action={
              <Button asChild>
                <Link href="/catalogo">Ver todo el catalogo</Link>
              </Button>
            }
          />
        )}
      </div>
    </PageSection>
  );
}

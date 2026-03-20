'use client';

import { Check, SlidersHorizontal, X } from 'lucide-react';
import { useDeferredValue, useEffect, useState, useTransition } from 'react';
import { useRouter } from 'next/navigation';
import {
  buildCatalogHref,
  togglePlatformSelection,
  type PlatformFilter,
} from '@/features/catalogo/catalog-page.utils';
import { normalizeGameText } from '@/shared/components/domain/game-domain.utils';
import { cn } from '@/lib/cn';
import { Button } from '@/shared/components/ui/Button';
import {
  Dialog,
  DialogBody,
  DialogContent,
  DialogFooter,
  DialogHeader,
  DialogTitle,
  DialogTrigger,
} from '@/shared/components/ui/Dialog';
import { Input } from '@/shared/components/ui/Input';

const MAX_VISIBLE_SELECTED_PLATFORMS = 6;

interface CatalogPlatformFiltersProps {
  platformFilters: PlatformFilter[];
  selectedPlatforms: string[];
}

function getSelectedPlatformLabel(selectedPlatform: string, platformFilters: PlatformFilter[]) {
  return (
    platformFilters.find(
      (platformFilter) =>
        normalizeGameText(platformFilter.value) === normalizeGameText(selectedPlatform),
    )?.label ?? selectedPlatform
  );
}

export function CatalogPlatformFilters({
  platformFilters,
  selectedPlatforms,
}: CatalogPlatformFiltersProps) {
  const router = useRouter();
  const [isOpen, setIsOpen] = useState(false);
  const [searchValue, setSearchValue] = useState('');
  const deferredSearchValue = useDeferredValue(searchValue.trim());
  const [optimisticSelectedPlatforms, setOptimisticSelectedPlatforms] = useState(selectedPlatforms);
  const [isPending, startNavigation] = useTransition();

  useEffect(() => {
    setOptimisticSelectedPlatforms(selectedPlatforms);
  }, [selectedPlatforms]);

  const normalizedSearchValue = normalizeGameText(deferredSearchValue);
  const selectedPlatformSummaries = optimisticSelectedPlatforms.map((selectedPlatform) => ({
    label: getSelectedPlatformLabel(selectedPlatform, platformFilters),
    value: selectedPlatform,
  }));
  const visibleSelectedPlatformSummaries = selectedPlatformSummaries.slice(
    0,
    MAX_VISIBLE_SELECTED_PLATFORMS,
  );
  const hiddenSelectedPlatformsCount = Math.max(
    selectedPlatformSummaries.length - MAX_VISIBLE_SELECTED_PLATFORMS,
    0,
  );
  const visibleFilters = normalizedSearchValue
    ? platformFilters.filter((platformFilter) =>
        platformFilter.tokens.some((token) =>
          normalizeGameText(token).includes(normalizedSearchValue),
        ),
      )
    : platformFilters;
  const filteredPlatformFilters = [...visibleFilters].sort((leftFilter, rightFilter) => {
    const leftSelected = optimisticSelectedPlatforms.some(
      (selectedPlatform) =>
        normalizeGameText(selectedPlatform) === normalizeGameText(leftFilter.value),
    );
    const rightSelected = optimisticSelectedPlatforms.some(
      (selectedPlatform) =>
        normalizeGameText(selectedPlatform) === normalizeGameText(rightFilter.value),
    );

    if (leftSelected === rightSelected) {
      return leftFilter.label.localeCompare(rightFilter.label, 'es', {
        sensitivity: 'base',
      });
    }

    return leftSelected ? -1 : 1;
  });

  const navigateToSelection = (nextSelectedPlatforms: string[]) => {
    setOptimisticSelectedPlatforms(nextSelectedPlatforms);

    startNavigation(() => {
      router.replace(
        buildCatalogHref({
          page: 1,
          selectedPlatforms: nextSelectedPlatforms,
        }),
      );
    });
  };

  const clearSelection = () => {
    setSearchValue('');

    if (!optimisticSelectedPlatforms.length) {
      return;
    }

    navigateToSelection([]);
  };

  return (
    <div className="grid gap-4">
      <div className="flex flex-col gap-4 lg:flex-row lg:items-start lg:justify-between">
        <div className="grid gap-3">
          <div className="flex flex-wrap items-center gap-3">
            <span className="text-sm text-secondary">
              {optimisticSelectedPlatforms.length
                ? `${optimisticSelectedPlatforms.length} seleccionadas`
                : 'Todas las plataformas'}
            </span>
          </div>

          {optimisticSelectedPlatforms.length ? (
            <div className="flex flex-wrap gap-2">
              {visibleSelectedPlatformSummaries.map((platform) => {
                const nextSelectedPlatforms = togglePlatformSelection(
                  optimisticSelectedPlatforms,
                  platform.value,
                );

                return (
                  <button
                    key={platform.value}
                    type="button"
                    className="inline-flex min-h-9 items-center gap-2 rounded-pill border border-border bg-surface px-3 py-1.5 text-sm font-medium text-foreground transition-colors hover:border-border-strong hover:bg-card"
                    onClick={() => navigateToSelection(nextSelectedPlatforms)}
                  >
                    <span>{platform.label}</span>
                    <X className="size-3.5 text-muted-foreground" aria-hidden="true" />
                  </button>
                );
              })}

              {hiddenSelectedPlatformsCount ? (
                <span className="inline-flex min-h-9 items-center rounded-pill border border-border bg-background px-3 py-1.5 text-sm text-secondary">
                  +{hiddenSelectedPlatformsCount} mas
                </span>
              ) : null}
            </div>
          ) : null}
        </div>

        <div className="flex flex-wrap items-center gap-2">
          {optimisticSelectedPlatforms.length ? (
            <Button variant="ghost" size="sm" onClick={clearSelection} disabled={isPending}>
              Limpiar
            </Button>
          ) : null}

          <Dialog open={isOpen} onOpenChange={setIsOpen}>
            <DialogTrigger asChild>
              <Button size="sm" className="gap-2 text-white! hover:text-white">
                <SlidersHorizontal className="size-4 text-current" aria-hidden="true" />
                Filtrar plataformas
              </Button>
            </DialogTrigger>

            <DialogContent className="max-w-3xl p-0">
              <DialogHeader className="gap-3 border-b border-border px-6 pt-6 pb-0">
                <DialogTitle>Filtrar por plataformas</DialogTitle>
              </DialogHeader>

              <DialogBody className="gap-4 px-6">
                <Input
                  value={searchValue}
                  placeholder="Buscar plataforma"
                  autoFocus
                  onChange={(event) => setSearchValue(event.target.value)}
                />

                <div className="flex flex-wrap items-center justify-between gap-3 text-sm">
                  <span className="text-secondary">
                    {filteredPlatformFilters.length} plataformas visibles
                  </span>
                  <span className="text-secondary">
                    {optimisticSelectedPlatforms.length} seleccionadas
                  </span>
                </div>

                <div className="max-h-[min(60vh,32rem)] overflow-y-auto pb-1">
                  <div className="grid gap-2">
                    {filteredPlatformFilters.length ? (
                      filteredPlatformFilters.map((platformFilter) => {
                        const isSelected = optimisticSelectedPlatforms.some(
                          (selectedPlatform) =>
                            normalizeGameText(selectedPlatform) ===
                            normalizeGameText(platformFilter.value),
                        );
                        const nextSelectedPlatforms = togglePlatformSelection(
                          optimisticSelectedPlatforms,
                          platformFilter.value,
                        );

                        return (
                          <button
                            key={platformFilter.value}
                            type="button"
                            className={cn(
                              'flex w-full items-center justify-between gap-3 rounded-2xl border px-4 py-3 text-left transition-colors',
                              isSelected
                                ? 'border-primary bg-primary-soft text-primary'
                                : 'border-border bg-card text-foreground hover:border-border-strong hover:bg-surface',
                            )}
                            onClick={() => navigateToSelection(nextSelectedPlatforms)}
                          >
                            <div className="grid gap-1">
                              <span className="font-semibold">{platformFilter.label}</span>
                              <span className="text-sm text-secondary">
                                {platformFilter.tokens.join(' / ')}
                              </span>
                            </div>
                            <span
                              className={cn(
                                'inline-flex size-6 shrink-0 items-center justify-center rounded-pill border',
                                isSelected
                                  ? 'border-primary bg-primary text-primary-foreground'
                                  : 'border-border bg-background text-transparent',
                              )}
                              aria-hidden="true"
                            >
                              <Check className="size-4" />
                            </span>
                          </button>
                        );
                      })
                    ) : (
                      <div className="rounded-2xl border border-dashed border-border bg-background px-4 py-8 text-center text-sm text-secondary">
                        No hemos encontrado plataformas para &quot;{searchValue.trim()}&quot;.
                      </div>
                    )}
                  </div>
                </div>
              </DialogBody>

              <DialogFooter className="border-t border-border px-6 py-4">
                <Button
                  variant="ghost"
                  onClick={clearSelection}
                  disabled={!optimisticSelectedPlatforms.length && !searchValue}
                >
                  Limpiar filtros
                </Button>
                <Button variant="secondary" onClick={() => setIsOpen(false)}>
                  Cerrar
                </Button>
              </DialogFooter>
            </DialogContent>
          </Dialog>
        </div>
      </div>
    </div>
  );
}

export default CatalogPlatformFilters;

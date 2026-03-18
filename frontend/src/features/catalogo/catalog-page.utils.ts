import type { CatalogPlatform, Game } from '@/features/catalogo/model/catalog.types';
import { normalizeGameText } from '@/shared/components/domain/game-domain.utils';

export interface PlatformFilter {
  label: string;
  tokens: string[];
  value: string;
}

export function getPlatformLabel(platform: CatalogPlatform) {
  const abbreviation = platform.abbreviation?.trim();

  return abbreviation || platform.name.trim();
}

export function getPlatformTokens(platform: CatalogPlatform) {
  return [platform.name, platform.abbreviation, platform.alternativeName]
    .filter((token): token is string => Boolean(token?.trim()))
    .map((token) => token.trim());
}

export function getPageIndex(value: string | string[] | undefined) {
  const rawValue = Array.isArray(value) ? value[0] : value;
  const parsedPage = Number.parseInt(rawValue ?? '', 10);

  if (!Number.isFinite(parsedPage) || parsedPage < 1) {
    return 0;
  }

  return parsedPage - 1;
}

export function getSelectedPlatforms(value: string | string[] | undefined) {
  const rawValues = Array.isArray(value) ? value : value ? [value] : [];

  return rawValues
    .flatMap((rawValue) => rawValue.split(','))
    .map((rawValue) => rawValue.trim())
    .filter(Boolean)
    .reduce<string[]>((selectedPlatforms, rawValue) => {
      const normalizedValue = normalizeGameText(rawValue);

      if (
        selectedPlatforms.some(
          (selectedPlatform) => normalizeGameText(selectedPlatform) === normalizedValue,
        )
      ) {
        return selectedPlatforms;
      }

      selectedPlatforms.push(rawValue);

      return selectedPlatforms;
    }, []);
}

export function buildCatalogHref({
  page,
  selectedPlatforms,
}: {
  page?: number;
  selectedPlatforms?: string[];
}) {
  const params = new URLSearchParams();

  selectedPlatforms?.forEach((selectedPlatform) => {
    if (selectedPlatform.trim()) {
      params.append('platform', selectedPlatform.trim());
    }
  });

  if (page && page > 1) {
    params.set('page', String(page));
  }

  const serializedParams = params.toString();

  return serializedParams ? `/catalogo?${serializedParams}` : '/catalogo';
}

export function buildVisiblePages(currentPage: number, totalPages: number, maxVisiblePages = 5) {
  if (totalPages <= 0) {
    return [];
  }

  const halfWindow = Math.floor(maxVisiblePages / 2);
  const startPage = Math.max(
    Math.min(currentPage - halfWindow, totalPages - maxVisiblePages),
    0,
  );
  const endPage = Math.min(startPage + maxVisiblePages, totalPages);

  return Array.from({ length: endPage - startPage }, (_, index) => startPage + index);
}

export function buildPlatformFilters(platforms: CatalogPlatform[]) {
  return platforms.reduce<PlatformFilter[]>((filters, platform) => {
    const value = getPlatformLabel(platform);
    const tokens = getPlatformTokens(platform);

    if (!value || !tokens.length) {
      return filters;
    }

    if (
      filters.some(
        (existingFilter) => normalizeGameText(existingFilter.value) === normalizeGameText(value),
      )
    ) {
      return filters;
    }

    filters.push({
      label: value,
      tokens,
      value,
    });

    return filters;
  }, []);
}

export function togglePlatformSelection(selectedPlatforms: string[], platformValue: string) {
  const normalizedTarget = normalizeGameText(platformValue);
  const isSelected = selectedPlatforms.some(
    (selectedPlatform) => normalizeGameText(selectedPlatform) === normalizedTarget,
  );

  if (isSelected) {
    return selectedPlatforms.filter(
      (selectedPlatform) => normalizeGameText(selectedPlatform) !== normalizedTarget,
    );
  }

  return [...selectedPlatforms, platformValue];
}

export function buildPlatformTokenSet(
  selectedPlatforms: string[],
  platformFilters: PlatformFilter[],
) {
  return new Set(
    selectedPlatforms.flatMap((selectedPlatform) => {
      const matchingFilter = platformFilters.find(
        (platformFilter) =>
          normalizeGameText(platformFilter.value) === normalizeGameText(selectedPlatform),
      );

      const tokens = matchingFilter?.tokens ?? [selectedPlatform];

      return tokens.map((token) => normalizeGameText(token));
    }),
  );
}

export function filterGamesByPlatforms(
  games: Game[],
  selectedPlatforms: string[],
  platformFilters: PlatformFilter[],
) {
  if (!selectedPlatforms.length) {
    return games;
  }

  const activePlatformTokens = buildPlatformTokenSet(selectedPlatforms, platformFilters);

  return games.filter((game) =>
    game.platforms.some((platform) => activePlatformTokens.has(normalizeGameText(platform))),
  );
}

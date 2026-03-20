import type {
  CatalogGameSummary,
  CatalogGamesPage,
  CatalogPlatform,
  Game,
  GameDetailMedia,
} from '@/features/catalogo/model/catalog.types';
import { getApiBaseUrl } from '@/shared/config/api';

const DEFAULT_REVALIDATE_SECONDS = 60;

class CatalogRequestError extends Error {
  path: string;
  status: number;

  constructor(path: string, response: Response) {
    super(`Catalog API request failed (${response.status}) for ${path}`);
    this.name = 'CatalogRequestError';
    this.path = path;
    this.status = response.status;
    Object.setPrototypeOf(this, new.target.prototype);
  }
}

async function catalogFetch(path: string) {
  return fetch(`${getApiBaseUrl()}${path}`, {
    headers: {
      Accept: 'application/json',
    },
    next: {
      revalidate: DEFAULT_REVALIDATE_SECONDS,
    },
  });
}

function buildCatalogRequestError(path: string, response: Response) {
  return new CatalogRequestError(path, response);
}

async function catalogRequest<T>(path: string, allowNotFound: true): Promise<T | null>;
async function catalogRequest<T>(path: string, allowNotFound?: false): Promise<T>;
async function catalogRequest<T>(path: string, allowNotFound = false) {
  const response = await catalogFetch(path);

  if (allowNotFound && response.status === 404) {
    return null;
  }

  if (!response.ok) {
    throw buildCatalogRequestError(path, response);
  }

  return (await response.json()) as T;
}

function readPaginationHeader(headers: Headers, headerName: string, fallbackValue: number) {
  const headerValue = Number(headers.get(headerName));

  return Number.isFinite(headerValue) ? headerValue : fallbackValue;
}

function getCatalogPlatformSortLabel(platform: CatalogPlatform) {
  return platform.abbreviation?.trim() || platform.name.trim();
}

export async function getCatalogGamesPage({
  page = 0,
  size = 20,
  platforms = [],
}: {
  page?: number;
  size?: number;
  platforms?: string[];
} = {}): Promise<CatalogGamesPage> {
  const searchParams = new URLSearchParams({
    page: String(Math.max(page, 0)),
    size: String(Math.max(size, 1)),
  });

  platforms
    .map((platform) => platform.trim())
    .filter(Boolean)
    .forEach((platform) => {
      searchParams.append('platform', platform);
    });

  const response = await catalogFetch(`/v1/catalogo/games?${searchParams.toString()}`);

  if (!response.ok) {
    throw buildCatalogRequestError('/v1/catalogo/games', response);
  }

  const items = (await response.json()) as CatalogGameSummary[];
  const currentPage = readPaginationHeader(response.headers, 'X-Current-Page', page);
  const pageSize = readPaginationHeader(response.headers, 'X-Page-Size', size);
  const totalCount = readPaginationHeader(response.headers, 'X-Total-Count', items.length);
  const totalPages = readPaginationHeader(
    response.headers,
    'X-Total-Pages',
    items.length ? currentPage + 1 : 0,
  );

  return {
    items,
    page: currentPage,
    size: pageSize,
    totalCount,
    totalPages,
    hasNextPage: totalPages > 0 && currentPage < totalPages - 1,
    hasPreviousPage: currentPage > 0,
  };
}

export async function getCatalogPlatforms() {
  const platforms = await catalogRequest<CatalogPlatform[]>('/v1/catalogo/platforms');

  return [...platforms].sort((leftPlatform, rightPlatform) =>
    getCatalogPlatformSortLabel(leftPlatform).localeCompare(
      getCatalogPlatformSortLabel(rightPlatform),
      'es',
      { sensitivity: 'base' },
    ),
  );
}

export async function getGameById(gameId: number) {
  return catalogRequest<Game>(`/v1/catalogo/games/${gameId}`, true);
}

export async function getGameDetailMedia(gameId: number) {
  try {
    return await catalogRequest<GameDetailMedia>(`/v1/catalogo/games/${gameId}/detail`, true);
  } catch (error) {
    // Some catalog entries have a valid game record but no resolvable media detail payload.
    if (error instanceof CatalogRequestError && error.status === 400) {
      return null;
    }

    throw error;
  }
}

export async function getGamesByIds(gameIds: number[]) {
  const uniqueIds = [...new Set(gameIds.filter((gameId) => Number.isFinite(gameId)))];

  const relatedGames = await Promise.allSettled(
    uniqueIds.map(async (gameId) => {
      const game = await getGameById(gameId);

      return game ? ([gameId, game] as const) : null;
    }),
  );

  return relatedGames.reduce<Map<number, Game>>((gamesMap, result) => {
    if (result.status === 'fulfilled' && result.value) {
      const [gameId, game] = result.value;
      gamesMap.set(gameId, game);
    }

    return gamesMap;
  }, new Map<number, Game>());
}

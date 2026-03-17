import type { Game, GameDetailMedia } from '@/features/catalogo/model/catalog.types';
import { getApiBaseUrl } from '@/shared/config/api';

const DEFAULT_REVALIDATE_SECONDS = 60;

async function catalogRequest<T>(path: string, allowNotFound = false) {
  const response = await fetch(`${getApiBaseUrl()}${path}`, {
    headers: {
      Accept: 'application/json',
    },
    next: {
      revalidate: DEFAULT_REVALIDATE_SECONDS,
    },
  });

  if (allowNotFound && response.status === 404) {
    return null;
  }

  if (!response.ok) {
    throw new Error(`Catalog API request failed (${response.status}) for ${path}`);
  }

  return (await response.json()) as T;
}

export async function getCatalogGames() {
  const games = await catalogRequest<Game[]>('/v1/catalogo/games');

  return [...games].sort((leftGame, rightGame) =>
    leftGame.name.localeCompare(rightGame.name, 'es', { sensitivity: 'base' }),
  );
}

export async function getGameById(gameId: number) {
  return catalogRequest<Game>(`/v1/catalogo/games/${gameId}`, true);
}

export async function getGameDetailMedia(gameId: number) {
  return catalogRequest<GameDetailMedia>(`/v1/catalogo/games/${gameId}/detail`, true);
}

export async function getGamesByIds(gameIds: number[]) {
  const uniqueIds = [...new Set(gameIds.filter((gameId) => Number.isFinite(gameId)))];

  const relatedGames = await Promise.allSettled(
    uniqueIds.map(async (gameId) => {
      const game = await getGameById(gameId);

      return game ? [gameId, game] : null;
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

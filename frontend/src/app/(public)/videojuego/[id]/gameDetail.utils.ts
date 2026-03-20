import type { Game } from '@/features/catalogo/model/catalog.types';

export interface RelatedEntry {
  href: string;
  id: number;
  label: string;
}

export function resolveGameId(rawId: string) {
  const parsedId = Number(rawId);

  if (!Number.isInteger(parsedId) || parsedId <= 0) {
    return null;
  }

  return parsedId;
}

export function buildRelatedEntries(gameIds: number[], relatedGames: Map<number, Game>) {
  const uniqueIds = [...new Set(gameIds)];

  return uniqueIds.map((gameId) => ({
    id: gameId,
    href: `/videojuego/${gameId}`,
    label: relatedGames.get(gameId)?.name ?? `Juego #${gameId}`,
  }));
}

export function buildSingleRelatedEntry(
  gameId: number,
  relatedGames: Map<number, Game>,
): RelatedEntry {
  return {
    id: gameId,
    href: `/videojuego/${gameId}`,
    label: relatedGames.get(gameId)?.name ?? `Juego #${gameId}`,
  };
}

export function getYouTubeEmbedUrl(videoUrl: string) {
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

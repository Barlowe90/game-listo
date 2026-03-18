import type { Game } from '@/features/catalogo/model/catalog.types';

const DEFAULT_GAME_DESCRIPTION =
  'Esta ficha ya esta conectada al backend del catalogo y lista para crecer con biblioteca, media y contenido social.';

const DEFAULT_HERO_TAG_LIMIT = 12;
const DEFAULT_ADDITIONAL_TAG_LIMIT = 18;

export function normalizeGameText(value: string | null | undefined) {
  return value?.trim().toLowerCase() ?? '';
}

export function buildGameSearchIndex(game: Game) {
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
    .map(normalizeGameText)
    .join(' ');
}

export function uniqueStrings(values: Array<string | null | undefined>) {
  const seen = new Set<string>();
  const items: string[] = [];

  for (const value of values) {
    const trimmedValue = value?.trim();

    if (!trimmedValue) {
      continue;
    }

    const normalizedValue = trimmedValue.toLowerCase();

    if (seen.has(normalizedValue)) {
      continue;
    }

    seen.add(normalizedValue);
    items.push(trimmedValue);
  }

  return items;
}

export function formatGameMetaLabel(value: string | null | undefined) {
  if (!value) {
    return null;
  }

  const normalizedValue = value.replace(/_/g, ' ').trim();

  if (!normalizedValue) {
    return null;
  }

  return normalizedValue.charAt(0).toUpperCase() + normalizedValue.slice(1);
}

export function getGamePrimaryStudio(game: Game) {
  return game.involvedCompanies[0] ?? 'Estudio no especificado';
}

export function getGameCollaborators(game: Game) {
  return uniqueStrings(game.involvedCompanies).slice(1);
}

export function getGameShortDescription(game: Game) {
  return game.summary?.trim() ?? DEFAULT_GAME_DESCRIPTION;
}

export function getGamePrimaryBadge(game: Game) {
  return game.genres[0] ?? game.gameType ?? 'Videojuego';
}

export function getGameHeroTags(game: Game, limit = DEFAULT_HERO_TAG_LIMIT) {
  return uniqueStrings([
    ...game.genres,
    ...game.playerPerspectives,
    ...game.gameModes,
    ...game.themes,
  ]).slice(0, limit);
}

export function getGameAdditionalTags(game: Game, limit = DEFAULT_ADDITIONAL_TAG_LIMIT) {
  return uniqueStrings(game.keywords).slice(0, limit);
}

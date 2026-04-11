import type { GameSuggestion, SuggestionsResponse } from '@/features/busquedas/model/sugerencias.types';

interface GetGameSuggestionsOptions {
  signal?: AbortSignal;
  size?: number;
}

export async function getGameSuggestions(
  query: string,
  { signal, size = 5 }: GetGameSuggestionsOptions = {},
) {
  const trimmedQuery = query.trim();

  if (trimmedQuery.length < 2) {
    return [] as GameSuggestion[];
  }

  const searchParams = new URLSearchParams({
    q: trimmedQuery,
    size: String(size),
  });

  const response = await fetch(`/api/busquedas/sugerencia?${searchParams}`, {
    headers: {
      Accept: 'application/json',
    },
    cache: 'no-store',
    signal,
  });

  if (!response.ok) {
    throw new Error(`Suggestions API request failed (${response.status})`);
  }

  const data = (await response.json()) as SuggestionsResponse;

  return data.results ?? [];
}

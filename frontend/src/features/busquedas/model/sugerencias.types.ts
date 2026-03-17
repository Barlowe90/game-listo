export interface GameSuggestion {
  gameId: number;
  title: string;
}

export interface SuggestionsResponse {
  query: string;
  results: GameSuggestion[];
}

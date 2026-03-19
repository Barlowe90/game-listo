export interface CatalogGameSummary {
  id: number;
  coverUrl: string | null;
  gameModes: string[];
  name: string;
  platforms: string[];
}

export interface Game {
  id: number;
  alternativeNames: string[];
  coverUrl: string | null;
  dlcIds: number[];
  expandedGames: number[];
  expansionIds: number[];
  externalGames: string[];
  franchises: string[];
  gameModes: string[];
  gameStatus: string | null;
  gameType: string | null;
  genres: string[];
  involvedCompanies: string[];
  keywords: string[];
  multiplayerModeIds: number[];
  name: string;
  parentGameId: number | null;
  platforms: string[];
  playerPerspectives: string[];
  remakeIds: number[];
  remasterIds: number[];
  similarGames: number[];
  summary: string | null;
  themes: string[];
}

export interface GameDetailMedia {
  gameId: number;
  screenshots: string[];
  videos: string[];
}

export interface CatalogGamesPage {
  items: CatalogGameSummary[];
  page: number;
  size: number;
  totalCount: number;
  totalPages: number;
  hasNextPage: boolean;
  hasPreviousPage: boolean;
}

export interface CatalogPlatform {
  id: number;
  name: string;
  abbreviation: string | null;
  alternativeName: string | null;
  logoURL: string | null;
  tipo: string | null;
}

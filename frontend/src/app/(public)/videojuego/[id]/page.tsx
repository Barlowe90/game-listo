import { notFound } from 'next/navigation';
import { getGameById, getGameDetailMedia, getGamesByIds } from '@/features/catalogo/api/catalogApi';
import { GamePublicacionesSection } from '@/features/publicaciones/components/GamePublicacionesSection';
import {
  formatGameMetaLabel,
  getGameAdditionalTags,
  getGameCollaborators,
  getGameHeroTags,
  getGamePrimaryStudio,
  uniqueStrings,
} from '@/shared/components/domain/game-domain.utils';
import { PageSection } from '@/shared/components/layout/PageSection';
import { Tabs, TabsContent } from '@/shared/components/ui/Tabs';
import { GameDetailHeroSection } from './components/GameDetailHeroSection';
import { GameDetailScreenshotsTab, GameDetailVideosTab } from './components/GameDetailMediaTabs';
import { GameDetailOverviewTab } from './components/GameDetailOverviewTab';
import { resolveGameId } from './gameDetail.utils';

export default async function VideojuegoPage({ params }: { params: Promise<{ id: string }> }) {
  const { id } = await params;
  const gameId = resolveGameId(id);

  if (!gameId) {
    notFound();
  }

  const [game, media] = await Promise.all([getGameById(gameId), getGameDetailMedia(gameId)]);

  if (!game) {
    notFound();
  }

  const relatedIdsToResolve = [
    ...(game.parentGameId ? [game.parentGameId] : []),
    ...game.dlcIds,
    ...game.expandedGames,
    ...game.expansionIds,
    ...game.remakeIds,
    ...game.remasterIds,
    ...game.similarGames,
  ];

  const relatedGames = await getGamesByIds(relatedIdsToResolve);
  const heroTags = getGameHeroTags(game);
  const additionalTags = getGameAdditionalTags(game);
  const alternativeNames = uniqueStrings(game.alternativeNames);
  const platforms = uniqueStrings(game.platforms);
  const themes = uniqueStrings(game.themes);
  const genres = uniqueStrings(game.genres);
  const gameModes = uniqueStrings(game.gameModes);
  const perspectives = uniqueStrings(game.playerPerspectives);
  const franchises = uniqueStrings(game.franchises);
  const extraStudios = getGameCollaborators(game);
  const gameTypeLabel = formatGameMetaLabel(game.gameType) ?? 'No definido';
  const gameStatusLabel = formatGameMetaLabel(game.gameStatus) ?? 'No definido';

  return (
    <div className="relative overflow-hidden bg-[radial-gradient(circle_at_top_left,#f8f9ff_0%,#eef0ff_42%,#e7e7fb_100%)]">
      <div className="pointer-events-none absolute left-[-8rem] top-14 h-72 w-72 rounded-full bg-white/40 blur-3xl" />
      <div className="pointer-events-none absolute right-[-6rem] top-40 h-80 w-80 rounded-full bg-primary-soft blur-3xl" />

      <PageSection size="wide" className="relative z-10 py-10 lg:py-14">
        <Tabs defaultValue="sobre" className="grid gap-8">
          <GameDetailHeroSection
            additionalTags={additionalTags}
            extraStudios={extraStudios}
            franchises={franchises}
            game={game}
            gameStatusLabel={gameStatusLabel}
            gameTypeLabel={gameTypeLabel}
            heroTags={heroTags}
            platforms={platforms}
            studio={getGamePrimaryStudio(game)}
          />

          <GameDetailOverviewTab
            alternativeNames={alternativeNames}
            franchises={franchises}
            game={game}
            gameModes={gameModes}
            genres={genres}
            perspectives={perspectives}
            relatedGames={relatedGames}
            themes={themes}
          />

          <TabsContent value="publicaciones">
            <GamePublicacionesSection gameId={game.id} />
          </TabsContent>

          <GameDetailVideosTab videos={media?.videos} />
          <GameDetailScreenshotsTab screenshots={media?.screenshots} />
        </Tabs>
      </PageSection>
    </div>
  );
}

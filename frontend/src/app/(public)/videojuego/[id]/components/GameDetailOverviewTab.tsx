import type { Game } from '@/features/catalogo/model/catalog.types';
import { InfoPanelCard } from '@/shared/components/domain/InfoPanelCard';
import { TagList } from '@/shared/components/domain/TagList';
import { TabsContent } from '@/shared/components/ui/Tabs';
import { ExternalLinksList, ParentGameLink, RelatedGameList, TextList } from './GameDetailShared';

interface GameDetailOverviewTabProps {
  alternativeNames: string[];
  franchises: string[];
  game: Game;
  gameModes: string[];
  genres: string[];
  perspectives: string[];
  relatedGames: Map<number, Game>;
  themes: string[];
}

export function GameDetailOverviewTab({
  alternativeNames,
  franchises,
  game,
  gameModes,
  genres,
  perspectives,
  relatedGames,
  themes,
}: Readonly<GameDetailOverviewTabProps>) {
  return (
    <TabsContent value="sobre" className="grid gap-5 md:grid-cols-2 xl:grid-cols-3">
      <InfoPanelCard title="Descripcion general" className="md:col-span-2">
        <p className="text-[15px] leading-7 text-secondary">
          {game.summary?.trim() ?? 'Este juego no tiene descripcion general.'}
        </p>
      </InfoPanelCard>

      <InfoPanelCard title="Titulos alternativos">
        <TextList items={alternativeNames} emptyLabel="Este juego no tiene nombres alternativos." />
      </InfoPanelCard>

      <InfoPanelCard title="Estilo y modos">
        <div className="grid gap-4">
          <div className="grid gap-2">
            <span className="text-sm font-semibold text-foreground">Generos</span>
            <TagList
              items={genres}
              tone="genre"
              emptyLabel="No hay generos asociados a este juego."
            />
          </div>
          <div className="grid gap-2">
            <span className="text-sm font-semibold text-foreground">Perspectivas</span>
            <TagList
              items={perspectives}
              tone="tag"
              emptyLabel="No hay perspectivas de juego registradas."
            />
          </div>
          <div className="grid gap-2">
            <span className="text-sm font-semibold text-foreground">Modos</span>
            <TagList items={gameModes} tone="tag" emptyLabel="No hay modos de juego asociados." />
          </div>
        </div>
      </InfoPanelCard>

      <InfoPanelCard title="DLC">
        <RelatedGameList
          gameIds={game.dlcIds}
          relatedGames={relatedGames}
          emptyLabel="No hay DLC enlazados a este juego."
        />
      </InfoPanelCard>

      <InfoPanelCard title="Expansiones">
        <RelatedGameList
          gameIds={game.expansionIds}
          relatedGames={relatedGames}
          emptyLabel="No hay expansiones registradas."
        />
      </InfoPanelCard>

      <InfoPanelCard title="Juegos expandidos">
        <RelatedGameList
          gameIds={game.expandedGames}
          relatedGames={relatedGames}
          emptyLabel="No hay expanded games relacionados."
        />
      </InfoPanelCard>

      <InfoPanelCard title="Remakes">
        <RelatedGameList
          gameIds={game.remakeIds}
          relatedGames={relatedGames}
          emptyLabel="No hay remakes enlazados a este titulo."
        />
      </InfoPanelCard>

      <InfoPanelCard title="Remasters">
        <RelatedGameList
          gameIds={game.remasterIds}
          relatedGames={relatedGames}
          emptyLabel="No hay remasters enlazados a este titulo."
        />
      </InfoPanelCard>

      <InfoPanelCard title="Juegos similares" className="md:col-span-2">
        <RelatedGameList
          gameIds={game.similarGames}
          relatedGames={relatedGames}
          emptyLabel="No hay juegos similares asociados todavia."
        />
      </InfoPanelCard>

      <InfoPanelCard title="Franquicias">
        <TagList
          items={franchises}
          tone="tag"
          emptyLabel="No se han asociado franquicias a este juego."
        />
      </InfoPanelCard>

      <InfoPanelCard title="Tematicas">
        <TagList items={themes} tone="tag" emptyLabel="No hay tematicas registradas." />
      </InfoPanelCard>

      <InfoPanelCard title="Enlaces externos">
        <ExternalLinksList links={game.externalGames} />
      </InfoPanelCard>

      <InfoPanelCard title="Juego padre">
        <ParentGameLink gameId={game.parentGameId} relatedGames={relatedGames} />
      </InfoPanelCard>
    </TabsContent>
  );
}

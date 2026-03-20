import type { Game } from '@/features/catalogo/model/catalog.types';
import { GameBibliotecaActions } from '@/features/biblioteca/components/GameBibliotecaActions';
import { GameSocialSummaryCard } from '@/features/social/components/GameSocialSummaryCard';
import { GameHero } from '@/shared/components/domain/GameHero';
import { InfoPanelCard } from '@/shared/components/domain/InfoPanelCard';
import { TagList } from '@/shared/components/domain/TagList';
import { TabsList, TabsTrigger } from '@/shared/components/ui/Tabs';
import { MetaStat } from './GameDetailShared';

interface GameDetailHeroSectionProps {
  additionalTags: string[];
  extraStudios: string[];
  franchises: string[];
  game: Game;
  gameStatusLabel: string;
  gameTypeLabel: string;
  heroTags: string[];
  platforms: string[];
  studio: string;
}

export function GameDetailHeroSection({
  additionalTags,
  extraStudios,
  franchises,
  game,
  gameStatusLabel,
  gameTypeLabel,
  heroTags,
  platforms,
  studio,
}: Readonly<GameDetailHeroSectionProps>) {
  return (
    <div className="grid gap-4">
      <div className="grid gap-6 xl:grid-cols-[minmax(0,1.02fr)_minmax(0,0.98fr)]">
        <GameHero
          coverUrl={game.coverUrl}
          title={game.name}
          studio={studio}
          collaborators={extraStudios}
          badges={[
            { label: gameTypeLabel, variant: 'primary' },
            { label: gameStatusLabel, variant: 'neutral' },
          ]}
          platforms={platforms}
          actionBar={<GameBibliotecaActions gameId={game.id} />}
          className="xl:col-start-1 xl:row-start-1"
        />

        <div className="grid gap-6 xl:col-start-2 xl:row-start-1 xl:content-start">
          <InfoPanelCard title="Genero y estilo" className="h-fit">
            <div className="grid gap-6">
              <div className="grid gap-3">
                <span className="text-sm font-semibold tracking-[0.08em] text-primary uppercase">
                  Descriptores principales
                </span>
                <TagList
                  items={heroTags}
                  tone="genre"
                  emptyLabel="No hay descriptores principales disponibles para esta ficha."
                />
              </div>

              <div className="grid gap-3">
                <span className="text-sm font-semibold tracking-[0.08em] text-primary uppercase">
                  Tags adicionales
                </span>
                <TagList
                  items={additionalTags}
                  tone="tag"
                  getHref={(tag) => `/catalogo?q=${encodeURIComponent(tag)}`}
                  emptyLabel="Este juego no tiene tags."
                />
              </div>

              <div className="grid gap-3 border-t border-border pt-4 sm:grid-cols-3">
                <MetaStat label="Tipo" value={gameTypeLabel} />
                <MetaStat label="Estado" value={gameStatusLabel} />
                <MetaStat label="Franquicia" value={franchises[0] ?? 'Titulo independiente'} />
              </div>
            </div>
          </InfoPanelCard>

          <GameSocialSummaryCard gameId={game.id} className="h-fit" />
        </div>

        <TabsList className="border-border bg-primary-soft/80 xl:col-span-2 xl:row-start-2">
          <TabsTrigger value="sobre">Sobre</TabsTrigger>
          <TabsTrigger value="publicaciones">Publicaciones</TabsTrigger>
          <TabsTrigger value="videos">Videos</TabsTrigger>
          <TabsTrigger value="screenshots">Screenshots</TabsTrigger>
        </TabsList>
      </div>
    </div>
  );
}

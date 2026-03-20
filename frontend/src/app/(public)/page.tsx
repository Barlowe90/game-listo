import Image from 'next/image';
import Link from 'next/link';
import { BookMarked, Compass, UsersRound, type LucideIcon } from 'lucide-react';
import { FeatureCard } from '@/shared/components/domain/FeatureCard';
import { ImportLibraryBanner } from '@/shared/components/domain/ImportLibraryBanner';
import { Grid } from '@/shared/components/layout/Grid';
import { PageSection } from '@/shared/components/layout/PageSection';
import { Button } from '@/shared/components/ui/Button';
import { Card } from '@/shared/components/ui/Card';
import { SectionHeader } from '@/shared/components/ui/SectionHeader';

const highlights: ReadonlyArray<{
  title: string;
  description: string;
  icon: LucideIcon;
}> = [
  {
    title: 'Organiza tu biblioteca',
    description: 'Gestiona todos tus juegos en un solo lugar.',
    icon: BookMarked,
  },
  {
    title: 'Conecta con otros jugadores',
    description: 'Crea publicaciones y busca compañeros de juego.',
    icon: UsersRound,
  },
  {
    title: 'Descubre nuevos títulos',
    description: 'Explora el catálogo de juegos más completo.',
    icon: Compass,
  },
];

export default function Home() {
  return (
    <>
      <PageSection spacing="hero">
        <Card variant="informative" padding="lg">
          <Grid variant="feature" className="items-center gap-8">
            <div className="grid gap-6">
              <div className="grid gap-4">
                <h1 className="text-3xl font-bold tracking-tight text-white lg:text-4xl">
                  Tu biblioteca gamer organizada y conectada
                </h1>
                <p className="max-w-3xl text-base leading-relaxed text-white">
                  Lleva el control de tus juegos, crea listas personalizadas y conecta con jugadores
                  que comparten tus mismos gustos. Todo en un mismo lugar, sin complicaciones.
                </p>
              </div>

              <div className="flex flex-wrap gap-3">
                <Button asChild variant="secondary">
                  <Link href="/login">Empieza gratis</Link>
                </Button>
              </div>
            </div>

            <div className="mx-auto w-full max-w-xs sm:max-w-sm lg:max-w-md">
              <Image
                src="/zero-two.png"
                alt="Ilustracion de Zero Two"
                width={700}
                height={700}
                priority
                sizes="(min-width: 1024px) 28rem, (min-width: 640px) 24rem, 18rem"
                className="h-auto w-full object-contain"
              />
            </div>
          </Grid>
        </Card>
      </PageSection>

      <PageSection spacing="compact">
        <div className="grid gap-6 [&>div:first-child_h2]:text-inverse">
          <SectionHeader
            title="¿Por qué Game Listo?"
            className="items-center justify-center text-center sm:items-center sm:justify-center"
          />

          <Grid variant="cards">
            {highlights.map((highlight) => (
              <FeatureCard
                key={highlight.title}
                title={highlight.title}
                description={highlight.description}
                icon={<highlight.icon className="size-6" aria-hidden="true" />}
              />
            ))}
          </Grid>
        </div>
      </PageSection>

      <PageSection spacing="hero">
        <ImportLibraryBanner
          title="Empieza trayendo tu biblioteca"
          description="Importa tu biblioteca de Steam o PlayStation para organizar todos tus videojuegos en un solo lugar."
          action={
            <Button asChild variant="secondary">
              <Link href="/login">Importar biblioteca</Link>
            </Button>
          }
        />
      </PageSection>
    </>
  );
}

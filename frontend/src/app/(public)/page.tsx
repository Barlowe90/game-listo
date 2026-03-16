import Image from 'next/image';
import Link from 'next/link';
import { Grid } from '@/shared/components/layout/Grid';
import { PageSection } from '@/shared/components/layout/PageSection';
import { Button } from '@/shared/components/ui/Button';
import { Card } from '@/shared/components/ui/Card';
import { SectionHeader } from '@/shared/components/ui/SectionHeader';

type HighlightIconProps = {
  className?: string;
};

function LibraryIcon({ className }: HighlightIconProps) {
  return (
    <svg
      aria-hidden="true"
      viewBox="0 0 24 24"
      fill="none"
      stroke="currentColor"
      strokeWidth="2.2"
      strokeLinecap="round"
      strokeLinejoin="round"
      className={className}
    >
      <path d="M5 4.5v15" />
      <path d="M10 4.5v15" />
      <path d="m15 5.5 4 13" />
    </svg>
  );
}

function PlayersIcon({ className }: HighlightIconProps) {
  return (
    <svg
      aria-hidden="true"
      viewBox="0 0 24 24"
      fill="none"
      stroke="currentColor"
      strokeWidth="1.9"
      strokeLinecap="round"
      strokeLinejoin="round"
      className={className}
    >
      <circle cx="8" cy="8" r="3" />
      <circle cx="17" cy="7" r="2.5" />
      <path d="M3.5 19a4.5 4.5 0 0 1 9 0" />
      <path d="M13.5 17.5a4 4 0 0 1 7 0" />
    </svg>
  );
}

function DiscoverIcon({ className }: HighlightIconProps) {
  return (
    <svg
      aria-hidden="true"
      viewBox="0 0 24 24"
      fill="none"
      stroke="currentColor"
      strokeWidth="1.9"
      strokeLinecap="round"
      strokeLinejoin="round"
      className={className}
    >
      <path d="M13.5 4.5c-2.8.8-5.7 3.7-6.5 6.5L6.5 13.5l4 4 2.5-.5c2.8-.8 5.7-3.7 6.5-6.5l.5-2.5z" />
      <path d="m9.5 14.5-3 5 5-3" />
      <circle cx="14.5" cy="9.5" r="1.5" />
    </svg>
  );
}

const highlights = [
  {
    title: 'Organiza tu biblioteca',
    description: 'Gestiona todos tus juegos en un solo lugar.',
    icon: LibraryIcon,
  },
  {
    title: 'Conecta con otros jugadores',
    description: 'Crea publicaciones y busca compañeros de juego.',
    icon: PlayersIcon,
  },
  {
    title: 'Descubre nuevos títulos',
    description: 'Explora el catálogo de juegos más completo.',
    icon: DiscoverIcon,
  },
] as const;

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
                  <Link href="/videojuego/demo">Empieza gratis</Link>
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
              <Card key={highlight.title} variant="home" padding="md" className="h-full">
                <div className="grid h-full gap-4">
                  <div className="flex items-start gap-4">
                    <span className="inline-flex size-12 shrink-0 items-center justify-center rounded-2xl bg-white/10 text-white">
                      <highlight.icon className="size-6" />
                    </span>
                    <h2 className="text-lg font-semibold tracking-tight text-white">
                      {highlight.title}
                    </h2>
                  </div>
                  <p className="text-sm leading-relaxed text-white">{highlight.description}</p>
                </div>
              </Card>
            ))}
          </Grid>
        </div>
      </PageSection>

      <PageSection spacing="hero">
        <Card variant="home" padding="lg">
          <Grid variant="stack" className="justify-items-center gap-6 text-center">
            <div className="grid max-w-3xl gap-4 justify-items-center">
              <h1 className="text-3xl font-bold tracking-tight text-white lg:text-4xl">
                Empieza trayendo tu biblioteca
              </h1>
              <p className="max-w-2xl text-base leading-relaxed text-white">
                Importa tu biblioteca de Steam o PlayStation para organizar todos tus videojuegos en
                un solo lugar.
              </p>
            </div>

            <div className="flex flex-wrap justify-center gap-3">
              <Button asChild variant="secondary">
                <Link href="/videojuego/demo">Importar biblioteca</Link>
              </Button>
            </div>
          </Grid>
        </Card>
      </PageSection>
    </>
  );
}

import Link from 'next/link';
import { Grid } from '@/shared/components/layout/Grid';
import { PageSection } from '@/shared/components/layout/PageSection';
import { SearchBar } from '@/shared/components/layout/SearchBar';
import { Badge } from '@/shared/components/ui/Badge';
import { Button } from '@/shared/components/ui/Button';
import {
  Card,
  CardBody,
  CardDescription,
  CardHeader,
  CardTitle,
} from '@/shared/components/ui/Card';
import { SectionHeader } from '@/shared/components/ui/SectionHeader';

const highlights = [
  {
    title: 'Shell global reutilizable',
    description:
      'Header, footer y contenedores ya no dependen de repetir estructura en cada pagina del MVP.',
  },
  {
    title: 'Busqueda visible y conectada',
    description:
      'El buscador del header y del catalogo comparte el mismo patron y empuja resultados reales por query string.',
  },
  {
    title: 'Grids y secciones oficiales',
    description:
      'Home, catalogo, biblioteca y ficha ahora usan wrappers estructurales con ritmo vertical y columnas coherentes.',
  },
] as const;

const mvpScreens = [
  {
    href: '/catalogo',
    title: 'Catalogo',
    description: 'Listado conectado al SearchBar y preparado para crecer con datos reales.',
  },
  {
    href: '/videojuego/demo',
    title: 'Ficha de videojuego',
    description: 'Tabs, dialog, grids y secciones de detalle sobre patrones compartidos.',
  },
  {
    href: '/registro',
    title: 'Formularios',
    description: 'Login, registro y recuperacion heredan ya la shell global sin repetir layout.',
  },
] as const;

export default function Home() {
  return (
    <>
      <PageSection spacing="hero">
        <Card variant="informative" padding="lg">
          <Grid variant="feature" className="items-start">
            <div className="grid gap-6">
              <Badge variant="primary" className="w-fit">
                Fase 4.4
              </Badge>

              <div className="grid gap-4">
                <h1 className="text-3xl font-bold tracking-tight text-foreground lg:text-4xl">
                  GameListo ya tiene un layout shell reutilizable para todo el MVP
                </h1>
                <p className="max-w-3xl text-base leading-relaxed text-secondary">
                  La base visual ya no cubre solo atoms y patrones compuestos. Ahora header,
                  footer, contenedores, buscador, secciones y grids estructuran la aplicacion con
                  una navegacion global clara.
                </p>
              </div>

              <SearchBar className="max-w-xl" />

              <div className="flex flex-wrap gap-3">
                <Button asChild>
                  <Link href="/catalogo">Explorar catalogo</Link>
                </Button>
                <Button asChild variant="secondary">
                  <Link href="/videojuego/demo">Abrir ficha demo</Link>
                </Button>
              </div>
            </div>

            <div className="grid gap-4 sm:grid-cols-2">
              <Card padding="md">
                <div className="grid gap-2">
                  <span className="text-sm font-medium text-muted-foreground">Fase activa</span>
                  <strong className="text-2xl font-bold tracking-tight text-foreground">4.4</strong>
                  <p className="text-sm leading-relaxed text-secondary">
                    Layout shell, navegacion global y busqueda compartida sobre foundations.
                  </p>
                </div>
              </Card>

              <Card padding="md">
                <div className="grid gap-2">
                  <span className="text-sm font-medium text-muted-foreground">Patrones clave</span>
                  <strong className="text-2xl font-bold tracking-tight text-foreground">
                    8 bloques
                  </strong>
                  <p className="text-sm leading-relaxed text-secondary">
                    App shell, header, footer, container, grid, page section, search bar y nav
                    link.
                  </p>
                </div>
              </Card>
            </div>
          </Grid>
        </Card>
      </PageSection>

      <PageSection spacing="compact">
        <div className="grid gap-6">
          <SectionHeader
            eyebrow="Sistema"
            title="Lo que ya resuelve esta capa"
            subtitle="Estos patrones nacen para usarse en pantallas reales del producto, no como piezas aisladas de libreria."
          />

          <Grid variant="cards">
            {highlights.map((highlight) => (
              <Card key={highlight.title} variant="informative" padding="md">
                <div className="grid gap-2">
                  <h2 className="text-lg font-semibold tracking-tight text-foreground">
                    {highlight.title}
                  </h2>
                  <p className="text-sm leading-relaxed text-secondary">
                    {highlight.description}
                  </p>
                </div>
              </Card>
            ))}
          </Grid>
        </div>
      </PageSection>

      <PageSection spacing="compact">
        <div className="grid gap-6">
          <SectionHeader
            eyebrow="Pantallas"
            title="Rutas del MVP ya conectadas al sistema"
            subtitle="Cada una reutiliza la misma base visual para evitar estructura duplicada y facilitar las siguientes fases."
            action={
              <Button asChild variant="secondary">
                <Link href="/registro">Probar formularios</Link>
              </Button>
            }
          />

          <Grid variant="cards">
            {mvpScreens.map((screen) => (
              <Card key={screen.href} asChild variant="clickable">
                <Link href={screen.href} className="block h-full">
                  <CardHeader>
                    <CardTitle>{screen.title}</CardTitle>
                    <CardDescription>{screen.description}</CardDescription>
                  </CardHeader>
                  <CardBody className="pt-4">
                    <span className="text-sm font-semibold text-primary">Abrir vista</span>
                  </CardBody>
                </Link>
              </Card>
            ))}
          </Grid>
        </div>
      </PageSection>
    </>
  );
}

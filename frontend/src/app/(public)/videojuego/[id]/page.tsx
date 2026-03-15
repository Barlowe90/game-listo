import Link from 'next/link';
import { cn } from '@/lib/cn';
import { Grid } from '@/shared/components/layout/Grid';
import { PageSection } from '@/shared/components/layout/PageSection';
import { Badge } from '@/shared/components/ui/Badge';
import { Button } from '@/shared/components/ui/Button';
import {
  Card,
  CardBody,
  CardDescription,
  CardFooter,
  CardHeader,
  CardTitle,
} from '@/shared/components/ui/Card';
import {
  Dialog,
  DialogBody,
  DialogClose,
  DialogContent,
  DialogDescription,
  DialogFooter,
  DialogHeader,
  DialogTitle,
  DialogTrigger,
} from '@/shared/components/ui/Dialog';
import { EmptyState } from '@/shared/components/ui/EmptyState';
import { FormField } from '@/shared/components/ui/FormField';
import { Input, inputVariants } from '@/shared/components/ui/Input';
import { SectionHeader } from '@/shared/components/ui/SectionHeader';
import { Tabs, TabsContent, TabsList, TabsTrigger } from '@/shared/components/ui/Tabs';

const publications = [
  {
    title: 'Busco equipo para explorar todas las secundarias',
    author: 'MartaLuna',
    status: 'Activo',
    summary:
      'Quiero montar una partida tranquila para comentar lore y rutas opcionales durante la semana.',
  },
  {
    title: 'Impresiones tras las primeras diez horas',
    author: 'RafaXP',
    status: 'Destacado',
    summary:
      'El combate funciona muy bien con builds hibridas y la interfaz ya pide secciones sociales reutilizables.',
  },
] as const;

function formatTitleFromSlug(slug: string) {
  return slug
    .split('-')
    .filter(Boolean)
    .map((part) => part.charAt(0).toUpperCase() + part.slice(1))
    .join(' ');
}

export default async function VideojuegoPage({
  params,
}: {
  params: Promise<{ id: string }>;
}) {
  const { id } = await params;
  const title = id === 'demo' ? 'GameListo Demo' : formatTitleFromSlug(decodeURIComponent(id));

  return (
    <>
      <PageSection>
        <div className="grid gap-8">
          <SectionHeader
            eyebrow="Ficha de videojuego"
            title={title}
            subtitle="La pantalla ya combina card, dialog, tabs, section headers, grid estructural y estados vacios sobre la misma base de foundations y atoms."
            action={
              <>
                <Dialog>
                  <DialogTrigger asChild>
                    <Button>Anadir a mi lista</Button>
                  </DialogTrigger>
                  <DialogContent>
                    <DialogHeader>
                      <DialogTitle>Guardar en biblioteca</DialogTitle>
                      <DialogDescription>
                        Este dialog demuestra el patron basico del MVP con header, body, footer,
                        overlay y acciones claras.
                      </DialogDescription>
                    </DialogHeader>

                    <DialogBody>
                      <Card variant="informative" padding="md" className="grid gap-2">
                        <span className="text-sm font-medium text-muted-foreground">
                          Videojuego seleccionado
                        </span>
                        <strong className="text-lg font-semibold text-foreground">{title}</strong>
                        <p className="text-sm leading-relaxed text-secondary">
                          El formulario puede crecer despues con datos reales, sin rehacer el
                          componente.
                        </p>
                      </Card>

                      <FormField
                        label="Estado de la lista"
                        htmlFor="list-status"
                        helpText="Usa un select nativo ligero mientras el MVP no necesite un select custom."
                      >
                        <select
                          id="list-status"
                          defaultValue="wishlist"
                          className={cn(inputVariants({ size: 'md' }), 'appearance-none pr-10')}
                        >
                          <option value="wishlist">Lo quiero jugar</option>
                          <option value="playing">Lo estoy jugando</option>
                          <option value="completed">Completado</option>
                        </select>
                      </FormField>

                      <FormField
                        label="Nota rapida"
                        htmlFor="personal-note"
                        helpText="Tambien puedes combinar el wrapper con primitives atomicos existentes."
                      >
                        <Input id="personal-note" placeholder="Ejemplo: priorizar este finde" />
                      </FormField>
                    </DialogBody>

                    <DialogFooter>
                      <DialogClose asChild>
                        <Button type="button" variant="secondary">
                          Cancelar
                        </Button>
                      </DialogClose>
                      <DialogClose asChild>
                        <Button type="button">Guardar en biblioteca</Button>
                      </DialogClose>
                    </DialogFooter>
                  </DialogContent>
                </Dialog>

                <Button asChild variant="secondary">
                  <Link href="/catalogo">Volver al catalogo</Link>
                </Button>
              </>
            }
          />

          <Grid variant="contentAside">
            <Card>
              <CardBody className="gap-6">
                <div
                  className="grid aspect-[16/9] place-items-center rounded-xl border border-border"
                  style={{
                    background:
                      'linear-gradient(135deg, var(--color-primary-soft), var(--color-surface))',
                  }}
                >
                  <div className="grid justify-items-center gap-3 text-center">
                    <span className="inline-flex size-16 items-center justify-center rounded-pill bg-primary text-xl font-bold text-primary-foreground shadow-surface">
                      G
                    </span>
                    <div className="grid gap-1">
                      <strong className="text-xl font-semibold text-foreground">{title}</strong>
                      <span className="text-sm text-secondary">Ficha compuesta del MVP</span>
                    </div>
                  </div>
                </div>

                <div className="flex flex-wrap gap-2">
                  <Badge variant="primary">Action RPG</Badge>
                  <Badge>PC</Badge>
                  <Badge>PlayStation 5</Badge>
                  <Badge>Cooperativo online</Badge>
                </div>

                <p className="text-base leading-relaxed text-secondary">
                  Esta vista sirve como referencia para las futuras fichas reales: ya resuelve
                  jerarquia, acciones, metadatos, bloques de detalle y contenido segmentado por
                  tabs.
                </p>
              </CardBody>
            </Card>

            <div className="grid gap-4">
              <Card variant="informative" padding="md" className="grid gap-2">
                <span className="text-sm font-medium text-muted-foreground">Estado</span>
                <strong className="text-2xl font-bold tracking-tight text-foreground">
                  MVP listo
                </strong>
                <p className="text-sm leading-relaxed text-secondary">
                  La ficha ya no depende de layouts placeholder para evolucionar.
                </p>
              </Card>

              <Card>
                <CardHeader>
                  <CardTitle>Resumen rapido</CardTitle>
                  <CardDescription>Metadatos compactos sobre una card reutilizable.</CardDescription>
                </CardHeader>
                <CardBody className="gap-3 pt-4">
                  <div className="grid gap-1">
                    <span className="text-sm font-medium text-muted-foreground">Estudio</span>
                    <span className="text-sm text-foreground">Equipo interno de demo</span>
                  </div>
                  <div className="grid gap-1">
                    <span className="text-sm font-medium text-muted-foreground">Lanzamiento</span>
                    <span className="text-sm text-foreground">2026</span>
                  </div>
                  <div className="grid gap-1">
                    <span className="text-sm font-medium text-muted-foreground">Modo</span>
                    <span className="text-sm text-foreground">Campana narrativa + cooperativo</span>
                  </div>
                </CardBody>
              </Card>
            </div>
          </Grid>
        </div>
      </PageSection>

      <PageSection spacing="compact">
        <Tabs defaultValue="sobre" className="grid gap-6">
          <TabsList>
            <TabsTrigger value="sobre">Sobre</TabsTrigger>
            <TabsTrigger value="publicaciones">Publicaciones</TabsTrigger>
            <TabsTrigger value="videos">Videos</TabsTrigger>
            <TabsTrigger value="screenshots">Screenshots</TabsTrigger>
          </TabsList>

          <TabsContent value="sobre" className="grid gap-6">
            <SectionHeader
              title="Vision general"
              subtitle="Las tabs del MVP ya permiten separar bloques relacionados sin recurrir a estructuras duplicadas."
            />

            <Grid variant="twoColumn">
              <Card>
                <CardHeader>
                  <CardTitle>Por que esta ficha ya es util</CardTitle>
                  <CardDescription>
                    El patron combina informacion principal, acciones y detalle secundario.
                  </CardDescription>
                </CardHeader>
                <CardBody className="pt-4">
                  <ul className="grid gap-3 text-sm leading-relaxed text-secondary">
                    <li>Jerarquia compartida con section header y cards.</li>
                    <li>Accion principal encapsulada en dialog reutilizable.</li>
                    <li>Contenidos segmentados para comunidad, videos y screenshots.</li>
                  </ul>
                </CardBody>
              </Card>

              <Card variant="informative" padding="md" className="grid gap-3">
                <h3 className="text-lg font-semibold tracking-tight text-foreground">
                  Preparada para siguientes fases
                </h3>
                <p className="text-sm leading-relaxed text-secondary">
                  Cuando lleguen los datos reales, esta pantalla podra conectar resenas,
                  biblioteca, videos o capturas manteniendo exactamente la misma estructura visual.
                </p>
                <div className="flex flex-wrap gap-2">
                  <Badge>Tabs</Badge>
                  <Badge>Cards</Badge>
                  <Badge>Dialog</Badge>
                  <Badge>Empty states</Badge>
                </div>
              </Card>
            </Grid>
          </TabsContent>

          <TabsContent value="publicaciones" className="grid gap-6">
            <SectionHeader
              title="Publicaciones de la comunidad"
              subtitle="Las cards con header, body y footer ya sirven para modelar contenido social del MVP."
            />

            <div className="grid gap-4">
              {publications.map((publication) => (
                <Card key={publication.title}>
                  <CardHeader>
                    <div className="flex flex-wrap items-start justify-between gap-3">
                      <div className="grid gap-1">
                        <CardTitle>{publication.title}</CardTitle>
                        <CardDescription>Por {publication.author}</CardDescription>
                      </div>
                      <Badge variant="primary">{publication.status}</Badge>
                    </div>
                  </CardHeader>
                  <CardBody className="pt-4">
                    <p className="text-sm leading-relaxed text-secondary">{publication.summary}</p>
                  </CardBody>
                  <CardFooter>
                    <span className="text-sm font-semibold text-primary">Abrir conversacion</span>
                  </CardFooter>
                </Card>
              ))}
            </div>
          </TabsContent>

          <TabsContent value="videos">
            <EmptyState
              title="Todavia no hay videos publicados"
              description="Este estado vacio comparte tono visual con el resto de la ficha y puede reciclarse en otras secciones sin contenido."
              action={
                <Button asChild variant="secondary">
                  <Link href="/contacto">Proponer integracion</Link>
                </Button>
              }
            />
          </TabsContent>

          <TabsContent value="screenshots">
            <EmptyState
              title="No hay capturas disponibles por ahora"
              description="Las screenshots podran entrar despues como cards o galeria sin cambiar el patron de navegacion local."
            />
          </TabsContent>
        </Tabs>
      </PageSection>
    </>
  );
}


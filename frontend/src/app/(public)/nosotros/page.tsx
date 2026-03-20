import Link from 'next/link';
import { FeatureCard } from '@/shared/components/domain/FeatureCard';
import { Grid } from '@/shared/components/layout/Grid';
import { PageSection } from '@/shared/components/layout/PageSection';
import { Button } from '@/shared/components/ui/Button';
import { Card } from '@/shared/components/ui/Card';

type IconProps = {
  className?: string;
};

function LibraryIcon({ className }: IconProps) {
  return (
    <svg
      aria-hidden="true"
      viewBox="0 0 24 24"
      fill="none"
      stroke="currentColor"
      strokeWidth="2.1"
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

function DiscoveryIcon({ className }: IconProps) {
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

function CommunityIcon({ className }: IconProps) {
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

function LayersIcon({ className }: IconProps) {
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
      <path d="m12 4 8 4.5-8 4.5-8-4.5L12 4Z" />
      <path d="m4 12 8 4.5 8-4.5" />
      <path d="m4 15.5 8 4.5 8-4.5" />
    </svg>
  );
}

function DataIcon({ className }: IconProps) {
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
      <ellipse cx="12" cy="6" rx="6.5" ry="3" />
      <path d="M5.5 6v6c0 1.7 2.9 3 6.5 3s6.5-1.3 6.5-3V6" />
      <path d="M5.5 12v6c0 1.7 2.9 3 6.5 3s6.5-1.3 6.5-3v-6" />
    </svg>
  );
}

function ConnectionIcon({ className }: IconProps) {
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
      <path d="M7.5 7.5h4" />
      <path d="M12.5 7.5h4" />
      <path d="M7.5 16.5h4" />
      <path d="M12.5 16.5h4" />
      <path d="M7.5 7.5v9" />
      <path d="M16.5 7.5v9" />
      <circle cx="7.5" cy="7.5" r="2" />
      <circle cx="16.5" cy="7.5" r="2" />
      <circle cx="7.5" cy="16.5" r="2" />
      <circle cx="16.5" cy="16.5" r="2" />
    </svg>
  );
}

function InterfaceIcon({ className }: IconProps) {
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
      <rect x="3.5" y="5" width="17" height="14" rx="2.5" />
      <path d="M8 9.5h8" />
      <path d="M8 13h5" />
      <path d="M8 16.5h3" />
    </svg>
  );
}

const productAreas = [
  {
    title: 'Biblioteca personal',
    description:
      'Controla estados como lo quiero, jugando o completado, puntua tus juegos y crea listas personalizadas para ordenar tu recorrido.',
    icon: LibraryIcon,
  },
  {
    title: 'Catalogo para descubrir',
    description:
      'El catalogo se alimenta con datos de IGDB para facilitar la exploracion de titulos, plataformas y nueva informacion.',
    icon: DiscoveryIcon,
  },
  {
    title: 'Comunidad y publicaciones',
    description:
      'La parte social conecta jugadores con amistades, publicaciones y grupos para que la experiencia no se quede solo en el seguimiento.',
    icon: CommunityIcon,
  },
] as const;

const quickFacts = [
  {
    label: 'Objetivo',
    value: 'Organizar juegos, descubrir titulos y conectar jugadores.',
  },
  {
    label: 'Enfoque',
    value: 'TFG academico con decisiones pragmaticas y principio KISS.',
  },
  {
    label: 'Arquitectura',
    value: 'Microservicios desacoplados con DDD y arquitectura hexagonal.',
  },
  {
    label: 'Integracion',
    value: 'REST, GraphQL BFF y eventos con RabbitMQ.',
  },
] as const;

const architectureHighlights = [
  {
    title: 'Dominio bien separado',
    description:
      'El backend se reparte en servicios con responsabilidades concretas para mantener el dominio claro, explicable y facil de evolucionar.',
    icon: LayersIcon,
  },
  {
    title: 'Persistencia poliglota',
    description:
      'PostgreSQL, MongoDB, Neo4j, OpenSearch y Redis aparecen donde mejor encajan segun el tipo de dato y consulta.',
    icon: DataIcon,
  },
  {
    title: 'Comunicacion mixta',
    description:
      'Las consultas simples viajan por REST, la agregacion se resuelve con GraphQL y la sincronizacion eventual se apoya en eventos.',
    icon: ConnectionIcon,
  },
  {
    title: 'Frontend moderno',
    description:
      'La interfaz se construye con React, TypeScript y un lenguaje visual cuidado para que el proyecto sea tecnico sin dejar de ser cercano.',
    icon: InterfaceIcon,
  },
] as const;

const guidingPrinciples = [
  'El README deja claro que no busca ser un producto de produccion, sino una demostracion academica seria y defendible.',
  'La filosofia KISS prioriza resolver bien lo importante antes que sobrecargar el sistema con complejidad innecesaria.',
  'La legibilidad del codigo y la capacidad de explicar cada decision son parte del objetivo del proyecto.',
  'El testing se enfoca en los casos principales para sostener el valor demostrativo sin perder el ritmo de desarrollo.',
] as const;

export default function NosotrosPage() {
  return (
    <>
      <PageSection spacing="hero">
        <Card
          variant="informative"
          padding="lg"
          className="rounded-[2rem] border border-[#d8e0ff] bg-gradient-to-br from-[#162246] via-[#274189] to-[#3f63d9] shadow-[0_28px_90px_rgba(22,34,70,0.35)]"
        >
          <Grid variant="feature" className="items-start gap-8">
            <div className="grid gap-6">
              <span className="inline-flex w-fit rounded-full border border-white/20 bg-white/10 px-4 py-2 text-xs font-semibold uppercase tracking-[0.24em] text-white/80">
                Nosotros
              </span>

              <div className="grid gap-4">
                <h1 className="max-w-3xl text-3xl font-bold tracking-tight text-white lg:text-5xl">
                  GameListo es una plataforma social para jugadores nacida como TFG de
                  Ingenieria Informatica.
                </h1>

                <p className="max-w-3xl text-base leading-relaxed text-white/90">
                  El proyecto une biblioteca personal, catalogo y funciones sociales en una misma
                  experiencia para organizar juegos, descubrir titulos y encontrar compania dentro
                  de la comunidad.
                </p>

                <p className="max-w-3xl text-sm leading-relaxed text-white/75">
                  El README la presenta como una propuesta academica desarrollada en la Universidad
                  de Murcia, con interes real por la arquitectura, el despliegue y la experiencia
                  de usuario, pero sin perder un enfoque pragmatico y demostrativo.
                </p>
              </div>

              <div className="flex flex-wrap gap-3">
                <Button
                  asChild
                  variant="secondary"
                  className="border-white/20 bg-white text-slate-900 hover:bg-white/90"
                >
                  <Link href="/catalogo">Explorar catalogo</Link>
                </Button>

                <Button
                  asChild
                  variant="ghost"
                  className="border border-white/15 bg-white/10 text-white! hover:bg-white/20 hover:text-white!"
                >
                  <Link href="/registro">Crear cuenta</Link>
                </Button>
              </div>
            </div>

            <div className="grid gap-4">
              <Card
                variant="informative"
                className="rounded-[1.75rem] border border-white/15 bg-white/10 p-6 backdrop-blur-sm"
              >
                <div className="grid gap-4">
                  <div className="grid gap-2">
                    <p className="text-xs font-semibold uppercase tracking-[0.24em] text-white/70">
                      Resumen rapido
                    </p>
                    <p className="text-sm leading-relaxed text-white/80">
                      GameListo mezcla producto, comunidad y aprendizaje tecnico en una unica base
                      pensada para demostrar criterio de arquitectura y utilidad real.
                    </p>
                  </div>

                  <div className="grid gap-3">
                    {quickFacts.map((fact) => (
                      <div
                        key={fact.label}
                        className="rounded-[1.25rem] border border-white/10 bg-black/10 px-4 py-3"
                      >
                        <p className="text-xs font-semibold uppercase tracking-[0.18em] text-white/60">
                          {fact.label}
                        </p>
                        <p className="mt-1 text-sm font-medium text-white">{fact.value}</p>
                      </div>
                    ))}
                  </div>
                </div>
              </Card>

              <Grid variant="stats" className="gap-3">
                <div className="rounded-[1.5rem] border border-white/12 bg-white/10 px-5 py-4">
                  <p className="text-2xl font-bold text-white">8</p>
                  <p className="text-sm text-white/70">servicios entre gateway, dominios y BFF</p>
                </div>

                <div className="rounded-[1.5rem] border border-white/12 bg-white/10 px-5 py-4">
                  <p className="text-2xl font-bold text-white">5</p>
                  <p className="text-sm text-white/70">tecnologias de datos dentro de la misma arquitectura</p>
                </div>

                <div className="rounded-[1.5rem] border border-white/12 bg-white/10 px-5 py-4">
                  <p className="text-2xl font-bold text-white">1</p>
                  <p className="text-sm text-white/70">
                    idea central: que jugar y con quien compartirlo
                  </p>
                </div>
              </Grid>
            </div>
          </Grid>
        </Card>
      </PageSection>

      <PageSection spacing="compact">
        <div className="grid gap-6">
          <div className="grid gap-3 text-center">
            <p className="text-sm font-semibold uppercase tracking-[0.24em] text-secondary">
              Que ofrece GameListo
            </p>
            <h2 className="text-3xl font-bold tracking-tight text-foreground">
              Una plataforma para ordenar tu biblioteca y vivirla con mas gente
            </h2>
            <p className="mx-auto max-w-3xl text-base leading-relaxed text-secondary">
              El proyecto no gira solo alrededor del catalogo. La propuesta combina seguimiento
              personal, descubrimiento y capa social para que la experiencia gamer tenga contexto
              antes, durante y despues de jugar.
            </p>
          </div>

          <Grid variant="cards">
            {productAreas.map((area) => (
              <FeatureCard
                key={area.title}
                title={area.title}
                description={area.description}
                icon={<area.icon className="size-6" />}
              />
            ))}
          </Grid>
        </div>
      </PageSection>

      <PageSection spacing="compact">
        <Grid variant="contentAside" className="items-start">
          <Card
            padding="lg"
            className="rounded-[2rem] border border-[#e2e8f0] bg-white/80 shadow-[0_24px_60px_rgba(59,63,183,0.08)] backdrop-blur-sm"
          >
            <div className="grid gap-6">
              <div className="grid gap-3">
                <p className="text-sm font-semibold uppercase tracking-[0.24em] text-secondary">
                  Como esta construido
                </p>
                <h2 className="text-3xl font-bold tracking-tight text-foreground">
                  Una arquitectura pensada para aprender haciendo
                </h2>
                <p className="max-w-3xl text-base leading-relaxed text-secondary">
                  Detras de la interfaz hay una base tecnica ambiciosa: microservicios
                  desacoplados, DDD, arquitectura hexagonal, BFF con GraphQL y comunicacion por
                  eventos para modelar un sistema moderno sin perder claridad.
                </p>
              </div>

              <div className="grid gap-4 md:grid-cols-2">
                {architectureHighlights.map((highlight) => (
                  <div
                    key={highlight.title}
                    className="rounded-[1.5rem] border border-[#e2e8f0] bg-surface px-5 py-4"
                  >
                    <div className="flex items-start gap-4">
                      <span className="inline-flex size-11 shrink-0 items-center justify-center rounded-2xl bg-primary/10 text-primary">
                        <highlight.icon className="size-5" />
                      </span>

                      <div className="grid gap-2">
                        <h3 className="text-base font-semibold tracking-tight text-foreground">
                          {highlight.title}
                        </h3>
                        <p className="text-sm leading-relaxed text-secondary">
                          {highlight.description}
                        </p>
                      </div>
                    </div>
                  </div>
                ))}
              </div>
            </div>
          </Card>

          <Card
            padding="lg"
            className="rounded-[2rem] border border-[#e2e8f0] bg-white/75 shadow-[0_24px_60px_rgba(59,63,183,0.06)] backdrop-blur-sm"
          >
            <div className="grid gap-5">
              <div className="grid gap-3">
                <p className="text-sm font-semibold uppercase tracking-[0.24em] text-secondary">
                  Filosofia del proyecto
                </p>
                <h2 className="text-3xl font-bold tracking-tight text-foreground">
                  Un TFG con ambicion tecnica y pies en el suelo
                </h2>
                <p className="text-base leading-relaxed text-secondary">
                  El propio README marca el tono: GameListo quiere ser explicable, util y coherente
                  antes que gigantesco. Esa intencion tambien ayuda a contar mejor quienes somos y
                  por que existe esta plataforma.
                </p>
              </div>

              <ul className="grid gap-3">
                {guidingPrinciples.map((principle) => (
                  <li
                    key={principle}
                    className="flex gap-3 rounded-[1.25rem] border border-[#e2e8f0] bg-white px-4 py-3"
                  >
                    <span className="mt-2 size-2 shrink-0 rounded-full bg-primary" />
                    <span className="text-sm leading-relaxed text-secondary">{principle}</span>
                  </li>
                ))}
              </ul>
            </div>
          </Card>
        </Grid>
      </PageSection>
    </>
  );
}

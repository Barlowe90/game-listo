import Link from 'next/link';
import { getCatalogGames } from '@/features/catalogo/api/catalogApi';
import type { CatalogGameSummary } from '@/features/catalogo/model/catalog.types';
import type {
  AvailabilityMatrixValue,
  AvailabilityPeriod,
} from '@/shared/components/domain/AvailabilityMatrix';
import type { AvatarGroupMember } from '@/shared/components/domain/AvatarGroup';
import { EmptyPublicationsState } from '@/shared/components/domain/EmptyPublicationsState';
import { FilterBar, type FilterGroup } from '@/shared/components/domain/FilterBar';
import { PublicationCard } from '@/shared/components/domain/PublicationCard';
import { PageSection } from '@/shared/components/layout/PageSection';
import { Button } from '@/shared/components/ui/Button';
import { SectionHeader } from '@/shared/components/ui/SectionHeader';

type FilterKey = 'experiencia' | 'estilo' | 'horario' | 'idioma' | 'plataforma';

interface PublicationFilters {
  experiencia: string;
  estilo: string;
  horario: string;
  idioma: string;
  plataforma: string;
}

interface PublicationBlueprint {
  availability: AvailabilityMatrixValue;
  description: string;
  experience: string;
  language: string;
  participants: AvatarGroupMember[];
  style: string;
  timeOfDay: AvailabilityPeriod;
  title: string;
}

interface PublicationRecord {
  availability: AvailabilityMatrixValue;
  description: string;
  experience: string;
  game: CatalogGameSummary;
  id: string;
  language: string;
  participants: AvatarGroupMember[];
  platform: string;
  style: string;
  timeOfDay: AvailabilityPeriod;
  title: string;
}

const PUBLICATION_BLUEPRINTS: PublicationBlueprint[] = [
  {
    title: 'Grupo para empezar campaña cooperativa',
    description:
      'Buscamos 2 personas para jugar sin prisas, con micro y ganas de aprender juntos.',
    language: 'Espanol',
    experience: 'Casual',
    style: 'Cooperativo',
    timeOfDay: 'noche',
    availability: {
      jueves: ['noche'],
      viernes: ['noche'],
      sabado: ['tarde', 'noche'],
    },
    participants: [
      { name: 'Marta Ruiz' },
      { name: 'Alex Cano' },
      { name: 'Jorge Leon' },
    ],
  },
  {
    title: 'Squad fijo para sesiones semanales',
    description:
      'Queremos montar grupo estable para jugar cada semana y coordinarnos por Discord.',
    language: 'Espanol',
    experience: 'Intermedio',
    style: 'Competitivo',
    timeOfDay: 'tarde',
    availability: {
      martes: ['tarde'],
      jueves: ['tarde'],
      domingo: ['tarde'],
    },
    participants: [
      { name: 'Lucia Perez' },
      { name: 'Dani Soto' },
      { name: 'Pablo Vera' },
      { name: 'Noa Vidal' },
    ],
  },
  {
    title: 'Partidas relajadas para descubrir el juego',
    description:
      'Ideal para quien empieza ahora y quiere compartir progresion, dudas y primeras impresiones.',
    language: 'Ingles',
    experience: 'Principiante',
    style: 'Exploracion',
    timeOfDay: 'manana',
    availability: {
      lunes: ['manana'],
      miercoles: ['manana'],
      viernes: ['manana'],
    },
    participants: [
      { name: 'Emma Brooks' },
      { name: 'Leo Martin' },
    ],
  },
  {
    title: 'Grupo flexible para fines de semana',
    description:
      'Nos organizamos segun disponibilidad y priorizamos buen ambiente antes que rendimiento.',
    language: 'Espanol',
    experience: 'Mixto',
    style: 'Social',
    timeOfDay: 'noche',
    availability: {
      sabado: ['tarde', 'noche'],
      domingo: ['tarde', 'noche'],
    },
    participants: [
      { name: 'Sara Medina' },
      { name: 'Ivan Lara' },
      { name: 'Nuria Costa' },
    ],
  },
] as const;

function getSearchValue(value: string | string[] | undefined) {
  if (Array.isArray(value)) {
    return value[0] ?? '';
  }

  return value ?? '';
}

function normalizeFilterValue(value: string | string[] | undefined) {
  return getSearchValue(value).trim();
}

function getPrimaryPlatform(game: CatalogGameSummary) {
  return game.platforms[0] ?? 'Multiplataforma';
}

function buildPublicationRecords(games: CatalogGameSummary[]) {
  const sourceGames = games.slice(0, PUBLICATION_BLUEPRINTS.length);

  return PUBLICATION_BLUEPRINTS.map((blueprint, index) => {
    const game = sourceGames[index] ?? games[index % Math.max(games.length, 1)];

    if (!game) {
      return null;
    }

    return {
      id: `${game.id}-${index}`,
      title: blueprint.title,
      description: blueprint.description,
      language: blueprint.language,
      experience: blueprint.experience,
      style: blueprint.style,
      timeOfDay: blueprint.timeOfDay,
      platform: getPrimaryPlatform(game),
      availability: blueprint.availability,
      participants: blueprint.participants,
      game,
    } satisfies PublicationRecord;
  }).filter((publication): publication is PublicationRecord => Boolean(publication));
}

function matchesFilters(publication: PublicationRecord, filters: PublicationFilters) {
  return (
    (!filters.idioma || publication.language === filters.idioma) &&
    (!filters.experiencia || publication.experience === filters.experiencia) &&
    (!filters.estilo || publication.style === filters.estilo) &&
    (!filters.horario || publication.timeOfDay === filters.horario) &&
    (!filters.plataforma || publication.platform === filters.plataforma)
  );
}

function buildFilterHref(filters: PublicationFilters, key: FilterKey, value: string) {
  const params = new URLSearchParams();
  const nextFilters = {
    ...filters,
    [key]: value,
  };

  (Object.entries(nextFilters) as Array<[FilterKey, string]>).forEach(([entryKey, entryValue]) => {
    if (entryValue) {
      params.set(entryKey, entryValue);
    }
  });

  const queryString = params.toString();

  return queryString ? `/publicaciones?${queryString}` : '/publicaciones';
}

function buildFilterGroup(
  filters: PublicationFilters,
  key: FilterKey,
  label: string,
  options: string[],
) {
  const uniqueOptions = [...new Set(options)].sort((left, right) =>
    left.localeCompare(right, 'es', { sensitivity: 'base' }),
  );

  return {
    label,
    options: [
      {
        label: 'Todos',
        active: !filters[key],
        href: buildFilterHref(filters, key, ''),
      },
      ...uniqueOptions.map((option) => ({
        label: option,
        active: filters[key] === option,
        href: buildFilterHref(filters, key, option),
      })),
    ],
  } satisfies FilterGroup;
}

export default async function PublicacionesPage({
  searchParams,
}: {
  searchParams: Promise<Record<FilterKey, string | string[] | undefined>>;
}) {
  const resolvedSearchParams = await searchParams;
  const games = await getCatalogGames();
  const publications = buildPublicationRecords(games);
  const filters: PublicationFilters = {
    idioma: normalizeFilterValue(resolvedSearchParams.idioma),
    experiencia: normalizeFilterValue(resolvedSearchParams.experiencia),
    estilo: normalizeFilterValue(resolvedSearchParams.estilo),
    horario: normalizeFilterValue(resolvedSearchParams.horario),
    plataforma: normalizeFilterValue(resolvedSearchParams.plataforma),
  };

  const filteredPublications = publications.filter((publication) =>
    matchesFilters(publication, filters),
  );

  const filterGroups: FilterGroup[] = [
    buildFilterGroup(
      filters,
      'idioma',
      'Idioma',
      publications.map((publication) => publication.language),
    ),
    buildFilterGroup(
      filters,
      'experiencia',
      'Experiencia',
      publications.map((publication) => publication.experience),
    ),
    buildFilterGroup(
      filters,
      'estilo',
      'Estilo',
      publications.map((publication) => publication.style),
    ),
    buildFilterGroup(
      filters,
      'horario',
      'Horario',
      publications.map((publication) => publication.timeOfDay),
    ),
    buildFilterGroup(
      filters,
      'plataforma',
      'Plataforma',
      publications.map((publication) => publication.platform),
    ),
  ];

  const hasActiveFilters = Object.values(filters).some(Boolean);

  return (
    <PageSection size="wide">
      <div className="grid gap-8">
        <SectionHeader
          eyebrow="Social"
          title="Encuentra publicaciones y grupos compatibles"
          subtitle="La capa social del MVP ya tiene filtros de dominio, cards reutilizables y disponibilidad semanal sin depender todavia de un backend especifico."
          action={
            <Button asChild>
              <Link href="/login">Crear publicacion</Link>
            </Button>
          }
        />

        <FilterBar
          title="Filtra por idioma, experiencia, estilo, horario y plataforma"
          subtitle="Cada chip mantiene el mismo lenguaje visual del producto y permite explorar grupos sin rehacer estructura."
          groups={filterGroups}
        />

        {filteredPublications.length ? (
          <div className="grid gap-5 xl:grid-cols-2">
            {filteredPublications.map((publication) => (
              <PublicationCard
                key={publication.id}
                title={publication.title}
                description={publication.description}
                game={{
                  title: publication.game.name,
                  href: `/videojuego/${publication.game.id}`,
                }}
                badges={[
                  {
                    label: publication.game.gameModes[0] ?? 'Grupo abierto',
                    variant: 'primary',
                  },
                  { label: publication.platform },
                  { label: publication.language },
                  { label: publication.experience },
                  { label: publication.style },
                ]}
                availability={publication.availability}
                participants={publication.participants}
                cta={
                  <Button asChild>
                    <Link href="/login">Unirme</Link>
                  </Button>
                }
                secondaryAction={
                  <Button asChild variant="ghost">
                    <Link href={`/videojuego/${publication.game.id}`}>Ver ficha</Link>
                  </Button>
                }
              />
            ))}
          </div>
        ) : (
          <EmptyPublicationsState
            title="No hay publicaciones que encajen con esos filtros"
            description="Prueba otra combinacion o vuelve a ver todas las publicaciones disponibles del MVP."
            action={
              <>
                {hasActiveFilters ? (
                  <Button asChild>
                    <Link href="/publicaciones">Limpiar filtros</Link>
                  </Button>
                ) : null}
                <Button asChild variant="secondary">
                  <Link href="/login">Crear la primera</Link>
                </Button>
              </>
            }
          />
        )}
      </div>
    </PageSection>
  );
}

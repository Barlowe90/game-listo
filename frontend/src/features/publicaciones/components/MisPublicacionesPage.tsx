'use client';

import axios from 'axios';
import Link from 'next/link';
import { useEffect, useState } from 'react';
import { getGamesByIds } from '@/features/catalogo/api/catalogApi';
import { useAuth } from '@/features/auth/hooks/useAuth';
import { publicacionesApi } from '@/features/publicaciones/api/publicacionesApi';
import {
  PUBLICACION_DIAS,
  PUBLICACION_ESTILO_JUEGO_OPTIONS,
  PUBLICACION_EXPERIENCIA_OPTIONS,
  PUBLICACION_IDIOMA_OPTIONS,
  type Publicacion,
  type PublicacionDiaSemana,
  type PublicacionDisponibilidad,
  type PublicacionFranjaHoraria,
} from '@/features/publicaciones/model/publicaciones.types';
import {
  AvailabilityMatrix,
  type AvailabilityDay,
  type AvailabilityMatrixValue,
  type AvailabilityPeriod,
} from '@/shared/components/domain/AvailabilityMatrix';
import { EmptyPublicationsState } from '@/shared/components/domain/EmptyPublicationsState';
import { PageSection } from '@/shared/components/layout/PageSection';
import { Badge } from '@/shared/components/ui/Badge';
import { Button } from '@/shared/components/ui/Button';
import { Card } from '@/shared/components/ui/Card';
import { Toast } from '@/shared/components/ui/Toast';

interface ApiErrorResponse {
  error?: string;
  message?: string;
}

const idiomaLabelMap = new Map(
  PUBLICACION_IDIOMA_OPTIONS.map((option) => [option.value, option.label]),
);

const experienciaLabelMap = new Map(
  PUBLICACION_EXPERIENCIA_OPTIONS.map((option) => [option.value, option.label]),
);

const estiloLabelMap = new Map(
  PUBLICACION_ESTILO_JUEGO_OPTIONS.map((option) => [option.value, option.label]),
);

const backendToMatrixDayMap: Record<PublicacionDiaSemana, AvailabilityDay> = {
  LUNES: 'lunes',
  MARTES: 'martes',
  MIERCOLES: 'miercoles',
  JUEVES: 'jueves',
  VIERNES: 'viernes',
  SABADO: 'sabado',
  DOMINGO: 'domingo',
};

const backendToMatrixPeriodMap: Record<PublicacionFranjaHoraria, AvailabilityPeriod> = {
  DIA: 'manana',
  TARDE: 'tarde',
  NOCHE: 'noche',
};

function getApiErrorMessage(error: unknown, fallback: string) {
  if (axios.isAxiosError<ApiErrorResponse>(error)) {
    return error.response?.data?.error ?? error.response?.data?.message ?? fallback;
  }

  return fallback;
}

function getPublicacionesCountLabel(count: number) {
  return `${count} ${count === 1 ? 'publicacion' : 'publicaciones'}`;
}

function mapDisponibilidadToMatrix(
  disponibilidad: PublicacionDisponibilidad | null,
): AvailabilityMatrixValue {
  if (!disponibilidad) {
    return {};
  }

  return PUBLICACION_DIAS.reduce<AvailabilityMatrixValue>((matrix, day) => {
    const franjas = disponibilidad[day.value] ?? [];

    if (!franjas.length) {
      return matrix;
    }

    matrix[backendToMatrixDayMap[day.value]] = franjas.map(
      (franja) => backendToMatrixPeriodMap[franja],
    );

    return matrix;
  }, {});
}

function PublicacionAutorCard({
  publicacion,
  gameTitle,
}: Readonly<{
  publicacion: Publicacion;
  gameTitle?: string;
}>) {
  const gameHref = `/videojuego/${publicacion.gameId}`;
  const resolvedGameTitle = gameTitle ?? `Juego #${publicacion.gameId}`;

  return (
    <Card className="w-full justify-self-start rounded-[calc(var(--radius-xl)+0.5rem)] border border-border bg-white/90 shadow-elevated backdrop-blur-sm sm:max-w-full sm:w-fit">
      <div className="grid gap-5 p-6">
        <div className="grid gap-3">
          <Link
            href={gameHref}
            className="text-sm font-semibold tracking-[0.08em] text-primary uppercase hover:underline"
          >
            {resolvedGameTitle}
          </Link>

          <div className="grid gap-2">
            <h2 className="text-xl font-semibold tracking-tight text-foreground">
              {publicacion.titulo}
            </h2>
          </div>

          <div className="flex flex-wrap gap-2">
            <Badge variant="primary">
              {idiomaLabelMap.get(publicacion.idioma) ?? publicacion.idioma}
            </Badge>
            <Badge>
              {experienciaLabelMap.get(publicacion.experiencia) ?? publicacion.experiencia}
            </Badge>
            <Badge>{estiloLabelMap.get(publicacion.estiloJuego) ?? publicacion.estiloJuego}</Badge>
            <Badge>Hasta {publicacion.jugadoresMaximos} jugadores</Badge>
          </div>
        </div>

        <AvailabilityMatrix
          availability={mapDisponibilidadToMatrix(publicacion.disponibilidad)}
          compact
          stretch
          abbreviatedLabels={false}
        />
      </div>
    </Card>
  );
}

export function MisPublicacionesPage() {
  const { user } = useAuth();
  const userId = user?.id ?? null;
  const [publicaciones, setPublicaciones] = useState<Publicacion[]>([]);
  const [gameTitlesById, setGameTitlesById] = useState<Record<string, string>>({});
  const [isLoading, setIsLoading] = useState(true);
  const [loadError, setLoadError] = useState<string | null>(null);

  useEffect(() => {
    let ignore = false;

    async function loadPublicaciones() {
      if (!userId) {
        setIsLoading(false);
        return;
      }

      setIsLoading(true);
      setLoadError(null);

      try {
        const nextPublicaciones = await publicacionesApi.getPublicacionesPorUsuario(userId);

        if (ignore) {
          return;
        }

        setPublicaciones(nextPublicaciones);

        const gameIds = nextPublicaciones
          .map((publicacion) => Number.parseInt(publicacion.gameId, 10))
          .filter(Number.isFinite);

        if (!gameIds.length) {
          setGameTitlesById({});
          return;
        }

        try {
          const gamesMap = await getGamesByIds(gameIds);

          if (ignore) {
            return;
          }

          setGameTitlesById(
            Object.fromEntries(
              Array.from(gamesMap.entries()).map(([gameId, game]) => [String(gameId), game.name]),
            ),
          );
        } catch {
          if (!ignore) {
            setGameTitlesById({});
          }
        }
      } catch (error) {
        if (ignore) {
          return;
        }

        setLoadError(
          getApiErrorMessage(error, 'No se pudieron cargar tus publicaciones como autor.'),
        );
      } finally {
        if (!ignore) {
          setIsLoading(false);
        }
      }
    }

    void loadPublicaciones();

    return () => {
      ignore = true;
    };
  }, [userId]);

  return (
    <PageSection size="wide">
      <div className="grid gap-8">
        <div className="grid gap-4">
          <div className="flex flex-wrap items-start justify-between gap-4">
            <div className="grid gap-2">
              <h1 className="text-3xl font-bold tracking-tight text-foreground">
                Mis publicaciones
              </h1>
            </div>
          </div>

          <div className="flex flex-wrap items-center gap-3">
            <Badge variant="primary">{getPublicacionesCountLabel(publicaciones.length)}</Badge>
          </div>
        </div>

        {loadError ? <Toast variant="error" title={loadError} /> : null}

        {isLoading ? (
          <Card className="rounded-[calc(var(--radius-xl)+0.5rem)] border border-border bg-white/80 shadow-surface">
            <div className="grid gap-3 p-6">
              <h2 className="text-lg font-semibold tracking-tight text-foreground">
                Cargando tus publicaciones
              </h2>
              <p className="text-sm leading-relaxed text-secondary">
                Estamos recuperando las publicaciones donde figuras como autor.
              </p>
            </div>
          </Card>
        ) : publicaciones.length ? (
          <div className="grid gap-5 xl:grid-cols-2">
            {publicaciones.map((publicacion) => (
              <PublicacionAutorCard
                key={publicacion.id}
                publicacion={publicacion}
                gameTitle={gameTitlesById[publicacion.gameId]}
              />
            ))}
          </div>
        ) : (
          <EmptyPublicationsState
            title="Todavia no has creado publicaciones"
            description="Crea una publicacion desde la ficha de un videojuego para que aparezca aqui como autor."
            action={
              <Button asChild>
                <Link href="/catalogo">Buscar un juego</Link>
              </Button>
            }
          />
        )}
      </div>
    </PageSection>
  );
}

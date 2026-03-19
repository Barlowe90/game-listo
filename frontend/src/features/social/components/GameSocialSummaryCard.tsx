'use client';

import axios from 'axios';
import Link from 'next/link';
import { useEffect, useState } from 'react';
import { useAuth } from '@/features/auth/hooks/useAuth';
import { socialApi } from '@/features/social/api/socialApi';
import type { ResumenSocialJuego, UsuarioRef } from '@/features/social/model/social.types';
import { InfoPanelCard } from '@/shared/components/domain/InfoPanelCard';
import { Avatar } from '@/shared/components/ui/Avatar';
import { Badge } from '@/shared/components/ui/Badge';
import { Button } from '@/shared/components/ui/Button';
import { Skeleton } from '@/shared/components/ui/Skeleton';
import { Toast } from '@/shared/components/ui/Toast';

interface ApiErrorResponse {
  error?: string;
  message?: string;
}

interface GameSocialSummaryCardProps {
  className?: string;
  gameId: number;
}

function getApiErrorMessage(error: unknown, fallback: string) {
  if (axios.isAxiosError<ApiErrorResponse>(error)) {
    return error.response?.data?.error ?? error.response?.data?.message ?? fallback;
  }

  return fallback;
}

function UserPreviewList({
  emptyLabel,
  friends,
  title,
  totalCount,
}: Readonly<{
  emptyLabel: string;
  friends: UsuarioRef[];
  title: string;
  totalCount: number;
}>) {
  const remainingCount = Math.max(totalCount - friends.length, 0);

  return (
    <div className="grid gap-3 rounded-[calc(var(--radius-xl)+0.1rem)] border border-border/70 bg-white/70 p-4">
      <div className="flex items-center justify-between gap-3">
        <span className="text-sm font-semibold text-foreground">{title}</span>
        <Badge variant="primary">{totalCount}</Badge>
      </div>

      {friends.length ? (
        <ul className="flex flex-wrap gap-2">
          {friends.map((friend) => (
            <li key={friend.id}>
              <Link
                href={`/usuario/${friend.id}`}
                className="inline-flex items-center gap-2 rounded-pill border border-border bg-background px-2.5 py-1.5 text-sm font-medium text-foreground transition-colors hover:border-primary/30 hover:bg-primary-soft/60"
              >
                <Avatar name={friend.username} src={friend.avatar} size="sm" />
                <span className="max-w-32 truncate">{friend.username}</span>
              </Link>
            </li>
          ))}
        </ul>
      ) : (
        <p className="text-sm leading-relaxed text-secondary">{emptyLabel}</p>
      )}

      {remainingCount ? (
        <p className="text-xs font-medium text-secondary">+{remainingCount} mas en tu red</p>
      ) : null}
    </div>
  );
}

function GameSocialSummaryLoadingState({
  className,
}: Readonly<Pick<GameSocialSummaryCardProps, 'className'>>) {
  return (
    <InfoPanelCard
      title="Resumen social"
      description="Descubre quien de tu red lo quiere jugar y quien ya esta dentro."
      className={className}
    >
      <div className="grid gap-3 lg:grid-cols-2">
        {[0, 1].map((item) => (
          <div
            key={item}
            className="grid gap-3 rounded-[calc(var(--radius-xl)+0.1rem)] border border-border/70 bg-white/70 p-4"
          >
            <div className="flex items-center justify-between gap-3">
              <Skeleton variant="line" size="sm" className="w-24" />
              <Skeleton variant="line" size="sm" className="w-8" />
            </div>
            <div className="flex flex-wrap gap-2">
              <div className="flex items-center gap-2 rounded-pill border border-border bg-background px-2.5 py-1.5">
                <Skeleton variant="avatar" size="sm" />
                <Skeleton variant="line" size="sm" className="w-16" />
              </div>
              <div className="flex items-center gap-2 rounded-pill border border-border bg-background px-2.5 py-1.5">
                <Skeleton variant="avatar" size="sm" />
                <Skeleton variant="line" size="sm" className="w-20" />
              </div>
            </div>
          </div>
        ))}
      </div>
    </InfoPanelCard>
  );
}

export function GameSocialSummaryCard({ className, gameId }: GameSocialSummaryCardProps) {
  const { status } = useAuth();
  const [summary, setSummary] = useState<ResumenSocialJuego | null>(null);
  const [loadError, setLoadError] = useState<string | null>(null);
  const [isLoading, setIsLoading] = useState(false);
  const [reloadKey, setReloadKey] = useState(0);

  useEffect(() => {
    if (status === 'anonymous') {
      setSummary(null);
      setLoadError(null);
      setIsLoading(false);
      return;
    }

    if (status !== 'authenticated') {
      setIsLoading(true);
      return;
    }

    let ignore = false;

    async function loadSummary() {
      setIsLoading(true);
      setLoadError(null);
      setSummary(null);

      try {
        const nextSummary = await socialApi.getGameSummary(gameId);

        if (ignore) {
          return;
        }

        setSummary(nextSummary);
      } catch (error) {
        if (ignore) {
          return;
        }

        setLoadError(getApiErrorMessage(error, 'No se pudo cargar el resumen social del juego.'));
      } finally {
        if (!ignore) {
          setIsLoading(false);
        }
      }
    }

    void loadSummary();

    return () => {
      ignore = true;
    };
  }, [gameId, reloadKey, status]);

  if (status === 'loading' || (isLoading && !summary)) {
    return <GameSocialSummaryLoadingState className={className} />;
  }

  if (status !== 'authenticated') {
    return (
      <InfoPanelCard
        title="Resumen social"
        description="Conectalo con tu red para ver quien lo desea y quien ya lo esta jugando."
        className={className}
      >
        <div className="grid gap-4 rounded-[calc(var(--radius-xl)+0.1rem)] bg-background p-4">
          <p className="text-sm leading-relaxed text-secondary">
            Inicia sesion para ver este resumen personalizado con la actividad de tus amistades.
          </p>
          <div className="flex flex-wrap gap-3">
            <Button asChild>
              <Link href="/login">Iniciar sesion</Link>
            </Button>
          </div>
        </div>
      </InfoPanelCard>
    );
  }

  if (loadError && !summary) {
    return (
      <InfoPanelCard
        title="Resumen social"
        description="Conectalo con tu red para ver quien lo desea y quien ya lo esta jugando."
        className={className}
      >
        <div className="grid gap-4">
          <Toast variant="error" title={loadError} />
          <div className="flex flex-wrap gap-3">
            <Button
              variant="secondary"
              onClick={() => setReloadKey((currentValue) => currentValue + 1)}
            >
              Reintentar
            </Button>
          </div>
        </div>
      </InfoPanelCard>
    );
  }

  if (!summary) {
    return null;
  }

  return (
    <InfoPanelCard>
      <div className="grid gap-4">
        {loadError ? <Toast variant="error" title={loadError} /> : null}

        <div className="grid gap-3 lg:grid-cols-2">
          <UserPreviewList
            title="Amigos que lo desean"
            totalCount={summary.amigosDeseadoCount}
            friends={summary.amigosDeseadoPreview}
            emptyLabel="Nadie de tu red lo tiene en deseados por ahora."
          />
          <UserPreviewList
            title="Amigos jugando"
            totalCount={summary.amigosJugandoCount}
            friends={summary.amigosJugandoPreview}
            emptyLabel="Todavia no hay amigos jugandolo."
          />
        </div>
      </div>
    </InfoPanelCard>
  );
}

'use client';

import axios from 'axios';
import Link from 'next/link';
import { useEffect, useState } from 'react';
import { useAuth } from '@/features/auth/hooks/useAuth';
import { socialApi } from '@/features/social/api/socialApi';
import type { UsuarioRef } from '@/features/social/model/social.types';
import { InfoPanelCard } from '@/shared/components/domain/InfoPanelCard';
import { Avatar } from '@/shared/components/ui/Avatar';
import { Badge } from '@/shared/components/ui/Badge';
import { Button } from '@/shared/components/ui/Button';
import { Card } from '@/shared/components/ui/Card';
import { EmptyState } from '@/shared/components/ui/EmptyState';
import { SectionHeader } from '@/shared/components/ui/SectionHeader';
import { Skeleton } from '@/shared/components/ui/Skeleton';
import { Toast } from '@/shared/components/ui/Toast';

interface ApiErrorResponse {
  error?: string;
  message?: string;
}

function getApiErrorMessage(error: unknown, fallback: string) {
  if (axios.isAxiosError<ApiErrorResponse>(error)) {
    return error.response?.data?.error ?? error.response?.data?.message ?? fallback;
  }

  return fallback;
}

function getFriendsCountLabel(count: number) {
  return `${count} ${count === 1 ? 'amigo' : 'amigos'}`;
}

function sortFriends(friends: UsuarioRef[]) {
  return [...friends].sort((leftFriend, rightFriend) =>
    leftFriend.username.localeCompare(rightFriend.username, 'es', { sensitivity: 'base' }),
  );
}

function FriendsLoadingState() {
  return (
    <div className="grid gap-6">
      <SectionHeader title="Amigos" />

      <div className="grid gap-4 md:grid-cols-2 xl:grid-cols-3">
        {[0, 1, 2, 3, 4, 5].map((item) => (
          <Card
            key={item}
            className="rounded-[calc(var(--radius-xl)+0.75rem)] border border-border bg-white/90 shadow-elevated backdrop-blur-sm"
          >
            <div className="flex items-center gap-4 p-5">
              <Skeleton variant="avatar" size="lg" />

              <div className="grid min-w-0 flex-1 gap-2">
                <Skeleton variant="line" size="md" className="w-36" />
                <Skeleton variant="line" size="sm" className="w-28" />
              </div>
            </div>
          </Card>
        ))}
      </div>
    </div>
  );
}

function FriendCard({ friend }: Readonly<{ friend: UsuarioRef }>) {
  const profileHref = `/usuario/${friend.id}`;

  return (
    <Card className="rounded-[calc(var(--radius-xl)+0.75rem)] border border-border bg-white/90 shadow-elevated backdrop-blur-sm transition-[border-color,transform,box-shadow] duration-[var(--duration-fast)] ease-[var(--easing-standard)] hover:-translate-y-0.5 hover:border-primary/25">
      <div className="flex items-center gap-4 p-5">
        <Avatar
          name={friend.username}
          src={friend.avatar}
          size="lg"
          className="size-14 shadow-[0_18px_40px_rgba(59,99,183,0.14)]"
        />

        <div className="grid min-w-0 flex-1 gap-3">
          <div className="grid min-w-0 gap-1">
            <h3 className="truncate text-lg font-semibold tracking-tight text-foreground">
              {friend.username}
            </h3>
          </div>

          <div className="flex flex-wrap gap-3">
            <Button asChild variant="secondary" size="sm">
              <Link href={profileHref} aria-label={`Ver perfil de ${friend.username}`}>
                Ver perfil
              </Link>
            </Button>
          </div>
        </div>
      </div>
    </Card>
  );
}

export function ProfileFriendsSection() {
  const { status } = useAuth();
  const [friends, setFriends] = useState<UsuarioRef[]>([]);
  const [friendsError, setFriendsError] = useState<string | null>(null);
  const [isLoadingFriends, setIsLoadingFriends] = useState(false);
  const [reloadKey, setReloadKey] = useState(0);

  useEffect(() => {
    if (status === 'anonymous') {
      setFriends([]);
      setFriendsError(null);
      setIsLoadingFriends(false);
      return;
    }

    if (status !== 'authenticated') {
      setIsLoadingFriends(true);
      return;
    }

    let ignore = false;

    async function loadFriends() {
      setIsLoadingFriends(true);
      setFriendsError(null);

      try {
        const nextFriends = await socialApi.listFriends();

        if (ignore) {
          return;
        }

        setFriends(sortFriends(nextFriends));
      } catch (error) {
        if (ignore) {
          return;
        }

        setFriendsError(getApiErrorMessage(error, 'No se pudieron cargar tus amigos.'));
      } finally {
        if (!ignore) {
          setIsLoadingFriends(false);
        }
      }
    }

    void loadFriends();

    return () => {
      ignore = true;
    };
  }, [reloadKey, status]);

  if (status === 'loading' || (isLoadingFriends && !friends.length)) {
    return <FriendsLoadingState />;
  }

  if (status !== 'authenticated') {
    return (
      <div className="grid gap-6">
        <SectionHeader title="Amigos" />
        <EmptyState
          title="Inicia sesion para ver tus amigos"
          description="La lista de amistades se carga desde tu perfil autenticado."
          action={
            <Button asChild>
              <Link href="/login">Iniciar sesion</Link>
            </Button>
          }
        />
      </div>
    );
  }

  if (friendsError && !friends.length) {
    return (
      <div className="grid gap-6">
        <SectionHeader
          title="Amigos"
          action={
            <Button onClick={() => setReloadKey((currentValue) => currentValue + 1)}>
              Reintentar
            </Button>
          }
        />

        <Toast variant="error" title={friendsError} />

        <InfoPanelCard title="No pudimos cargar tu lista de amigos">
          <div className="flex flex-wrap justify-end gap-3"></div>
        </InfoPanelCard>
      </div>
    );
  }

  return (
    <div className="grid gap-6">
      <SectionHeader
        title="Amigos"
        action={
          <div className="flex flex-wrap items-center gap-3">
            <Badge variant="primary">{getFriendsCountLabel(friends.length)}</Badge>
          </div>
        }
      />

      {friendsError ? <Toast variant="error" title={friendsError} /> : null}

      {friends.length ? (
        <div className="grid gap-4 md:grid-cols-2 xl:grid-cols-3">
          {friends.map((friend) => (
            <FriendCard key={friend.id} friend={friend} />
          ))}
        </div>
      ) : (
        <EmptyState title="Todavia no tienes amigos agregados" />
      )}
    </div>
  );
}

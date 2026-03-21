'use client';

import type {
  Publicacion,
  PublicacionDetalle,
} from '@/features/publicaciones/model/publicaciones.types';
import { PublicacionCard } from '@/features/publicaciones/components/PublicacionCard';
import {
  Dialog,
  DialogBody,
  DialogContent,
  DialogDescription,
  DialogHeader,
  DialogTitle,
} from '@/shared/components/ui/Dialog';

interface PublicacionInfoDialogProps {
  currentUserId: string | null;
  disableActions: boolean;
  gameTitle?: string;
  onLeaveGroup: (publicacion: Publicacion) => void;
  onOpenChange: (open: boolean) => void;
  onViewGroupInfo: (publicacion: Publicacion) => void;
  publicacion: PublicacionDetalle | null;
}

export function PublicacionInfoDialog({
  currentUserId,
  disableActions,
  gameTitle,
  onLeaveGroup,
  onOpenChange,
  onViewGroupInfo,
  publicacion,
}: Readonly<PublicacionInfoDialogProps>) {
  const isOpen = publicacion !== null;
  const isAuthor = currentUserId === publicacion?.autorId;

  function handleViewGroupInfo() {
    if (!publicacion) {
      return;
    }

    onOpenChange(false);
    onViewGroupInfo(publicacion);
  }

  function handleLeaveGroup() {
    if (!publicacion) {
      return;
    }

    onOpenChange(false);
    onLeaveGroup(publicacion);
  }

  return (
    <Dialog open={isOpen} onOpenChange={onOpenChange}>
      <DialogContent className="max-w-5xl p-0">
        <DialogHeader className="gap-2 border-b border-border px-6 pt-6 pb-0">
          <DialogTitle>Informacion de la publicacion</DialogTitle>
          <DialogDescription>
            Aqui puedes revisar los detalles completos de la publicacion a la que te has unido.
          </DialogDescription>
        </DialogHeader>

        <DialogBody className="px-6 pb-6">
          {publicacion ? (
            <PublicacionCard
              publicacion={publicacion}
              participantes={publicacion.participantes}
              gameTitle={gameTitle}
              showGameLink
              disableActions={disableActions}
              onLeaveGroup={isAuthor ? undefined : handleLeaveGroup}
              onViewGroupInfo={publicacion.grupoId ? handleViewGroupInfo : undefined}
            />
          ) : null}
        </DialogBody>
      </DialogContent>
    </Dialog>
  );
}

'use client';

import type { Publicacion } from '@/features/publicaciones/model/publicaciones.types';
import { Button } from '@/shared/components/ui/Button';
import {
  Dialog,
  DialogBody,
  DialogContent,
  DialogDescription,
  DialogFooter,
  DialogHeader,
  DialogTitle,
} from '@/shared/components/ui/Dialog';

interface PublicacionAbandonarGrupoDialogProps {
  open: boolean;
  publicacion: Publicacion | null;
  isLeaving: boolean;
  onOpenChange: (open: boolean) => void;
  onConfirm: () => Promise<void> | void;
}

export function PublicacionAbandonarGrupoDialog({
  open,
  publicacion,
  isLeaving,
  onOpenChange,
  onConfirm,
}: Readonly<PublicacionAbandonarGrupoDialogProps>) {
  return (
    <Dialog open={open} onOpenChange={onOpenChange}>
      <DialogContent className="max-w-lg">
        <DialogHeader>
          <DialogTitle>Abandonar grupo</DialogTitle>
          <DialogDescription>
            Saldras del grupo vinculado a esta publicacion. Si quieres volver, tendras que seguir
            el flujo disponible en la aplicacion.
          </DialogDescription>
        </DialogHeader>

        <DialogBody>
          <p className="text-sm leading-relaxed text-secondary">
            Vas a abandonar el grupo de{' '}
            <strong className="font-semibold text-foreground">{publicacion?.titulo}</strong>.
          </p>
        </DialogBody>

        <DialogFooter>
          <Button
            type="button"
            variant="secondary"
            onClick={() => onOpenChange(false)}
            disabled={isLeaving}
          >
            Cancelar
          </Button>
          <Button
            type="button"
            variant="destructive"
            onClick={() => void onConfirm()}
            loading={isLeaving}
          >
            Abandonar grupo
          </Button>
        </DialogFooter>
      </DialogContent>
    </Dialog>
  );
}

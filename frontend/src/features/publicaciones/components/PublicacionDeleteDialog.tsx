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

interface PublicacionDeleteDialogProps {
  open: boolean;
  publicacion: Publicacion | null;
  isDeleting: boolean;
  onOpenChange: (open: boolean) => void;
  onConfirm: () => Promise<void> | void;
}

export function PublicacionDeleteDialog({
  open,
  publicacion,
  isDeleting,
  onOpenChange,
  onConfirm,
}: Readonly<PublicacionDeleteDialogProps>) {
  return (
    <Dialog open={open} onOpenChange={onOpenChange}>
      <DialogContent className="max-w-lg">
        <DialogHeader>
          <DialogTitle>Eliminar publicacion</DialogTitle>
          <DialogDescription>
            Esta accion eliminara la publicacion, su grupo y las solicitudes asociadas. No se puede
            deshacer.
          </DialogDescription>
        </DialogHeader>

        <DialogBody>
          <p className="text-sm leading-relaxed text-secondary">
            Vas a eliminar{' '}
            <strong className="font-semibold text-foreground">{publicacion?.titulo}</strong>.
          </p>
        </DialogBody>

        <DialogFooter>
          <Button
            type="button"
            variant="secondary"
            onClick={() => onOpenChange(false)}
            disabled={isDeleting}
          >
            Cancelar
          </Button>
          <Button
            type="button"
            variant="destructive"
            onClick={() => void onConfirm()}
            loading={isDeleting}
          >
            Eliminar publicacion
          </Button>
        </DialogFooter>
      </DialogContent>
    </Dialog>
  );
}

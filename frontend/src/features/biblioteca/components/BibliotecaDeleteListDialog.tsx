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

interface BibliotecaDeleteListDialogProps {
  isDeletingList: boolean;
  listaNombre: string | undefined;
  onConfirm: () => void;
  onOpenChange: (open: boolean) => void;
  open: boolean;
}

export function BibliotecaDeleteListDialog({
  isDeletingList,
  listaNombre,
  onConfirm,
  onOpenChange,
  open,
}: Readonly<BibliotecaDeleteListDialogProps>) {
  return (
    <Dialog open={open} onOpenChange={onOpenChange}>
      <DialogContent className="max-w-lg">
        <DialogHeader>
          <DialogTitle>Eliminar lista</DialogTitle>
          <DialogDescription>
            Esta accion eliminara la lista personalizada y no se puede deshacer.
          </DialogDescription>
        </DialogHeader>

        <DialogBody>
          <p className="text-sm leading-relaxed text-secondary">
            Vas a eliminar <strong className="font-semibold text-foreground">{listaNombre}</strong>.
          </p>
        </DialogBody>

        <DialogFooter>
          <Button
            type="button"
            variant="secondary"
            onClick={() => onOpenChange(false)}
            disabled={isDeletingList}
          >
            Cancelar
          </Button>
          <Button type="button" variant="destructive" onClick={onConfirm} loading={isDeletingList}>
            Eliminar lista
          </Button>
        </DialogFooter>
      </DialogContent>
    </Dialog>
  );
}

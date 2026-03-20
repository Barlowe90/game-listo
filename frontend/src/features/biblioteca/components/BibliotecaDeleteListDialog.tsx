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
import { BIBLIOTECA_LIST_TEXT } from './biblioteca.shared';

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
          <DialogTitle>{BIBLIOTECA_LIST_TEXT.deleteList}</DialogTitle>
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
            {BIBLIOTECA_LIST_TEXT.cancel}
          </Button>
          <Button type="button" variant="destructive" onClick={onConfirm} loading={isDeletingList}>
            {BIBLIOTECA_LIST_TEXT.deleteList}
          </Button>
        </DialogFooter>
      </DialogContent>
    </Dialog>
  );
}

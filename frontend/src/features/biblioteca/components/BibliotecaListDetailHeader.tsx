import Image from 'next/image';
import Link from 'next/link';
import type { BibliotecaLista } from '@/features/biblioteca/model/biblioteca.types';
import { Button } from '@/shared/components/ui/Button';
import { Input } from '@/shared/components/ui/Input';
import { SectionHeader } from '@/shared/components/ui/SectionHeader';

interface BibliotecaListDetailHeaderProps {
  backHref: string;
  canManageList: boolean;
  isDeletingList: boolean;
  isEditingName: boolean;
  isSavingName: boolean;
  lista: BibliotecaLista | null;
  nombreDraft: string;
  nombreError: string | null;
  onBeginEditing: () => void;
  onCancelEditing: () => void;
  onDeleteClick: () => void;
  onDraftChange: (value: string) => void;
  onSave: () => void;
}

export function BibliotecaListDetailHeader({
  backHref,
  canManageList,
  isDeletingList,
  isEditingName,
  isSavingName,
  lista,
  nombreDraft,
  nombreError,
  onBeginEditing,
  onCancelEditing,
  onDeleteClick,
  onDraftChange,
  onSave,
}: Readonly<BibliotecaListDetailHeaderProps>) {
  return (
    <SectionHeader
      title={
        lista ? (
          <span className="inline-flex flex-wrap items-center gap-3">
            {isEditingName ? (
              <Input
                value={nombreDraft}
                onChange={(event) => onDraftChange(event.target.value)}
                onKeyDown={(event) => {
                  if (event.key === 'Enter') {
                    event.preventDefault();
                    onSave();
                  }

                  if (event.key === 'Escape') {
                    event.preventDefault();
                    onCancelEditing();
                  }
                }}
                autoFocus
                disabled={isSavingName}
                state={nombreError ? 'error' : 'default'}
                className="w-[min(26rem,70vw)] bg-white"
                aria-label="Editar nombre de la lista"
              />
            ) : (
              <span>{lista.nombre}</span>
            )}

            {canManageList && !isEditingName ? (
              <button
                type="button"
                onClick={onBeginEditing}
                className="inline-flex size-10 items-center justify-center rounded-pill border border-border bg-white/80 transition-colors hover:border-border-strong hover:bg-white"
                aria-label="Editar nombre de la lista"
              >
                <Image
                  src="/lapiz_editar.svg"
                  alt=""
                  width={18}
                  height={18}
                  className="size-[18px]"
                />
              </button>
            ) : null}
          </span>
        ) : (
          'Detalle de lista'
        )
      }
      action={
        <div className="flex flex-wrap items-center gap-3">
          {canManageList ? (
            <Button variant="destructive" onClick={onDeleteClick} disabled={isDeletingList}>
              Eliminar lista
            </Button>
          ) : null}
          <Button asChild variant="secondary">
            <Link href={backHref}>Volver a biblioteca</Link>
          </Button>
        </div>
      }
    />
  );
}

import axios from 'axios';
import type { BibliotecaLista } from '@/features/biblioteca/model/biblioteca.types';
import { cn } from '@/lib/cn';

interface ApiErrorResponse {
  error?: string;
  errors?: Record<string, string>;
  message?: string;
}

interface BibliotecaListNameValidationResult {
  errorMessage: string | null;
  normalizedName: string;
}

export const LIST_NAME_PATTERN = /^[a-zA-Z0-9 _-]{3,30}$/;

export const BIBLIOTECA_LIST_TEXT = {
  backToLibrary: 'Volver a biblioteca',
  cancel: 'Cancelar',
  deleteList: 'Eliminar lista',
  editName: 'Editar nombre de la lista',
  reservedName: 'Ese nombre esta reservado para una lista oficial.',
  requiredName: 'Introduce un nombre para la lista.',
  validNameHint:
    'Usa entre 3 y 30 caracteres con letras, numeros, espacios, guiones o guiones bajos.',
} as const;

export function getApiErrorMessage(error: unknown, fallback: string) {
  if (axios.isAxiosError<ApiErrorResponse>(error)) {
    const responseData = error.response?.data;

    return responseData?.error ?? responseData?.message ?? fallback;
  }

  return fallback;
}

export function getApiFieldErrorMessage(error: unknown, field: string) {
  if (!axios.isAxiosError<ApiErrorResponse>(error)) {
    return null;
  }

  return error.response?.data?.errors?.[field] ?? null;
}

export function getGameCountLabel(count: number, suffix?: string) {
  const baseLabel = `${count} ${count === 1 ? 'juego' : 'juegos'}`;
  return suffix ? `${baseLabel} ${suffix}` : baseLabel;
}

export function validateBibliotecaListName(
  nameDraft: string,
  officialListNames: Set<string>,
): BibliotecaListNameValidationResult {
  const normalizedName = nameDraft.trim();

  if (!normalizedName) {
    return {
      errorMessage: BIBLIOTECA_LIST_TEXT.requiredName,
      normalizedName,
    };
  }

  if (!LIST_NAME_PATTERN.test(normalizedName)) {
    return {
      errorMessage: BIBLIOTECA_LIST_TEXT.validNameHint,
      normalizedName,
    };
  }

  if (officialListNames.has(normalizedName.toUpperCase())) {
    return {
      errorMessage: BIBLIOTECA_LIST_TEXT.reservedName,
      normalizedName,
    };
  }

  return {
    errorMessage: null,
    normalizedName,
  };
}

export function ListTypeBadge({ tipo }: Readonly<{ tipo: BibliotecaLista['tipo'] }>) {
  const isPersonalizada = tipo === 'PERSONALIZADA';

  return (
    <span
      className={cn(
        'inline-flex items-center rounded-pill px-3 py-1 text-[11px] font-semibold tracking-[0.08em] uppercase',
        isPersonalizada ? 'bg-primary-soft text-primary' : 'bg-surface text-muted-foreground',
      )}
    >
      {isPersonalizada ? 'Personalizada' : 'Oficial'}
    </span>
  );
}

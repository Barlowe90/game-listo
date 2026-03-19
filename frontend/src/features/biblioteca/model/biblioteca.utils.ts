import {
  BIBLIOTECA_ESTADOS,
  type BibliotecaEstado,
  type BibliotecaLista,
} from '@/features/biblioteca/model/biblioteca.types';

export function formatBibliotecaEnumLabel(value: string) {
  return value
    .toLowerCase()
    .split('_')
    .filter(Boolean)
    .map((chunk) => chunk.charAt(0).toUpperCase() + chunk.slice(1))
    .join(' ');
}

export function getOfficialListNames(lists: BibliotecaLista[]) {
  return new Set(
    lists
      .filter((lista) => lista.tipo === 'OFICIAL')
      .map((lista) => lista.nombre.trim().toUpperCase())
      .filter(Boolean),
  );
}

export function isBibliotecaEstado(value: string): value is BibliotecaEstado {
  return BIBLIOTECA_ESTADOS.includes(value as BibliotecaEstado);
}

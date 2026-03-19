import { httpClient } from '@/features/auth/api/httpClient';
import type {
  BibliotecaLista,
  CrearBibliotecaListaPayload,
} from '@/features/biblioteca/model/biblioteca.types';

async function getUserLists(): Promise<BibliotecaLista[]> {
  const response = await httpClient.get<BibliotecaLista[]>('/v1/biblioteca/lists');

  return response.data;
}

async function getListById(listaId: string): Promise<BibliotecaLista> {
  const response = await httpClient.get<BibliotecaLista>(`/v1/biblioteca/lists/${listaId}`);

  return response.data;
}

async function updateListName(listaId: string, nombre: string): Promise<BibliotecaLista> {
  const response = await httpClient.patch<BibliotecaLista>(`/v1/biblioteca/lists/${listaId}`, {
    nombre,
  });

  return response.data;
}

async function deleteList(listaId: string): Promise<void> {
  await httpClient.delete(`/v1/biblioteca/lists/${listaId}`);
}

async function createList({
  nombre,
  tipo = 'PERSONALIZADA',
}: CrearBibliotecaListaPayload): Promise<BibliotecaLista> {
  const response = await httpClient.post<BibliotecaLista>('/v1/biblioteca/lists', {
    nombre,
    tipo,
  });

  return response.data;
}

export const bibliotecaApi = {
  createList,
  deleteList,
  getListById,
  getUserLists,
  updateListName,
};

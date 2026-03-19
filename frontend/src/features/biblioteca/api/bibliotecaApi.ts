import { httpClient } from '@/features/auth/api/httpClient';
import type {
  BibliotecaEstado,
  BibliotecaGameEstado,
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

async function getGameStates(gameId: number): Promise<BibliotecaGameEstado[]> {
  const response = await httpClient.get<BibliotecaGameEstado[]>(`/v1/biblioteca/games/${gameId}`);

  return response.data;
}

async function createGameState(gameId: number, estado: BibliotecaEstado): Promise<void> {
  await httpClient.post(`/v1/biblioteca/games/${gameId}/state`, {
    estado,
  });
}

async function rateGame(gameId: number, rating: number): Promise<void> {
  await httpClient.post(`/v1/biblioteca/games/${gameId}/rate`, {
    rating,
  });
}

async function addGameToList(listaId: string, gameId: number): Promise<void> {
  await httpClient.post(`/v1/biblioteca/lists/${listaId}/games/${gameId}`, null);
}

async function removeGameFromList(listaId: string, gameId: number): Promise<void> {
  await httpClient.delete(`/v1/biblioteca/lists/${listaId}/games/${gameId}`);
}

export const bibliotecaApi = {
  addGameToList,
  createGameState,
  createList,
  deleteList,
  getGameStates,
  getListById,
  getUserLists,
  removeGameFromList,
  rateGame,
  updateListName,
};

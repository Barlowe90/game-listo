package com.gamelisto.biblioteca.domain;

import java.util.List;

public interface ListaGameItemRepositorio {
  void add(ListaGameId listaId, GameId gameRefId);

  void remove(ListaGameId listaId, GameId gameRefId);

  boolean exists(ListaGameId listaId, GameId gameRefId);

  List<GameId> findGameIdsByListaId(ListaGameId listaId);

  void deleteAllByListaId(ListaGameId listaId);
}

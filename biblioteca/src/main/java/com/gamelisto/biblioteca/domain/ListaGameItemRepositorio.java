package com.gamelisto.biblioteca.domain;

import java.util.List;
import java.util.UUID;

public interface ListaGameItemRepositorio {
  void add(UUID listaId, Long gameRefId);

  void remove(UUID listaId, Long gameRefId);

  boolean exists(UUID listaId, Long gameRefId);

  List<Long> findGameIdsByListaId(UUID listaId);

  void deleteAllByListaId(UUID listaId);
}

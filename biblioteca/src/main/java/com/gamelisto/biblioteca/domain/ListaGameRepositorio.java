package com.gamelisto.biblioteca.domain;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ListaGameRepositorio {
  ListaGame save(ListaGame listaGame);

  Optional<ListaGame> findById(ListaGameId id);

  List<ListaGame> findByUsuarioRefId(UUID usuarioRefId);

  void deleteById(ListaGameId id);
}

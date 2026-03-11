package com.gamelisto.biblioteca.domain;

import java.util.List;
import java.util.Optional;

public interface ListaGameRepositorio {
  ListaGame save(ListaGame listaGame);

  Optional<ListaGame> findById(ListaGameId id);

  List<ListaGame> findByUsuarioRefId(UsuarioId usuarioRefId);

  void deleteById(ListaGameId id);
}

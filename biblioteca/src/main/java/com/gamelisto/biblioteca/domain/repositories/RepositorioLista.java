package com.gamelisto.biblioteca.domain.repositories;

import com.gamelisto.biblioteca.domain.listas.ListaGame;
import com.gamelisto.biblioteca.domain.listas.ListaGameId;

import java.util.List;
import java.util.Optional;

public interface RepositorioLista {
  ListaGame save(ListaGame listaGame);

  Optional<ListaGame> findById(ListaGameId id);

  List<ListaGame> findAll();

  void delete(ListaGame listaGame);
}

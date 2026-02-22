package com.gamelisto.biblioteca.infrastructure.out.persistence.postgres.repository;

import com.gamelisto.biblioteca.domain.listas.ListaGame;
import com.gamelisto.biblioteca.domain.listas.ListaGameId;
import com.gamelisto.biblioteca.domain.repositories.RepositorioLista;
import com.gamelisto.biblioteca.infrastructure.out.persistence.postgres.mapper.ListaGameMapper;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public class RepositorioListaGamePostgre implements RepositorioLista {

  private final ListaGameJpaRepository jpaRepository;
  private final ListaGameMapper mapper;

  public RepositorioListaGamePostgre(ListaGameJpaRepository jpaRepository, ListaGameMapper mapper) {
    this.jpaRepository = jpaRepository;
    this.mapper = mapper;
  }

  @Override
  public ListaGame save(ListaGame listaGame) {
    return null;
  }

  @Override
  public Optional<ListaGame> findById(ListaGameId id) {
    return Optional.empty();
  }

  @Override
  public List<ListaGame> findAll() {
    return List.of();
  }

  @Override
  public void delete(ListaGameId id) {}
}

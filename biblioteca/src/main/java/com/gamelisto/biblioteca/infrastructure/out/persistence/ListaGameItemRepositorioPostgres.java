package com.gamelisto.biblioteca.infrastructure.out.persistence;

import com.gamelisto.biblioteca.domain.ListaGameItemRepositorio;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public class ListaGameItemRepositorioPostgres implements ListaGameItemRepositorio {
  private final ListaGameItemJpaRepository jpa;

  public ListaGameItemRepositorioPostgres(ListaGameItemJpaRepository jpa) {
    this.jpa = jpa;
  }

  @Override
  public void add(com.gamelisto.biblioteca.domain.ListaGameId listaId,
      com.gamelisto.biblioteca.domain.GameId gameRefId) {
    ListaGameItemId id = new ListaGameItemId(listaId.value(), gameRefId.value());

    if (jpa.existsById(id)) {
      return;
    }

    ListaGameItemEntity e = new ListaGameItemEntity();
    e.setListaId(listaId.value());
    e.setGameRefId(gameRefId.value());
    jpa.save(e);
  }

  @Override
  public void remove(com.gamelisto.biblioteca.domain.ListaGameId listaId,
      com.gamelisto.biblioteca.domain.GameId gameRefId) {
    jpa.deleteById(new ListaGameItemId(listaId.value(), gameRefId.value()));
  }

  @Override
  public boolean exists(com.gamelisto.biblioteca.domain.ListaGameId listaId,
      com.gamelisto.biblioteca.domain.GameId gameRefId) {
    return jpa.existsById(new ListaGameItemId(listaId.value(), gameRefId.value()));
  }

  @Override
  public List<com.gamelisto.biblioteca.domain.GameId> findGameIdsByListaId(
      com.gamelisto.biblioteca.domain.ListaGameId listaId) {
    return jpa.findByListaId(listaId.value()).stream()
        .map(e -> com.gamelisto.biblioteca.domain.GameId.of(e.getGameRefId())).toList();
  }

  @Override
  public void deleteAllByListaId(com.gamelisto.biblioteca.domain.ListaGameId listaId) {
    jpa.deleteByListaId(listaId.value());
  }
}

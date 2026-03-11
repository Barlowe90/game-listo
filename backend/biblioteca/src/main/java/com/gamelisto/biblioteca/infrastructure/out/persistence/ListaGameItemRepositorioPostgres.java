package com.gamelisto.biblioteca.infrastructure.out.persistence;

import com.gamelisto.biblioteca.domain.GameId;
import com.gamelisto.biblioteca.domain.ListaGameId;
import com.gamelisto.biblioteca.domain.ListaGameItemRepositorio;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class ListaGameItemRepositorioPostgres implements ListaGameItemRepositorio {
  private final ListaGameItemJpaRepository jpa;

  public ListaGameItemRepositorioPostgres(ListaGameItemJpaRepository jpa) {
    this.jpa = jpa;
  }

  @Override
  public void add(ListaGameId listaId, GameId gameRefId) {
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
  public void remove(ListaGameId listaId, GameId gameRefId) {
    jpa.deleteById(new ListaGameItemId(listaId.value(), gameRefId.value()));
  }

  @Override
  public boolean exists(ListaGameId listaId, GameId gameRefId) {
    return jpa.existsById(new ListaGameItemId(listaId.value(), gameRefId.value()));
  }

  @Override
  public List<GameId> findGameIdsByListaId(ListaGameId listaId) {
    return jpa.findByListaId(listaId.value()).stream()
        .map(e -> GameId.of(e.getGameRefId()))
        .toList();
  }

  @Override
  public void deleteAllByListaId(ListaGameId listaId) {
    jpa.deleteByListaId(listaId.value());
  }
}

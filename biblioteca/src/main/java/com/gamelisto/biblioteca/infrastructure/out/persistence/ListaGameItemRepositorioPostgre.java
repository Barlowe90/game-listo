package com.gamelisto.biblioteca.infrastructure.out.persistence;

import com.gamelisto.biblioteca.domain.ListaGameItemRepositorio;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public class ListaGameItemRepositorioPostgre implements ListaGameItemRepositorio {
  private final ListaGameItemJpaRepository jpa;

  public ListaGameItemRepositorioPostgre(ListaGameItemJpaRepository jpa) {
    this.jpa = jpa;
  }

  @Override
  public void add(UUID listaId, Long gameRefId) {
    ListaGameItemId id = new ListaGameItemId(listaId, gameRefId);

    if (jpa.existsById(id)) {
      return;
    }

    ListaGameItemEntity e = new ListaGameItemEntity();
    e.setListaId(listaId);
    e.setGameRefId(gameRefId);
    jpa.save(e);
  }

  @Override
  public void remove(UUID listaId, Long gameRefId) {
    jpa.deleteById(new ListaGameItemId(listaId, gameRefId));
  }

  @Override
  public boolean exists(UUID listaId, Long gameRefId) {
    return jpa.existsById(new ListaGameItemId(listaId, gameRefId));
  }

  @Override
  public List<Long> findGameIdsByListaId(UUID listaId) {
    return jpa.findByListaId(listaId).stream().map(ListaGameItemEntity::getGameRefId).toList();
  }

  @Override
  public void deleteAllByListaId(UUID listaId) {
    jpa.deleteByListaId(listaId);
  }
}

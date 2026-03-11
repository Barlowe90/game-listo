package com.gamelisto.biblioteca.infrastructure.out.persistence;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface ListaGameItemJpaRepository
    extends JpaRepository<ListaGameItemEntity, ListaGameItemId> {
  List<ListaGameItemEntity> findByListaId(UUID listaId);

  void deleteByListaId(UUID listaId);
}

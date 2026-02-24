package com.gamelisto.biblioteca.infrastructure.out.persistence.postgres.repository;

import com.gamelisto.biblioteca.domain.listas.ListaGame;
import com.gamelisto.biblioteca.domain.listas.ListaGameId;
import com.gamelisto.biblioteca.domain.repositories.RepositorioLista;
import com.gamelisto.biblioteca.infrastructure.out.persistence.postgres.entity.ListaGameEntity;
import com.gamelisto.biblioteca.infrastructure.out.persistence.postgres.mapper.ListaGameMapper;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

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
    ListaGameEntity entity = mapper.toEntity(listaGame);
    ListaGameEntity savedEntity = jpaRepository.save(entity);
    return mapper.toDomain(savedEntity);
  }

  @Override
  public Optional<ListaGame> findById(ListaGameId id) {
    return jpaRepository.findById(id.value()).map(mapper::toDomain);
  }

  @Override
  public List<ListaGame> findAll() {
    return jpaRepository.findAll().stream().map(mapper::toDomain).toList();
  }

  @Override
  public void delete(ListaGame listaGame) {
    ListaGameEntity entity = mapper.toEntity(listaGame);
    jpaRepository.delete(entity);
  }

  @Override
  public List<ListaGame> findByUsuarioRefId(UUID usuarioRefId) {
    return jpaRepository.findByUsuarioRef_Id(usuarioRefId).stream().map(mapper::toDomain).toList();
  }
}

package com.gamelisto.biblioteca.infrastructure.out.persistence;

import com.gamelisto.biblioteca.domain.ListaGame;
import com.gamelisto.biblioteca.domain.ListaGameId;
import com.gamelisto.biblioteca.domain.ListaGameRepositorio;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public class ListaGameRepositorioPostgre implements ListaGameRepositorio {

  private final ListaGameJpaRepository listaJpa;
  private final ListaGameMapper mapper;
  private final UsuarioRefJpaRepository usuarioRefJpa;

  public ListaGameRepositorioPostgre(
      ListaGameJpaRepository jpaRepository,
      ListaGameMapper mapper,
      UsuarioRefJpaRepository usuarioRefJpa) {
    this.listaJpa = jpaRepository;
    this.mapper = mapper;
    this.usuarioRefJpa = usuarioRefJpa;
  }

  @Override
  public ListaGame save(ListaGame listaGame) {
    ListaGameEntity entity = mapper.toEntity(listaGame);

    UsuarioRefEntity usuarioRefProxy = usuarioRefJpa.getReferenceById(listaGame.getUsuarioRefId());
    entity.setUsuarioRef(usuarioRefProxy);

    ListaGameEntity savedEntity = listaJpa.save(entity);
    return mapper.toDomain(savedEntity);
  }

  @Override
  public Optional<ListaGame> findById(ListaGameId id) {
    return listaJpa.findById(id.value()).map(mapper::toDomain);
  }

  @Override
  public List<ListaGame> findByUsuarioRefId(UUID usuarioRefId) {
    return listaJpa.findByUsuarioRef_Id(usuarioRefId).stream().map(mapper::toDomain).toList();
  }

  @Override
  public void deleteById(ListaGameId id) {
    listaJpa.deleteById(id.value());
  }
}

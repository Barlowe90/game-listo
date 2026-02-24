package com.gamelisto.biblioteca.infrastructure.out.persistence.postgres.repository;

import com.gamelisto.biblioteca.domain.listas.ListaGame;
import com.gamelisto.biblioteca.domain.listas.ListaGameId;
import com.gamelisto.biblioteca.domain.repositories.RepositorioLista;
import com.gamelisto.biblioteca.domain.repositories.RepositorioUsuariosRef;
import com.gamelisto.biblioteca.domain.usuario.UsuarioRef;
import com.gamelisto.biblioteca.infrastructure.out.persistence.postgres.entity.UsuarioRefEntity;
import com.gamelisto.biblioteca.infrastructure.out.persistence.postgres.mapper.UsuarioRefMapper;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public class RepositorioUsuarioRefPostgre implements RepositorioUsuariosRef {

  private final UsuarioRefJpaRepository jpaRepository;
  private final UsuarioRefMapper mapper;
  private final RepositorioLista repositorioLista;

  public RepositorioUsuarioRefPostgre(
      UsuarioRefJpaRepository jpaRepository,
      UsuarioRefMapper mapper,
      RepositorioLista repositorioLista) {
    this.jpaRepository = jpaRepository;
    this.mapper = mapper;
    this.repositorioLista = repositorioLista;
  }

  @Override
  public UsuarioRef save(UsuarioRef usuario) {
    UsuarioRefEntity entity = mapper.toEntity(usuario);
    UsuarioRefEntity savedEntity = jpaRepository.save(entity);

    for (ListaGame lista : usuario.getListas()) {
      ListaGameId id = lista.getId();
      Optional<ListaGame> existente = repositorioLista.findById(id);
      if (existente.isEmpty()) {
        repositorioLista.save(lista);
      }
    }

    return mapper.toDomain(savedEntity);
  }

  @Override
  public Optional<UsuarioRef> findById(UUID id) {
    return jpaRepository.findById(id).map(mapper::toDomain).map(this::cargarListasDelUsuario);
  }

  @Override
  public Optional<UsuarioRef> findByUsername(String username) {
    return jpaRepository
        .findByUsername(username)
        .map(mapper::toDomain)
        .map(this::cargarListasDelUsuario);
  }

  @Override
  public void delete(UsuarioRef usuario) {
    UsuarioRefEntity entity = mapper.toEntity(usuario);
    jpaRepository.delete(entity);
  }

  private UsuarioRef cargarListasDelUsuario(UsuarioRef usuarioRef) {
    for (ListaGame lista : repositorioLista.findByUsuarioRefId(usuarioRef.getId())) {
      usuarioRef.addNuevaLista(lista);
    }
    return usuarioRef;
  }
}

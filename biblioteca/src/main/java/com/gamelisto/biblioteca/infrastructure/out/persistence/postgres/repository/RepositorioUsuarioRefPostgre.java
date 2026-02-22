package com.gamelisto.biblioteca.infrastructure.out.persistence.postgres.repository;

import com.gamelisto.biblioteca.domain.repositories.RepositorioUsuariosRef;
import com.gamelisto.biblioteca.domain.usuario.UsuarioRef;
import com.gamelisto.biblioteca.infrastructure.out.persistence.postgres.mapper.UsuarioRefMapper;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public class RepositorioUsuarioRefPostgre implements RepositorioUsuariosRef {

  private final UsuarioRefJpaRepository jpaRepository;
  private final UsuarioRefMapper mapper;

  public RepositorioUsuarioRefPostgre(
      UsuarioRefJpaRepository jpaRepository, UsuarioRefMapper mapper) {
    this.jpaRepository = jpaRepository;
    this.mapper = mapper;
  }

  @Override
  public UsuarioRef save(UsuarioRef usuario) {
    return null;
  }

  @Override
  public Optional<UsuarioRef> finById(String id) {
    return Optional.empty();
  }

  @Override
  public Optional<UsuarioRef> finByUsername(String username) {
    return Optional.empty();
  }

  @Override
  public void delete(UsuarioRef id) {}
}

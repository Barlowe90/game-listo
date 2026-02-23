package com.gamelisto.biblioteca.infrastructure.out.persistence.postgres.repository;

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

  public RepositorioUsuarioRefPostgre(
      UsuarioRefJpaRepository jpaRepository, UsuarioRefMapper mapper) {
    this.jpaRepository = jpaRepository;
    this.mapper = mapper;
  }

  @Override
  public UsuarioRef save(UsuarioRef usuario) {
    UsuarioRefEntity entity = mapper.toEntity(usuario);
    UsuarioRefEntity savedEntity = jpaRepository.save(entity);
    return mapper.toDomain(savedEntity);
  }

  @Override
  public Optional<UsuarioRef> findById(UUID id) {
    return jpaRepository.findById(id).map(mapper::toDomain);
  }

  @Override
  public Optional<UsuarioRef> findByUsername(String username) {
    return jpaRepository.findByUsername(username).map(mapper::toDomain);
  }

  @Override
  public void delete(UsuarioRef usuario) {
    UsuarioRefEntity entity = mapper.toEntity(usuario);
    jpaRepository.delete(entity);
  }
}

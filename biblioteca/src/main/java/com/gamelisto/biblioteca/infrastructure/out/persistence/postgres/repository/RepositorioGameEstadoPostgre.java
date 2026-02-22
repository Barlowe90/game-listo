package com.gamelisto.biblioteca.infrastructure.out.persistence.postgres.repository;

import com.gamelisto.biblioteca.domain.gameEstado.Estado;
import com.gamelisto.biblioteca.domain.gameEstado.GameEstado;
import com.gamelisto.biblioteca.domain.repositories.RepositorioGameEstado;
import com.gamelisto.biblioteca.infrastructure.out.persistence.postgres.entity.GameEstadoEntity;
import com.gamelisto.biblioteca.infrastructure.out.persistence.postgres.mapper.GameEstadoMapper;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public class RepositorioGameEstadoPostgre implements RepositorioGameEstado {

  private final GameEstadoJpaRepository jpaRepository;
  private final GameEstadoMapper mapper;

  public RepositorioGameEstadoPostgre(
      GameEstadoJpaRepository jpaRepository, GameEstadoMapper mapper) {
    this.jpaRepository = jpaRepository;
    this.mapper = mapper;
  }

  @Override
  public GameEstado save(GameEstado gameEstado) {
    GameEstadoEntity entity = mapper.toEntity(gameEstado);
    GameEstadoEntity savedEntity = jpaRepository.save(entity);
    return mapper.toDomain(savedEntity);
  }

  @Override
  public Optional<GameEstado> findById(UUID id) {
    return jpaRepository.findById(id).map(mapper::toDomain);
  }

  @Override
  public Optional<GameEstado> findByEstado(Estado estado) {
    return jpaRepository.findByEstado(estado).map(mapper::toDomain);
  }
}

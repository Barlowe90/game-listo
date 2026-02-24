package com.gamelisto.biblioteca.infrastructure.out.persistence.postgres.repository;

import com.gamelisto.biblioteca.domain.gameref.GameRef;
import com.gamelisto.biblioteca.domain.repositories.RepositorioGameRef;
import com.gamelisto.biblioteca.infrastructure.out.persistence.postgres.entity.GameRefEntity;
import com.gamelisto.biblioteca.infrastructure.out.persistence.postgres.mapper.GameRefMapper;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public class RepositorioGameRefPostgre implements RepositorioGameRef {

  private final GameRefJpaRepository jpaRepository;
  private final GameRefMapper mapper;

  public RepositorioGameRefPostgre(GameRefJpaRepository jpaRepository, GameRefMapper mapper) {
    this.jpaRepository = jpaRepository;
    this.mapper = mapper;
  }

  @Override
  public GameRef save(GameRef gameRef) {
    GameRefEntity entity = mapper.toEntity(gameRef);
    GameRefEntity savedEntity = jpaRepository.save(entity);
    return mapper.toDomain(savedEntity);
  }

  @Override
  public Optional<GameRef> findById(UUID id) {
    return jpaRepository.findById(id).map(mapper::toDomain);
  }

  @Override
  public Optional<GameRef> findByGameRefId(Long gameRefId) {
    return jpaRepository.findByGameRefId(gameRefId).map(mapper::toDomain);
  }
}

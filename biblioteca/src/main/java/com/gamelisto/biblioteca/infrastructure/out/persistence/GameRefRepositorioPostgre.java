package com.gamelisto.biblioteca.infrastructure.out.persistence;

import com.gamelisto.biblioteca.domain.GameRef;
import com.gamelisto.biblioteca.domain.GameRefRepositorio;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public class GameRefRepositorioPostgre implements GameRefRepositorio {

  private final GameRefJpaRepository jpaRepository;
  private final GameRefMapper mapper;

  public GameRefRepositorioPostgre(GameRefJpaRepository jpaRepository, GameRefMapper mapper) {
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
  public Optional<GameRef> findById(Long id) {
    return jpaRepository.findById(id).map(mapper::toDomain);
  }
}

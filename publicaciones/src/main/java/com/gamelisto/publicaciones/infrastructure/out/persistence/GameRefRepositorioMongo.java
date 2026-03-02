package com.gamelisto.publicaciones.infrastructure.out.persistence;

import java.util.Optional;

import com.gamelisto.publicaciones.domain.GameRef;
import com.gamelisto.publicaciones.domain.GameRefRepositorio;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class GameRefRepositorioMongo implements GameRefRepositorio {

  private final GameRefRepository mongoRepository;
  private final GameRefMapper mapper;

  @Override
  public GameRef save(GameRef gameRef) {
    GameRefDocument document = mapper.toDocument(gameRef);
    GameRefDocument saved = mongoRepository.save(document);
    return mapper.toDomain(saved);
  }

  @Override
  public Optional<GameRef> findById(Long id) {
    return mongoRepository.findById(id).map(mapper::toDomain);
  }
}

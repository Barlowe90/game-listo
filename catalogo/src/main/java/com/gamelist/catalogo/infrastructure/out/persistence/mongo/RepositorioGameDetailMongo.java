package com.gamelist.catalogo.infrastructure.out.persistence.mongo;

import com.gamelist.catalogo.domain.GameId;
import com.gamelist.catalogo.domain.GameDetail;
import com.gamelist.catalogo.domain.GameDetailRepositorio;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class RepositorioGameDetailMongo implements GameDetailRepositorio {

  private final RepositorioGameDetail mongoRepository;
  private final GameDetailMapper mapper;

  @Override
  public GameDetail save(GameDetail gameDetail) {
    GameDetailDocument document = mapper.toDocument(gameDetail);

    Optional<GameDetailDocument> existing =
        mongoRepository.findByGameId(gameDetail.getGameId().value());
    existing.ifPresent(gameDetailDocument -> document.setId(gameDetailDocument.getId()));

    GameDetailDocument saved = mongoRepository.save(document);
    return mapper.toDomain(saved);
  }

  @Override
  public Optional<GameDetail> findByGameId(GameId gameId) {
    return mongoRepository.findByGameId(gameId.value()).map(mapper::toDomain);
  }
}

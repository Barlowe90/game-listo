package com.gamelist.catalogo.infrastructure.out.persistence.mongo.adapter;

import com.gamelist.catalogo.domain.game.GameId;
import com.gamelist.catalogo.domain.gamedetail.GameDetail;
import com.gamelist.catalogo.domain.repositories.IGameDetailRepository;
import com.gamelist.catalogo.infrastructure.out.persistence.mongo.document.GameDetailDocument;
import com.gamelist.catalogo.infrastructure.out.persistence.mongo.mapper.GameDetailMapper;
import com.gamelist.catalogo.infrastructure.out.persistence.mongo.repository.GameDetailMongoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class GameDetailRepositoryMongo implements IGameDetailRepository {

  private final GameDetailMongoRepository mongoRepository;
  private final GameDetailMapper mapper;

  @Override
  public GameDetail save(GameDetail gameDetail) {
    GameDetailDocument document = mapper.toDocument(gameDetail);

    // Si ya existe, actualizar manteniendo el _id de MongoDB
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

  @Override
  public void deleteByGameId(GameId gameId) {
    mongoRepository.deleteByGameId(gameId.value());
  }
}

package com.gamelist.catalogo_service.infrastructure.persistence.mongo.repository;

import com.gamelist.catalogo_service.infrastructure.persistence.mongo.document.GameDetailDocument;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface GameDetailMongoRepository extends MongoRepository<GameDetailDocument, String> {

  Optional<GameDetailDocument> findByGameId(Long gameId);

  void deleteByGameId(Long gameId);
}

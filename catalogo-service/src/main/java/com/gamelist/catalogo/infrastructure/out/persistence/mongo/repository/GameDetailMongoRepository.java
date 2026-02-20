package com.gamelist.catalogo.infrastructure.out.persistence.mongo.repository;

import com.gamelist.catalogo.infrastructure.out.persistence.mongo.document.GameDetailDocument;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface GameDetailMongoRepository extends MongoRepository<GameDetailDocument, String> {

  Optional<GameDetailDocument> findByGameId(Long gameId);
}

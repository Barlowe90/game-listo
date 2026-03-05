package com.gamelisto.catalogo.infrastructure.out.persistence.mongo;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface RepositorioGameDetail extends MongoRepository<GameDetailDocument, String> {

  Optional<GameDetailDocument> findByGameId(Long gameId);
}

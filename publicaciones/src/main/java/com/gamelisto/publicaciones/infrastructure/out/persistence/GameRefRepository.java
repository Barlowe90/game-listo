package com.gamelisto.publicaciones.infrastructure.out.persistence;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface GameRefRepository extends MongoRepository<GameRefDocument, Long> {
  Optional<GameRefDocument> findById(Long id);
}

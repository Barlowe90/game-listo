package com.gamelisto.publicaciones.infraestructure.out.persistence;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface GameRefRepository extends MongoRepository<GameRefDocument, Long> {
  Optional<GameRefDocument> findById(Long id);
}

package com.gamelisto.publicaciones.infrastructure.out.persistence;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface GrupoJuegoRepository extends MongoRepository<GrupoJuegoDocument, UUID> {
  Optional<GrupoJuegoDocument> findByPublicacionId(UUID publicacionId);
}

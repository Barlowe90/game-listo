package com.gamelisto.publicaciones.infraestructure.out.persistence;

import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;
import java.util.UUID;

public interface PublicacionRepository extends MongoRepository<PublicacionDocument, UUID> {
  List<PublicacionDocument> findByAutorId(UUID autorId);

  List<PublicacionDocument> findByGameId(Long gameId);
}

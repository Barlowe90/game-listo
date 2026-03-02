package com.gamelisto.publicaciones.infrastructure.out.persistence;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface PeticionUnionRepository extends MongoRepository<PeticionUnionDocument, UUID> {
  List<PeticionUnionDocument> findByPublicacionId(UUID publicacionId);

  Optional<PeticionUnionDocument> findByPublicacionIdAndUsuarioId(
      UUID publicacionId, UUID usuarioId);

  List<PeticionUnionDocument> findByUsuarioId(UUID usuarioId); // solicitudes enviadas

  List<PeticionUnionDocument> findByPublicacionIdIn(
      List<UUID> publicacionIds); // solicitudes recibidas
}

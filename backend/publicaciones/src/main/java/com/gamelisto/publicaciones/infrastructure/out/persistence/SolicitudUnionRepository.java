package com.gamelisto.publicaciones.infrastructure.out.persistence;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface SolicitudUnionRepository extends MongoRepository<SolicitudUnionDocument, UUID> {
  List<SolicitudUnionDocument> findByPublicacionId(UUID publicacionId);

  Optional<SolicitudUnionDocument> findByPublicacionIdAndUsuarioId(
      UUID publicacionId, UUID usuarioId);

  List<SolicitudUnionDocument> findByUsuarioId(UUID usuarioId); // solicitudes enviadas

  List<SolicitudUnionDocument> findByPublicacionIdIn(
      List<UUID> publicacionIds); // solicitudes recibidas
}

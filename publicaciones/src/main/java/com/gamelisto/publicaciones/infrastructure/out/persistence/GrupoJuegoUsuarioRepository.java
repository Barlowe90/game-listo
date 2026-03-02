package com.gamelisto.publicaciones.infrastructure.out.persistence;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface GrupoJuegoUsuarioRepository
    extends MongoRepository<GrupoJuegoUsuarioDocument, UUID> {
  List<GrupoJuegoUsuarioDocument> findByGrupoId(UUID grupoId);

  boolean existsByGrupoIdAndUsuarioId(UUID grupoId, UUID usuarioId);

  long deleteByGrupoIdAndUsuarioId(UUID grupoId, UUID usuarioId);
}

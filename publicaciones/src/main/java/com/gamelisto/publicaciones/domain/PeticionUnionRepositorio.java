package com.gamelisto.publicaciones.domain;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface PeticionUnionRepositorio {
  PeticionUnion save(PeticionUnion peticionUnion);

  Optional<PeticionUnion> findById(UUID id);

  List<PeticionUnion> findByPublicacionId(UUID publicacionId);

  Optional<PeticionUnion> findByPublicacionIdAndUsuarioId(UUID publicacionId, UUID usuarioId);

  List<PeticionUnion> findByUsuarioId(UUID usuarioId);

  List<PeticionUnion> findByPublicacionIdIn(List<UUID> publicacionIds);
}

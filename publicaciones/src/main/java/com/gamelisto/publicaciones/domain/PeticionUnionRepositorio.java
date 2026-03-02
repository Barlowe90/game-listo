package com.gamelisto.publicaciones.domain;

import com.gamelisto.publicaciones.domain.vo.PeticionId;
import com.gamelisto.publicaciones.domain.vo.PublicacionId;
import com.gamelisto.publicaciones.domain.vo.UsuarioId;
import java.util.List;
import java.util.Optional;

public interface PeticionUnionRepositorio {
  PeticionUnion save(PeticionUnion peticionUnion);

  Optional<PeticionUnion> findById(PeticionId id);

  List<PeticionUnion> findByPublicacionId(PublicacionId publicacionId);

  Optional<PeticionUnion> findByPublicacionIdAndUsuarioId(
      PublicacionId publicacionId, UsuarioId usuarioId);

  List<PeticionUnion> findByUsuarioId(UsuarioId usuarioId);

  List<PeticionUnion> findByPublicacionIdIn(List<PublicacionId> publicacionIds);
}

package com.gamelisto.publicaciones.domain;

import com.gamelisto.publicaciones.domain.vo.PublicacionId;
import com.gamelisto.publicaciones.domain.vo.SolicitudId;
import com.gamelisto.publicaciones.domain.vo.UsuarioId;
import java.util.List;
import java.util.Optional;

public interface SolicitudUnionRepositorio {
  SolicitudUnion save(SolicitudUnion solicitudUnion);

  Optional<SolicitudUnion> findById(SolicitudId id);

  List<SolicitudUnion> findByPublicacionId(PublicacionId publicacionId);

  Optional<SolicitudUnion> findByPublicacionIdAndUsuarioId(
      PublicacionId publicacionId, UsuarioId usuarioId);

  List<SolicitudUnion> findByUsuarioId(UsuarioId usuarioId);

  List<SolicitudUnion> findByPublicacionIdIn(List<PublicacionId> publicacionIds);

  void deleteByPublicacionId(PublicacionId publicacionId);
}

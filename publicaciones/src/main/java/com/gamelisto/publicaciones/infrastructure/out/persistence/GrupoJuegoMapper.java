package com.gamelisto.publicaciones.infrastructure.out.persistence;

import com.gamelisto.publicaciones.domain.GrupoJuego;
import com.gamelisto.publicaciones.domain.vo.GrupoId;
import com.gamelisto.publicaciones.domain.vo.PublicacionId;
import org.springframework.stereotype.Component;

@Component
public class GrupoJuegoMapper {

  public GrupoJuegoDocument toDocument(GrupoJuego grupoJuego) {
    GrupoJuegoDocument document = new GrupoJuegoDocument();
    document.setId(grupoJuego.getId().value());
    document.setPublicacionId(grupoJuego.getPublicacionId().value());
    document.setFechaCreacion(grupoJuego.getFechaCreacion());
    return document;
  }

  public GrupoJuego toDomain(GrupoJuegoDocument document) {
    return GrupoJuego.reconstitute(
        GrupoId.of(document.getId()),
        PublicacionId.of(document.getPublicacionId()),
        document.getFechaCreacion());
  }
}

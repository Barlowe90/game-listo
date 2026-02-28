package com.gamelisto.publicaciones.infraestructure.out.persistence;

import com.gamelisto.publicaciones.domain.GrupoJuego;
import org.springframework.stereotype.Component;

@Component
public class GrupoJuegoMapper {

  public GrupoJuegoDocument toDocument(GrupoJuego grupoJuego) {
    GrupoJuegoDocument document = new GrupoJuegoDocument();
    document.setId(grupoJuego.getId());
    document.setPublicacionId(grupoJuego.getPublicacionId());
    document.setFechaCreacion(grupoJuego.getFechaCreacion());
    return document;
  }

  public GrupoJuego toDomain(GrupoJuegoDocument document) {
    return GrupoJuego.reconstitute(
        document.getId(), document.getPublicacionId(), document.getFechaCreacion());
  }
}

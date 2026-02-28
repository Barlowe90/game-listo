package com.gamelisto.publicaciones.infraestructure.out.persistence;

import com.gamelisto.publicaciones.domain.GrupoJuegoUsuario;
import org.springframework.stereotype.Component;

@Component
public class GrupoJuegoUsuarioMapper {

  public GrupoJuegoUsuarioDocument toDocument(GrupoJuegoUsuario grupo) {
    GrupoJuegoUsuarioDocument document = new GrupoJuegoUsuarioDocument();
    document.setId(grupo.getId());
    document.setGrupoId(grupo.getGrupoId());
    document.setUsuarioId(grupo.getUsuarioId());
    return document;
  }

  public GrupoJuegoUsuario toDomain(GrupoJuegoUsuarioDocument document) {
    return GrupoJuegoUsuario.reconstitute(
        document.getId(), document.getGrupoId(), document.getUsuarioId());
  }
}

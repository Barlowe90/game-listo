package com.gamelisto.publicaciones.infraestructure.out.persistence;

import com.gamelisto.publicaciones.domain.GrupoJuegoUsuario;
import com.gamelisto.publicaciones.domain.vo.GrupoJuegoUsuarioId;
import com.gamelisto.publicaciones.domain.vo.GrupoId;
import com.gamelisto.publicaciones.domain.vo.UsuarioId;
import org.springframework.stereotype.Component;

@Component
public class GrupoJuegoUsuarioMapper {

  public GrupoJuegoUsuarioDocument toDocument(GrupoJuegoUsuario grupo) {
    GrupoJuegoUsuarioDocument document = new GrupoJuegoUsuarioDocument();
    document.setId(grupo.getId().value());
    document.setGrupoId(grupo.getGrupoId().value());
    document.setUsuarioId(grupo.getUsuarioId().value());
    return document;
  }

  public GrupoJuegoUsuario toDomain(GrupoJuegoUsuarioDocument document) {
    return GrupoJuegoUsuario.reconstitute(
        GrupoJuegoUsuarioId.of(document.getId()),
        GrupoId.of(document.getGrupoId()),
        UsuarioId.of(document.getUsuarioId()));
  }
}

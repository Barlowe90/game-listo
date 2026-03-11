package com.gamelisto.publicaciones.infrastructure.out.persistence;

import com.gamelisto.publicaciones.domain.SolicitudUnion;
import com.gamelisto.publicaciones.domain.vo.PublicacionId;
import com.gamelisto.publicaciones.domain.vo.SolicitudId;
import com.gamelisto.publicaciones.domain.vo.UsuarioId;
import org.springframework.stereotype.Component;

@Component
public class SolicitudUnionMapper {

  public SolicitudUnionDocument toDocument(SolicitudUnion peticion) {
    SolicitudUnionDocument document = new SolicitudUnionDocument();
    document.setId(peticion.getId().value());
    document.setPublicacionId(peticion.getPublicacionId().value());
    document.setUsuarioId(peticion.getUsuarioId().value());
    document.setEstadoSolicitud(peticion.getEstadoSolicitud());
    return document;
  }

  public SolicitudUnion toDomain(SolicitudUnionDocument document) {
    return SolicitudUnion.reconstitute(
        SolicitudId.of(document.getId()),
        PublicacionId.of(document.getPublicacionId()),
        UsuarioId.of(document.getUsuarioId()),
        document.getEstadoSolicitud());
  }
}

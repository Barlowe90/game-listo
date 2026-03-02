package com.gamelisto.publicaciones.infraestructure.out.persistence;

import com.gamelisto.publicaciones.domain.PeticionUnion;
import com.gamelisto.publicaciones.domain.vo.PeticionId;
import com.gamelisto.publicaciones.domain.vo.PublicacionId;
import com.gamelisto.publicaciones.domain.vo.UsuarioId;
import org.springframework.stereotype.Component;

@Component
public class PeticionUnionMapper {

  public PeticionUnionDocument toDocument(PeticionUnion peticion) {
    PeticionUnionDocument document = new PeticionUnionDocument();
    document.setId(peticion.getId().value());
    document.setPublicacionId(peticion.getPublicacionId().value());
    document.setUsuarioId(peticion.getUsuarioId().value());
    document.setEstadoSolicitud(peticion.getEstadoSolicitud());
    return document;
  }

  public PeticionUnion toDomain(PeticionUnionDocument document) {
    return PeticionUnion.reconstitute(
        PeticionId.of(document.getId()),
        PublicacionId.of(document.getPublicacionId()),
        UsuarioId.of(document.getUsuarioId()),
        document.getEstadoSolicitud());
  }
}

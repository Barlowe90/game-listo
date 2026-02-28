package com.gamelisto.publicaciones.infraestructure.out.persistence;

import com.gamelisto.publicaciones.domain.PeticionUnion;
import org.springframework.stereotype.Component;

@Component
public class PeticionUnionMapper {

  public PeticionUnionDocument toDocument(PeticionUnion peticion) {
    PeticionUnionDocument document = new PeticionUnionDocument();
    document.setId(peticion.getId());
    document.setPublicacionId(peticion.getPublicacionId());
    document.setUsuarioId(peticion.getUsuarioId());
    document.setEstadoPeticion(peticion.getEstadoPeticion());
    return document;
  }

  public PeticionUnion toDomain(PeticionUnionDocument document) {
    return PeticionUnion.reconstitute(
        document.getId(),
        document.getPublicacionId(),
        document.getUsuarioId(),
        document.getEstadoPeticion());
  }
}

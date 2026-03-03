package com.gamelisto.publicaciones.infrastructure.out.persistence;

import com.gamelisto.publicaciones.domain.Publicacion;
import org.springframework.stereotype.Component;

@Component
public class PublicacionMapper {
  public PublicacionDocument toDocument(Publicacion publicacion) {
    PublicacionDocument document = new PublicacionDocument();
    document.setId(publicacion.getId().value());
    document.setAutorId(publicacion.getAutorId());
    document.setGameId(publicacion.getGameId());
    document.setTitulo(publicacion.getTitulo());
    document.setIdioma(publicacion.getIdioma());
    document.setExperiencia(publicacion.getExperiencia());
    document.setEstiloJuego(publicacion.getEstiloJuego());
    document.setJugadoresMaximos(publicacion.getJugadoresMaximos());
    return document;
  }

  public Publicacion toDomain(PublicacionDocument document) {
    return Publicacion.reconstitute(
        document.getId(),
        document.getAutorId(),
        document.getGameId(),
        document.getTitulo(),
        document.getIdioma(),
        document.getExperiencia(),
        document.getEstiloJuego(),
        document.getJugadoresMaximos());
  }
}

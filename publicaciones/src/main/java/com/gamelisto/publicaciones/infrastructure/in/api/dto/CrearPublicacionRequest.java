package com.gamelisto.publicaciones.infraestructure.in.api.dto;

import com.gamelisto.publicaciones.application.usecases.CrearPublicacionCommand;

import java.util.UUID;

public record CrearPublicacionRequest(
    Long gameId,
    String titulo,
    String idioma,
    String experiencia,
    String estiloJuego,
    int jugadoresMaximos) {

  public CrearPublicacionCommand toCommand(UUID autorId) {
    return new CrearPublicacionCommand(
        autorId, gameId, titulo, idioma, experiencia, estiloJuego, jugadoresMaximos);
  }
}

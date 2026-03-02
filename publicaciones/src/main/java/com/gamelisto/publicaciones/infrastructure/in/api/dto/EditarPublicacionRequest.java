package com.gamelisto.publicaciones.infraestructure.in.api.dto;

import com.gamelisto.publicaciones.application.usecases.EditarPublicacionCommand;

import java.util.UUID;

public record EditarPublicacionRequest(
    String titulo, String idioma, String experiencia, String estiloJuego, int jugadoresMaximos) {

  public EditarPublicacionCommand toCommand(UUID publicacionId, UUID autorId) {
    return new EditarPublicacionCommand(
        publicacionId, autorId, titulo, idioma, experiencia, estiloJuego, jugadoresMaximos);
  }
}

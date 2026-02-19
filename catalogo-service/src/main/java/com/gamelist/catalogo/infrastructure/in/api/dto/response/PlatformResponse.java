package com.gamelist.catalogo.infrastructure.in.api.dto.response;

import com.gamelist.catalogo.application.dto.results.PlatformDTO;
import io.swagger.v3.oas.annotations.media.Schema;

/** Response DTO para información de una plataforma. */
@Schema(description = "Información de una plataforma de videojuegos")
public record PlatformResponse(
    @Schema(description = "ID de la plataforma", example = "130") Long id,
    @Schema(description = "Nombre de la plataforma", example = "Nintendo Switch") String name,
    @Schema(description = "Abreviación de la plataforma", example = "Switch") String abbreviation) {

  public static PlatformResponse from(PlatformDTO dto) {
    return new PlatformResponse(dto.id(), dto.name(), dto.abbreviation());
  }
}

package com.gamelist.catalogo.infrastructure.out.dto;

import com.gamelist.catalogo.application.dto.out.PlatformDTO;
import io.swagger.v3.oas.annotations.media.Schema;

/** Response DTO para información de una plataforma. */
@Schema(description = "Información de una plataforma de videojuegos")
public record PlatformResponse(
    @Schema(description = "ID de la plataforma", example = "130") Long id,
    @Schema(description = "Nombre de la plataforma", example = "Nintendo Switch") String name,
    @Schema(description = "Abreviación de la plataforma", example = "Switch") String abbreviation,
    @Schema(description = "Nombre alternativo de la plataforma", example = "NS")
        String alternativeName,
    @Schema(
            description = "URL del logo de la plataforma",
            example = "https://images.igdb.com/xyz.png")
        String logoURL,
    @Schema(description = "Tipo de plataforma (ej. consola, móvil, PC)", example = "Consola")
        String tipo) {

  public static PlatformResponse from(PlatformDTO dto) {
    return new PlatformResponse(
        dto.id(), dto.name(), dto.abbreviation(), dto.alternativeName(), dto.logoURL(), dto.tipo());
  }
}

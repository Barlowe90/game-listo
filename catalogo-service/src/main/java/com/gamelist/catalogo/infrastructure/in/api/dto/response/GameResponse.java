package com.gamelist.catalogo.infrastructure.in.api.dto.response;

import com.gamelist.catalogo.application.dto.results.GameDTO;
import io.swagger.v3.oas.annotations.media.Schema;

import java.util.Set;
import java.util.stream.Collectors;

/** Response DTO para información básica de un juego. */
@Schema(description = "Información básica de un videojuego")
public record GameResponse(
    @Schema(description = "ID del juego", example = "1234") Long id,
    @Schema(description = "Nombre del juego", example = "The Legend of Zelda: Breath of the Wild")
        String name,
    @Schema(description = "Resumen del juego") String summary,
    @Schema(description = "URL de la portada") String coverUrl,
    @Schema(description = "Plataformas donde está disponible") Set<PlatformResponse> platforms) {

  public static GameResponse from(GameDTO dto) {
    Set<PlatformResponse> platformResponses =
        dto.platforms().stream().map(PlatformResponse::from).collect(Collectors.toSet());

    return new GameResponse(dto.id(), dto.name(), dto.summary(), dto.coverUrl(), platformResponses);
  }
}

package com.gamelist.catalogo.infrastructure.out.api.dto.response;

import com.gamelist.catalogo.application.dto.results.GameDetailDTO;
import io.swagger.v3.oas.annotations.media.Schema;

import java.util.List;

/** Response DTO para detalles multimedia de un juego */
@Schema(description = "Multimedia de un videojuego (screenshots y videos)")
public record GameDetailResponse(
    @Schema(description = "ID del juego") Long gameId,
    @Schema(description = "Nombres alternativos") List<String> alternativeNames,
    @Schema(description = "URL de la portada") String coverUrl,
    @Schema(description = "URLs de screenshots del juego") List<String> screenshots,
    @Schema(description = "URLs o identificadores de videos del juego") List<String> videos) {

  public static GameDetailResponse from(GameDetailDTO dto) {
    if (dto == null) return null;
    return new GameDetailResponse(
        dto.gameId(), dto.alternativeNames(), dto.coverUrl(), dto.screenshots(), dto.videos());
  }
}

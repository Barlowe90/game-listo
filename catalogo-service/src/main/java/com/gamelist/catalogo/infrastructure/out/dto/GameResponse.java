package com.gamelist.catalogo.infrastructure.out.dto;

import com.gamelist.catalogo.application.dto.out.GameDTO;
import io.swagger.v3.oas.annotations.media.Schema;

import java.util.List;

/** Response DTO para información completa de un juego desde PostgreSQL. */
@Schema(description = "Información completa de un videojuego")
public record GameResponse(
    @Schema(description = "ID del juego") Long id,
    @Schema(description = "Nombres alternativos") List<String> alternativeNames,
    @Schema(description = "URL de la portada") String coverUrl,
    @Schema(description = "IDs de DLCs") List<Long> dlcIds,
    @Schema(description = "IDs de juegos expandidos") List<Long> expandedGames,
    @Schema(description = "IDs de expansiones") List<Long> expansionIds,
    @Schema(description = "URLs de juegos externos") List<String> externalGames,
    @Schema(description = "Franquicias") List<String> franchises,
    @Schema(description = "Modos de juego") List<String> gameModes,
    @Schema(description = "Estado del juego") String gameStatus,
    @Schema(description = "Tipo de juego (main_game, dlc, expansion, etc.)") String gameType,
    @Schema(description = "Géneros") List<String> genres,
    @Schema(description = "Compañías involucradas") List<String> involvedCompanies,
    @Schema(description = "Keywords/Tags") List<String> keywords,
    @Schema(description = "IDs de modos multijugador") List<Long> multiplayerModeIds,
    @Schema(description = "Nombre del juego") String name,
    @Schema(description = "ID del juego padre (si es DLC o expansión)") Long parentGameId,
    @Schema(description = "Plataformas donde está disponible") List<String> platforms,
    @Schema(description = "Perspectivas del jugador") List<String> playerPerspectives,
    @Schema(description = "IDs de remakes") List<Long> remakeIds,
    @Schema(description = "IDs de remasters") List<Long> remasterIds,
    @Schema(description = "IDs de juegos similares") List<Long> similarGames,
    @Schema(description = "Resumen del juego") String summary,
    @Schema(description = "Temáticas") List<String> themes,
    @Schema(description = "Screenshots") List<String> screenshots,
    @Schema(description = "Videos") List<String> videos) {

  public static GameResponse from(GameDTO dto) {
    if (dto == null) return null;
    return new GameResponse(
        dto.id(),
        dto.alternativeNames(),
        dto.coverUrl(),
        dto.dlcIds(),
        dto.expandedGames(),
        dto.expansionIds(),
        dto.externalGames(),
        dto.franchises(),
        dto.gameModes(),
        dto.gameStatus(),
        dto.gameType(),
        dto.genres(),
        dto.involvedCompanies(),
        dto.keywords(),
        dto.multiplayerModeIds(),
        dto.name(),
        dto.parentGameId(),
        dto.platforms(),
        dto.playerPerspectives(),
        dto.remakeIds(),
        dto.remasterIds(),
        dto.similarGames(),
        dto.summary(),
        dto.themes(),
        dto.screenshots(),
        dto.videos());
  }
}

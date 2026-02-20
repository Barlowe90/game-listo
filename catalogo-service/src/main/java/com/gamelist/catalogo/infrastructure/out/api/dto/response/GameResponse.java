package com.gamelist.catalogo.infrastructure.out.api.dto.response;

import com.gamelist.catalogo.application.dto.results.GameDTO;
import io.swagger.v3.oas.annotations.media.Schema;

import java.util.List;

/** Response DTO para información completa de un juego desde PostgreSQL. */
@Schema(description = "Información completa de un videojuego")
public record GameResponse(
    @Schema(description = "ID del juego") Long id,
    @Schema(description = "Nombre del juego") String name,
    @Schema(description = "Resumen del juego") String summary,
    @Schema(description = "URL de la portada") String coverUrl,
    @Schema(description = "Plataformas donde está disponible") List<String> platforms,
    @Schema(description = "Tipo de juego (main_game, dlc, expansion, etc.)") String gameType,
    @Schema(description = "Estado del juego") String gameStatus,
    @Schema(description = "ID del juego padre (si es DLC o expansión)") Long parentGameId,
    @Schema(description = "Géneros") List<String> genres,
    @Schema(description = "Modos de juego") List<String> gameModes,
    @Schema(description = "Perspectivas del jugador") List<String> playerPerspectives,
    @Schema(description = "Keywords/Tags") List<String> keywords,
    @Schema(description = "Compañías involucradas") List<String> involvedCompanies,
    @Schema(description = "Nombres alternativos") List<String> alternativeNames,
    @Schema(description = "Franquicias") List<String> franchises,
    @Schema(description = "Temáticas") List<String> themes,
    @Schema(description = "URLs de juegos externos") List<String> externalGames,
    @Schema(description = "IDs de modos multijugador") List<Long> multiplayerModeIds,
    @Schema(description = "IDs de DLCs") List<Long> dlcIds,
    @Schema(description = "IDs de juegos expandidos") List<Long> expandedGames,
    @Schema(description = "IDs de expansiones") List<Long> expansionIds,
    @Schema(description = "IDs de remakes") List<Long> remakeIds,
    @Schema(description = "IDs de remasters") List<Long> remasterIds,
    @Schema(description = "IDs de juegos similares") List<Long> similarGames,
    @Schema(description = "Screenshots") List<String> screenshots,
    @Schema(description = "Videos") List<String> videos) {

  public static GameResponse from(GameDTO dto) {
    if (dto == null) return null;
    return new GameResponse(
        dto.id(),
        dto.name(),
        dto.summary(),
        dto.coverUrl(),
        dto.platforms(),
        dto.gameType(),
        dto.gameStatus(),
        dto.parentGameId(),
        dto.genres(),
        dto.gameModes(),
        dto.playerPerspectives(),
        dto.keywords(),
        dto.involvedCompanies(),
        dto.alternativeNames(),
        dto.franchises(),
        dto.themes(),
        dto.externalGames(),
        dto.multiplayerModeIds(),
        dto.dlcIds(),
        dto.expandedGames(),
        dto.expansionIds(),
        dto.remakeIds(),
        dto.remasterIds(),
        dto.similarGames(),
        dto.screenshots(),
        dto.videos());
  }
}

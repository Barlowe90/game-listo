package com.gamelisto.catalogo.infrastructure.in.api.dto;

import com.gamelisto.catalogo.application.usecases.GameResult;

import java.util.List;

/** Response DTO para información completa de un juego desde PostgreSQL. */
public record GameResponse(
    Long id,
    List<String> alternativeNames,
    String coverUrl,
    List<Long> dlcIds,
    List<Long> expandedGames,
    List<Long> expansionIds,
    List<String> externalGames,
    List<String> franchises,
    List<String> gameModes,
    String gameStatus,
    String gameType,
    List<String> genres,
    List<String> involvedCompanies,
    List<String> keywords,
    List<Long> multiplayerModeIds,
    String name,
    Long parentGameId,
    List<String> platforms,
    List<String> playerPerspectives,
    List<Long> remakeIds,
    List<Long> remasterIds,
    List<Long> similarGames,
    String summary,
    List<String> themes) {

  public static GameResponse from(GameResult dto) {
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
        dto.themes());
  }
}

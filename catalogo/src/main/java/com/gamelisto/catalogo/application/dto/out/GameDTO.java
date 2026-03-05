package com.gamelisto.catalogo.application.dto.out;

import com.gamelisto.catalogo.domain.Game;

import java.util.List;

/** DTO de aplicación con campos alineados al domain `Game`. */
public record GameDTO(
    Long id,
    String name,
    String summary,
    String coverUrl,
    List<String> platforms,
    String gameType,
    String gameStatus,
    List<String> alternativeNames,
    List<Long> dlcIds,
    List<Long> expandedGames,
    List<Long> expansionIds,
    List<String> externalGames,
    List<String> franchises,
    List<String> gameModes,
    List<String> genres,
    List<String> involvedCompanies,
    List<String> keywords,
    List<Long> multiplayerModeIds,
    Long parentGameId,
    List<String> playerPerspectives,
    List<Long> remakeIds,
    List<Long> remasterIds,
    List<Long> similarGames,
    List<String> themes) {

  public static GameDTO from(Game game) {
    if (game == null) return null;

    return new GameDTO(
        game.getId().value(),
        game.getName().value(),
        game.getSummary() != null ? game.getSummary().value() : null,
        game.getCoverUrl() != null ? game.getCoverUrl().value() : null,
        game.getPlatforms() != null ? List.copyOf(game.getPlatforms()) : List.of(),
        game.getGameType(),
        game.getGameStatus(),
        game.getAlternativeNames() != null ? List.copyOf(game.getAlternativeNames()) : List.of(),
        game.getDlcs() != null ? List.copyOf(game.getDlcs()) : List.of(),
        game.getExpandedGames() != null ? List.copyOf(game.getExpandedGames()) : List.of(),
        game.getExpansionIds() != null ? List.copyOf(game.getExpansionIds()) : List.of(),
        game.getExternalGames() != null ? List.copyOf(game.getExternalGames()) : List.of(),
        game.getFranchises() != null ? List.copyOf(game.getFranchises()) : List.of(),
        game.getGameModes() != null ? List.copyOf(game.getGameModes()) : List.of(),
        game.getGenres() != null ? List.copyOf(game.getGenres()) : List.of(),
        game.getInvolvedCompanies() != null ? List.copyOf(game.getInvolvedCompanies()) : List.of(),
        game.getKeywords() != null ? List.copyOf(game.getKeywords()) : List.of(),
        game.getMultiplayerModeIds() != null
            ? List.copyOf(game.getMultiplayerModeIds())
            : List.of(),
        game.getParentGameId(),
        game.getPlayerPerspectives() != null
            ? List.copyOf(game.getPlayerPerspectives())
            : List.of(),
        game.getRemakeIds() != null ? List.copyOf(game.getRemakeIds()) : List.of(),
        game.getRemasterIds() != null ? List.copyOf(game.getRemasterIds()) : List.of(),
        game.getSimilarGames() != null ? List.copyOf(game.getSimilarGames()) : List.of(),
        game.getThemes() != null ? List.copyOf(game.getThemes()) : List.of());
  }
}

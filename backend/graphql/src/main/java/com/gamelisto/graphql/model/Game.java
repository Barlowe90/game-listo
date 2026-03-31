package com.gamelisto.graphql.model;

import java.util.List;

public record Game(
    Integer id,
    String name,
    String coverUrl,
    String summary,
    List<String> alternativeNames,
    List<Integer> dlcIds,
    List<Integer> expandedGames,
    List<Integer> expansionIds,
    List<String> externalGames,
    List<String> franchises,
    List<String> gameModes,
    String gameStatus,
    String gameType,
    List<String> genres,
    List<String> involvedCompanies,
    List<String> keywords,
    List<Integer> multiplayerModeIds,
    Integer parentGameId,
    List<String> platforms,
    List<String> playerPerspectives,
    List<Integer> remakeIds,
    List<Integer> remasterIds,
    List<Integer> similarGames,
    List<String> themes
) {}

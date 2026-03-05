package com.gamelisto.catalogo.application.dto.in;

import com.gamelisto.catalogo.domain.CoverUrl;
import com.gamelisto.catalogo.domain.Game;
import com.gamelisto.catalogo.domain.GameId;
import com.gamelisto.catalogo.domain.GameName;
import com.gamelisto.catalogo.domain.Summary;

import java.util.List;

/** DTO de aplicación para juegos obtenidos desde IGDB que me pasan desde infraestructura */
public record IgdbGameDTO(
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
    List<String> themes,
    List<String> screenshots,
    List<String> videos) {

  public Game toDomain() {
    GameId gid = GameId.of(id());
    GameName gname = GameName.of(name());
    Summary gsummary = summary() != null ? Summary.of(summary()) : Summary.empty();
    CoverUrl gcover = coverUrl() != null ? CoverUrl.of(coverUrl()) : CoverUrl.empty();

    List<String> platformsAsStrings = platforms() != null ? List.copyOf(platforms()) : List.of();

    return Game.reconstitute(
        gid,
        gname,
        gsummary,
        gcover,
        platformsAsStrings,
        gameType(),
        gameStatus(),
        alternativeNames(),
        dlcIds(),
        expandedGames(),
        expansionIds(),
        externalGames(),
        franchises(),
        gameModes(),
        genres(),
        involvedCompanies(),
        keywords(),
        multiplayerModeIds(),
        parentGameId(),
        playerPerspectives(),
        remakeIds(),
        remasterIds(),
        similarGames(),
        themes());
  }
}

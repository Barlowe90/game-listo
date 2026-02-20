package com.gamelist.catalogo.application.dto.results;

import com.gamelist.catalogo.domain.game.CoverUrl;
import com.gamelist.catalogo.domain.game.Game;
import com.gamelist.catalogo.domain.game.GameId;
import com.gamelist.catalogo.domain.game.GameName;
import com.gamelist.catalogo.domain.game.Summary;

import java.util.List;

/** DTO de aplicación para juegos obtenidos desde IGDB */
public record IgdbGameDTO(
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
        themes(),
        screenshots(),
        videos());
  }

  public static Game toDomain(IgdbGameDTO dto) {
    if (dto == null) return null;
    return dto.toDomain();
  }
}

package com.gamelist.catalogo.domain.game;

import com.gamelist.catalogo.domain.exceptions.DomainException;
import lombok.Getter;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Getter
public class Game {

  private final GameId id;
  private GameName name;
  private Summary summary;
  private CoverUrl coverUrl;
  private List<String> platforms;
  private String gameType;
  private String gameStatus;
  private List<String> alternativeNames;
  private List<Long> dlcs;
  private List<Long> expandedGames;
  private List<Long> expansionIds;
  private List<String> externalGames;
  private List<String> franchises;
  private List<String> gameModes;
  private List<String> genres;
  private List<String> involvedCompanies;
  private List<String> keywords;
  private List<Long> multiplayerModeIds;
  private Long parentGameId;
  private List<String> playerPerspectives;
  private List<Long> remakeIds;
  private List<Long> remasterIds;
  private List<Long> similarGames;
  private List<String> themes;
  private List<String> screenshots;
  private List<String> videos;

  private Game(
      GameId id,
      GameName name,
      Summary summary,
      CoverUrl coverUrl,
      List<String> platforms,
      String gameType,
      String gameStatus,
      List<String> alternativeNames,
      List<Long> dlcs,
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

    this.id = Objects.requireNonNull(id, "GameId no puede ser nulo");
    this.name = Objects.requireNonNull(name, "GameName no puede ser nulo");
    this.summary = summary != null ? summary : Summary.empty();
    this.coverUrl = coverUrl != null ? coverUrl : CoverUrl.empty();
    this.platforms = platforms != null ? new ArrayList<>(platforms) : new ArrayList<>();
    this.gameType = gameType;
    this.gameStatus = gameStatus;
    this.alternativeNames =
        alternativeNames != null ? new ArrayList<>(alternativeNames) : new ArrayList<>();
    this.dlcs = dlcs != null ? new ArrayList<>(dlcs) : new ArrayList<>();
    this.expandedGames = expandedGames != null ? new ArrayList<>(expandedGames) : new ArrayList<>();
    this.expansionIds = expansionIds != null ? new ArrayList<>(expansionIds) : new ArrayList<>();
    this.externalGames = externalGames != null ? new ArrayList<>(externalGames) : new ArrayList<>();
    this.franchises = franchises != null ? new ArrayList<>(franchises) : new ArrayList<>();
    this.gameModes = gameModes != null ? new ArrayList<>(gameModes) : new ArrayList<>();
    this.genres = genres != null ? new ArrayList<>(genres) : new ArrayList<>();
    this.involvedCompanies =
        involvedCompanies != null ? new ArrayList<>(involvedCompanies) : new ArrayList<>();
    this.keywords = keywords != null ? new ArrayList<>(keywords) : new ArrayList<>();
    this.multiplayerModeIds =
        multiplayerModeIds != null ? new ArrayList<>(multiplayerModeIds) : new ArrayList<>();
    this.parentGameId = parentGameId;
    this.playerPerspectives =
        playerPerspectives != null ? new ArrayList<>(playerPerspectives) : new ArrayList<>();
    this.remakeIds = remakeIds != null ? new ArrayList<>(remakeIds) : new ArrayList<>();
    this.remasterIds = remasterIds != null ? new ArrayList<>(remasterIds) : new ArrayList<>();
    this.similarGames = similarGames != null ? new ArrayList<>(similarGames) : new ArrayList<>();
    this.themes = themes != null ? new ArrayList<>(themes) : new ArrayList<>();
    this.screenshots = screenshots != null ? new ArrayList<>(screenshots) : new ArrayList<>();
    this.videos = videos != null ? new ArrayList<>(videos) : new ArrayList<>();
  }

  public static Game create(GameId id, GameName name, Summary summary, CoverUrl coverUrl) {
    if (id == null) throw new DomainException("El ID del juego es obligatorio");
    if (name == null) throw new DomainException("El nombre del juego es obligatorio");
    return new Game(
        id, name, summary, coverUrl, null, null, null, null, null, null, null, null, null, null,
        null, null, null, null, null, null, null, null, null, null, null, null);
  }

  public static Game reconstitute(
      GameId id,
      GameName name,
      Summary summary,
      CoverUrl coverUrl,
      List<String> platforms,
      String gameType,
      String gameStatus,
      List<String> alternativeNames,
      List<Long> dlcs,
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
    return new Game(
        id,
        name,
        summary,
        coverUrl,
        platforms,
        gameType,
        gameStatus,
        alternativeNames,
        dlcs,
        expandedGames,
        expansionIds,
        externalGames,
        franchises,
        gameModes,
        genres,
        involvedCompanies,
        keywords,
        multiplayerModeIds,
        parentGameId,
        playerPerspectives,
        remakeIds,
        remasterIds,
        similarGames,
        themes,
        screenshots,
        videos);
  }

  public void updateMetadata(GameName newName, Summary newSummary, CoverUrl newCoverUrl) {
    if (newName != null) this.name = newName;
    if (newSummary != null) this.summary = newSummary;
    if (newCoverUrl != null) this.coverUrl = newCoverUrl;
  }

  public boolean hasCover() {
    return coverUrl != null && !coverUrl.isEmpty();
  }

  public boolean hasSummary() {
    return summary != null && !summary.isEmpty();
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    Game game = (Game) o;
    return Objects.equals(id, game.id);
  }

  @Override
  public int hashCode() {
    return Objects.hash(id);
  }

  @Override
  public String toString() {
    return "Game{" + "id=" + id + ", name=" + name + ", platforms=" + platforms + '}';
  }
}

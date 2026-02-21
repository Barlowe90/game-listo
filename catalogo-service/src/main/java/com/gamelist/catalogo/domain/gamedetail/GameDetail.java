package com.gamelist.catalogo.domain.gamedetail;

import com.gamelist.catalogo.domain.exceptions.DomainException;
import com.gamelist.catalogo.domain.game.GameId;
import lombok.Getter;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

@Getter
public class GameDetail {
  private final GameId gameId;
  private final List<String> alternativeNames;
  private final String coverUrl;
  private final List<String> screenshots;
  private final List<String> videos;

  private GameDetail(
      GameId gameId,
      List<String> alternativeNames,
      String coverUrl,
      List<String> screenshots,
      List<String> videos) {
    this.gameId = Objects.requireNonNull(gameId, "GameId no puede ser nulo");
    this.alternativeNames =
        alternativeNames != null ? new ArrayList<>(alternativeNames) : new ArrayList<>();
    this.coverUrl = coverUrl;
    this.screenshots = screenshots != null ? new ArrayList<>(screenshots) : new ArrayList<>();
    this.videos = videos != null ? new ArrayList<>(videos) : new ArrayList<>();
  }

  public static GameDetail create(
      GameId gameId,
      List<String> alternativeNames,
      String coverUrl,
      List<String> screenshots,
      List<String> videos) {
    if (gameId == null) {
      throw new DomainException("El GameId es obligatorio para GameDetail");
    }
    return new GameDetail(gameId, alternativeNames, coverUrl, screenshots, videos);
  }

  public static GameDetail empty(GameId gameId) {
    return new GameDetail(gameId, new ArrayList<>(), null, new ArrayList<>(), new ArrayList<>());
  }

  public static GameDetail reconstitute(
      GameId gameId,
      List<String> alternativeNames,
      String coverUrl,
      List<String> screenshots,
      List<String> videos) {
    return new GameDetail(gameId, alternativeNames, coverUrl, screenshots, videos);
  }

  public void addScreenshot(String screenshot) {
    if (screenshot != null) {
      this.screenshots.add(screenshot);
    }
  }

  public void addVideo(String video) {
    if (video != null) {
      this.videos.add(video);
    }
  }

  public void setScreenshots(List<String> newScreenshots) {
    this.screenshots.clear();
    if (newScreenshots != null) {
      this.screenshots.addAll(newScreenshots);
    }
  }

  public void setVideos(List<String> newVideos) {
    this.videos.clear();
    if (newVideos != null) {
      this.videos.addAll(newVideos);
    }
  }

  public List<String> getScreenshots() {
    return Collections.unmodifiableList(screenshots);
  }

  public List<String> getVideos() {
    return Collections.unmodifiableList(videos);
  }

  public List<String> getAlternativeNames() {
    return Collections.unmodifiableList(alternativeNames);
  }

  public boolean hasContent() {
    return !screenshots.isEmpty() || !videos.isEmpty();
  }

  public boolean hasScreenshots() {
    return !screenshots.isEmpty();
  }

  public boolean hasVideos() {
    return !videos.isEmpty();
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    GameDetail that = (GameDetail) o;
    return Objects.equals(gameId, that.gameId);
  }

  @Override
  public int hashCode() {
    return Objects.hash(gameId);
  }

  @Override
  public String toString() {
    return "GameDetail{"
        + "gameId="
        + gameId
        + ", screenshotsCount="
        + screenshots.size()
        + ", videosCount="
        + videos.size()
        + '}';
  }
}

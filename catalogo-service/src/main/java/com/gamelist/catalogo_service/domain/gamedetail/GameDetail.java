package com.gamelist.catalogo_service.domain.gamedetail;

import com.gamelist.catalogo_service.domain.exceptions.InvalidGameDataException;
import com.gamelist.catalogo_service.domain.game.GameId;
import lombok.Getter;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

@Getter
public class GameDetail {
  private final GameId gameId;
  private final List<Screenshot> screenshots;
  private final List<Video> videos;

  private GameDetail(GameId gameId, List<Screenshot> screenshots, List<Video> videos) {
    this.gameId = Objects.requireNonNull(gameId, "GameId no puede ser nulo");
    this.screenshots = screenshots != null ? new ArrayList<>(screenshots) : new ArrayList<>();
    this.videos = videos != null ? new ArrayList<>(videos) : new ArrayList<>();
  }

  public static GameDetail create(GameId gameId, List<Screenshot> screenshots, List<Video> videos) {
    if (gameId == null) {
      throw new InvalidGameDataException("El GameId es obligatorio para GameDetail");
    }
    return new GameDetail(gameId, screenshots, videos);
  }

  public static GameDetail empty(GameId gameId) {
    return new GameDetail(gameId, new ArrayList<>(), new ArrayList<>());
  }

  public static GameDetail reconstitute(
      GameId gameId, List<Screenshot> screenshots, List<Video> videos) {
    return new GameDetail(gameId, screenshots, videos);
  }

  public void addScreenshot(Screenshot screenshot) {
    if (screenshot != null) {
      this.screenshots.add(screenshot);
    }
  }

  public void addVideo(Video video) {
    if (video != null) {
      this.videos.add(video);
    }
  }

  public void setScreenshots(List<Screenshot> newScreenshots) {
    this.screenshots.clear();
    if (newScreenshots != null) {
      this.screenshots.addAll(newScreenshots);
    }
  }

  public void setVideos(List<Video> newVideos) {
    this.videos.clear();
    if (newVideos != null) {
      this.videos.addAll(newVideos);
    }
  }

  public List<Screenshot> getScreenshots() {
    return Collections.unmodifiableList(screenshots);
  }

  public List<Video> getVideos() {
    return Collections.unmodifiableList(videos);
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

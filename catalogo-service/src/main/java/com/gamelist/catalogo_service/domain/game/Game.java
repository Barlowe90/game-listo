package com.gamelist.catalogo_service.domain.game;

import com.gamelist.catalogo_service.domain.catalog.PlatformId;
import com.gamelist.catalogo_service.domain.exceptions.InvalidGameDataException;
import lombok.Getter;

import java.time.Instant;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

@Getter
public class Game {
  private final GameId id;
  private GameName name;
  private Summary summary;
  private CoverUrl coverUrl;
  private Set<PlatformId> platformIds; // IDs de plataformas (relación lazy)
  private final Instant createdAt;
  private Instant updatedAt;

  private Game(
      GameId id,
      GameName name,
      Summary summary,
      CoverUrl coverUrl,
      Set<PlatformId> platformIds,
      Instant createdAt,
      Instant updatedAt) {
    this.id = Objects.requireNonNull(id, "GameId no puede ser nulo");
    this.name = Objects.requireNonNull(name, "GameName no puede ser nulo");
    this.summary = summary != null ? summary : Summary.empty();
    this.coverUrl = coverUrl != null ? coverUrl : CoverUrl.empty();
    this.platformIds = platformIds != null ? new HashSet<>(platformIds) : new HashSet<>();
    this.createdAt = createdAt != null ? createdAt : Instant.now();
    this.updatedAt = updatedAt != null ? updatedAt : Instant.now();
  }

  public static Game create(GameId id, GameName name, Summary summary, CoverUrl coverUrl) {
    if (id == null) {
      throw new InvalidGameDataException("El ID del juego es obligatorio");
    }
    if (name == null) {
      throw new InvalidGameDataException("El nombre del juego es obligatorio");
    }

    Instant now = Instant.now();
    return new Game(id, name, summary, coverUrl, new HashSet<>(), now, now);
  }

  public static Game reconstitute(
      GameId id,
      GameName name,
      Summary summary,
      CoverUrl coverUrl,
      Set<PlatformId> platformIds,
      Instant createdAt,
      Instant updatedAt) {
    return new Game(id, name, summary, coverUrl, platformIds, createdAt, updatedAt);
  }

  public void updateMetadata(GameName newName, Summary newSummary, CoverUrl newCoverUrl) {
    if (newName != null) {
      this.name = newName;
    }
    if (newSummary != null) {
      this.summary = newSummary;
    }
    if (newCoverUrl != null) {
      this.coverUrl = newCoverUrl;
    }
    this.updatedAt = Instant.now();
  }

  public void addPlatform(PlatformId platformId) {
    if (platformId == null) {
      throw new InvalidGameDataException("PlatformId no puede ser nulo");
    }
    this.platformIds.add(platformId);
    this.updatedAt = Instant.now();
  }

  public void setPlatforms(Set<PlatformId> newPlatformIds) {
    this.platformIds = newPlatformIds != null ? new HashSet<>(newPlatformIds) : new HashSet<>();
    this.updatedAt = Instant.now();
  }

  public void removePlatform(PlatformId platformId) {
    this.platformIds.remove(platformId);
    this.updatedAt = Instant.now();
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
    return "Game{"
        + "id="
        + id
        + ", name="
        + name
        + ", platformCount="
        + platformIds.size()
        + ", createdAt="
        + createdAt
        + '}';
  }
}

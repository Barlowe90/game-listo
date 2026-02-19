package com.gamelist.catalogo.domain.game;

import com.gamelist.catalogo.domain.catalog.PlatformId;
import com.gamelist.catalogo.domain.exceptions.DomainException;
import lombok.Getter;

import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

@Getter
public class Game {
  private final GameId id;
  private GameName name;
  private Summary summary;
  private CoverUrl coverUrl;
  private Set<PlatformId> platformIds; // IDes de plataformas (relación lazy)

  private Game(
      GameId id, GameName name, Summary summary, CoverUrl coverUrl, Set<PlatformId> platformIds) {
    this.id = Objects.requireNonNull(id, "GameId no puede ser nulo");
    this.name = Objects.requireNonNull(name, "GameName no puede ser nulo");
    this.summary = summary != null ? summary : Summary.empty();
    this.coverUrl = coverUrl != null ? coverUrl : CoverUrl.empty();
    this.platformIds = platformIds != null ? new HashSet<>(platformIds) : new HashSet<>();
  }

  public static Game create(GameId id, GameName name, Summary summary, CoverUrl coverUrl) {
    if (id == null) {
      throw new DomainException("El ID del juego es obligatorio");
    }
    if (name == null) {
      throw new DomainException("El nombre del juego es obligatorio");
    }

    return new Game(id, name, summary, coverUrl, new HashSet<>());
  }

  public static Game reconstitute(
      GameId id, GameName name, Summary summary, CoverUrl coverUrl, Set<PlatformId> platformIds) {
    return new Game(id, name, summary, coverUrl, platformIds);
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
  }

  public void addPlatform(PlatformId platformId) {
    if (platformId == null) {
      throw new DomainException("PlatformId no puede ser nulo");
    }
    this.platformIds.add(platformId);
  }

  public void setPlatforms(Set<PlatformId> newPlatformIds) {
    this.platformIds = newPlatformIds != null ? new HashSet<>(newPlatformIds) : new HashSet<>();
  }

  public void removePlatform(PlatformId platformId) {
    this.platformIds.remove(platformId);
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
    return "Game{" + "id=" + id + ", name=" + name + ", platformCount=" + platformIds.size() + '}';
  }
}

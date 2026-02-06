package com.gamelist.catalogo_service.domain.catalog;

import com.gamelist.catalogo_service.domain.exceptions.InvalidGameDataException;
import lombok.Getter;

import java.util.Objects;

@Getter
public class Platform {
  private final PlatformId id;
  private PlatformName name;
  private PlatformAbbreviation abbreviation;

  private Platform(PlatformId id, PlatformName name, PlatformAbbreviation abbreviation) {
    this.id = Objects.requireNonNull(id, "PlatformId no puede ser nulo");
    this.name = Objects.requireNonNull(name, "PlatformName no puede ser nulo");
    this.abbreviation = abbreviation != null ? abbreviation : PlatformAbbreviation.empty();
  }

  public static Platform create(
      PlatformId id, PlatformName name, PlatformAbbreviation abbreviation) {
    if (id == null) {
      throw new InvalidGameDataException("El ID de plataforma es obligatorio");
    }
    if (name == null) {
      throw new InvalidGameDataException("El nombre de plataforma es obligatorio");
    }
    return new Platform(id, name, abbreviation);
  }

  public static Platform reconstitute(
      PlatformId id, PlatformName name, PlatformAbbreviation abbreviation) {
    return new Platform(id, name, abbreviation);
  }

  public void update(PlatformName newName, PlatformAbbreviation newAbbreviation) {
    if (newName != null) {
      this.name = newName;
    }
    if (newAbbreviation != null) {
      this.abbreviation = newAbbreviation;
    }
  }

  public boolean hasAbbreviation() {
    return abbreviation != null && !abbreviation.isEmpty();
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    Platform platform = (Platform) o;
    return Objects.equals(id, platform.id);
  }

  @Override
  public int hashCode() {
    return Objects.hash(id);
  }

  @Override
  public String toString() {
    return "Platform{" + "id=" + id + ", name=" + name + ", abbreviation=" + abbreviation + '}';
  }
}

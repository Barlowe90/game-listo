package com.gamelist.catalogo.domain;

import com.gamelist.catalogo.domain.exceptions.DomainException;
import lombok.Getter;

import java.util.Objects;

@Getter
public class Platform {
  private final PlatformId id;
  private PlatformName name;
  private PlatformAbbreviation abbreviation;
  private String alternativeName;
  private String logoURL;
  private String tipo;

  private Platform(
      PlatformId id,
      PlatformName name,
      PlatformAbbreviation abbreviation,
      String alternativeName,
      String logoURL,
      String tipo) {
    this.id = Objects.requireNonNull(id, "PlatformId no puede ser nulo");
    this.name = Objects.requireNonNull(name, "PlatformName no puede ser nulo");
    this.abbreviation = abbreviation != null ? abbreviation : PlatformAbbreviation.empty();
    this.alternativeName = alternativeName;
    this.logoURL = logoURL;
    this.tipo = tipo;
  }

  public static Platform create(
      PlatformId id,
      PlatformName name,
      PlatformAbbreviation abbreviation,
      String alternativeName,
      String logoURL,
      String tipo) {
    if (id == null) {
      throw new DomainException("El ID de plataforma es obligatorio");
    }
    if (name == null) {
      throw new DomainException("El nombre de plataforma es obligatorio");
    }
    return new Platform(id, name, abbreviation, alternativeName, logoURL, tipo);
  }

  public static Platform reconstitute(
      PlatformId id,
      PlatformName name,
      PlatformAbbreviation abbreviation,
      String alternativeName,
      String logoURL,
      String tipo) {
    return new Platform(id, name, abbreviation, alternativeName, logoURL, tipo);
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
    return "Platform{"
        + "id="
        + id
        + ", name="
        + name
        + ", abbreviation="
        + abbreviation
        + ", alternativeName="
        + alternativeName
        + ", logoURL="
        + logoURL
        + ", tipo="
        + tipo
        + '}';
  }
}

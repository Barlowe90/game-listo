package com.gamelisto.catalogo.application.dto.out;

import com.gamelisto.catalogo.domain.Platform;

public record PlatformDTO(
    Long id,
    String name,
    String abbreviation,
    String alternativeName,
    String logoURL,
    String tipo) {

  public PlatformDTO(Long id, String name, String abbreviation) {
    this(id, name, abbreviation, null, null, null);
  }

  public static PlatformDTO from(Platform platform) {
    return new PlatformDTO(
        platform.getId().value(),
        platform.getName().value(),
        platform.getAbbreviation().value(),
        platform.getAlternativeName(),
        platform.getLogoURL(),
        platform.getTipo());
  }
}

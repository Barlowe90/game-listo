package com.gamelisto.catalogo.application.usecases;

import com.gamelisto.catalogo.domain.Platform;

public record PlatformResult(
    Long id,
    String name,
    String abbreviation,
    String alternativeName,
    String logoURL,
    String tipo) {

  public PlatformResult(Long id, String name, String abbreviation) {
    this(id, name, abbreviation, null, null, null);
  }

  public static PlatformResult from(Platform platform) {
    return new PlatformResult(
        platform.getId().value(),
        platform.getName().value(),
        platform.getAbbreviation().value(),
        platform.getAlternativeName(),
        platform.getLogoURL(),
        platform.getTipo());
  }
}

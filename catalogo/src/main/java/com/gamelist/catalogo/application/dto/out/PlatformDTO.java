package com.gamelist.catalogo.application.dto.out;

import com.gamelist.catalogo.domain.platform.Platform;

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

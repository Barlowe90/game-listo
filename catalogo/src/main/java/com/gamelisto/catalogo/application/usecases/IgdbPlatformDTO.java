package com.gamelisto.catalogo.application.usecases;

import com.gamelisto.catalogo.domain.Platform;
import com.gamelisto.catalogo.domain.PlatformAbbreviation;
import com.gamelisto.catalogo.domain.PlatformId;
import com.gamelisto.catalogo.domain.PlatformName;

public record IgdbPlatformDTO(
    Long id,
    String name,
    String abbreviation,
    String alternativeName,
    String logoURL,
    String tipo) {

  public Platform toDomain() {
    PlatformId pid = PlatformId.of(id());
    PlatformName pname = PlatformName.of(name());
    PlatformAbbreviation pabbrev =
        abbreviation() != null
            ? PlatformAbbreviation.of(abbreviation())
            : PlatformAbbreviation.empty();

    return Platform.create(pid, pname, pabbrev, alternativeName(), logoURL(), tipo());
  }
}

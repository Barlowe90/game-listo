package com.gamelist.catalogo.application.dto.in;

import com.gamelist.catalogo.domain.platform.Platform;
import com.gamelist.catalogo.domain.platform.PlatformAbbreviation;
import com.gamelist.catalogo.domain.platform.PlatformId;
import com.gamelist.catalogo.domain.platform.PlatformName;

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

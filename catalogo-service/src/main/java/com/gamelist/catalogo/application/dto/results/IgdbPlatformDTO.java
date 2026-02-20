package com.gamelist.catalogo.application.dto.results;

import com.gamelist.catalogo.domain.catalog.Platform;
import com.gamelist.catalogo.domain.catalog.PlatformAbbreviation;
import com.gamelist.catalogo.domain.catalog.PlatformId;
import com.gamelist.catalogo.domain.catalog.PlatformName;

public record IgdbPlatformDTO(Long id, String name, String abbreviation) {

  public Platform toDomain() {
    PlatformId pid = PlatformId.of(id());
    PlatformName pname = PlatformName.of(name());
    PlatformAbbreviation pabbrev =
        abbreviation() != null
            ? PlatformAbbreviation.of(abbreviation())
            : PlatformAbbreviation.empty();

    return Platform.create(pid, pname, pabbrev);
  }
}

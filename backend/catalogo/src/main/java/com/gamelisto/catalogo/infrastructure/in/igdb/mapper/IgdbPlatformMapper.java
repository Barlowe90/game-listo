package com.gamelisto.catalogo.infrastructure.in.igdb.mapper;

import com.gamelisto.catalogo.application.usecases.IgdbPlatformDTO;
import com.gamelisto.catalogo.infrastructure.in.igdb.IgdbImageSizes;
import com.gamelisto.catalogo.infrastructure.in.igdb.dto.PlatformFromIGDBResponse;
import org.springframework.stereotype.Component;

@Component
public class IgdbPlatformMapper {

  public IgdbPlatformDTO toApplicationDto(PlatformFromIGDBResponse platformIGDBdto) {
    if (platformIGDBdto == null) return null;

    String logoUrl =
        platformIGDBdto.platformLogo() != null
            ? platformIGDBdto.platformLogo().toSizedUrl(IgdbImageSizes.PLATFORM_LOGO_HIGH)
            : null;

    String tipo =
        platformIGDBdto.platformType() != null
            ? formatCategory(platformIGDBdto.platformType().name())
            : null;

    return new IgdbPlatformDTO(
        platformIGDBdto.id(),
        platformIGDBdto.name(),
        platformIGDBdto.abbreviation(),
        platformIGDBdto.alternativeName(),
        logoUrl,
        tipo);
  }

  private String formatCategory(String tipo) {
    return (tipo != null && !tipo.isBlank()) ? tipo : null;
  }
}

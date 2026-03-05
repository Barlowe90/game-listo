package com.gamelisto.catalogo.infrastructure.out.persistence.postgres;

import com.gamelisto.catalogo.domain.Platform;
import com.gamelisto.catalogo.domain.PlatformAbbreviation;
import com.gamelisto.catalogo.domain.PlatformId;
import com.gamelisto.catalogo.domain.PlatformName;
import org.springframework.stereotype.Component;

@Component
public class PlatformMapper {

  public PlatformEntity toEntity(Platform platform) {
    if (platform == null) {
      return null;
    }

    PlatformEntity entity = new PlatformEntity();
    entity.setId(platform.getId().value());
    entity.setName(platform.getName().value());
    entity.setAbbreviation(
        platform.getAbbreviation() != null && !platform.getAbbreviation().isEmpty()
            ? platform.getAbbreviation().value()
            : null);

    entity.setAlternativeName(platform.getAlternativeName());
    entity.setLogoURL(platform.getLogoURL());
    entity.setTipo(platform.getTipo());

    return entity;
  }

  public Platform toDomain(PlatformEntity entity) {
    if (entity == null) {
      return null;
    }

    PlatformId id = PlatformId.of(entity.getId());
    PlatformName name = PlatformName.of(entity.getName());
    PlatformAbbreviation abbreviation =
        entity.getAbbreviation() != null
            ? PlatformAbbreviation.of(entity.getAbbreviation())
            : PlatformAbbreviation.empty();

    return Platform.reconstitute(
        id, name, abbreviation, entity.getAlternativeName(), entity.getLogoURL(), entity.getTipo());
  }

  public void updateEntity(Platform platform, PlatformEntity entity) {
    entity.setName(platform.getName().value());
    entity.setAbbreviation(
        platform.getAbbreviation() != null && !platform.getAbbreviation().isEmpty()
            ? platform.getAbbreviation().value()
            : null);

    entity.setAlternativeName(platform.getAlternativeName());
    entity.setLogoURL(platform.getLogoURL());
    entity.setTipo(platform.getTipo());
  }
}

package com.gamelist.catalogo.infrastructure.out.persistence.postgres.mapper;

import com.gamelist.catalogo.domain.catalog.*;
import com.gamelist.catalogo.infrastructure.out.persistence.postgres.entity.PlatformEntity;
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

    return Platform.reconstitute(id, name, abbreviation);
  }

  public void updateEntity(Platform platform, PlatformEntity entity) {
    entity.setName(platform.getName().value());
    entity.setAbbreviation(
        platform.getAbbreviation() != null && !platform.getAbbreviation().isEmpty()
            ? platform.getAbbreviation().value()
            : null);
  }
}

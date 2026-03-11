package com.gamelisto.publicaciones.infrastructure.out.persistence;

import com.gamelisto.publicaciones.domain.GameRef;
import org.springframework.stereotype.Component;

@Component
public class GameRefMapper {

  public GameRefDocument toDocument(GameRef gameRef) {
    GameRefDocument document = new GameRefDocument();
    document.setId(gameRef.getId());
    document.setNombre(gameRef.getNombre());
    document.setPlatforms(gameRef.getPlatforms());
    return document;
  }

  public GameRef toDomain(GameRefDocument document) {
    return GameRef.reconstitute(document.getId(), document.getNombre(), document.getPlatforms());
  }
}

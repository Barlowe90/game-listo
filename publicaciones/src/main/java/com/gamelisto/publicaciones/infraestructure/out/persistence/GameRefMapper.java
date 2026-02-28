package com.gamelisto.publicaciones.infraestructure.out.persistence;

import com.gamelisto.publicaciones.domain.GameRef;
import org.springframework.stereotype.Component;

@Component
public class GameRefMapper {

  public GameRefDocument toDocument(GameRef gameRef) {
    GameRefDocument document = new GameRefDocument();
    document.setId(gameRef.getId());
    document.setNombre(gameRef.getNombre());
    document.setPlataforma(gameRef.getPlataforma());
    return document;
  }

  public GameRef toDomain(GameRefDocument document) {
    return GameRef.reconstitute(document.getId(), document.getNombre(), document.getPlataforma());
  }
}

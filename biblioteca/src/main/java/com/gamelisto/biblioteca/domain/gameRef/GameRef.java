package com.gamelisto.biblioteca.domain.gameRef;

import lombok.Getter;
import lombok.ToString;

import java.util.UUID;

@Getter
@ToString
public class GameRef {
  private final UUID id;
  private final UUID gameRefId;
  private final String nombre;
  private final String cover;

  private GameRef(UUID gameRefId, String nombre, String cover) {
    this.id = UUID.randomUUID();
    this.gameRefId = gameRefId;
    this.nombre = nombre;
    this.cover = cover;
  }

  private GameRef(UUID id, UUID gameRefId, String nombre, String cover) {
    this.id = id;
    this.gameRefId = gameRefId;
    this.nombre = nombre;
    this.cover = cover;
  }

  public static GameRef create(UUID gameRefId, String nombre, String cover) {
    return new GameRef(gameRefId, nombre, cover);
  }

  public static GameRef reconstitute(UUID id, UUID gameRefId, String nombre, String cover) {
    return new GameRef(id, gameRefId, nombre, cover);
  }
}

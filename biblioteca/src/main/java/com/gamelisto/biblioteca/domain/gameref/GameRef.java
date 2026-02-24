package com.gamelisto.biblioteca.domain.gameref;

import lombok.Getter;
import lombok.ToString;

import java.util.UUID;

@Getter
@ToString
public class GameRef {
  private final UUID id;
  private final Long gameRefId;
  private final String nombre;
  private final String cover;

  private GameRef(Long gameRefId, String nombre, String cover) {
    this.id = UUID.randomUUID();
    this.gameRefId = gameRefId;
    this.nombre = nombre;
    this.cover = cover;
  }

  private GameRef(UUID id, Long gameRefId, String nombre, String cover) {
    this.id = id;
    this.gameRefId = gameRefId;
    this.nombre = nombre;
    this.cover = cover;
  }

  public static GameRef create(Long gameRefId, String nombre, String cover) {
    return new GameRef(gameRefId, nombre, cover);
  }

  public static GameRef reconstitute(UUID id, Long gameRefId, String nombre, String cover) {
    return new GameRef(id, gameRefId, nombre, cover);
  }
}

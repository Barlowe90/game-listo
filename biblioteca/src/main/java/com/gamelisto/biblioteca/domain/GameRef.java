package com.gamelisto.biblioteca.domain;

import lombok.Getter;
import lombok.ToString;

import java.util.Objects;
import java.util.UUID;

@Getter
@ToString
public class GameRef {
  private final UUID id;
  private final String gameRefId;
  private final String nombre;
  private final String cover;

  private GameRef(String gameRefId, String nombre, String cover) {
    this.id = UUID.randomUUID();
    this.gameRefId = Objects.requireNonNull(gameRefId, "gameRefId no puede ser null");
    this.nombre = Objects.requireNonNull(nombre, "nombre no puede ser null");
    this.cover = cover;
  }

  private GameRef(UUID id, String gameRefId, String nombre, String cover) {
    this.id = Objects.requireNonNull(id, "id no puede ser null");
    this.gameRefId = Objects.requireNonNull(gameRefId, "gameRefId no puede ser null");
    this.nombre = Objects.requireNonNull(nombre, "nombre no puede ser null");
    this.cover = cover;
  }

  public static GameRef create(String gameRefId, String nombre, String cover) {
    return new GameRef(gameRefId, nombre, cover);
  }

  public static GameRef reconstitute(UUID id, String gameRefId, String nombre, String cover) {
    return new GameRef(id, gameRefId, nombre, cover);
  }
}

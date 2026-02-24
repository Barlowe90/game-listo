package com.gamelisto.biblioteca.domain.gameref;

import lombok.Getter;
import lombok.ToString;

import java.util.UUID;

@Getter
@ToString
public class GameRef {
  private final UUID id;
  private final Long catalogGameId;
  private final String nombre;
  private final String cover;

  private GameRef(Long catalogGameId, String nombre, String cover) {
    this.id = UUID.randomUUID();
    this.catalogGameId = catalogGameId;
    this.nombre = nombre;
    this.cover = cover;
  }

  private GameRef(UUID id, Long catalogGameId, String nombre, String cover) {
    this.id = id;
    this.catalogGameId = catalogGameId;
    this.nombre = nombre;
    this.cover = cover;
  }

  public static GameRef create(Long catalogGameId, String nombre, String cover) {
    return new GameRef(catalogGameId, nombre, cover);
  }

  public static GameRef reconstitute(UUID id, Long catalogGameId, String nombre, String cover) {
    return new GameRef(id, catalogGameId, nombre, cover);
  }
}

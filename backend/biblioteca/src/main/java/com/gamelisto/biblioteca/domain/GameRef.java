package com.gamelisto.biblioteca.domain;

import lombok.Getter;
import lombok.ToString;

@Getter
@ToString
public class GameRef {
  private final Long id;
  private final String nombre;
  private final String cover;

  private GameRef(Long id, String nombre, String cover) {
    this.id = id;
    this.nombre = nombre;
    this.cover = cover;
  }

  public static GameRef create(Long id, String nombre, String cover) {
    return new GameRef(id, nombre, cover);
  }

  public static GameRef reconstitute(Long id, String nombre, String cover) {
    return new GameRef(id, nombre, cover);
  }
}

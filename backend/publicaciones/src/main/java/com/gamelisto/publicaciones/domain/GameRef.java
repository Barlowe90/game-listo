package com.gamelisto.publicaciones.domain;

import lombok.Getter;
import lombok.ToString;

import java.util.List;

@Getter
@ToString
public class GameRef {
  private final Long id;
  private final String nombre;
  private final List<String> platforms;

  private GameRef(Long id, String nombre, List<String> platforms) {
    this.id = id;
    this.nombre = nombre;
    this.platforms = platforms;
  }

  public static GameRef create(Long id, String nombre, List<String> platforms) {
    return new GameRef(id, nombre, platforms);
  }

  public static GameRef reconstitute(Long id, String nombre, List<String> platforms) {
    return new GameRef(id, nombre, platforms);
  }
}

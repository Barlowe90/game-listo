package com.gamelisto.publicaciones.domain;

import lombok.Getter;
import lombok.ToString;

@Getter
@ToString
public class GameRef {
  private final Long id;
  private final String nombre;
  private final String plataforma;

  private GameRef(Long id, String nombre, String plataforma) {
    this.id = id;
    this.nombre = nombre;
    this.plataforma = plataforma;
  }

  public static GameRef create(Long id, String nombre, String plataforma) {
    return new GameRef(id, nombre, plataforma);
  }

  public static GameRef reconstitute(Long id, String nombre, String plataforma) {
    return new GameRef(id, nombre, plataforma);
  }
}

package com.gamelist.catalogo.domain.events;

public record GameCreado(String id, String name, String portada) {
  public static GameCreado of(String id, String name, String portada) {
    return new GameCreado(id, name, portada);
  }
}

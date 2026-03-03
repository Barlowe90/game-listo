package com.gamelisto.busquedas.domain;

import lombok.Getter;

import java.util.List;

@Getter
public class BuscarJuegoDoc {

  private final long gameId;
  private final String title;
  private final List<String> alternativeNames;

  public BuscarJuegoDoc(long gameId, String title, List<String> alternativeNames) {
    this.gameId = gameId;
    this.title = title;
    this.alternativeNames = alternativeNames == null ? List.of() : List.copyOf(alternativeNames);
  }
}

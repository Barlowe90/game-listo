package com.gamelisto.biblioteca.domain.eventos;

public record EstadoActualizado(String usuarioId, Long gameId, String estado) {

  public static EstadoActualizado of(String usuarioId, Long gameId, String estado) {
    return new EstadoActualizado(usuarioId, gameId, estado);
  }
}

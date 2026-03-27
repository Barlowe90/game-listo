package com.gamelisto.biblioteca.domain.eventos;

import java.util.UUID;

public record EstadoActualizado(UUID usuarioId, Long gameId, String estado) {

  public static EstadoActualizado of(UUID usuarioId, Long gameId, String estado) {
    return new EstadoActualizado(usuarioId, gameId, estado);
  }
}
